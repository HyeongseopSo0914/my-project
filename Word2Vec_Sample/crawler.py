"""
LCK 채널 T1경기 영상 댓글 분석
    -감성 분석 ( 재밌다, 재미없다, 잘한다, 못한다, ...)
    -주제 분석 ( 선수, 챔피언, 전략, ...)
    -Word2Vec 임베딩 및 유사어 관계 시각화
"""

"""
    조건 : "T1" 포함, LCK 채널 영상 최신순 100개
    API로 영상 ID 수집 -> 댓글 수집

    댓글을 형태소 분석해서 불용어 제거
    선수태그
    챔피언 태그
    감성 키워드 분리

    주체-의견 relation의 structure 정리

    전체 댓글 토큰화해서 학습
    주체 - 가장 가까운 단어 및 패턴분석

    유사 단어 추출
    감정 점수 분류
    챔피언/ 선수별 인기도 분석

    분석결과 그래프, 워드클라우드
    선수 이름 검색 -> 관련 단어 시각화
    감정 점수 변화 그래프도 가능

    진행도
    1. Youtube 댓글 수집
    2. 댓글 -> df -> tokenize(Okt, Mecab, ...)
    3. 선수/ 챔피언/ 감성 단어 사전 구축
    4. Word2Vec 훈련(gensim)
    5. Flask에서 결과 분석 페이지 출력

    etc)
    Word2Vec은 유사도 파악에 강하지만 감성 분석은 룰 기반/ML 모델로 병행
"""

"""
1. Youtube 댓글 크롤링
"""
from config                     import YOUTUBE_API_KEY
import pandas                   as pd
import time

from googleapiclient.discovery  import build
from googleapiclient.errors     import HttpError

#API KEY 
youtube = build("youtube","v3",developerKey=YOUTUBE_API_KEY)