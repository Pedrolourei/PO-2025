param(
    [switch]$Release21
)

# Create output directory
if (-Not (Test-Path -Path ./out)) { New-Item -ItemType Directory -Path ./out | Out-Null }

$files = Get-ChildItem -Path . -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if ($files.Count -eq 0) {
    Write-Error "No Java source files found under the current directory. Run this from the project root."
    exit 1
}

if ($Release21) {
    Write-Host "Compiling with --release 21 (requires JDK 21)"
    javac --release 21 -d out $files
} else {
    Write-Host "Compiling with default javac"
    javac -d out $files
}

if ($LASTEXITCODE -eq 0) { Write-Host "Compilation successful. Classes written to ./out" } else { Write-Error "Compilation failed."; exit $LASTEXITCODE }
