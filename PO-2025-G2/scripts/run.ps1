param(
    [string]$MainClass = "br.com.gestorfinanceiro.GestorFinanceiroApp"
)

if (-Not (Test-Path -Path ./out)) {
    Write-Error "No compiled classes found in ./out. Run scripts/compile.ps1 first."
    exit 1
}

Write-Host "Running $MainClass"
java -cp out $MainClass

if ($LASTEXITCODE -ne 0) { Write-Error "Application exited with code $LASTEXITCODE"; exit $LASTEXITCODE }
