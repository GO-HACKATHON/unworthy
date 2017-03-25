from flask import Flask, jsonify

app = Flask(__name__)

@app.route('/')
def index():
    return "We are Unworthy";

if __name__ == '__main__':
    app.run()
