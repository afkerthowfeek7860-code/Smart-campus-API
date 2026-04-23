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

 Part-01

Part 1.1 — JAX-RS resource lifecycle

By default, a JAX-RS resource class is usually request-scoped, meaning a new instance is created for each incoming request. This is safer because instance fields inside the resource class are not shared between concurrent requests. However, because this coursework requires in-memory storage using structures like HashMap or ArrayList, the shared data should not be stored in ordinary per-request resource fields. Instead, it should be stored in a shared service/store class, ideally as static collections or in an application-level repository. Since multiple requests may access the same data at the same time, the collections should be managed carefully to avoid race conditions, lost updates, or inconsistent state. Using thread-safe collections such as ConcurrentHashMap, synchronized blocks, or controlled update logic helps prevent corruption of shared data. The brief explicitly asks you to explain how lifecycle affects management of in-memory maps/lists and race conditions

Part 1.2 — Why hypermedia/HATEOAS is useful

Hypermedia is important in REST because the API response can include links that tell the client what it can do next, instead of forcing the client to rely only on fixed external documentation. This makes the API more self-descriptive and easier to navigate. For client developers, this reduces hard-coded URLs and makes the system more flexible if endpoints evolve later. A client can discover related resources such as /rooms, /sensors, or nested reading endpoints directly from responses, which improves usability and reduces maintenance effort. The brief specifically requires a discovery endpoint returning API metadata and resource links.

Part-02

Part 2.1 — Returning IDs only vs full room objects

Returning only room IDs reduces response size, uses less bandwidth, and is useful when the client only needs identifiers for later requests. However, the client then has to make additional calls to fetch full room details, which increases client-side work and the number of HTTP requests. Returning full room objects is more convenient because the client immediately gets the room name, capacity, and sensor list, but the payload is larger. In practice, full objects are better when the number of rooms is small or when the client needs display-ready data, while IDs-only responses are better for lightweight navigation or very large datasets. The question in the brief asks you to compare bandwidth and client-side processing tradeoffs.

Part 2.2 — Is DELETE idempotent?

Yes, DELETE is idempotent if repeated identical requests leave the system in the same final state. In this coursework, if a room exists and has no sensors, the first DELETE removes it. If the same DELETE request is sent again, the room is already gone, so the system state does not change further. The second request may return 404 Not Found, but the final result is still that the room does not exist. If the room contains sensors, every repeated DELETE should continue to fail with the same custom conflict response until those sensors are removed. Therefore, the operation is idempotent because repeating it does not produce additional state changes beyond the first successful delete or repeated rejection. The deletion rule and the “room cannot be deleted if it still has sensors” constraint are stated in the brief.

Part-03

Part 3.1 — What happens if the client sends non-JSON data?

If the POST method is annotated with @Consumes(MediaType.APPLICATION_JSON), JAX-RS expects the request body to be JSON. If a client sends text/plain or application/xml, the request content type does not match what the endpoint consumes. In that case, JAX-RS typically rejects the request with HTTP 415 Unsupported Media Type because there is no suitable message body reader for that content type for that method. So the method is not processed normally, and the server protects the API from invalid formats. The brief explicitly asks about the effect of using @Consumes(MediaType.APPLICATION_JSON) on the sensor POST endpoint.

Part 3.2 — Why use a query parameter for filtering?

Using @QueryParam("type") for a filter such as /api/v1/sensors?type=CO2 is generally better because it keeps /sensors as the main collection resource and treats type as an optional filter on that collection. This is more flexible and more RESTful for searching because clients can combine filters later, for example ?type=CO2&status=ACTIVE. In contrast, putting the filter into the path like /sensors/type/CO2 makes filtering look like a different resource hierarchy rather than a search condition. Query parameters are the standard way to express optional filtering, sorting, and searching on collections. The brief asks you to compare @QueryParam against path-based filtering.

Part-04

Part 4 — Benefits of the sub-resource locator pattern

The Sub-Resource Locator pattern improves structure by delegating nested functionality to a dedicated class. Instead of putting every path and every reading-related method inside one large SensorResource, you return a SensorReadingResource for paths like /sensors/{sensorId}/readings. This keeps the code modular, easier to maintain, and easier to test. It also mirrors the domain more naturally: sensors own readings, so reading logic is handled in its own class. In large APIs, this avoids a massive controller with mixed responsibilities and reduces complexity when adding deeper nesting or more rules later. The brief requires a sub-resource locator and asks for its architectural benefits

Part-05

Part 5.1 — Why 422 is more accurate than 404 here

422 Unprocessable Entity is often more accurate because the request itself reaches a valid endpoint and contains valid JSON syntax, but the content inside the JSON is semantically wrong. In this case, the client sends a sensor object whose roomId refers to a room that does not exist. The problem is not that the API endpoint is missing; the problem is that the submitted entity contains an invalid reference. A 404 Not Found usually means the requested URI itself does not exist. So 422 better communicates that the payload was understood, but could not be processed because of a semantic validation problem. The brief directly frames this as a missing linked resource inside a valid JSON payload

Part 5.2 — Why exposing stack traces is risky

Exposing raw Java stack traces is dangerous because attackers can learn internal implementation details about your system. A stack trace may reveal package names, class names, file names, line numbers, library versions, method names, internal paths, and details about how your API is structured. This information can help an attacker identify weaknesses, guess hidden endpoints, target known vulnerabilities in libraries, or craft more precise attacks. Returning a generic 500 response instead protects internal details while still informing the client that the server failed. The brief specifically requires a catch-all ExceptionMapper<Throwable> and asks about cybersecurity risks of exposing traces.

Part 5.3 — Why use JAX-RS filters for logging?

JAX-RS filters are better for logging because logging is a cross-cutting concern that applies to many endpoints. If you place Logger.info() inside every resource method, the code becomes repetitive, harder to maintain, and easy to forget in some methods. A ContainerRequestFilter and ContainerResponseFilter centralize logging in one place, so every request and response is captured consistently. This keeps resource classes focused on business logic while improving maintainability and separation of concerns. The brief explicitly asks for request/response logging through filters instead of manual logging everywhere 
