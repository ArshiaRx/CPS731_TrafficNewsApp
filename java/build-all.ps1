# Build All Services Script
# Run from the java/ directory

# Helper function to detect and set JAVA_HOME
function Set-JavaHome {
    Write-Host "Detecting JAVA_HOME..." -ForegroundColor Cyan
    
    # Check if JAVA_HOME is already set and valid
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
        Write-Host "  JAVA_HOME is already set: $env:JAVA_HOME" -ForegroundColor Green
        return
    }
    
    # Try to find Java from PATH
    $javaPath = $null
    try {
        $javaCmd = Get-Command java -ErrorAction Stop
        $javaPath = $javaCmd.Source
        Write-Host "  Found Java in PATH: $javaPath" -ForegroundColor Cyan
    }
    catch {
        Write-Host "  Java not found in PATH, checking common locations..." -ForegroundColor Yellow
        
        # Check common Java installation paths
        $commonPaths = @(
            "$env:LOCALAPPDATA\Programs\Eclipse Adoptium\*\bin\java.exe",
            "$env:ProgramFiles\Java\*\bin\java.exe",
            "$env:ProgramFiles(x86)\Java\*\bin\java.exe",
            "C:\Program Files\Java\*\bin\java.exe"
        )
        
        foreach ($pattern in $commonPaths) {
            $found = Get-ChildItem $pattern -ErrorAction SilentlyContinue | 
            Where-Object { Test-Path "$(Split-Path (Split-Path $_.FullName))\bin\javac.exe" } | 
            Select-Object -First 1
            if ($found) {
                $javaPath = $found.FullName
                Write-Host "  Found Java at: $javaPath" -ForegroundColor Cyan
                break
            }
        }
    }
    
    if ($javaPath -and (Test-Path $javaPath)) {
        $jdkHome = Split-Path (Split-Path $javaPath)
        
        # Verify it's a valid JDK
        if (Test-Path "$jdkHome\bin\java.exe" -and Test-Path "$jdkHome\bin\javac.exe") {
            $env:JAVA_HOME = $jdkHome
            Write-Host "  ✅ Detected JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Green
        }
        else {
            Write-Host "  ❌ Invalid JDK at: $jdkHome" -ForegroundColor Red
            Write-Host "  Please set JAVA_HOME manually" -ForegroundColor Yellow
            Write-Host "  Example: `$env:JAVA_HOME = 'C:\Users\Arshi\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.6.7-hotspot'" -ForegroundColor Yellow
            exit 1
        }
    }
    else {
        Write-Host "  ❌ Java not found in PATH or common locations" -ForegroundColor Red
        Write-Host "  Please set JAVA_HOME manually:" -ForegroundColor Yellow
        Write-Host "  `$env:JAVA_HOME = 'C:\Users\Arshi\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.6.7-hotspot'" -ForegroundColor Yellow
        exit 1
    }
}

# Set JAVA_HOME before building
Set-JavaHome
Write-Host ""

Write-Host "Building all microservices..." -ForegroundColor Green

# Incident Service
Write-Host "`nBuilding Incident Service..." -ForegroundColor Yellow
cd incident-service
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build Incident Service" -ForegroundColor Red
    exit 1
}
cd ..

# Map Service
Write-Host "`nBuilding Map Service..." -ForegroundColor Yellow
cd map-service
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build Map Service" -ForegroundColor Red
    exit 1
}
cd ..

# User Service
Write-Host "`nBuilding User Service..." -ForegroundColor Yellow
cd user-service
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build User Service" -ForegroundColor Red
    exit 1
}
cd ..

# Scheduler Service
Write-Host "`nBuilding Scheduler Service..." -ForegroundColor Yellow
cd scheduler-service
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build Scheduler Service" -ForegroundColor Red
    exit 1
}
cd ..

# Web App
Write-Host "`nBuilding Web App..." -ForegroundColor Yellow
cd web-app
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build Web App" -ForegroundColor Red
    exit 1
}
cd ..

Write-Host "`nAll services built successfully!" -ForegroundColor Green
Write-Host "WAR files are in each service's target/ directory" -ForegroundColor Cyan











