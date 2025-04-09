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

# ✅ Fetch All Bus Stops
@app.route("/get_all_stops", methods=["GET"], strict_slashes=False)
def get_all_stops():
    with connect_db() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT DISTINCT stop_name FROM stops")
        stops = [row[0] for row in cursor.fetchall()]
    
    return jsonify({"stops": stops})
@app.route("/get_buses", methods=["GET"], strict_slashes=False)
def get_buses():
    origin = request.args.get("origin")
    destination = request.args.get("destination")

    if not origin or not destination:
        return jsonify({"error": "Missing origin or destination"}), 400

    with connect_db() as conn:
        cursor = conn.cursor()
        
        query = """
        SELECT b.bus_id, b.vehicle_number, b.route, 
               instr(b.stops, ?) AS start_index, 
               instr(b.stops, ?) AS end_index
        FROM buses b
        WHERE instr(b.stops, ?) > 0 
          AND instr(b.stops, ?) > 0 
          AND instr(b.stops, ?) < instr(b.stops, ?);
        """
        
        cursor.execute(query, (origin, destination, origin, destination, origin, destination))
        buses = cursor.fetchall()


    if not buses:
        return jsonify({"error": "No buses found"}), 404

    result = []
    for bus in buses:
        bus_id, vehicle_number, route, start_index, end_index = bus
        stop_count = route.count(",") if start_index and end_index else 0

        # Calculate fare
        fare = 6 if stop_count <= 2 else 6 + ((stop_count - 2) * 6)

        result.append({
            "bus_id": bus_id,
            "vehicle_number": vehicle_number,
            "route": route,
            "fare": fare  # Include fare in the response
        })

    return jsonify(result)

# ✅ Fetch Stops for a Specific Bus
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
