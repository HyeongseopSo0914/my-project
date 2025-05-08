import pandas as pd
import numpy as np
from sklearn.preprocessing import LabelEncoder
from sklearn.ensemble import GradientBoostingRegressor
from sklearn.model_selection import StratifiedKFold
from sklearn.metrics import mean_absolute_error
import lightgbm as lgb
from xgboost import XGBRegressor
import random

# Seed 고정
random.seed(42)
np.random.seed(42)

# 데이터 로딩
train = pd.read_csv('train.csv').drop(columns=['ID'])
test = pd.read_csv('test.csv').drop(columns=['ID'])
sample_submission = pd.read_csv('sample_submission.csv')

# 범위형 → 숫자형 변환 함수
def convert_range_to_float(value):
    if isinstance(value, str) and '-' in value:
        try:
            low, high = map(float, value.split('-'))
            return (low + high) / 2
        except:
            return np.nan
    try:
        return float(value)
    except:
        return np.nan

# 범주형 인코딩 함수
def encode_categoricals(df, cols):
    df = df.copy()
    for col in cols:
        le = LabelEncoder()
        df[col] = le.fit_transform(df[col].astype(str))
    return df

# 결측치 보간
def fill_missing_values_v3(df, is_train=True):
    df = df.copy()

    for col in ['연매출(억원)', '총 투자금(억원)', '기업가치(백억원)']:
        df[col] = df[col].apply(convert_range_to_float)

    if '분야' in df.columns:
        df['분야'] = df['분야'].fillna('Unknown')
        df['분야'] = LabelEncoder().fit_transform(df['분야'])

    df = encode_categoricals(df, ['국가', '투자단계'])

    def add_missing_flag(column):
        df[f'{column}_결측'] = df[column].isnull().astype(int)

    def get_features(base):
        return base + (['성공확률'] if is_train else [])

    # 직원 수
    if '직원 수' in df.columns:
        add_missing_flag('직원 수')
        features = get_features(['설립연도', '국가', '투자단계', '연매출(억원)', '총 투자금(억원)', 'SNS 팔로워 수(백만명)'])
        complete = df[df['직원 수'].notnull()]
        missing = df[df['직원 수'].isnull()]
        if not complete.empty and not missing.empty:
            model = GradientBoostingRegressor()
            model.fit(complete[features], complete['직원 수'])
            df.loc[df['직원 수'].isnull(), '직원 수'] = model.predict(missing[features])

    # 고객수
    if '고객수(백만명)' in df.columns:
        add_missing_flag('고객수(백만명)')
        features = get_features(['설립연도', '직원 수', '분야', '연매출(억원)', '총 투자금(억원)', 'SNS 팔로워 수(백만명)'])
        complete = df[df['고객수(백만명)'].notnull()]
        missing = df[df['고객수(백만명)'].isnull()]
        if not complete.empty and not missing.empty:
            model = GradientBoostingRegressor()
            model.fit(complete[features], complete['고객수(백만명)'])
            df.loc[df['고객수(백만명)'].isnull(), '고객수(백만명)'] = model.predict(missing[features])

    # 기업가치
    if '기업가치(백억원)' in df.columns:
        add_missing_flag('기업가치(백억원)')
        features = get_features(['설립연도', '직원 수', '고객수(백만명)', '분야', '연매출(억원)', '총 투자금(억원)', 'SNS 팔로워 수(백만명)'])
        complete = df[df['기업가치(백억원)'].notnull()]
        missing = df[df['기업가치(백억원)'].isnull()]
        if not complete.empty and not missing.empty:
            model = GradientBoostingRegressor()
            model.fit(complete[features], complete['기업가치(백억원)'])
            df.loc[df['기업가치(백억원)'].isnull(), '기업가치(백억원)'] = model.predict(missing[features])

    return df

# 이상치 처리 함수
def process_outliers_train_test(train_df, test_df, num_cols, method='flag+clip'):
    train_processed = train_df.copy()
    test_processed = test_df.copy()

    for col in num_cols:
        Q1 = train_df[col].quantile(0.25)
        Q3 = train_df[col].quantile(0.75)
        IQR = Q3 - Q1
        lower = Q1 - 1.5 * IQR
        upper = Q3 + 1.5 * IQR

        if 'flag' in method:
            train_processed[f'{col}_이상치여부'] = ((train_df[col] < lower) | (train_df[col] > upper)).astype(int)
            test_processed[f'{col}_이상치여부'] = ((test_df[col] < lower) | (test_df[col] > upper)).astype(int)

        if 'clip' in method:
            train_processed[col] = train_df[col].clip(lower, upper)
            test_processed[col] = test_df[col].clip(lower, upper)

    return train_processed, test_processed

# 파생변수 생성
def create_features(df):
    df = df.copy()
    df['직원 수_로그'] = np.log1p(df['직원 수'])
    df['연매출_로그'] = np.log1p(df['연매출(억원)'])
    df['총 투자금_로그'] = np.log1p(df['총 투자금(억원)'])

    df['고객수_직원비'] = df['고객수(백만명)'] / (df['직원 수'] + 1)
    df['연매출_직원비'] = df['연매출(억원)'] / (df['직원 수'] + 1)
    df['투자대비매출'] = df['연매출(억원)'] / (df['총 투자금(억원)'] + 1)
    df['SNS당고객'] = df['고객수(백만명)'] / (df['SNS 팔로워 수(백만명)'] + 1)
    df['기업가치대비투자'] = df['기업가치(백억원)'] / (df['총 투자금(억원)'] + 1)

    df['설립년차'] = 2025 - df['설립연도']
    return df

# 결측치 및 이상치 처리
train_filled = fill_missing_values_v3(train, is_train=True)
test_filled = fill_missing_values_v3(test, is_train=False)

num_cols = train_filled.select_dtypes(include=np.number).columns.tolist()
num_cols = [col for col in num_cols if col != '성공확률']

train_processed, test_processed = process_outliers_train_test(train_filled, test_filled, num_cols)

# 파생변수 생성
X = create_features(train_processed)
X_test = create_features(test_processed)

# 필요 없는 열 제거
drop_cols = ['직원 수', '연매출(억원)', '직원 수_결측', '고객수_결측', '설립연도']
X = X.drop(columns=[col for col in drop_cols if col in X.columns])
X_test = X_test.drop(columns=[col for col in drop_cols if col in X_test.columns])

# 범주형 인코딩 (이진형)
for col in ['인수여부', '상장여부']:
    X[col] = X[col].map({'No': 0, 'Yes': 1})
    X_test[col] = X_test[col].map({'No': 0, 'Yes': 1})

# 타겟 분리 및 정렬
y = train_processed['성공확률']
X_test = X_test[X.columns]  # 열 정렬

# Stratified K-Fold 분할용 그룹 생성
bins = np.linspace(0, 1, 6)
y_binned = np.digitize(y, bins)

# 모델 훈련 (예시: XGB)
skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
cv_scores = []
models = []
test_preds = []

for fold, (train_idx, val_idx) in enumerate(skf.split(X, y_binned)):
    X_train, X_val = X.iloc[train_idx], X.iloc[val_idx]
    y_train, y_val = y.iloc[train_idx], y.iloc[val_idx]

    model = XGBRegressor(n_estimators=500, learning_rate=0.01, max_depth=6, random_state=42)
    model.fit(X_train, y_train, eval_set=[(X_val, y_val)], early_stopping_rounds=50, verbose=False)

    pred_val = model.predict(X_val)
    pred_test = model.predict(X_test)

    mae = mean_absolute_error(y_val, pred_val)
    cv_scores.append(mae)
    models.append(model)
    test_preds.append(pred_test)

print(f"평균 MAE: {np.mean(cv_scores):.5f}")
