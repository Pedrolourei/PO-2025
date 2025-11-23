Upgrade to Java 21 — quick steps

This project doesn't use Maven/Gradle (no pom.xml or build.gradle). To test and run with Java 21 locally, follow these steps.

1) Install JDK 21

- Download and install a JDK 21 distribution (Adoptium Temurin, BellSoft Liberica, Oracle JDK, etc.).
- After installation, confirm in PowerShell:

```powershell
java -version
javac -version
```

You should see a Java 21.x output.

2) (Optional) Set JAVA_HOME and update PATH

Temporary (for current PowerShell session):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
$env:Path = "$env:JAVA_HOME\\bin;" + $env:Path
```

Permanent (applies after restarting shells):

```powershell
setx JAVA_HOME "C:\Program Files\Java\jdk-21"
```

Adjust the path to match where your JDK 21 is installed.

3) Compile the project (from project root)

Use the helper script to compile all .java files and put classes under `out/`.

```powershell
# compile with the default installed javac
.\scripts\compile.ps1

# or compile targeting Java 21 explicitly (requires javac 21 to be available)
.\scripts\compile.ps1 -Release21
```

The script uses `javac --release 21` when `-Release21` is passed.

4) Run the application

```powershell
.\scripts\run.ps1
```

That will run `br.com.gestorfinanceiro.GestorFinanceiroApp` (the project's main class).

Notes

- I attempted to run the automated Copilot "upgrade plan" tools, but they are restricted to paid Copilot plans and could not be executed here. Because this project has no Maven/Gradle build files, the most straightforward approach is to install JDK 21 locally and compile with `--release 21`.

- If you want, I can:
  - Try to install JDK 21 on your machine automatically (I can attempt it),
  - Or help you convert the project to a simple Maven project and run the automated upgrade recipes (this will create a `pom.xml` and require more work).

Tell me which you'd prefer and I will proceed.
