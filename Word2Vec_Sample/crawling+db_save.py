from crawler import collect_all
from db import get_connection
import pandas as pd

# 설정
CHANNEL_ID = "UCw1DsweY9b2AKGjV4kGJP1A"
QUERY = "T1"
CSV_PATH = "t1_comments.csv"

# 1. 댓글 수집 및 CSV 저장
def crawl_and_save_csv():
    print("📥 유튜브 댓글 수집 중...")
    df = collect_all(CHANNEL_ID, query=QUERY)
    if df.empty:
        print("❌ 수집된 데이터가 없습니다.")
        return None
    df.to_csv(CSV_PATH, index=False, encoding="utf-8-sig")
    print(f"✅ CSV 저장 완료 → {CSV_PATH}")
    return df

# 2. DB에 저장
def save_df_to_db(df):
    print("🗃️  DB 저장 중...")
    conn = get_connection()
    cursor = conn.cursor()

    insert_query = """
    INSERT INTO comments (video_id, comment)
    VALUES (%s, %s)
    """

    for _, row in df.iterrows():
        cursor.execute(insert_query, (row['video_id'], row['comment']))

    conn.commit()
    cursor.close()
    conn.close()
    print("✅ DB 저장 완료")

# 실행 스크립트
if __name__ == "__main__":
    df = crawl_and_save_csv()
    if df is not None:
        save_df_to_db(df)
