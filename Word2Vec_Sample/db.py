import mysql.connector
from config import MYSQL_HOST, MYSQL_PORT, MYSQL_USER, MYSQL_PASSWORD, MYSQL_DB

def get_connection():
    return mysql.connector.connect(
        host=MYSQL_HOST,
        port=MYSQL_PORT,
        user=MYSQL_USER,
        password=MYSQL_PASSWORD,
        database=MYSQL_DB
    )

# 테스트 코드
if __name__ == "__main__":
    try:
        conn = get_connection()
        print("✅ MySQL 연결 성공!")
        conn.close()
    except Exception as e:
        print("❌ MySQL 연결 실패:", e)