from flask import Flask, request, jsonify
from flask_cors import CORS
import sqlite3
import razorpay

app = Flask(__name__)
CORS(app)

def connect_db():
    return sqlite3.connect("busdata.db")

@app.route("/", methods=["GET"])
def home():
    return "Server is Running!"

@app.route("/get_bus", methods=["POST"], strict_slashes=False)
def get_bus():
    data = request.json
    bus_id = data.get("bus_id")

    with connect_db() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM buses WHERE bus_id=?", (bus_id,))
        bus = cursor.fetchone()

    if bus:
        return jsonify({
            "bus_id": bus[1],
            "vehicle_number": bus[2],
            "route": bus[3],
            "stops": bus[4].split(",") if bus[4] else []
        })
    else:
        return jsonify({"error": "Bus not found"}), 404

@app.route("/get_bus_stops", methods=["GET"], strict_slashes=False)
def get_bus_stops():
    bus_id = request.args.get("bus_id")

    with connect_db() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT stops FROM buses WHERE bus_id=?", (bus_id,))
        bus = cursor.fetchone()

    if bus and bus[0]:
        return jsonify({"stops": bus[0].split(",")})
    else:
        return jsonify({"error": "Bus stops not found"}), 404

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
