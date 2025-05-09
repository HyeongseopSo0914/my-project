💤 Q 계열 (주관적 상태 지표)
변수	의미	값 범위
Q1	Overall sleep quality (수면의 질)	0, 1, 2
Q2	Fatigue level (피로 수준)	0, 1, 2
Q3	Stress level (스트레스 수준)	0, 1, 2

😴 S 계열 (수면 측정 지표)
변수	의미	값 범위
S1	Total sleep time (총 수면 시간)	0, 1, 2
S2	Sleep efficiency (수면 효율)	0, 1, 2
S3	Sleep onset latency (잠드는 데 걸린 시간)	0, 1, 2

학습 및 평가 시 **다중 클래스 분류 모델 (multi-class classification)**로 접근하는 게 적절

원하신다면 Q1~Q3 각각 또는 전부에 대해 모델을 학습하거나, S 계열까지 포함한 멀티타스크 학습도 구현할 수 있어요.

먼저 어떤 항목을 예측하고 싶으신가요? (Q1부터 시작할까요?)