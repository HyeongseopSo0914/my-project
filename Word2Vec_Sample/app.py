from flask import Flask, render_template, request, jsonify
from db import get_comments_from_db
#from train_model import train_and_get_similar

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/analyze', methods=['POST'])
def analyze():
    keyword = request.json.get('keyword')
    #similar_words = train_and_get_similar(keyword)
    #return jsonify({'similar': similar_words})

if __name__ == '__main__':
    app.run(debug=True)
