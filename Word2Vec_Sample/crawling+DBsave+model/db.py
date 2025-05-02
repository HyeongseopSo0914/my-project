import mysql.connector
from config import MYSQL_HOST, MYSQL_PORT, MYSQL_USER, MYSQL_PASSWORD, MYSQL_DB
import pandas as pd

def get_connection():
    return mysql.connector.connect(
        host=MYSQL_HOST,
        port=MYSQL_PORT,
        user=MYSQL_USER,
        password=MYSQL_PASSWORD,
        database=MYSQL_DB
    )

def save_csv_to_db(csv_path):
    df = pd.read_csv(csv_path)

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
    print("✅ CSV 데이터 → DB 저장 완료")

def get_comments_from_db():
    conn = get_connection()
    df = pd.read_sql("SELECT comment FROM comments", conn)
    conn.close()
    return df
