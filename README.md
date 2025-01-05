# System Design for the Blog Application

## 1. High-Level Architecture

   The Blog Application will follow a layered architecture with the following key layers:

### Layers Overview:

**1. Presentation Layer**: Handles incoming HTTP requests and sends responses to the client.
- Framework: Spring MVC
- Components: Controllers

**2. Service Layer**: Contains business logic.
- Framework: Spring Core
- Components: Services

**3. Data Access Layer**: Interacts with the database.
- Framework: Spring Data JPA with Hibernate
- Components: Repositories

**4. Security Layer**: Handles authentication and authorization.
- Framework: Spring Security with JWT

**5. Infrastructure Layer**: Includes caching, logging, and monitoring.
- Tools: Redis, SLF4J/Logback, Prometheus/Grafana

## 2. High-Level Features

   The application will include the following features:

**1. Authentication & Authorization**:
- JWT-based authentication.
- Role-based access control (ADMIN, WRITER, READER).

**2. CRUD Operations**:
- ADMIN: Full access to posts.
- WRITER: Can create, edit, and view their own posts.
- READER: Can view published posts.

**3. RESTful APIs**:
- Follow REST principles for all endpoints.

**4. Data Validation**:
- Ensure valid input using Hibernate Validator (@Valid, @NotNull, etc.).

**5. Caching**:
- Use Redis to cache frequently accessed data.

**6. Logging**:
- Implement logging with SLF4J and Logback.
- Use AOP for centralized logging of method calls.

**7. Exception Handling**:
- Global exception handling using @ControllerAdvice and @ExceptionHandler.

**8. Monitoring**:
- Collect metrics with Prometheus and visualize them using Grafana.

**9. Testing**:
- Write unit tests with JUnit 5 and Mockito.
- Write integration tests for APIs.

## 3. Database Design

**Tables:**
1. **Users Table**:
- id (Primary Key)
- email (Unique)
- password (Hashed)
- role (ADMIN, WRITER, READER)
   
2. **Posts Table**:
- id (Primary Key)
- title
- content
- author_id (Foreign Key to Users)
- published (Boolean)
- created_at
- updated_at

**ER Diagram :**

+-----------------+      +-----------------+
|     Users       |      |      Posts      |
+-----------------+      +-----------------+
| id (PK)         |      | id (PK)         |
| email           |<---->| author_id (FK)  |
| password        |      | title           |
| role            |      | content         |
+-----------------+      | published       |
                         | created_at      |
                         | updated_at      |
                         +-----------------+


## 4. API Design

### **Authentication Endpoints:**

1. **Signup (POST /api/auth/signup)**
- Open to all.
- Registers a new user.

**2. Login (POST /api/auth/login)**
- Open to all.
- Generates a JWT for authenticated users.

### Post Endpoints:

1. **Create Post (POST /api/posts)**
- Access: WRITER, ADMIN
- Create a new post.

2. **Update Post (PUT /api/posts/{id})**
- Access: WRITER (only their posts), ADMIN
- Update an existing post.

3. **Delete Post (DELETE /api/posts/{id})**
- Access: ADMIN
- Delete a post.

4. **Get All Posts (GET /api/posts)**
- Access: ADMIN, READER
- Fetch all posts (including unpublished for ADMIN).

5. Get Published Posts (GET /api/posts/published)
- Access: READER
- Fetch only published posts.

## 5. Security Design

### Roles and Permissions:
Role    = Permissions
- ADMIN = Full access to all resources.
- WRITER = Create, update, and view their own posts.
- READER = View published posts.

**Securing Endpoints:**
1. **Public Endpoints:**
- /api/auth/signup
- /api/auth/login

2. **Secured Endpoints:**
- All other endpoints require a valid JWT and appropriate role.

**JWT Payload:**
Example structure:
- {
- "sub": "user@example.com",
- "roles": ["ROLE_WRITER"],
- "iat": 1672845823,
- "exp": 1672932223
- }

**Authorization Logic:**
- Extract roles from the JWT.
- Use @PreAuthorize to restrict access based on roles.

## 6. Caching Strategy

- Use Redis for caching frequently accessed resources like published posts.
- Cache expiry can be set based on the expected update frequency.

## 7. Logging Design

- Use SLF4J and Logback for logging.
- Log critical actions such as authentication, authorization, and database updates.
- Use AOP to log method calls and execution time.

## 8. Monitoring and Metrics

- Use Prometheus to collect application metrics (e.g., request count, response time).
- Use Grafana for visualizing metrics.
- Monitor key metrics such as:
- Authentication success/failure rate.
- API response time.
- Cache hit/miss ratio.