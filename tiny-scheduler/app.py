from flask import Flask, jsonify
import requests

app = Flask(__name__)
SPRING_BOOT_URL = "https://resonance-backend.onrender.com/health"  

@app.route('/')
def home():
    return "Flask server is running on Vercel. Use /ping to manually ping Spring Boot."

@app.route('/ping')
def ping_spring_boot():
    try:
        response = requests.get(SPRING_BOOT_URL)
        return jsonify({"message": "Pinged Spring Boot", "status": response.status_code})
    except requests.RequestException as e:
        return jsonify({"error": str(e)}), 500
