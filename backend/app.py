from flask import Flask, request, jsonify
from dotenv import load_dotenv
from pymongo import MongoClient
import os

load_dotenv()

app = Flask(__name__)

mongodb_uri = os.getenv("MONGO_URI", "mongodb://localhost:27017/resonance")
spotify_token = os.getenv("SPOTIFY_TOKEN")

db_client = MongoClient(mongodb_uri)

@app.route("/", methods=["GET"])
def home():
    if (db_client is None):
        return jsonify({"message": "resonance not achieved :("})
    
    db_client.admin.command("ping")
    return jsonify({"message": "resonance reached !"})

if __name__ == "__main__":
    app.run(debug=False)
