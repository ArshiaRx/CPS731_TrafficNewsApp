# Deploy and Run Traffic News App
# This script builds all services, deploys to Tomcat, and starts the server
# Run from the java/ directory

param(
    [string]$TomcatPath = "C:\Program Files\Apache Software Foundation\Tomcat 9.0",
    [string]$MySQLPassword = "student",
    [switch]$SkipBuild = $false,
    [switch]$SkipDeploy = $false
)

$ErrorActionPreference = "Stop"

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
    } catch {
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
        } else {
            Write-Host "  ❌ Invalid JDK at: $jdkHome" -ForegroundColor Red
            Write-Host "  Please set JAVA_HOME manually" -ForegroundColor Yellow
            Write-Host "  Example: `$env:JAVA_HOME = 'C:\Users\Arshi\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.6.7-hotspot'" -ForegroundColor Yellow
            exit 1
        }
    } else {
        Write-Host "  ❌ Java not found in PATH or common locations" -ForegroundColor Red
        Write-Host "  Please set JAVA_HOME manually:" -ForegroundColor Yellow
        Write-Host "  `$env:JAVA_HOME = 'C:\Users\Arshi\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.6.7-hotspot'" -ForegroundColor Yellow
        exit 1
    }
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Traffic News App - Deploy & Run" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Set JAVA_HOME before building
Set-JavaHome
Write-Host ""

# Step 1: Build all services
if (-not $SkipBuild) {
    Write-Host "Step 1: Building all microservices..." -ForegroundColor Yellow
    Write-Host ""
    
    $services = @(
        @{Name = "Incident Service"; Path = "incident-service" },
        @{Name = "Map Service"; Path = "map-service" },
        @{Name = "User Service"; Path = "user-service" },
        @{Name = "Scheduler Service"; Path = "scheduler-service" },
        @{Name = "Web App"; Path = "web-app" }
    )
    
    foreach ($service in $services) {
        Write-Host "Building $($service.Name)..." -ForegroundColor Cyan
        Push-Location $service.Path
        try {
            mvn clean package -DskipTests
            if ($LASTEXITCODE -ne 0) {
                Write-Host "❌ Failed to build $($service.Name)" -ForegroundColor Red
                Pop-Location
                exit 1
            }
            Write-Host "✅ $($service.Name) built successfully" -ForegroundColor Green
        }
        finally {
            Pop-Location
        }
        Write-Host ""
    }
    
    Write-Host "All services built successfully!" -ForegroundColor Green
    Write-Host ""
}
else {
    Write-Host "Skipping build step..." -ForegroundColor Yellow
    Write-Host ""
}

# Step 2: Verify WAR files exist
Write-Host "Step 2: Verifying WAR files..." -ForegroundColor Yellow
$warFiles = @(
    "incident-service\target\incident-service-1.0.0.war",
    "map-service\target\map-service-1.0.0.war",
    "user-service\target\user-service-1.0.0.war",
    "scheduler-service\target\scheduler-service-1.0.0.war",
    "web-app\target\web-app-1.0.0.war"
)

$allExist = $true
foreach ($war in $warFiles) {
    if (Test-Path $war) {
        $size = (Get-Item $war).Length / 1KB
        Write-Host "  ✅ $war ($([math]::Round($size, 2)) KB)" -ForegroundColor Green
    }
    else {
        Write-Host "  ❌ $war - NOT FOUND" -ForegroundColor Red
        $allExist = $false
    }
}

if (-not $allExist) {
    Write-Host ""
    Write-Host "Some WAR files are missing. Please build the services first." -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 3: Stop Tomcat if running
Write-Host "Step 3: Stopping Tomcat (if running)..." -ForegroundColor Yellow
$tomcatBin = Join-Path $TomcatPath "bin"
$shutdownScript = Join-Path $tomcatBin "shutdown.bat"

if (Test-Path $shutdownScript) {
    try {
        & $shutdownScript 2>&1 | Out-Null
        Start-Sleep -Seconds 3
        Write-Host "  Tomcat shutdown command executed" -ForegroundColor Cyan
    }
    catch {
        Write-Host "  Note: Tomcat may not have been running" -ForegroundColor Yellow
    }
}
else {
    Write-Host "  ⚠ Warning: Tomcat shutdown script not found at: $shutdownScript" -ForegroundColor Yellow
}
Write-Host ""

# Step 4: Deploy WAR files to Tomcat
if (-not $SkipDeploy) {
    Write-Host "Step 4: Deploying WAR files to Tomcat..." -ForegroundColor Yellow
    $tomcatWebapps = Join-Path $TomcatPath "webapps"
    
    if (-not (Test-Path $tomcatWebapps)) {
        Write-Host "  ❌ Tomcat webapps directory not found: $tomcatWebapps" -ForegroundColor Red
        Write-Host "  Please verify Tomcat path: $TomcatPath" -ForegroundColor Yellow
        exit 1
    }
    
    foreach ($war in $warFiles) {
        $warName = Split-Path $war -Leaf
        $destination = Join-Path $tomcatWebapps $warName
        
        try {
            Copy-Item $war -Destination $destination -Force
            Write-Host "  ✅ Deployed: $warName" -ForegroundColor Green
        }
        catch {
            Write-Host "  ❌ Failed to deploy: $warName" -ForegroundColor Red
            Write-Host "    Error: $_" -ForegroundColor Red
            exit 1
        }
    }
    Write-Host ""
}
else {
    Write-Host "Skipping deployment step..." -ForegroundColor Yellow
    Write-Host ""
}

# Step 5: Start Tomcat
Write-Host "Step 5: Starting Tomcat..." -ForegroundColor Yellow
$startupScript = Join-Path $tomcatBin "startup.bat"

if (Test-Path $startupScript) {
    try {
        # Start Tomcat in a new window
        Start-Process -FilePath $startupScript -WorkingDirectory $tomcatBin
        Write-Host "  ✅ Tomcat startup command executed" -ForegroundColor Green
        Write-Host "  Waiting for Tomcat to start..." -ForegroundColor Cyan
        Start-Sleep -Seconds 10
    }
    catch {
        Write-Host "  ❌ Failed to start Tomcat: $_" -ForegroundColor Red
        exit 1
    }
}
else {
    Write-Host "  ❌ Tomcat startup script not found at: $startupScript" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 6: Verify Tomcat is running
Write-Host "Step 6: Verifying Tomcat is running..." -ForegroundColor Yellow
$maxRetries = 6
$retryCount = 0
$tomcatRunning = $false

while ($retryCount -lt $maxRetries -and -not $tomcatRunning) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080" -TimeoutSec 5 -UseBasicParsing -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            $tomcatRunning = $true
            Write-Host "  ✅ Tomcat is running! (Status: $($response.StatusCode))" -ForegroundColor Green
        }
    }
    catch {
        $retryCount++
        if ($retryCount -lt $maxRetries) {
            Write-Host "  Waiting... ($retryCount/$maxRetries)" -ForegroundColor Yellow
            Start-Sleep -Seconds 5
        }
        else {
            Write-Host "  ⚠ Tomcat may still be starting. Check manually:" -ForegroundColor Yellow
            Write-Host "    http://localhost:8080" -ForegroundColor Cyan
        }
    }
}
Write-Host ""

# Step 7: Verify application is accessible
Write-Host "Step 7: Verifying application..." -ForegroundColor Yellow
$appUrl = "http://localhost:8080/web-app-1.0.0/"
try {
    $response = Invoke-WebRequest -Uri $appUrl -TimeoutSec 10 -UseBasicParsing -ErrorAction Stop
    Write-Host "  ✅ Application is accessible!" -ForegroundColor Green
    Write-Host "  Status: $($response.StatusCode)" -ForegroundColor Cyan
}
catch {
    Write-Host "  ⚠ Application may still be deploying. Wait a few seconds and try:" -ForegroundColor Yellow
    Write-Host "    $appUrl" -ForegroundColor Cyan
}
Write-Host ""

# Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Deployment Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Application URLs:" -ForegroundColor Yellow
Write-Host "  Main App:     http://localhost:8080/web-app-1.0.0/" -ForegroundColor Cyan
Write-Host "  Incident API: http://localhost:8080/incident-service-1.0.0/api/incidents" -ForegroundColor Cyan
Write-Host "  Map API:      http://localhost:8080/map-service-1.0.0/api/map/geocode?address=Toronto" -ForegroundColor Cyan
Write-Host "  User API:     http://localhost:8080/user-service-1.0.0/api/routes" -ForegroundColor Cyan
Write-Host "  Scheduler:   http://localhost:8080/scheduler-service-1.0.0/api/scheduler/queue" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host "  1. Open the main app URL in your browser" -ForegroundColor White
Write-Host "  2. Verify MySQL database is running and accessible" -ForegroundColor White
Write-Host "  3. Check database credentials in DatabaseConnection.java files" -ForegroundColor White
Write-Host ""
Write-Host "To stop Tomcat:" -ForegroundColor Yellow
Write-Host "  & `"$shutdownScript`"" -ForegroundColor Cyan
Write-Host ""


