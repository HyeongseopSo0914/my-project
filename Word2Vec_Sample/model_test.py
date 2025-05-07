from gensim.models import Word2Vec

# 모델 로드
model = Word2Vec.load("models/word2vec.model")

# 저장된 단어 리스트 확인
vocab = model.wv.index_to_key

# 상위 30개 단어만 보기
print(vocab[:30])