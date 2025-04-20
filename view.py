import sqlite3

# Connect to SQLite (or create database)
conn = sqlite3.connect("busdata.db")
cursor = conn.cursor()

# Fetch all data
cursor.execute('SELECT bus_id, vehicle_number, route, stops FROM buses WHERE stops LIKE "%Majestic%" AND stops LIKE "%Banashankari%";')
rows = cursor.fetchall()

# Print data
for row in rows:
    print(row)

# Close connection
conn.close()
