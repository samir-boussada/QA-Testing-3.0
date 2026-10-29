# 🐱 Apache Tomcat Localhost Deployment Guide — ZaraStyle

The **ZaraStyle Fashion Inventory Platform** is configured with `<packaging>war</packaging>` and extends Spring Boot's `SpringBootServletInitializer`, making it deployable on **Apache Tomcat** both as an embedded server and as a standalone `.war` file in any Apache Tomcat container.

---

## Method 1: Run with Embedded Tomcat (Fastest — 1 Click)

You don't need to manually install Apache Tomcat; Spring Boot includes an embedded Tomcat 10 container:

```powershell
cd C:\Users\Samir\.gemini\antigravity\scratch\zara-fashion-inventory
mvn spring-boot:run
```
*Access in browser:* `http://localhost:8080/`

---

## Method 2: Deploy to a Standalone Apache Tomcat Server

If you have a local installation of Apache Tomcat (Tomcat 10 or 10.1+ supporting Jakarta EE 10 / Servlet 6.0):

### 1. Build the Production `.WAR` Archive
```powershell
cd C:\Users\Samir\.gemini\antigravity\scratch\zara-fashion-inventory
mvn clean package -DskipTests
```
*The packaged WAR file is generated at:*
`target/zara-system.war`

### 2. Copy the `.WAR` to Tomcat `webapps/` Folder
Copy `zara-system.war` into your Tomcat installation directory:
```powershell
# Example Windows path:
Copy-Item "target\zara-system.war" "C:\apache-tomcat-10.1.x\webapps\zara-system.war"
```

### 3. Start Apache Tomcat
* **On Windows:** Double click `bin\startup.bat` (or run it from PowerShell).
* **On Linux / Mac:** Run `./bin/startup.sh`.

Tomcat will automatically unpack `zara-system.war` and deploy the servlet.

### 4. Access the Application
Open your browser and navigate to:
```
http://localhost:8080/zara-system/
```
* Transfers Portal: `http://localhost:8080/zara-system/transfers.html`
* H2 Database Console: `http://localhost:8080/zara-system/h2-console`
