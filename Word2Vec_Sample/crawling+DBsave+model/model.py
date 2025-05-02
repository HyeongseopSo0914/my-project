from db import get_comments_from_db
from konlpy.tag import Okt
from gensim.models import Word2Vec
import re

def tokenize_comments(comments):
    okt = Okt()
    stopwords = ['하다', '되다', '있다', '없다']  # 필요에 따라 추가
    tokenized = []

    for comment in comments:
        # 한글만 추출
        comment = re.sub(r"[^ㄱ-ㅎㅏ-ㅣ가-힣 ]", "", str(comment))
        words = okt.morphs(comment, stem=True)
        words = [w for w in words if w not in stopwords and len(w) > 1]
        tokenized.append(words)

    return tokenized

def train_and_get_similar(keyword):
    df = get_comments_from_db()
    comments = df['comment'].dropna().tolist()
    tokenized = tokenize_comments(comments)

    model = Word2Vec(sentences=tokenized, vector_size=100, window=5, min_count=2, workers=4)
    
    try:
        similar = model.wv.most_similar(keyword, topn=10)
        return [w for w, _ in similar]
    except KeyError:
        return ["❗ 학습된 단어가 아닙니다."]
