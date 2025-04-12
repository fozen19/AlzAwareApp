# AlzAware Application

This is the backend API for the AlzAware application, designed to help Alzheimer's patients, caregivers, and medical professionals.

## Prerequisites

- Java 21
- Maven
- PostgreSQL

## Database Setup

1. Make sure PostgreSQL is installed and running on your system.
2. Run the database setup script:
   ```
   ./setup_db.sh
   ```
   This will create the `alzaware_db` database if it doesn't exist.

## Running the Application

1. Navigate to the project directory:

   ```
   cd AlzAware-App
   ```

2. Build the application:

   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

The application will be available at http://localhost:8080

## API Documentation

The API documentation is available at:

- http://localhost:8080/swagger-ui.html

## Authentication Endpoints

- Register a new user: `POST /api/auth/signup`
- Login: `POST /api/auth/signin`

## Test Endpoints

- Public access: `GET /api/test/all`
- User access: `GET /api/test/user`
- Caregiver access: `GET /api/test/caregiver`
- Doctor access: `GET /api/test/doctor`
- Admin access: `GET /api/test/admin`

## User Roles

- ROLE_USER: Basic user role
- ROLE_CAREGIVER: For caregivers
- ROLE_DOCTOR: For medical professionals
- ROLE_ADMIN: For administrators
