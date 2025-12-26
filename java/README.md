# Traffic News App - Java Implementation

## 📋 Project Overview

The **Traffic News App** is a web-based application that allows users to view, search, filter, and submit traffic incidents in real-time. The application displays incidents on an interactive map and provides a comprehensive interface for managing traffic-related information.

### Key Features

- **View Traffic Incidents**: Browse all traffic incidents with details (type, severity, location, description)
- **Interactive Map**: Visualize incidents on a Leaflet.js map with color-coded markers based on severity
- **Filter & Search**: Filter incidents by type, severity, or status, and search by keywords
- **Submit New Incidents**: Users can report new traffic incidents through a form
- **Real-time Updates**: Automatic data refresh every 30 seconds
- **RESTful API**: Microservices architecture with RESTful endpoints for all operations

---

## 🏗️ Architecture

This application follows a **Microservices Architecture** pattern with the following components:

### Microservices

1. **Incident Service** (`incident-service`)
   - Manages traffic incident CRUD operations
   - Handles validation, filtering, and searching
   - REST API endpoints for incident management

2. **Map Service** (`map-service`)
   - Provides geocoding functionality
   - Converts addresses to coordinates
   - Map-related operations

3. **User Service** (`user-service`)
   - Manages saved routes
   - Handles rate limiting
   - User-specific operations

4. **Scheduler Service** (`scheduler-service`)
   - Background task scheduling
   - Offline submission queue management
   - Automated data refresh

5. **Web Application** (`web-app`)
   - Frontend JSP-based web interface
   - Integrates all microservices
   - User-facing application

### Architecture Pattern

- **3-Layered Architecture**:
  - **Presentation Layer**: JSP pages with JavaScript for UI
  - **Business Logic Layer**: Java services (IncidentService, ValidationService, etc.)
  - **Data Layer**: JDBC DAOs for database access

---

## 🛠️ Technologies Used

### Backend
- **Java 11+** (OpenJDK 21.0.6)
- **Java Servlets** (RESTful web services)
- **JSP** (JavaServer Pages) for frontend
- **JDBC** for database connectivity
- **Maven** for build automation and dependency management

### Database
- **MySQL 8.0+** for data persistence
- Database: `trafficnewsapp`
- Tables: `incidents`, `routes`, `submissions`

### Server
- **Apache Tomcat 9.0** as the application server
- Default port: **8080**

### Frontend
- **Bootstrap 5** for responsive UI
- **Leaflet.js** for interactive map visualization
- **JavaScript (ES6)** for client-side interactivity
- **CSS3** for styling

### Testing
- **JUnit 5** for unit testing

### Build Tools
- **Maven 3.6+** for project management

---

## 📁 Project Structure

```
java/
├── incident-service/          # Microservice 1: Incident Management
│   ├── src/main/java/         # Java source code
│   ├── src/main/webapp/       # Web resources
│   ├── src/test/java/         # Unit tests
│   └── pom.xml                # Maven configuration
├── map-service/               # Microservice 2: Map Services
├── user-service/              # Microservice 3: User Services
├── scheduler-service/         # Microservice 4: Scheduler Services
├── web-app/                   # Frontend Web Application
│   ├── src/main/webapp/       # JSP pages and static resources
│   └── resources/js/          # JavaScript files
├── build-all.ps1              # Build script for all services
└── deploy-and-run.ps1          # Deployment and startup script
```

---

## ⚙️ Prerequisites

Before running the application, ensure you have:

- ✅ **Java JDK 11 or higher** (Currently using: OpenJDK 21.0.6)
- ✅ **Maven 3.6+** installed and in PATH
- ✅ **MySQL 8.0+** installed and running
- ✅ **Apache Tomcat 9.0** installed
  - Default location: `C:\Program Files\Apache Software Foundation\Tomcat 9.0`
- ✅ **PowerShell** (for running build scripts)

---

## 🚀 How to Run the Application

### Step 1: Setup Database

First, create the MySQL database and tables:

```powershell
# Navigate to project root
cd "F:\Courses\Fall2025\CPS731 - Software Engineering I (Minor)\TrafficNewsApp"

# Run the database schema script
mysql -u root -p < database\schema.sql
```

**Or using MySQL Workbench:**
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Open and execute `database\schema.sql`

**Important:** Update database credentials in the following files if your MySQL password is not "student":
- `incident-service/src/main/java/.../util/DatabaseConnection.java`
- `user-service/src/main/java/.../util/DatabaseConnection.java`
- `scheduler-service/src/main/java/.../util/DatabaseConnection.java`

Change the `DB_PASSWORD` value in each file.

---

### Step 2: Build All Services

Navigate to the `java` directory and run the build script:

```powershell
# Navigate to java directory
cd "F:\Courses\Fall2025\CPS731 - Software Engineering I (Minor)\TrafficNewsApp\java"

# Set JAVA_HOME if needed (if build script can't detect it automatically)
$env:JAVA_HOME = "C:\Users\Arshi\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.6.7-hotspot"

# Build all microservices
.\build-all.ps1
```

**Note:** The `build-all.ps1` script automatically detects JAVA_HOME from your PATH. If Java is not found, you may need to set it manually as shown above.

This will:
- ✅ Detect and set JAVA_HOME automatically
- ✅ Build all 5 microservices (Incident, Map, User, Scheduler, Web App)
- ✅ Create WAR files in each service's `target/` directory

---

### Step 3: Deploy and Run

After building, deploy to Tomcat and start the server:

```powershell
# Still in the java directory, run the deployment script
.\deploy-and-run.ps1
```

This script will:
- ✅ Build all services (if not already built)
- ✅ Deploy WAR files to Tomcat's `webapps/` directory
- ✅ Start Tomcat server
- ✅ Verify the application is running
- ✅ Display all accessible URLs

**Alternative:** If you need to specify a different Tomcat path:
```powershell
.\deploy-and-run.ps1 -TomcatPath "C:\Your\Custom\Tomcat\Path"
```

---

## 🌐 Accessing the Application

Once Tomcat is running, access the application at:

### Main Application
- **Web App**: http://localhost:8080/web-app-1.0.0/

### API Endpoints

- **Incident Service API**:
  - Get all incidents: http://localhost:8080/incident-service-1.0.0/api/incidents
  - Get incident by ID: http://localhost:8080/incident-service-1.0.0/api/incidents/{id}

- **Map Service API**:
  - Geocode address: http://localhost:8080/map-service-1.0.0/api/map/geocode?address=Toronto

- **User Service API**:
  - Get routes: http://localhost:8080/user-service-1.0.0/api/routes

- **Scheduler Service API**:
  - Get queue: http://localhost:8080/scheduler-service-1.0.0/api/scheduler/queue

---

## 🔧 Troubleshooting

### Issue: JAVA_HOME not defined correctly

**Error:** `The JAVA_HOME environment variable is not defined correctly`

**Solution:**
1. The build scripts automatically detect JAVA_HOME. If it fails:
   ```powershell
   $env:JAVA_HOME = "C:\Users\Arshi\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.6.7-hotspot"
   ```
2. Verify Java is accessible:
   ```powershell
   java -version
   ```

### Issue: Tomcat won't start

**Solution:**
1. Check if port 8080 is in use:
   ```powershell
   netstat -ano | findstr :8080
   ```
2. Check Tomcat logs:
   ```powershell
   Get-Content "C:\Program Files\Apache Software Foundation\Tomcat 9.0\logs\catalina.out" -Tail 50
   ```

### Issue: Database connection error

**Solution:**
1. Verify MySQL is running:
   ```powershell
   Get-Service | Where-Object {$_.Name -like "*mysql*"}
   ```
2. Check database credentials in `DatabaseConnection.java` files
3. Test MySQL connection:
   ```powershell
   mysql -u root -p -e "USE trafficnewsapp; SELECT COUNT(*) FROM incidents;"
   ```

### Issue: 404 errors when accessing application

**Solution:**
1. Wait 10-15 seconds after starting Tomcat for WAR files to deploy
2. Check if WAR files are in Tomcat's webapps directory:
   ```powershell
   Get-ChildItem "C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\*.war"
   ```
3. Check Tomcat logs for deployment errors

---

## 🛑 Stopping the Application

To stop Tomcat:

```powershell
# Option A: Using shutdown script
& "C:\Program Files\Apache Software Foundation\Tomcat 9.0\bin\shutdown.bat"

# Option B: Stop Windows Service
Stop-Service Tomcat9
```

---

## 📝 API Documentation

### Incident Service Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/incidents` | Get all incidents (supports filters: type, severity, status, keyword) |
| GET | `/api/incidents/{id}` | Get incident by ID |
| POST | `/api/incidents` | Create new incident |
| PUT | `/api/incidents/{id}` | Update incident |
| DELETE | `/api/incidents/{id}` | Delete incident |

### Example: Create Incident

```json
POST /api/incidents
Content-Type: application/json

{
  "type": "accident",
  "severity": "high",
  "location": "Highway 401 near Yonge St",
  "latitude": 43.6532,
  "longitude": -79.3832,
  "description": "Multi-vehicle collision",
  "reporter_id": "user123"
}
```

---

## 🧪 Testing

> **📖 For complete testing instructions, see:** [`TESTING_GUIDE.md`](TESTING_GUIDE.md)

### Prerequisites: Set JAVA_HOME

Before running tests, refresh JAVA_HOME in your current PowerShell session:

```powershell
$env:JAVA_HOME = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "User")
```

Verify it's set:
```powershell
mvn -version
```

### Quick Start: Run Individual Tests

Navigate to the service directory and run a specific test:

```powershell
# Refresh JAVA_HOME (if needed)
$env:JAVA_HOME = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "User")

# Navigate to service directory
cd incident-service

# Run a specific test
mvn test -Dtest=IncidentDAOTest
```

### Run All Unit Tests

```powershell
cd incident-service
mvn test
```

### Test File Locations

All test files are located in `src/test/java/` of each service:

- **Incident Service Tests**: `incident-service/src/test/java/com/trafficnewsapp/incident/`
  - `dao/IncidentDAOTest.java`
  - `services/IncidentServiceTest.java`
  - `services/ValidationServiceTest.java`
  - `services/FilterServiceTest.java`

- **User Service Tests**: `user-service/src/test/java/com/trafficnewsapp/user/`
  - `services/SavedRoutesServiceTest.java`

### Available Test Classes

| Test Class | Service | Command |
|------------|---------|---------|
| `IncidentDAOTest` | incident-service | `mvn test -Dtest=IncidentDAOTest` |
| `IncidentServiceTest` | incident-service | `mvn test -Dtest=IncidentServiceTest` |
| `ValidationServiceTest` | incident-service | `mvn test -Dtest=ValidationServiceTest` |
| `FilterServiceTest` | incident-service | `mvn test -Dtest=FilterServiceTest` |
| `SavedRoutesServiceTest` | user-service | `mvn test -Dtest=SavedRoutesServiceTest` |

### Additional Testing Resources

- **Complete Testing Guide**: [`TESTING_GUIDE.md`](TESTING_GUIDE.md) - Step-by-step instructions, directory structure, troubleshooting
- **Advanced Testing Methods**: [`RUN_INDIVIDUAL_TESTS.md`](RUN_INDIVIDUAL_TESTS.md) - Alternative testing approaches

---

---

## 👥 Development

### Project Information
- **Course**: CPS731 - Software Engineering I
- **Architecture Pattern**: Microservices with 3-Layered Architecture
- **Language**: Java 11+
- **Build Tool**: Maven
- **Server**: Apache Tomcat 9.0

### Key Components

- **Models**: Incident, Route, Submission
- **Services**: Business logic layer (IncidentService, ValidationService, etc.)
- **DAOs**: Data access layer (IncidentDAO, RouteDAO, SubmissionDAO)
- **Servlets**: REST API endpoints
- **JSP**: Frontend presentation layer

---

## 📄 License

This project is developed for educational purposes as part of CPS731 - Software Engineering I course.

---

## 🆘 Support

For issues or questions:
1. Check the Troubleshooting section above
2. Review Tomcat logs: `C:\Program Files\Apache Software Foundation\Tomcat 9.0\logs\`
3. Check application logs in Tomcat's logs directory
4. Verify all prerequisites are installed and configured correctly

---

**Last Updated**: 2025
**Version**: 1.0.0

