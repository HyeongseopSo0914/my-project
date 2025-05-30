******SHS_routes.py
코로나19전후 교통 및 유동인구 변화 분석(2020~2024) + 날씨의 온도와 강우량을 추가배경지식

시기        :   전개기/절정기/종식기            ->독립변수 (범주형)
교통이용량  :   지하철, 버스 이용 인원          ->종속변수
생활 인구   :   시간대/연령대별 유동 인구       ->종속변수
기온        :   일평균 기온                     ->공변량
강우량      :   일강우량 또는 비가 오는날비율   ->공변량

1. ANOVA (일변량 분산분석)
시기별 대중교통 또는 인구의 평균 차이 분석

단점: 날씨 영향 미반영

2. ANCOVA (공분산분석) 
시기별 차이를 보되, 기온/강우량의 영향 보정

진짜로 코로나 때문에 차이가 생겼는지를 강조 가능

4. 회귀분석 (경향 파악)
기온 상승 → 교통량 증가?

종식기일수록 버스 증가? 등 경향선을 확인할 때

5. MANOVA (다변량 분산분석)
여러 종속변수(예: [버스, 지하철, 인구])를 동시에 분석

시기별 전반적 변화가 유의미한지 판단

회귀선 플롯: 날씨와 교통량의 상관관계 시각화

시기별 Boxplot: 교통량/인구 변화 시기별 비교

강우량 오버레이: 교통량 시계열에 비오던 날 강조
