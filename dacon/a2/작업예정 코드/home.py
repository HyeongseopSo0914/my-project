import pandas as pd
import numpy as np

import matplotlib.pyplot as plt
import matplotlib.font_manager as fm
import seaborn as sns
from sklearn.preprocessing import LabelEncoder
from sklearn.ensemble import GradientBoostingRegressor
import lightgbm as lgb
import matplotlib.pyplot as plt
import seaborn as sns

# 맑은 고딕 설정
plt.rc('font', family='Malgun Gothic')  # Windows
# plt.rc('font', family='AppleGothic')  # macOS
# plt.rc('font', family='NanumGothic')  # Linux (Colab 등에서)

# 마이너스 깨짐 방지
plt.rcParams['axes.unicode_minus'] = False

train = pd.read_csv('train.csv')
test = pd.read_csv('test.csv')
sample_submission = pd.read_csv('sample_submission.csv')

#특성과 타겟 변수 분리
train = train.drop(columns=['ID'], axis = 1)
test = test.drop(columns=['ID'], axis = 1)

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
    
def encode_categoricals(df, cols):
    df = df.copy()
    for col in cols:
        le = LabelEncoder()
        df[col] = le.fit_transform(df[col].astype(str))
    return df

def fill_missing_values_v3(df, is_train=True):
    df = df.copy()

    # 범위 문자열 → 평균 숫자 처리
    for col in ['연매출(억원)', '총 투자금(억원)', '기업가치(백억원)']:
        df[col] = df[col].apply(convert_range_to_float)

    # 분야 결측 및 인코딩
    if '분야' in df.columns:
        df['분야'] = df['분야'].fillna('Unknown')
        df['분야'] = LabelEncoder().fit_transform(df['분야'])

    # 국가, 투자단계 인코딩
    df = encode_categoricals(df, ['국가', '투자단계'])

    # ✅ 결측 플래그 추가 함수
    def add_missing_flag(column):
        flag_col = f'{column}_결측'
        df[flag_col] = df[column].isnull().astype(int)

    # ✅ 피처셋 생성 함수
    def get_features(base):
        return base + (['성공확률'] if is_train else [])

    # 1. 직원 수
    if '직원 수' in df.columns:
        add_missing_flag('직원 수')
        features = get_features(['설립연도', '국가', '투자단계', '연매출(억원)', '총 투자금(억원)', 'SNS 팔로워 수(백만명)'])
        complete = df[df['직원 수'].notnull()]
        missing = df[df['직원 수'].isnull()]
        if not complete.empty and not missing.empty:
            model = GradientBoostingRegressor()
            model.fit(complete[features], complete['직원 수'])
            df.loc[df['직원 수'].isnull(), '직원 수'] = model.predict(missing[features])

    # 2. 고객 수
    if '고객수(백만명)' in df.columns:
        add_missing_flag('고객수(백만명)')
        features = get_features(['설립연도', '직원 수', '분야', '연매출(억원)', '총 투자금(억원)', 'SNS 팔로워 수(백만명)'])
        complete = df[df['고객수(백만명)'].notnull()]
        missing = df[df['고객수(백만명)'].isnull()]
        if not complete.empty and not missing.empty:
            model = GradientBoostingRegressor()
            model.fit(complete[features], complete['고객수(백만명)'])
            df.loc[df['고객수(백만명)'].isnull(), '고객수(백만명)'] = model.predict(missing[features])

    # 3. 기업가치
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

# 최종 결측치 보간 시도
train_filled = fill_missing_values_v3(train, is_train=True)
train_filled.isnull().sum()  # 모든 결측치가 잘 채워졌는지 확인

test_filled = fill_missing_values_v3(test, is_train=False)
test_filled.isnull().sum()  # 모든 결측치가 잘 채워졌는지 확인

# 1. 피처 및 타겟 설정
feature_cols = [col for col in train_filled.columns if col not in ['성공확률']]
target_col = '성공확률'

X = train_filled[feature_cols].copy()
y = train_filled[target_col].copy()

for col in X.select_dtypes(include='object').columns:
    if set(X[col].unique()) <= {'Yes', 'No'}:
        X[col] = X[col].map({'No': 0, 'Yes': 1})
print(X.dtypes[X.dtypes == 'object'])         
# 2. 모델 훈련
model = lgb.LGBMRegressor()
model.fit(X, y)

# 3. Feature Importance 추출
importance_df = pd.DataFrame({
    'feature': feature_cols,
    'importance': model.feature_importances_
}).sort_values(by='importance', ascending=False)

# 4. 시각화
plt.figure(figsize=(10, 6))
sns.barplot(x='importance', y='feature', data=importance_df.head(15))
plt.title('Top 15 Feature Importances')
plt.tight_layout()
plt.show()

def detect_outliers_summary(df, columns):
    summary = []
    for col in columns:
        Q1 = df[col].quantile(0.25)
        Q3 = df[col].quantile(0.75)
        IQR = Q3 - Q1
        lower_bound = Q1 - 1.5 * IQR
        upper_bound = Q3 + 1.5 * IQR
        outliers = df[(df[col] < lower_bound) | (df[col] > upper_bound)]
        summary.append({
            '컬럼명': col,
            '이상치 수': len(outliers),
            '전체 대비 비율(%)': round(len(outliers) / len(df) * 100, 2)
        })
    return pd.DataFrame(summary).sort_values(by='이상치 수', ascending=False)

# log 변환 (0보다 큰 값만 변환, log1p는 log(1+x))
num_cols = train_filled.select_dtypes(include=['int64', 'float64']).columns.tolist()
print(num_cols)
log_train = train_filled[num_cols].copy()
for col in num_cols:
    if (log_train[col] > 0).all():  # 음수, 0 있는 컬럼은 제외
        log_train[col] = np.log1p(log_train[col])

# melt 후 시각화
melted_log = log_train.melt(var_name='Feature', value_name='Value')

plt.figure(figsize=(12, 6))
sns.boxplot(x='Feature', y='Value', data=melted_log)
plt.xticks(rotation=45)
plt.title('Boxplot of Numeric Features (log1p transformed)')
plt.tight_layout()
plt.show()

def process_outliers_train_test(train_df, test_df, num_cols, method='flag+clip'):
    """
    train 데이터 기준으로 IQR 이상치 탐지 기준을 잡고,
    train/test 모두 동일한 방식으로 이상치 처리하는 함수
    """
    train_processed = train_df.copy()
    test_processed = test_df.copy()
    outlier_bounds = {}

    for col in num_cols:
        Q1 = train_df[col].quantile(0.25)
        Q3 = train_df[col].quantile(0.75)
        IQR = Q3 - Q1
        lower = Q1 - 1.5 * IQR
        upper = Q3 + 1.5 * IQR
        outlier_bounds[col] = (lower, upper)

        # 이상치 플래그
        if 'flag' in method:
            train_processed[f'{col}_이상치여부'] = ((train_df[col] < lower) | (train_df[col] > upper)).astype(int)
            test_processed[f'{col}_이상치여부'] = ((test_df[col] < lower) | (test_df[col] > upper)).astype(int)

        # 클리핑
        if 'clip' in method:
            train_processed[col] = train_df[col].clip(lower, upper)
            test_processed[col] = test_df[col].clip(lower, upper)

        # 로그 변환
        if 'log' in method:
            if (train_processed[col] >= 0).all() and (test_processed[col] >= 0).all():
                train_processed[col] = np.log1p(train_processed[col])
                test_processed[col] = np.log1p(test_processed[col])
            else:
                print(f"[경고] {col}은 log1p 불가능 (음수 또는 0 포함)")

    return train_processed, test_processed, outlier_bounds

# '성공확률'은 train에만 있으므로 제외
num_cols = train_filled.select_dtypes(include=np.number).columns.tolist()
num_cols = [col for col in num_cols if col != '성공확률']

# 이상치 처리 수행
train_processed, test_processed, bounds = process_outliers_train_test(train_filled, test_filled, num_cols, method='flag+clip')

# 결과 확인: 일부 컬럼에 대해 이상치 여부 플래그 분포 출력
for col in ['연매출(억원)', '고객수(백만명)', 'SNS 팔로워 수(백만명)']:
    flag_col = f'{col}_이상치여부'
    if flag_col in train_processed.columns:
        print(f"▶ {flag_col} - 이상치 개수(train): {train_processed[flag_col].sum()}")

# X에 필요한 컬럼들이 모두 있는지 확인
required_cols = ['직원 수', '연매출(억원)', '총 투자금(억원)', '고객수(백만명)', 
                 'SNS 팔로워 수(백만명)', '기업가치(백억원)', '설립연도']

missing = [col for col in required_cols if col not in X.columns]
print("❗ 누락된 컬럼:", missing if missing else "없음 — 파생변수 생성 가능")

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

    df['고객수_결측'] = df['고객수(백만명)'].isna().astype(int)
    df['직원 수_결측'] = df['직원 수'].isna().astype(int)
    
    return df

X = train_processed.copy()  # ← 여기서 train_processed에 이상치 플래그가 들어있어야 함
X = create_features(X)      # 파생변수 추가

outlier_flags = [col for col in X.columns if '이상치여부' in col]
print("📌 포함된 이상치 플래그 컬럼들:")
print(outlier_flags)

for col in outlier_flags:
    count = X[col].sum()
    print(f"{col} - 이상치 개수: {count}")

# 연속형 변수 간 상관계수
num_cols = [
    '직원 수', '연매출(억원)', '총 투자금(억원)', '고객수(백만명)', 
    'SNS 팔로워 수(백만명)', '기업가치(백억원)', 
    '직원 수_로그', '연매출_로그', '총 투자금_로그', 
    '고객수_직원비', '연매출_직원비', '투자대비매출',
    'SNS당고객', '기업가치대비투자', '설립년차'
]
corr_matrix = X[num_cols].corr()

plt.figure(figsize=(12, 10))
sns.heatmap(corr_matrix, annot=True, fmt=".2f", cmap="coolwarm", center=0)
plt.title("Selected Feature Correlation Heatmap")
plt.show()   

ols_to_drop = [
    '직원 수', '연매출(억원)', 
    '직원 수_결측', '고객수_결측', '설립연도'
    ,'총 투자금(억원)'
]

existing_cols_to_drop = [col for col in ols_to_drop if col in X.columns]
X = X.drop(columns=existing_cols_to_drop)

y = train_processed['성공확률']  # 또는 미리 분리한 y 사용

# '성공확률' 컬럼이 있다면 삭제
if '성공확률' in X.columns:
    X = X.drop(columns=['성공확률'])

# y 결합
X_with_y = X.join(y.rename("성공확률"))

# 숫자형만 남기고 상관계수 계산
X_with_y_numeric = X_with_y.select_dtypes(include=['number'])
cor_target = X_with_y_numeric.corr()['성공확률'].sort_values(key=abs, ascending=False)

print(cor_target.head(15))

from sklearn.model_selection import KFold
from sklearn.metrics import mean_absolute_error
from catboost import CatBoostRegressor, Pool
from xgboost import XGBRegressor
import random
from sklearn.model_selection import StratifiedKFold
random.seed(42)
np.random.seed(42)
X_test = test_processed.copy()
X_test = create_features(X_test)
cols_to_drop = ['직원 수', '연매출(억원)','직원 수_결측', '고객수_결측', '설립연도'
                #  ,'총 투자금(억원)'
                 ]
X_test = X_test.drop(columns=cols_to_drop, errors='ignore')
X_test = X_test[X.columns]

# kf = KFold(n_splits=5, shuffle=True, random_state=42)
# cv_scores = []
# models = []
# test_preds = []

categorical_features = ['국가', '분야', '투자단계']  # 필요시 실제 범주형 컬럼 이름으로 수정
for col in ['인수여부', '상장여부']:
    X[col] = X[col].map({'No': 0, 'Yes': 1})
    X_test[col] = X_test[col].map({'No': 0, 'Yes': 1})  # test에도 동일하게 적용

print(X.dtypes[X.dtypes == 'object'])

bins = np.linspace(0, 1, 6)
y_binned = np.digitize(y, bins)

# StratifiedKFold 적용
skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
cv_scores = []
models = []
test_preds = []

for fold, (train_idx, val_idx) in enumerate(skf.split(X, y_binned)):
    print(f"Fold {fold+1}")
    X_tr, y_tr = X.iloc[train_idx], y.iloc[train_idx]
    X_val, y_val = X.iloc[val_idx], y.iloc[val_idx]

    # model = CatBoostRegressor(
    #     iterations=1000,
    #     learning_rate=0.05,
    #     depth=6,
    #     cat_features=categorical_features,
    #     random_state=42,
    #     early_stopping_rounds=50,
    #     verbose=100
    # )
    # model.fit(X_tr, y_tr, eval_set=(X_val, y_val))
    model = XGBRegressor(
        n_estimators=500,
        learning_rate=0.03,
        max_depth=15,
        subsample=0.8,
        colsample_bytree=0.8,
        random_state=42,
        n_jobs=-1
    )
    model.fit(X_tr, y_tr)


    

    y_pred = model.predict(X_val)
    mae = mean_absolute_error(y_val, y_pred)
    cv_scores.append(mae)
    models.append(model)

    test_preds.append(model.predict(X_test))
    print(f"Fold {fold+1} MAE: {mae:.4f}")

plt.boxplot(cv_scores)
plt.title("MAE 분포 (CV)")
plt.show()

print(f"\nAverage MAE across folds: {np.mean(cv_scores):.5f}")

final_test_pred = np.mean(test_preds, axis=0)

# test_preds = np.zeros(len(X_test))

# for model in models:
#     test_preds += model.predict(X_test) / kf.get_n_splits()

# final_preds = test_preds

print("\n✅ Test 예측 완료 (KFold 모델 평균)")
print(final_test_pred[:10])

sample_submission['성공확률'] = final_test_pred
sample_submission.to_csv('submission_xgb_kfold_1.csv', index=False, encoding='utf-8-sig')
print("📁 submission_xgb_kfold_1.csv 저장 완료!")
sample_submission.head()