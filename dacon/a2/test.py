
import pandas as pd
import numpy as np

from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import KFold

import torch
from pytorch_tabnet.pretraining import TabNetPretrainer
from pytorch_tabnet.tab_model import TabNetRegressor

train = pd.read_csv('train.csv')
test = pd.read_csv('test.csv')
sample_submission = pd.read_csv('sample_submission.csv')

#특성과 타겟 변수 분리
train = train.drop(columns=['ID'], axis = 1)
test = test.drop(columns=['ID'], axis = 1)

# ✅ 1. 데이터 전처리
CURRENT_YEAR = 2025

# 나이 변환
train['나이'] = CURRENT_YEAR - train['설립연도']
test['나이'] = CURRENT_YEAR - test['설립연도']

# 국가 Encoding
le_country = LabelEncoder()
train['국가'] = le_country.fit_transform(train['국가'].fillna('Missing'))
test['국가'] = le_country.transform(test['국가'].fillna('Missing'))

# 분야 점수화 (구간 매핑)
field_score = {
    'AI': 5, '핀테크': 5, '기술': 4, '물류': 3, '에듀테크': 3,
    '푸드테크': 2, '게임': 2, '에너지': 1, '이커머스': 1, '헬스케어': 1, 0:0
}
train['분야점수'] = train['분야'].map(field_score).fillna(0)
test['분야점수'] = test['분야'].map(field_score).fillna(0)

# 투자단계 수치화
stage_mapping = {'Seed':0, 'Series A':1, 'Series B':2, 'Series C':3, 'IPO':4}
train['투자단계'] = train['투자단계'].map(stage_mapping).fillna(0)
test['투자단계'] = test['투자단계'].map(stage_mapping).fillna(0)

# 인수/상장 여부 0/1 매핑
bool_map = {'Yes':1, 'No':0}
for feature in ['인수여부', '상장여부']:
    train[feature] = train[feature].map(bool_map).fillna(0)
    test[feature] = test[feature].map(bool_map).fillna(0)

numeric_features = ['직원 수','고객수(백만명)','총 투자금(억원)','연매출(억원)','SNS 팔로워 수(백만명)']
# 수치형 변수 결측치를 평균값으로 대체
for feature in numeric_features:
    mean_value = train[feature].mean()
    train[feature] = train[feature].fillna(mean_value)
    test[feature] = test[feature].fillna(mean_value)

# 투자 대비 매출 Feature 추가
train['투자대비매출'] = train['연매출(억원)'].fillna(0) / (train['총 투자금(억원)'].fillna(0)+1)
test['투자대비매출'] = test['연매출(억원)'].fillna(0) / (test['총 투자금(억원)'].fillna(0)+1)

# SNS 팔로워 수 구간화
def sns_bin(x):
    if x < 1: return 0
    elif x < 3: return 1
    elif x < 5: return 2
    else: return 3
train['SNS팔로워구간'] = train['SNS 팔로워 수(백만명)'].apply(lambda x: sns_bin(x) if not pd.isna(x) else 0)
test['SNS팔로워구간'] = test['SNS 팔로워 수(백만명)'].apply(lambda x: sns_bin(x) if not pd.isna(x) else 0)

# 기업가치 수치화
def parse_value(x):
    if pd.isnull(x): return 0
    if '6000' in str(x): return 6
    if '4500' in str(x): return 5
    if '3500' in str(x): return 4
    if '2500' in str(x): return 3
    if '1500' in str(x): return 2
    return 1
train['기업가치_클래스'] = train['기업가치(백억원)'].apply(parse_value)
test['기업가치_클래스'] = test['기업가치(백억원)'].apply(parse_value)

# # 결측치는 모두 0 처리
# train = train.fillna(0)
# test = test.fillna(0)
# 최종 Feature 리스트
features = [
    '나이', '국가', '분야점수', '투자단계', '직원 수',
    '인수여부', '상장여부', '고객수(백만명)', '투자대비매출',
    'SNS팔로워구간', '기업가치_클래스'
]
category_features = ['국가', '분야점수', '투자단계', 'SNS팔로워구간', '기업가치_클래스']
cat_idxs = [features.index(col) for col in category_features]
cat_dims = [int(train[col].max())+2 for col in category_features]


# ✅ 2. TabNet 학습 (KFold)
N_FOLDS = 5
kf = KFold(n_splits=N_FOLDS, shuffle=True, random_state=42)

models = []
cv_scores = []

target = train['성공확률']
X = train[features]
y = target

for fold, (train_idx, valid_idx) in enumerate(kf.split(X)):
    print(f"\n🔁 Fold {fold+1}/{N_FOLDS}")

    X_train, X_valid = X.iloc[train_idx].values, X.iloc[valid_idx].values
    y_train, y_valid = y.iloc[train_idx].values.reshape(-1,1), y.iloc[valid_idx].values.reshape(-1,1)

    print("▶ Pretraining...")
    pretrainer = TabNetPretrainer(
        cat_idxs=cat_idxs,
        cat_dims=cat_dims,
        seed=42+fold,
        verbose=0
    )
    pretrainer.fit(
        X_train=X_train,
        eval_set=[X_valid],
        max_epochs=100,
        batch_size=512,
        virtual_batch_size=64,
        patience=10
    )

    print("▶ Fine-tuning...")
    model = TabNetRegressor(
        cat_idxs=cat_idxs,
        cat_dims=cat_dims,
        seed=42+fold,
        verbose=0,
        optimizer_fn=torch.optim.AdamW
    )
    model.fit(
        X_train=X_train, y_train=y_train,
        eval_set=[(X_valid, y_valid)],
        from_unsupervised=pretrainer,
        eval_metric=['mae'],
        max_epochs=100,
        patience=10
    )

    models.append(model)
    cv_scores.append(model.best_cost)

print("\n✅ 모든 fold 학습 완료!")
print("CV Scores:", cv_scores)
print("Mean CV:", np.mean(cv_scores))


# ✅ 3. Feature Importance 보기
model = models[0]
importance_df = pd.DataFrame({
    'feature': features,
    'importance': model.feature_importances_
}).sort_values(by='importance', ascending=False)

# 저장된 모델들로 예측
predictions_list = []

for fold, model in enumerate(models):
    print(f"Predict with fold {fold+1}")
    preds = model.predict(test[features].values)
    predictions_list.append(preds)

# 평균 예측
final_predictions = np.mean(predictions_list, axis=0)

sample_submission['성공확률'] = final_predictions
sample_submission.to_csv('./baseline_submission.csv', index = False, encoding = 'utf-8-sig')
