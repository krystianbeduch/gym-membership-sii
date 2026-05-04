# Gym Membership Management System
The application is a backend system for managing gyms, membership plans, and gym members. It supports the creation of gyms and plans, member registration, membership cancellation, and capacity validation for each plan.

### Contents
1. [Application functionality](#application-functionality)
2. [Technology](#technology)
3. [Package structure](#package-structure)
4. [Database schema](#database-schema)
5. [Build and Run](#build-and-run)
6. [REST API Endpoints](#rest-api-endpoints)
   - [Create a new gym](#create-a-new-gym)
   - [List all gyms](#list-all-gyms)
   - [Create a new membership plan for a given gym](#create-a-new-membership-plan-for-a-given-gym)
   - [List all membership plans for a given gym](#list-all-membership-plans-for-a-given-gym)
   - [Register a new member to a given membership plan](#register-a-new-member-to-a-given-membership-plan)
   - [List all members](#list-all-members)
   - [Cancel a membership](#cancel-a-membership)
   - [Get monthly revenue report](#get-monthly-revenue-report)
  
## Application functionality
- Manage gyms with a unique name, address, and phone number
- Create multiple membership plan for each gym
- Define membership plany by name, type, monthly price with currency, duration in months, and maximum number of members
- Register members to exactly one membership plan, ensuring email uniqueness among active members
- Automatically set the membership start date during registration
- Assign the `ACTIVE` status to every newly registered member
- Prevent new registrations when the number of active members reaches the plan capacity
- Cancel memberships by chaning the member status to `CANCELLED`
- Exclude cancelled members from the plan capacity count
- Generate a monthly revenue report per gym, grouped by currency

## Technology
- **Language**: Java 26
- **Framework**: Spring Boot 4.0.6
    - Spring Data JPA
    - Spring Web MVC
    - Spring Validation
- **Build Tool**: Gradle 9.4.1
- **Database**: H2 Database (in-memory)
- **Mapping**: MapStruct 1.6.3
- **Utilities**: Lombok 1.18.46
- **Testing**: JUnit 6 (via Spring Boot Starter Test)

## Package structure
The project uses a feature-based package structure. Each business domain is separated into its own package and contains the layers required to handle API requests, business rules, persistence, and data mapping.
```bash
├───common
│   ├───component
│   ├───configuration
│   └───exception
├───gym
│   ├───controller
│   ├───dto
│   ├───entity
│   ├───enums
│   ├───exception
│   ├───mapper
│   ├───repository
│   └───service
├───member
│   ├───controller
│   ├───dto
│   ├───entity
│   ├───exception
│   ├───mapper
│   ├───repository
│   └───service
├───membership
│   ├───controller
│   ├───dto
│   ├───entity
│   ├───enums
│   ├───exception
│   ├───mapper
│   ├───repository
│   └───service
└───report
    ├───controller
    ├───dto
    ├───exception
    └───service
```
### Main packages
- `common` - contains shared application components, configuration classes, and global exception handling used across multiple modules
- `gym` - handles gym management, including API endpoints, business logic, persistence, mapping, and gym-related domain objects
- `member` - contains all functionality related to member registration, membership cancellation, member data access, and related request/response models
- `membership` - manages membership plans, including plan creation, validation rules, plan types, persistence layer, and service logic
- `report` - provides reporting functionality, including revenue-related endpoints, DTOs, and business logic

### Package responsibilities

- `controller` - exposes REST API endpoints
- `dto` - contains request and response data transfer objects
- `entity` - defines JPA entity classes mapped to the database
- `enums` - stores enum types used by the domain model
- `exception` - contains module-specific exceptions and error-related classes
- `mapper` - maps entities to DTOs and DTOs to entities
- `repository` - provides database access through Spring Data JPA repositories
- `service` - contains business logic and application use cases

## Database schema
<img src="https://github.com/krystianbeduch/gym-membership-sii/blob/main/db_schema/db_schema.png" alt="Database schema" title="Database schema" height="350">

## Build and Run
### Prerequisites
- Make sure you have **JDK 26** installed
- The project uses the **Gradle Wrapper**, so a separate Gradle installation is not required

### Build the application
1. Open a terminal in the project root directory.
2. Run the following command to compile the project and execute tests:

```bash
./gradlew clean build
```

On Windows, use:

```bash
gradlew.bat clean build
```

### Run the application
The application can be started using Gradle tasks. The project supports two runtime profiles:
- the default profile, which starts the application without sample data
- the `dev` profile, which starts the application with sample data for local development

#### Run without sample data
Use the default profile to start the application:

```bash
./gradlew bootRun
```

On Windows:

```bash
gradlew.bat bootRun
```

#### Run with the `dev` profile
Use the dedicated Gradle task to start the application with the `dev` profile enabled:

```bash
./gradlew bootRunDev
```

On Windows:

```bash
gradlew.bat bootRunDev
```

The `dev` profile loads sample data such as initial gyms, membership plans, and members.

## REST API Endpoints

Base URL:

```text
http://localhost:8080
```

### 1. Create a new gym
**POST** `/api/gyms`

Creates a new gym with a unique name, address, and phone number. Example request body:

```json
{
  "name": "Fabryka Formy Katowice",
  "gymAddress": {
    "country": "POLAND",
    "city": "Katowice",
    "postalCode": "40-203",
    "street": "al. Rozdzienskiego",
    "buildingNumber": "1"
  },
  "phoneNumber": "+48 777-888-999"
}
```

### 2. List all gyms
**GET** `/api/gyms`

Returns a list of all gyms

### 3. Create a new membership plan for a given gym
**POST** `/api/gyms/{gymId}/membership-plans`

Creates a membership plan assigned to the selected gym. Example request body:

```json
{
  "name": "Premium 12M",
  "type": "PREMIUM",
  "monthlyPriceAmount": 999.99,
  "monthlyPriceCurrencyCode": "PLN",
  "durationInMonths": 12,
  "maxMembers": 300
}
```

### 4. List all membership plans for a given gym
**GET** `/api/gyms/{gymId}/membership-plans`

Returns all membership plans assigned to the selected gym

### 5. Register a new member to a given membership plan
**POST** `/api/members/membership-plan/{membershipPlanId}`

Registers a new member to the selected membership plan. The application validates plan capacity and verifies email uniqueness among active members. Example request body:

```json
{
  "firstName": "Jan",
  "lastName": "Kowalski",
  "email": "jan.kowalski@example.com"
}
```

### 6. List all members
**GET** `/api/members`

Returns all members together with membership plan details, gym name, and member status

### 7. Cancel a membership
**POST** `/api/members/{memberId}/cancel-membership`

Cancels the selected membership by changing the member status to `CANCELLED`

### 8. Get monthly revenue report
**GET** `/api/reports/monthly-revenue`

Returns the total monthly revenue per gym grouped by currency. Only active members are included in the calculation.

The endpoint supports two variants:
- without query parameters - returns the general monthly revenue report
- with `month` query parameter - returns the report for a specific month in `MM-yyyy` format

Example endpoint variants:

```text
GET /api/reports/monthly-revenue
GET /api/reports/monthly-revenue?month=05-2026
```