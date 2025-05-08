pip install -r requirements.txt


# 결측치 시각화
import missingno as msno

1. 데이터 확인
2. 목표 확인        -Survived 생존 여부 예측
3. 결측치 확인
4. 각 컬럼별 데이터 분석
    ->탐색적 데이터 분석(EDA) 강화
    ->수치형/범주형 변수별 분포 시각화
    ->outlier(이상치) 탐색 및 처리   
        ->seaborn.countplot, sns.violinplot, sns.boxplot
5. 데이터 라벨링 및 불리언
6. 필요없는 데이터 drop
7. feature engineering
    ->이름 추출
    ->가족수 -- 단독/동반 여부
    ->갑판 정보
    ->그룹화
    ->범주형 변수 (one-hot encoding or LabelEncoder, OrdinalEncoder)
8. 상관계수
    -> Randomforest, XGBoost, LightGBM 등에서 feature importance 확인
    -> SHAP/Permutaion Importance 등 사용가능
    -> 교차검증 (KFold, StratifiedKFold 등 써서 모델 안정성 평가)
    -> Hold-out 방식 비교
    -> 과적합 방지와 일반화 성능 확인에 중요
9. 모델 선택 (KNN, Decision Tree, Random Forest, GradientBoosting, HistGradientBoosting, Naive Bayes, Support Vecotr Machine ..)
10. 최종 모델 선정
    ->모델 튜닝(Hyperparameter Optimization)
        ->GridSearchCV, RandomizedSearchCV, Optuna 등으로 Hyperparameter 튜닝

    ->후에 쓴 모델 저장 ( joblib, pickle)
        -- 전처리 방식,파생변수생성코드,피처리스트,하이퍼파라미터설정,시드값통제
    ->여러 시드로 평균 성능 평가
11. 점수 확인 및 대회 평가 지표 ( kaggle : Metric)


------------------------------------
etc
🔸선형 모델	Logistic Regression	빠르고 해석 쉬움. baseline으로 좋음
🔸선형 모델	Ridge / Lasso / ElasticNet	정규화 포함 → 과적합 방지에 유리
🔹트리 기반	Decision Tree	매우 직관적. 단독 성능은 약함
🔹트리 기반	ExtraTreesClassifier	RandomForest의 변형. 더 무작위성
🔹트리 기반	CatBoost	범주형 변수 자동 인식. 튜닝 쉬움
🔹트리 기반	HistGradientBoosting	scikit-learn 내장 GBDT. 빠르고 정확
🔹부스팅	AdaBoost	과거에 인기 많았던 Boosting 방식
🔸거리 기반	K-Nearest Neighbors (KNN)	간단하고 직관적. 스케일링 민감
🔸확률 기반	Naive Bayes	단순하지만 텍스트/이산값에 강함
🔸SVM	Support Vector Machine (SVM)	작은 데이터에 강력함. 느릴 수 있음
🔸신경망	MLPClassifier (Multi-layer Perceptron)	기본적인 딥러닝 구조. 작은 데이터에도 적용 가능
🔸앙상블	VotingClassifier / StackingClassifier	여러 모델 결합하여 성능 향상

TabNet: 딥러닝 기반 모델인데 정형데이터에 특화됨 (PyTorch 기반)

AutoML 도구: Auto-sklearn, H2O.ai, TPOT 등 → 다양한 모델 자동 탐색

빠른 베이스라인	Logistic Regression, RandomForest
높은 정확도	LightGBM, XGBoost, CatBoost, Stacking
실험적 성능 향상	TabNet, VotingClassifier, HistGradientBoosting

추천 모델 Top 5

모델	이유
LightGBM	범주형 처리와 결측치에 강하고 학습 빠름
CatBoost	범주형 자동 처리, 튜닝 없이도 강력      !!!!!
XGBoost	성능 우수하나 전처리 필요 (결측치, 범주형)
HistGradientBoosting	sklearn의 GBDT, 빠르고 정확
Stacking Ensemble	위 모델들 조합으로 더 좋은 성능 가능

전처리 전략
결측치 처리

고객수, 직원 수 → LightGBM 예측 또는 평균/중앙값 대체

분야, 기업가치 → "Unknown" 또는 구간 인코딩

범주형 인코딩

국가, 분야, 투자단계, 인수여부, 상장여부 → Label Encoding 또는 CatBoost 사용

기업가치 범주 처리

"1500-2500" → 수치형으로 중앙값(2000) 변환 또는 구간 코드

Feature Engineering

설립연도 → "기업 나이" 파생

고객수 / 직원수, 연매출 / 총투자금 등 비율 파생변수

✅ 결론
이 데이터에는 LightGBM 또는 CatBoost가 가장 전략적입니다.

결측치 자동 처리

범주형 인코딩 간단

빠른 학습 속도

