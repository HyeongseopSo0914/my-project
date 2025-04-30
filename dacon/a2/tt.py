import numpy as np
import pandas as pd
import random
import torch
from pytorch_tabnet.pretraining import TabNetPretrainer
from pytorch_tabnet.tab_model import TabNetRegressor
from sklearn.model_selection import KFold
from sklearn.preprocessing import LabelEncoder
import warnings
warnings.filterwarnings('ignore')

SEED = 42
np.random.seed(SEED)
random.seed(SEED)
torch.manual_seed(SEED)

train = pd.read_csv('train.csv')
test = pd.read_csv('test.csv')
sample_submission = pd.read_csv('tt.csv')

#특성과 타겟 변수 분리
train = train.drop(columns=['ID'], axis = 1)
test = test.drop(columns=['ID'], axis = 1)

# 1. 데이터 전처리
train['설립연도'] = train['설립연도'].astype('object')
test['설립연도'] = test['설립연도'].astype('object')

category_features = ['설립연도', '국가', '분야', '투자단계', '기업가치(백억원)']
numeric_features = ['직원 수', '고객수(백만명)', '총 투자금(억원)', '연매출(억원)', 'SNS 팔로워 수(백만명)']
bool_features = ['인수여부', '상장여부']

encoders = {}
for feature in category_features:
    encoders[feature] = LabelEncoder()
    train[feature] = train[feature].fillna('Missing')
    test[feature] = test[feature].fillna('Missing')
    train[feature] = encoders[feature].fit_transform(train[feature])
    test[feature] = encoders[feature].transform(test[feature])

bool_map = {'Yes': 1, 'No': 0}
for feature in bool_features:
    train[feature] = train[feature].map(bool_map)
    test[feature] = test[feature].map(bool_map)

for feature in numeric_features:
    mean_value = train[feature].mean()
    train[feature] = train[feature].fillna(mean_value)
    test[feature] = test[feature].fillna(mean_value)

features = [col for col in train.columns if col != '성공확률']
cat_idxs = [features.index(col) for col in category_features]
cat_dims = [train[col].nunique() for col in category_features]

# 2. 모델 학습
N_FOLDS = 5
kf = KFold(n_splits=N_FOLDS, shuffle=True, random_state=SEED)

models = []
cv_scores = []

X = train[features].values
y = train['성공확률'].values.reshape(-1, 1)

for fold, (train_idx, valid_idx) in enumerate(kf.split(X)):
    print(f"\n\U0001F501 Fold {fold+1}/{N_FOLDS}")

    X_train, X_valid = X[train_idx], X[valid_idx]
    y_train, y_valid = y[train_idx], y[valid_idx]

    # # Pretraining
    # print("\u25b6 Pretraining...")
    # pretrainer = TabNetPretrainer(
    #     cat_idxs=cat_idxs,
    #     cat_dims=cat_dims,
    #     seed=SEED + fold,
    #     n_d=32,
    #     n_a=32,
    #     n_steps=5,
    #     verbose=0
    # )
    # pretrainer.fit(
    #     X_train=X_train,
    #     eval_set=[X_valid],
    #     max_epochs=50,
    #     batch_size=256,
    #     virtual_batch_size=128,
    #     patience=5
    # )

    # Fine-tuning
    print("\u25b6 Fine-tuning...")
    model = TabNetRegressor(
        cat_idxs=cat_idxs,
        cat_dims=cat_dims,
        seed=SEED + fold,
        n_d=32,
        n_a=32,
        n_steps=5,
        optimizer_fn=torch.optim.AdamW,
        optimizer_params={"lr":2e-3},
        scheduler_params={"T_max":200, "eta_min":1e-5},
        scheduler_fn=torch.optim.lr_scheduler.CosineAnnealingLR,
        verbose=0
    )
    model.fit(
        X_train=X_train, y_train=y_train,
        eval_set=[(X_valid, y_valid)],
        eval_metric=['mae'],
        max_epochs=200,
        patience=15,
        batch_size=512,
        virtual_batch_size=256
    )

    models.append(model)
    cv_scores.append(model.best_cost)

print("\n\u2705 모든 fold 모델 학습 완료!")
print(f"CV Scores (MAE): {cv_scores}")
print(f"Mean CV Score (MAE): {np.mean(cv_scores):.6f}")

# 3. 예측
predictions_list = []

for fold, model in enumerate(models):
    print(f"Predict with fold {fold+1}")
    preds = model.predict(test[features].values)
    predictions_list.append(preds)

final_predictions = np.mean(predictions_list, axis=0)

sample_submission['성공확률'] = final_predictions
sample_submission.to_csv('./tt_submission.csv', index=False, encoding='utf-8-sig')

