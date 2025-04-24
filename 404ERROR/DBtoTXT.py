import pandas as pd
import pymysql

# DB 연결
conn = pymysql.connect(
    host='localhost',
    user='root',
    password='1234',
    db='prj_404error',
    charset='utf8'
)

query = "SELECT * FROM members"
df = pd.read_sql(query, conn)
df.to_csv("404ERROR_members.txt", sep='\t', index=False)
query = "SELECT * FROM bus"
df = pd.read_sql(query, conn)
df.to_csv("404ERROR_bus.txt", sep='\t', index=False)
query = "SELECT * FROM train"
df = pd.read_sql(query, conn)
df.to_csv("404ERROR_train.txt", sep='\t', index=False)
query = "SELECT * FROM weather"
df = pd.read_sql(query, conn)
df.to_csv("404ERROR_weather.txt", sep='\t', index=False)
query = "SELECT * FROM population"
df = pd.read_sql(query, conn)
df.to_csv("404ERROR_population.txt", sep='\t', index=False)

print("fin")