import sqlite3

# Connect to SQLite (or create database if it doesn't exist)
conn = sqlite3.connect("busdata.db")
cursor = conn.cursor()

# Create table if not exists
cursor.execute("""
CREATE TABLE IF NOT EXISTS buses (
    bus_id TEXT,
    vehicle_number TEXT,
    route TEXT,
    stops TEXT
);
""")

# Insert all data
cursor.executemany("""
INSERT INTO buses (bus_id, vehicle_number, route, stops) VALUES (?, ?, ?, ?)
""", [
    ("G7", "KA-01-AA-1010", "Majestic → Electronic City", "Majestic,Corporation,Richmond Circle,Madiwala,Silk Board,HSR Layout,Bommanahalli,Singasandra,Infosys EC,Electronic City"),
    ("80A", "KA-01-AB-2020", "Shivajinagar → Bannerghatta National Park","Shivajinagar,M.G. Road,Brigade Road,Dairy Circle,J.P. Nagar,Jayanagar 4th Block,Meenakshi Mall,Gottigere,Anekal Cross,Bannerghatta National Park"),
    ("215M", "KA-01-AC-3030", "Yeshwanthpur → Kengeri", "Yeshwanthpur,Malleshwaram,Rajajinagar,Vijayanagar,Bapuji Nagar,Mysore Road,Nayandahalli,R.R. Nagar Arch,Kengeri TTMC,Kengeri Satellite Town"),
    ("335E", "KA-01-AD-4040", "Majestic → Whitefield", "Majestic,Anand Rao Circle,Ulsoor,Indiranagar,Domlur,Marathahalli,Kundalahalli,ITPL,Kadugodi,Whitefield"),
    ("500D", "KA-01-AE-5050", "Hebbal → Silk Board", "Hebbal,Mekhri Circle,Palace Guttahalli,Anand Rao Circle,Corporation,Richmond Circle,Forum Mall,Madiwala,BTM Layout,Silk Board"),
    ("307C", "KA-01-AF-6060", "K.R. Market → Hoskote", "K.R. Market,Shivajinagar,Ulsoor,Old Madras Road,KR Puram,Mahadevapura,Whitefield,Kadugodi,Narsapura,Hoskote"),
    ("401K", "KA-01-AG-7070", "Banashankari → Yelahanka", "Banashankari,South End Circle,Jayanagar,Lalbagh,Shivajinagar,Hebbal,Sahakar Nagar,Yelahanka Old Town,Kogilu Cross,Yelahanka New Town"),
    ("356W", "KA-01-AH-8080", "Majestic → HSR Layout", "Majestic,K.R. Market,Wilson Garden,Shantinagar,Forum Mall,Koramangala Water Tank,Agara,HSR Layout BDA Complex,HSR 27th Main,HSR Layout"),
    ("295F", "KA-01-AJ-9090", "Kengeri → Devanahalli", "Kengeri,R.R. Nagar,Nayandahalli,Mysore Road,Majestic,Mekhri Circle,Hebbal,Yelahanka,Airport Road,Devanahalli"),
    ("60A", "KA-01-AK-1011", "Nagarabhavi → Majestic", "Nagarabhavi,BDA Complex,Chandra Layout,Govindarajanagar,Vijayanagar,Mysore Road,Attiguppe,K.R. Market,City Railway Station,Majestic"),
    ("411G", "KA-01-AL-1112", "Indiranagar → Electronic City", "Indiranagar,Domlur,EGL Tech Park,Koramangala Sony World,St. John's Hospital,Silk Board,BTM Layout,Bommanahalli,Infosys EC,Electronic City"),
    ("365R", "KA-01-AM-1213", "Majestic → Jigani", "Majestic,K.R. Market,Wilson Garden,Dairy Circle,Bannerghatta Road,Gottigere,Jigani Industrial Area,APC Circle,Jigani Bus Stand,Jigani"),
    ("342F", "KA-01-AN-1314", "Majestic → Sarjapur", "Majestic,Anand Rao Circle,Domlur,Marathahalli,Bellandur,Carmelaram,Chikkakannalli,Sompura Gate,Sarjapur Police Station,Sarjapur"),
    ("253M", "KA-01-AP-1415", "Majestic → Nelamangala", "Majestic,Seshadripuram,Yeshwanthpur,Goraguntepalya,Dasarahalli,Peenya 2nd Stage,Jalahalli Cross,Makali,Tumkur Road,Nelamangala"),
    ("215H", "KA-01-AQ-1516", "J.P. Nagar → Manyata Tech Park", "J.P. Nagar,Jayanagar 4th Block,South End Circle,Lalbagh,Shivajinagar,Hebbal,Nagavara,Manyata Tech Park Gate 1,Manyata Tech Park Gate 2,Manyata Tech Park"),
    ("500N", "KA-01-AR-1617", "Electronic City → Whitefield", "Electronic City,Infosys EC,Bommanahalli,Silk Board,Marathahalli,Kadubeesanahalli,Prestige Tech Park,ITPL,Kadugodi,Whitefield"),
    ("82D", "KA-01-AS-1718", "Shivajinagar → Rajajinagar", "Shivajinagar,Vidhana Soudha,Majestic,Anand Rao Circle,Rajajinagar 1st Block,Rajajinagar 2nd Block,Rajajinagar 3rd Block,Rajajinagar 4th Block,Navarang,Rajajinagar"),
    ("75E", "KA-01-AT-1819", "Banashankari → Peenya", "Banashankari,Jayanagar,Majestic,Anand Rao Circle,Yeshwanthpur,Goraguntepalya,Peenya 1st Stage,Peenya 2nd Stage,Jalahalli Cross,Peenya"),
    ("328C", "KA-01-AU-1920", "Marathahalli → Kengeri", "Marathahalli,Kadubeesanahalli,Bellandur,HSR Layout,Silk Board,BTM Layout,Bannerghatta Road,J.P. Nagar,Banashankari,Kengeri"),
    ("216F", "KA-01-AV-2021", "Majestic → Kanakapura", "Majestic,City Market,Lalbagh,South End Circle,Banashankari,Jayanagar 9th Block,Uttarahalli,Thalagattapura,Harohalli,Kanakapura")
])

# Commit changes and close connection
conn.commit()
conn.close()

print("Data inserted successfully!")
