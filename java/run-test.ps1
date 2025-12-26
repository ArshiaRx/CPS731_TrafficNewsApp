# Run Individual Test Class
# Usage: .\run-test.ps1 -ServiceName "incident-service" -TestClass "IncidentDAOTest"
# Example: .\run-test.ps1 -ServiceName "incident-service" -TestClass "IncidentDAOTest"

param(
    [Parameter(Mandatory=$true)]
    [string]$ServiceName,
    
    [Parameter(Mandatory=$true)]
    [string]$TestClass
)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Running Individual Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Validate service directory exists
$servicePath = Join-Path $PSScriptRoot $ServiceName
if (-not (Test-Path $servicePath)) {
    Write-Host "❌ Service directory not found: $servicePath" -ForegroundColor Red
    exit 1
}

Write-Host "Service: $ServiceName" -ForegroundColor Yellow
Write-Host "Test Class: $TestClass" -ForegroundColor Yellow
Write-Host ""

# Change to service directory
Push-Location $servicePath

try {
    # Step 1: Compile test classes
    Write-Host "Step 1: Compiling test classes..." -ForegroundColor Cyan
    mvn test-compile
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Failed to compile test classes" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ Test classes compiled" -ForegroundColor Green
    Write-Host ""
    
    # Step 2: Get classpath from Maven
    Write-Host "Step 2: Building classpath..." -ForegroundColor Cyan
    
    # Get dependency classpath
    Write-Host "  Getting Maven dependencies..." -ForegroundColor Gray
    $depClasspathOutput = mvn dependency:build-classpath -DincludeScope=test 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Failed to get dependency classpath" -ForegroundColor Red
        exit 1
    }
    # Extract the classpath from Maven output (last line is the classpath)
    $depClasspath = ($depClasspathOutput | Select-Object -Last 1).ToString().Trim()
    
    # Build full classpath
    $mainClasses = Join-Path $servicePath "target\classes"
    $testClasses = Join-Path $servicePath "target\test-classes"
    
    $fullClasspath = "$mainClasses;$testClasses;$depClasspath"
    
    Write-Host "✅ Classpath built" -ForegroundColor Green
    Write-Host ""
    
    # Step 3: Find the full test class name
    Write-Host "Step 3: Locating test class..." -ForegroundColor Cyan
    
    # Search for test class in test-classes directory
    $testClassFile = Get-ChildItem -Path $testClasses -Recurse -Filter "$TestClass.class" -ErrorAction SilentlyContinue
    
    if (-not $testClassFile) {
        Write-Host "❌ Test class not found: $TestClass" -ForegroundColor Red
        Write-Host "Available test classes:" -ForegroundColor Yellow
        Get-ChildItem -Path $testClasses -Recurse -Filter "*Test.class" | ForEach-Object {
            $relativePath = $_.FullName.Replace($testClasses + "\", "").Replace("\", ".").Replace(".class", "")
            Write-Host "  - $relativePath" -ForegroundColor Cyan
        }
        exit 1
    }
    
    # Convert file path to class name
    $relativePath = $testClassFile.FullName.Replace($testClasses + "\", "").Replace("\", ".").Replace(".class", "")
    $fullTestClassName = $relativePath
    
    Write-Host "✅ Found test class: $fullTestClassName" -ForegroundColor Green
    Write-Host ""
    
    # Step 4: Run the test using JUnit Console Launcher
    Write-Host "Step 4: Running test..." -ForegroundColor Cyan
    Write-Host "----------------------------------------" -ForegroundColor Gray
    Write-Host ""
    
    # JUnit 5 Console Launcher is included in junit-jupiter
    # Use org.junit.platform.console.ConsoleLauncher with full classpath
    java -cp $fullClasspath `
        org.junit.platform.console.ConsoleLauncher `
        --class-path "$testClasses" `
        --select-class $fullTestClassName `
        --details verbose
    
    $testExitCode = $LASTEXITCODE
    Write-Host ""
    Write-Host "----------------------------------------" -ForegroundColor Gray
    Write-Host ""
    
    if ($testExitCode -eq 0) {
        Write-Host "✅ Test completed successfully!" -ForegroundColor Green
    } else {
        Write-Host "❌ Test failed with exit code: $testExitCode" -ForegroundColor Red
    }
    
    exit $testExitCode
    
} finally {
    Pop-Location
}

