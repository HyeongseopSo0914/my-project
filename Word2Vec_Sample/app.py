from flask import Flask, render_template, request
from gensim.models import Word2Vec
import numpy as np
import re

app = Flask(__name__)
model = Word2Vec.load("models/word2vec.model")

# 단어 벡터 결합 함수
def combine_word_vectors(words):
    word_vectors = []
    print(f"단어들: {words}")  # 디버깅: 전달된 단어 확인
    for word in words:
        if word in model.wv:
            word_vectors.append(model.wv[word])
        else:
            print(f"단어 '{word}'는 모델에 없습니다.")  # 디버깅용 출력
    if word_vectors:
        return np.sum(word_vectors, axis=0)  # 벡터들의 합
    else:
        print("유효한 단어 벡터가 없습니다.")  # 디버깅: 유효한 벡터가 없을 경우 출력
        return None

@app.route("/", methods=["GET", "POST"])
def index():
    result = []
    error = ""
    input_text = ""
    
    if request.method == "POST":
        input_text = request.form["query"].strip()

        try:
            # 특수문자 제거: 공백 외의 문자 제거
            cleaned_text = re.sub(r"[^\w\s\+\-]", "", input_text)  # +와 -만 허용

            # 공백 기준으로 split → 단어 연산 처리
            tokens = cleaned_text.split()
            positive = []
            negative = []

            i = 0
            cnt = len(tokens)
            while i < cnt: 
                token = tokens[i]
                print(f"처리된 토큰: {token}")  # 디버깅: 토큰 확인
                if "+" in token:  # '+' 기호가 있을 때
                    if i + 1 < cnt:
                        positive.append(tokens[i + 1])  # 다음 단어를 positive에 추가
                    i += 2  # '+'와 그 다음 단어를 처리했으므로 두 칸을 넘김
                elif "-" in token:  # '-' 기호가 있을 때
                    if i + 1 < cnt:
                        negative.append(tokens[i + 1])  # 다음 단어를 negative에 추가
                    i += 2  # '-'와 그 다음 단어를 처리했으므로 두 칸을 넘김
                else:  # 그 외의 단어는 기본적으로 positive에 추가
                    positive.append(token)
                    i += 1  # 한 단어를 처리했으므로 한 칸을 넘김

            print(f"positive: {positive}, negative: {negative}")  # 디버깅: 단어 리스트 확인

            # 모델에 없는 단어 확인
            missing_words = [word for word in positive + negative if word not in model.wv]
            if missing_words:
                raise ValueError(f"❌ 다음 단어는 모델에 없습니다: {', '.join(missing_words)}")

            # Word2Vec 연산: positive 단어들 더하고 negative 단어들 빼기
            # 단어 벡터 결합 후 연산
            positive_vector = combine_word_vectors(positive)
            negative_vector = combine_word_vectors(negative)

            if positive_vector is not None and negative_vector is not None:
                # 두 벡터 차이를 계산하고, 유사한 단어를 찾습니다.
                print("벡터 계산 완료!")  # 디버깅: 벡터 계산 완료 여부 확인
                result = model.wv.most_similar(positive=[positive_vector], negative=[negative_vector], topn=10)
            else:
                raise ValueError("❌ 모델에 유효한 단어가 없습니다.")

        except Exception as e:
            error = str(e)
            print(f"오류 발생: {error}")  # 디버깅: 오류 발생시 출력

    return render_template("index.html", input_text=input_text, result=result, error=error)

if __name__ == "__main__":
    app.run(debug=True)
