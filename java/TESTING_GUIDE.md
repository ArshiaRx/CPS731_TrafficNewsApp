# 🧪 Testing Guide - Running Java Test Files

This guide provides step-by-step instructions for running Java unit tests in the Traffic News App project.

---

## 📋 Prerequisites

### 1. Set JAVA_HOME (If Not Already Set)

Before running tests, ensure `JAVA_HOME` is set in your current PowerShell session:

```powershell
# Refresh JAVA_HOME from User Environment Variables
$env:JAVA_HOME = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "User")
```

**Note**: If `JAVA_HOME` is permanently set (as a User Environment Variable), you may still need to refresh it in your current terminal session using the command above.

### 2. Verify JAVA_HOME is Set

```powershell
# Check if JAVA_HOME is set
$env:JAVA_HOME

# Verify Maven can find Java
mvn -version
```

You should see output showing:
- Java version: 21.0.6 (or your installed version)
- Maven version: 3.9.9 (or your installed version)

---

## 📁 Test File Directory Structure

All test files are located in the `src/test/java/` directory of each service:

```
TrafficNewsApp/java/
├── incident-service/
│   └── src/test/java/com/trafficnewsapp/incident/
│       ├── dao/
│       │   └── IncidentDAOTest.java
│       └── services/
│           ├── IncidentServiceTest.java
│           ├── ValidationServiceTest.java
│           └── FilterServiceTest.java
│
└── user-service/
    └── src/test/java/com/trafficnewsapp/user/
        └── services/
            └── SavedRoutesServiceTest.java
```

### Complete File Paths

| Test Class | Full File Path |
|------------|----------------|
| **IncidentDAOTest** | `incident-service/src/test/java/com/trafficnewsapp/incident/dao/IncidentDAOTest.java` |
| **IncidentServiceTest** | `incident-service/src/test/java/com/trafficnewsapp/incident/services/IncidentServiceTest.java` |
| **ValidationServiceTest** | `incident-service/src/test/java/com/trafficnewsapp/incident/services/ValidationServiceTest.java` |
| **FilterServiceTest** | `incident-service/src/test/java/com/trafficnewsapp/incident/services/FilterServiceTest.java` |
| **SavedRoutesServiceTest** | `user-service/src/test/java/com/trafficnewsapp/user/services/SavedRoutesServiceTest.java` |

---

## 🚀 Quick Start: Running Tests

### Method 1: Run Individual Test (Recommended for Demo)

Navigate to the service directory and run a specific test:

```powershell
# Step 1: Refresh JAVA_HOME (if needed)
$env:JAVA_HOME = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "User")

# Step 2: Navigate to the service directory
cd "F:\Courses\Fall2025\CPS731 - Software Engineering I (Minor)\TrafficNewsApp\java\incident-service"

# Step 3: Run a specific test
mvn test -Dtest=IncidentDAOTest
```

### Method 2: Run All Tests in a Service

```powershell
cd incident-service
mvn test
```

---

## 📝 Step-by-Step Instructions

### Running Incident Service Tests

#### Test 1: IncidentDAO Test

```powershell
# Navigate to incident-service
cd "F:\Courses\Fall2025\CPS731 - Software Engineering I (Minor)\TrafficNewsApp\java\incident-service"

# Run the test
mvn test -Dtest=IncidentDAOTest
```

**What it tests**: Database operations (findAll, save, delete)

#### Test 2: IncidentService Test

```powershell
# (Already in incident-service directory)
mvn test -Dtest=IncidentServiceTest
```

**What it tests**: Business logic for incident management

#### Test 3: ValidationService Test

```powershell
mvn test -Dtest=ValidationServiceTest
```

**What it tests**: Input validation and data validation

#### Test 4: FilterService Test

```powershell
mvn test -Dtest=FilterServiceTest
```

**What it tests**: Filtering and searching functionality

---

### Running User Service Tests

#### Test 5: SavedRoutesService Test

```powershell
# Navigate to user-service
cd "F:\Courses\Fall2025\CPS731 - Software Engineering I (Minor)\TrafficNewsApp\java\user-service"

# Run the test
mvn test -Dtest=SavedRoutesServiceTest
```

**What it tests**: Saved routes functionality

---

## 🎯 Complete Demo Script

Copy and paste this complete script to run all tests one by one:

```powershell
# ============================================
# Complete Test Demo Script
# ============================================

# Refresh JAVA_HOME
$env:JAVA_HOME = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "User")

# Base directory
$baseDir = "F:\Courses\Fall2025\CPS731 - Software Engineering I (Minor)\TrafficNewsApp\java"

# Test 1: IncidentDAO
Write-Host "`n=== Test 1: IncidentDAO ===" -ForegroundColor Cyan
cd "$baseDir\incident-service"
mvn test -Dtest=IncidentDAOTest

# Test 2: IncidentService
Write-Host "`n=== Test 2: IncidentService ===" -ForegroundColor Cyan
mvn test -Dtest=IncidentServiceTest

# Test 3: ValidationService
Write-Host "`n=== Test 3: ValidationService ===" -ForegroundColor Cyan
mvn test -Dtest=ValidationServiceTest

# Test 4: FilterService
Write-Host "`n=== Test 4: FilterService ===" -ForegroundColor Cyan
mvn test -Dtest=FilterServiceTest

# Test 5: SavedRoutesService
Write-Host "`n=== Test 5: SavedRoutesService ===" -ForegroundColor Cyan
cd "$baseDir\user-service"
mvn test -Dtest=SavedRoutesServiceTest

Write-Host "`n=== All Tests Completed ===" -ForegroundColor Green
```

---

## 📊 Available Test Classes Summary

| # | Test Class | Service | Package | Tests Methods |
|---|------------|---------|---------|---------------|
| 1 | `IncidentDAOTest` | incident-service | `com.trafficnewsapp.incident.dao` | findAll, save, delete |
| 2 | `IncidentServiceTest` | incident-service | `com.trafficnewsapp.incident.services` | fetchIncidents, addIncident, updateIncident |
| 3 | `ValidationServiceTest` | incident-service | `com.trafficnewsapp.incident.services` | validateIncident, validateCoordinates |
| 4 | `FilterServiceTest` | incident-service | `com.trafficnewsapp.incident.services` | filterByType, filterBySeverity |
| 5 | `SavedRoutesServiceTest` | user-service | `com.trafficnewsapp.user.services` | saveRoute, getRoutes, deleteRoute |

---

## 🔍 Understanding Test Output

When you run `mvn test -Dtest=IncidentDAOTest`, you'll see:

```
[INFO] Scanning for projects...
[INFO] 
[INFO] --- maven-surefire-plugin:3.x.x:test (default-test) ---
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.trafficnewsapp.incident.dao.IncidentDAOTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: X.XXX s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**Key indicators:**
- ✅ `BUILD SUCCESS` = All tests passed
- ❌ `BUILD FAILURE` = Some tests failed (check the error messages)
- `Tests run: X` = Number of test methods executed
- `Failures: 0` = No test failures

---

## 🛠️ Troubleshooting

### Error: "JAVA_HOME environment variable is not defined correctly"

**Solution:**
```powershell
# Refresh JAVA_HOME in current session
$env:JAVA_HOME = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "User")

# Verify it's set
$env:JAVA_HOME

# If still not set, set it manually
$env:JAVA_HOME = "C:\Users\Arshi\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.6.7-hotspot"
```

### Error: "There is no POM in this directory"

**Solution:** You're in the wrong directory. Navigate to a service directory:
```powershell
cd incident-service  # or user-service
```

### Error: "Could not find or load main class"

**Solution:** This usually means dependencies aren't downloaded. Run:
```powershell
mvn clean test-compile
mvn test -Dtest=YourTestClass
```

### Error: "Access denied for user 'root'@'localhost'"

**Solution:** This is a database connection error. Ensure:
1. MySQL is running
2. Database credentials in `DatabaseConnection.java` are correct
3. Database `trafficnewsapp` exists

To skip database tests temporarily:
```powershell
mvn test -Dtest=ValidationServiceTest  # This doesn't need database
```

### Test Compilation Errors

**Solution:**
```powershell
# Clean and recompile
mvn clean test-compile
mvn test -Dtest=YourTestClass
```

---

## 📚 Additional Resources

- **Main README**: See [`README.md`](README.md) for project overview
- **Detailed Testing Methods**: See [`RUN_INDIVIDUAL_TESTS.md`](RUN_INDIVIDUAL_TESTS.md) for advanced testing options
- **Test Cases Documentation**: See [`../docs/TestCases.md`](../docs/TestCases.md) for detailed test case descriptions

---

## ✅ Quick Reference Card

| Command | Description |
|---------|-------------|
| `$env:JAVA_HOME = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "User")` | Refresh JAVA_HOME |
| `cd incident-service` | Navigate to incident service |
| `mvn test -Dtest=IncidentDAOTest` | Run specific test |
| `mvn test` | Run all tests in service |
| `mvn clean test` | Clean and run all tests |
| `mvn test-compile` | Compile test classes only |

---

**Last Updated**: 2025  
**Version**: 1.0.0






