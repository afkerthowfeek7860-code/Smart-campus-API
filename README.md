 Smart Campus Sensor & Room Management API
Overview
This project implements a RESTful API for a Smart Campus system at the University of Westminster.

 Key Features
•	Manage campus Rooms
•	Register and monitor Sensors
•	Store Sensor Readings (history)
•	Filter sensors by type
•	Built using JAX-RS (Jersey)

 Design Principles
•	RESTful architecture
•	Resource-based hierarchy
•	Sub-resource nesting
•	Centralized error handling
•	Thread-safe in-memory storage

How to Run
 Prerequisites
•	Java JDK 11+
•	Maven 3.6+
•	Apache Tomcat 9

 Steps
1. Clone repository
git clone https://github.com/your-username/SmartCampusAPI.git

2. Build project
mvn clean install

3. Deploy to Tomcat
Copy:
target/SmartCampusAPI.war

Into:
Tomcat/webapps

4. Start Tomcat
catalina.bat run

5. Access API
http://localhost:8080/SmartCampusAPI/api/v1

 API Endpoints

Discovery
•	GET /
  → Returns API metadata and resource links
 Room Management
•	GET /rooms → Get all rooms
•	POST /rooms → Create a room
•	GET /rooms/{id} → Get room details
•	DELETE /rooms/{id} → Delete room (blocked if sensors exist)

 Sensor Operations
•	GET /sensors → Get all sensors
•	GET /sensors?type={type} → Filter sensors
•	POST /sensors → Register sensor

 Sensor Readings
•	GET /sensors/{id}/readings → Get readings
•	POST /sensors/{id}/readings → Add reading

 Automatically updates sensor currentValue

Sample CURL Commands

1. Create Room
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50}'

2. Register Sensor
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","roomId":"LIB-301"}'

3. Add Sensor Reading
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
-H "Content-Type: application/json" \
-d '{"value":22.5}'

4. Filter Sensors
curl http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature

5. Delete Room (Error Example)
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301

→ Returns 409 Conflict if sensors exist

 Report Answers

Part 1: Service Architecture
•	JAX-RS resources are request-scoped (new instance per request)
•	Shared data handled using ConcurrentHashMap
•	HATEOAS improves API navigation and reduces dependency on static documentation

 Part 2: Room Management
•	IDs → smaller payload but more client requests
•	Full objects → larger payload but easier client use
•	DELETE is idempotent (same result even if repeated)

 Part 3: Sensor Operations
•	Wrong data format → 415 Unsupported Media Type
•	QueryParam is better for filtering than PathParam


 Part 4: Sub-Resources
•	Sub-resource locator separates logic into smaller classes
•	Improves maintainability and structure

 Part 5: Error Handling & Logging
•	422 → invalid data in request
•	404 → invalid URL
•	Stack traces should not be exposed (security risk)
•	Logging filters provide centralized request/response logging

 Notes
•	Uses in-memory storage (HashMap / ArrayList)
•	No database used (as required for coursework)

