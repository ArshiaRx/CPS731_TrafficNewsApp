# Traffic News App

A web-based application for viewing and reporting real-time traffic incidents, built with a **Microservices Architecture** using Java Servlets, MySQL, and JSP.

## Project Structure

```
TrafficNewsApp/
├── database/                    # Database schema and setup
│   └── schema.sql              # MySQL database schema
├── docs/                        # Documentation
│   └── TestCases.md            # Java JUnit test cases documentation
├── java/                        # Java implementation (main application)
│   ├── incident-service/       # Microservice 1: Incident Management
│   ├── map-service/            # Microservice 2: Map Services
│   ├── user-service/           # Microservice 3: User Services
│   ├── scheduler-service/      # Microservice 4: Scheduler Services
│   ├── web-app/                # Frontend Web Application
│   ├── build-all.ps1           # Build script for all services
│   ├── deploy-and-run.ps1      # Deployment and startup script
│   └── README.md               # Detailed Java implementation guide
├── Phase-III/                   # Phase III LaTeX report
├── Phase-III-IV/                # Combined Phase III & IV report
└── Phase-IV/                    # Phase IV LaTeX report
```

## Architecture

The application follows a **Microservices Architecture** with a **3-Layered Architecture** pattern:

### Microservices
1. **Incident Service** - Manages traffic incident CRUD operations
2. **Map Service** - Provides geocoding and map functionality
3. **User Service** - Handles user routes and rate limiting
4. **Scheduler Service** - Manages background tasks and queues
5. **Web App** - Frontend JSP-based web interface

### 3-Layered Architecture
- **Presentation Layer**: JSP pages with JavaScript for UI
- **Business Logic Layer**: Java services (IncidentService, ValidationService, etc.)
- **Data Layer**: JDBC DAOs for MySQL database access

## Technologies Used

- **Backend**: Java 11+, Java Servlets, JSP, JDBC
- **Database**: MySQL 8.0+
- **Server**: Apache Tomcat 9.0
- **Build Tool**: Maven
- **Frontend**: Bootstrap 5, Leaflet.js, JavaScript (ES6)
- **Testing**: JUnit 5

## Features

- View, filter, search, and sort traffic incidents
- Interactive map visualization with color-coded markers
- Report new incidents with validation
- Auto-refresh functionality
- RESTful API endpoints for all operations
- Microservices architecture for scalability

## Running the Application

For detailed instructions on how to run the application, see:
- **[java/README.md](java/README.md)** - Complete setup and run guide

### Quick Start

1. **Setup Database**: Run `database/schema.sql` in MySQL
2. **Build Services**: Navigate to `java/` and run `.\build-all.ps1`
3. **Deploy & Run**: Run `.\deploy-and-run.ps1`
4. **Access**: Open http://localhost:8080/web-app-1.0.0/

## Documentation

- **Java Implementation**: See `java/README.md` for complete documentation
- **Test Cases**: See `docs/TestCases.md` for JUnit test case documentation
- **Phase Reports**: See `Phase-III/`, `Phase-IV/`, and `Phase-III-IV/` folders

## Note on nbproject Folders

The `nbproject/` folders in the `java/` directory contain NetBeans IDE configuration files. These are:
- **Not necessary** for building or running the project (Maven handles that)
- **IDE-specific** - only needed if using NetBeans IDE
- **Auto-generated** - NetBeans will regenerate them if you open the project
- **Safe to remove** if you're not using NetBeans

If you're using a different IDE (IntelliJ, Eclipse, VS Code) or just building from command line, you can safely ignore or delete these folders.

## Course Information

- **Course**: CPS731 - Software Engineering I
- **Architecture Pattern**: Microservices with 3-Layered Architecture
- **Language**: Java 11+













