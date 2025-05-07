import mysql.connector
from config import MYSQL_HOST, MYSQL_PORT, MYSQL_USER, MYSQL_PASSWORD, MYSQL_DB

import pandas as pd
from mecab import MeCab
from gensim.models import Word2Vec
import re
import os
import jpype


# 1. DB 연결
def get_connection():
    return mysql.connector.connect(
        host=MYSQL_HOST,
        port=MYSQL_PORT,
        user=MYSQL_USER,
        password=MYSQL_PASSWORD,
        database=MYSQL_DB
    )

# 2. 댓글 데이터 불러오기
def load_comments():
    conn = get_connection()
    query = "SELECT comment FROM comments WHERE comment IS NOT NULL"
    df = pd.read_sql(query, conn)
    conn.close()
    return df

# 3. 전처리 함수
def clean_and_tokenize(comments):
    mecab = MeCab()
    tokenized_comments = []

    for comment in comments:
        # 한글, 숫자, 공백만 남기고 정제
        comment = re.sub(r"[^ㄱ-ㅎㅏ-ㅣ가-힣0-9\s]", "", str(comment))
        # 형태소 분석 + 명사/형용사/동사 추출
        tokens = mecab.pos(comment)
        words = [word for word, pos in tokens if pos in ["NNG", "VV", "VA"] and len(word) > 1]
        if words:
            tokenized_comments.append(words)

    return tokenized_comments

# 4. Word2Vec 모델 학습 및 저장
def train_word2vec(sentences):
    model = Word2Vec(
        sentences,
       vector_size=200,   # 벡터 차원 늘림
        window=10,         # 문맥 범위 늘림
        min_count=5,       # 최소 등장 빈도 늘림
        workers=4,         # 멀티코어 처리
        sg=1,              # Skip-gram 방식
        epochs=30          # 에폭수 늘림
    )
    os.makedirs("models", exist_ok=True)
    model.save("models/word2vec.model")
    print("✅ Word2Vec 모델 저장 완료")

# 5. 전체 실행 흐름
if __name__ == "__main__":
    df = load_comments()
    sentences = clean_and_tokenize(df["comment"].tolist())
    print(f"📄 총 {len(sentences)}개의 문장 학습 준비 완료")
    train_word2vec(sentences)


