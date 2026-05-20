# Windows PowerShell Setup Script for Isode X.400 Native Libraries
# This script helps setup and verify the required DLL files

param(
    [switch]$Verify = $false,
    [switch]$Help = $false
)

# Script configuration
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$LIB_DIR = Join-Path $SCRIPT_DIR "lib"
$AMDDIR = Join-Path $LIB_DIR "amd64"

# Display help
if ($Help) {
    Write-Host "Setup script for Isode X.400 Native Libraries" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Usage: .\setup-native-libs.ps1 [options]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Options:" -ForegroundColor Cyan
    Write-Host "  -Verify     Verify the DLL files are properly installed"
    Write-Host "  -Help       Display this help message"
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Cyan
    Write-Host "  .\setup-native-libs.ps1              # Setup directories and show instructions"
    Write-Host "  .\setup-native-libs.ps1 -Verify      # Verify DLL files installation"
    Write-Host ""
    exit 0
}

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "     AMHS UA Test Tool - Native Library Setup" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

# Function to verify DLL file
function Test-DLLFile {
    param($Path)
    
    if (Test-Path $Path -PathType Leaf) {
        $file = Get-Item $Path
        $size = $file.Length
        $sizeKB = [math]::Round($size / 1024, 2)
        Write-Host "  [OK] Found: $(Split-Path $Path -Leaf) ($sizeKB KB)" -ForegroundColor Green
        return $true
    } else {
        Write-Host "  [MISSING] $(Split-Path $Path -Leaf)" -ForegroundColor Red
        return $false
    }
}

# Function to get Java architecture
function Get-JavaArchitecture {
    try {
        $javaVersion = & java -version 2>&1 | Select-String "64-Bit|32-Bit" | Select-Object -First 1
        if ($javaVersion) {
            return $javaVersion.ToString().Trim()
        }
        return "Unknown architecture"
    } catch {
        return "Java not found or error checking version"
    }
}

# Display current Java version
Write-Host "Java Environment:" -ForegroundColor Yellow
try {
    $javaInfo = & java -version 2>&1
    foreach ($line in $javaInfo) {
        Write-Host "  $line"
    }
} catch {
    Write-Host "  ERROR: Java not found or cannot execute java command" -ForegroundColor Red
}

Write-Host ""

if ($Verify) {
    # Verify mode - check if DLL files are properly installed
    Write-Host "Verification Mode" -ForegroundColor Cyan
    Write-Host "========================================================" -ForegroundColor Cyan
    Write-Host ""
    
    $allFound = $true
    
    Write-Host "Checking 64-bit libraries (lib\amd64\):" -ForegroundColor Yellow
    $allFound = (Test-DLLFile (Join-Path $AMDDIR "pthreadvc2.dll")) -and $allFound
    $allFound = (Test-DLLFile (Join-Path $AMDDIR "CJavaInterface.dll")) -and $allFound
    
    Write-Host ""
    Write-Host "Checking 32-bit libraries (lib\):" -ForegroundColor Yellow
    $has32bit = $false
    if (Test-DLLFile (Join-Path $LIB_DIR "pthreadvc2.dll")) {
        $has32bit = $true
    }
    if (Test-DLLFile (Join-Path $LIB_DIR "CJavaInterface.dll")) {
        $has32bit = $true
    }
    
    Write-Host ""
    Write-Host "Summary:" -ForegroundColor Cyan
    Write-Host "  Java Architecture: $(Get-JavaArchitecture)" -ForegroundColor Yellow
    
    if ($allFound -and $has32bit) {
        Write-Host "  Status: [OK] Both 64-bit and 32-bit libraries are available" -ForegroundColor Green
    } elseif ($allFound) {
        Write-Host "  Status: [OK] 64-bit libraries found (recommended for 64-bit Java)" -ForegroundColor Green
    } elseif ($has32bit) {
        Write-Host "  Status: [WARNING] Only 32-bit libraries found (use 32-bit Java)" -ForegroundColor Yellow
    } else {
        Write-Host "  Status: [ERROR] No DLL files found - installation required" -ForegroundColor Red
    }
    
    Write-Host ""
    
} else {
    # Setup mode - create directories and show instructions
    Write-Host "Setup Mode" -ForegroundColor Cyan
    Write-Host "========================================================" -ForegroundColor Cyan
    Write-Host ""
    
    # Create directories if they don't exist
    if (-not (Test-Path $LIB_DIR)) {
        Write-Host "Creating lib directory..." -ForegroundColor Yellow
        New-Item -ItemType Directory -Path $LIB_DIR -Force | Out-Null
    }
    
    if (-not (Test-Path $AMDDIR)) {
        Write-Host "Creating lib\amd64 directory for 64-bit native libraries..." -ForegroundColor Yellow
        New-Item -ItemType Directory -Path $AMDDIR -Force | Out-Null
    }
    
    Write-Host ""
    Write-Host "Directories created successfully" -ForegroundColor Green
    Write-Host ""
    
    Write-Host "Next Steps:" -ForegroundColor Cyan
    Write-Host "========================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. Locate your Isode X.400 SDK installation" -ForegroundColor Yellow
    Write-Host "   Typical locations:" -ForegroundColor Gray
    Write-Host "     C:\Program Files\Isode\lib\amd64\" -ForegroundColor Gray
    Write-Host "     C:\Program Files (x86)\Isode\lib\amd64\" -ForegroundColor Gray
    Write-Host ""
    
    Write-Host "2. Copy the required DLL files:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "   For 64-bit Java:" -ForegroundColor Cyan
    Write-Host "     Copy pthreadvc2.dll to:    $AMDDIR\" -ForegroundColor Gray
    Write-Host "     Copy CJavaInterface.dll to: $AMDDIR\" -ForegroundColor Gray
    Write-Host ""
    Write-Host "   For 32-bit Java:" -ForegroundColor Cyan
    Write-Host "     Copy pthreadvc2.dll to:    $LIB_DIR\" -ForegroundColor Gray
    Write-Host "     Copy CJavaInterface.dll to: $LIB_DIR\" -ForegroundColor Gray
    Write-Host ""
    
    Write-Host "3. Verify the installation:" -ForegroundColor Yellow
    Write-Host "   .\setup-native-libs.ps1 -Verify" -ForegroundColor Gray
    Write-Host ""
    
    Write-Host "4. Rebuild the project:" -ForegroundColor Yellow
    Write-Host "   mvn clean package" -ForegroundColor Gray
    Write-Host ""
    
    Write-Host "5. Run the application:" -ForegroundColor Yellow
    Write-Host "   run.bat" -ForegroundColor Gray
    Write-Host ""
    
    Write-Host "Commands to copy files (for PowerShell):" -ForegroundColor Cyan
    Write-Host "========================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host 'Copy-Item "C:\Program Files\Isode\lib\amd64\pthreadvc2.dll" -Destination "'$AMDDIR'\"' -ForegroundColor Gray
    Write-Host 'Copy-Item "C:\Program Files\Isode\lib\amd64\CJavaInterface.dll" -Destination "'$AMDDIR'\"' -ForegroundColor Gray
    Write-Host ""
    
    Write-Host "For more detailed troubleshooting, see:" -ForegroundColor Cyan
    Write-Host "  NATIVE_LIBRARIES_TROUBLESHOOTING.md" -ForegroundColor Yellow
    Write-Host ""
}

Write-Host "========================================================" -ForegroundColor Cyan
