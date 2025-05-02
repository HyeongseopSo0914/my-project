from flask import Flask, render_template, request, jsonify
from config import YOUTUBE_API_KEY

from crawler import collect_t1_comments

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/analyze', methods=['POST'])
def analyze():
    keyword = request.json.get('keyword')
    LCK_CHANNEL_ID = "UCqR6eTk9N3ldATFpP_PwN3Q"
    df = collect_t1_comments(channel_id=LCK_CHANNEL_ID)
    df.to_csv("t1_comments.csv", index=False)
    print(df.head())
    similar_words = ['w1','w2','w3']

    return jsonify({'similar':similar_words})

if __name__ == '__main__':
    app.run(debug=True)