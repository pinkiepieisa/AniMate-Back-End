# How to run the AniMate backend (VS Code)

This file contains quick steps and VS Code tasks to run the backend locally on Windows.

Prerequisites
- Java (JDK) installed and on PATH (run `java -version`)
- Either Maven installed or the project contains a Maven wrapper (`mvnw.cmd` and `.mvn\wrapper`)

Recommended: open the folder `AniMate-Back-End/backend/backend` in VS Code (File → Open Folder...).

VS Code tasks
- Open Terminal → Run Task... (or press Ctrl+Shift+P and choose "Tasks: Run Task").

Available tasks (created for you):
- Run backend (mvnw) — runs `mvnw.cmd spring-boot:run` in the backend folder.
- Run backend (mvn) — runs `mvn spring-boot:run` (use only if `mvn` is installed).
- Package and run JAR — builds with the wrapper and executes the generated jar.
- Tail backend log (PowerShell) — tails `logs/backend.log` in a dedicated terminal.

Notes and troubleshooting
- If the Maven wrapper is missing you can generate it with a local Maven install:

  mvn -N io.takari:maven:wrapper

  Or install Maven and use the "Run backend (mvn)" task.

- If you edited `secrets.properties`, restart the backend task (Stop then Run) — Spring reads the file on startup.

- If you hit permission errors running `mvnw.cmd`, try running the terminal as Administrator once and/or unblock the file from Explorer (right-click → Properties → Unblock).

Debugging in VS Code
- With the Java extension pack installed, open `src/main/java/com/animate/backend/BackendApplication.java` and click the Run/Debug code lens above the `main` method. Or use the "Launch BackendApplication" entry in the Run view (uses `.vscode/launch.json`).

If anything fails, copy the terminal output and the last 100 lines of `logs/backend.log` here and I'll diagnose the error.
