BOG (Bog Standard) Backend
This is a bog-standard backend implementation. I caught myself creating the same project over and over again through the years, so this is a reusable foundation.

Features
JWT-based authentication and authorization

Role-based access control

Product CRUD operations

Request correlation ID filtering

Global exception handling

Audit logging for entity changes

OpenAPI (Swagger) documentation

Technologies
Java 17+

Spring Boot

Spring Security

Spring Data JPA

H2 or PostgreSQL (configurable)

Gradle

OpenAPI / Swagger

Project Structure
src/main/java/dev/digitalfoundries/bog_standard_backend/
├── controller # REST controllers
├── dto # Data transfer objects
├── entity # JPA entities
├── repository # Spring Data repositories
├── security # JWT filters, config, entities
├── audit # Auditing support
├── config # OpenAPI and other configs
├── handler # Exception handling
├── filter # Request filters

Build and Run
./gradlew build
./gradlew bootRun

API Documentation
Once running, visit:
http://localhost:8080/swagger-ui.html
or
http://localhost:8080/v3/api-docs

Development
Ensure Java 17+ and Gradle are installed.

To build and run locally:
./gradlew clean bootRun

To run tests:
./gradlew test

License
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or distribute this software, either in source code form or as a compiled binary, for any purpose, commercial or non-commercial, and by any means.

In jurisdictions that recognize copyright laws, the author or authors of this software dedicate any and all copyright interest in the software to the public domain. We make this dedication for the benefit of the public at large and to the detriment of our heirs and successors. We intend this dedication to be an overt act of relinquishment in perpetuity of all present and future rights to this software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
