# OSRM-REST-Service
This project implements a RESTful routing service built on top of the Open Source Routing Machine (OSRM). The system exposes routing functionality through a custom API, allowing client applications to request route information between geographic coordinates.

It was developed as part of coursework focused on service-centric and cloud-based systems.

The objective was to design and implement a REST service that interacts with a routing engine, exposing endpoints that can be consumed by client applications.

# Features
- Integration with Azure Cosmos DB (PaaS)
The service connects to Azure Cosmos DB, which stores multiple JSON datasets used by the routing system. Cloud-based storage is fully integrated with the REST API, allowing endpoints to retrieve and process structured routing data directly from the database.

- OSRM Routing Engine Integration
The service communicates with the Open Source Routing Machine (OSRM) to perform route calculations. Requests received by the API are processed and forwarded to the OSRM engine, with routing results returned to clients in structured JSON responses.

- RESTful API Design
All service functions are implemented using appropriate HTTP methods and follow RESTful -conventions, including:
-Correct endpoint routing
-Structured request and response headers
-Proper use of HTTP status codes
-JSON-based request and response bodies

- JSON-Based Service Communication
The orchestration layer communicates strictly using JSON, ensuring consistent and platform-independent data exchange between the API, database, and routing engine.

- Cloud-Based Data Storage Integration
The system leverages Azure Cosmos DB as a Platform-as-a-Service (PaaS) data store, enabling scalable and managed cloud storage that is directly accessible through the API layer.

- Quality of Service (QoS) Testing
Comprehensive QoS testing was conducted to evaluate the performance and reliability of the service. This included structured testing procedures, analysis of system behaviour under load, and evaluation of response times and throughput.

- Performance Analysis and Solution Implementation
Test results were analysed to identify performance bottlenecks and justify improvements. Solutions were implemented based on the findings and documented with supporting evidence.

- Pagination for Efficient Data Handling
Following QoS analysis, pagination was implemented within the API endpoints to improve performance when handling larger datasets and to reduce response payload size.

