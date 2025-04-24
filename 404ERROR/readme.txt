prj_404error 데이터베이스 구조

members 테이블

| 컬럼명        | 타입             | NULL 허용 | 키     | 기본값  |
|---------------|------------------|-----------|--------|--------|
| id            | int              | NO        | PRI    | auto_increment |
| email         | varchar(255)     | NO        | UNI    |        |
| password      | varchar(255)     | NO        |        |        |
| nickname      | varchar(50)      | NO        |        |        |
| grade         | enum('관리자','VIP','일반') | YES |        | 일반   |
| points        | int              | YES       |        | 0      |
| can_message   | tinyint(1)       | YES       |        | 1      |

---

bus/ train 테이블 (동일 구조)

| 컬럼명        | 타입         | NULL 허용 | 키     | 비고         |
|---------------|--------------|-----------|--------|--------------|
| id            | int          | NO        | PRI    |              |
| 일시          | varchar(20)  | YES       |        | 날짜/시간     |
| 자치구        | varchar(20)  | YES       |        | 지역구 정보   |
| 승차총승객수   | float        | YES       |        | (bus only)   |
| 하차총승객수   | float        | YES       |        | (bus only)   |

---

population 테이블

| 컬럼명           | 타입         | NULL 허용 | 키     | 설명                |
|------------------|--------------|-----------|--------|---------------------|
| id               | int          | NO        | PRI    |                     |
| 일시             | varchar(20)  | YES       |        | 날짜                |
| 시간대구분        | varchar(10)  | YES       |        | 예: 오전/오후       |
| 자치구           | varchar(20)  | YES       |        |                     |
| 총생활인구수       | float        | YES       |        |                     |
| 남자미성년자       | float        | YES       |        |                     |
| 남자청년           | float        | YES       |        |                     |
| 남자중년           | float        | YES       |        |                     |
| 남자노년           | float        | YES       |        |                     |
| 여자미성년자       | float        | YES       |        |                     |
| 여자청년           | float        | YES       |        |                     |
| 여자중년           | float        | YES       |        |                     |
| 여자노년           | float        | YES       |        |                     |

---

weather 테이블

| 컬럼명         | 타입         | NULL 허용 | 키     | 설명       |
|----------------|--------------|-----------|--------|------------|
| id             | int          | NO        | PRI    |            |
| 일시           | varchar(20)  | YES       |        | 날짜       |
| 평균기온        | float        | YES       |        |            |
| 최저기온        | float        | YES       |        |            |
| 최고기온        | float        | YES       |        |            |
| 강수_계속시간   | float        | YES       |        |            |
| 일강수량        | float        | YES       |        |            |

---

> ✅ 이 문서는 자동 생성된 테이블 구조이며, 샘플 데이터는 `/data/` 폴더의 CSV 파일을 참조하세요.
