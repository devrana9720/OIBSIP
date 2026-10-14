# 🚆 RailPlan — Online Reservation System

A Java Swing desktop application that simulates a train reservation workflow, including user registration, login, train search, reservation booking, reservation history, and cancellation using MySQL.

## 📌 Project Overview

**RailPlan** is a desktop-based Online Reservation System developed using Java 21, Java Swing, JDBC, MySQL, and Apache Maven as part of the OIBSIP Internship.

The project demonstrates how a Java desktop application can interact with a relational database to manage reservation records. It includes a graphical user interface, input validation, database operations, and a basic data-access layer.

**Note:** This is an educational simulation. It does not connect to IRCTC or live railway services, issue genuine tickets, process payments, or make real-world bookings.

## ✨ Features

- **User Registration:** Create an account with user details.
- **User Login:** Authenticate users using password-hash verification.
- **Train Search:** Search using source, destination, journey date, and travel class.
- **Sample Train Results:** Display simulated train details, schedules, and fares.
- **Reservation Booking:** Save reservation information in MySQL.
- **Reservation History:** View saved reservations in a table.
- **PNR-Based Lookup:** Find a reservation using its generated reference number.
- **Reservation Cancellation:** Cancel a confirmed reservation with confirmation.
- **Input Validation:** Check journey dates, city selections, and reservation information.
- **Database Connectivity:** Use JDBC and prepared SQL statements.
- **Maven Build:** Manage dependencies and package the application.

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application programming language |
| Java Swing | Desktop graphical user interface |
| Java AWT | GUI layouts, graphics, and styling |
| JDBC | Database connectivity |
| MySQL 8 | Relational database |
| Apache Maven | Build and dependency management |
| JUnit 5 | Unit testing framework |
| Git | Version control |
| GitHub | Source code hosting |
| Visual Studio Code | Development environment |

## 📂 Project Structure

```text
online-reservation-system/
├── .mvn/
│   ├── jvm.config
│   └── maven.config
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/oibsip/
│   │   │       ├── App.java
│   │   │       └── reservation/
│   │   │           ├── dao/
│   │   │           │   ├── ReservationDAO.java
│   │   │           │   └── UserDAO.java
│   │   │           ├── db/
│   │   │           │   ├── DBConnection.java
│   │   │           │   └── DBConnectionTest.java
│   │   │           ├── model/
│   │   │           │   └── Reservation.java
│   │   │           ├── service/
│   │   │           └── ui/
│   │   └── resources/
│   │       └── images/
│   │           └── railplan_bg.png
│   └── test/
│       └── java/
│           └── com/oibsip/
│               └── AppTest.java
└── pom.xml
```

### Folder and File Responsibilities

- **`App.java`** — Application entry point and Swing screens.
- **`reservation/model/`** — Contains the `Reservation` data model.
- **`reservation/dao/`** — Contains database access classes for reservations and users.
- **`reservation/db/`** — Manages database connectivity and includes a connection diagnostic.
- **`resources/images/`** — Stores graphical resources used by the interface.
- **`src/test/java/`** — Contains automated test source code.
- **`.mvn/`** — Stores Maven configuration files.
- **`pom.xml`** — Defines project dependencies and build configuration.
- **`target/`** — Maven-generated build output created during compilation and packaging; not included in the source tree above.

The `service` and `ui` directories are currently empty in the inspected source archive.

## ⚙️ Prerequisites

Install the following before running the application:

- JDK 21
- Apache Maven
- MySQL Server 8
- Git (optional, for cloning the repository)

Verify Java and Maven installations:

```bash
java -version
javac -version
mvn -v
```

## 🗄️ Database Configuration

The application expects a MySQL database named `online_reservation`.

The JDBC URL is:

```text
jdbc:mysql://localhost:3306/online_reservation
```

Configure the following environment variables before launching the application:

- `MYSQL_DB_USER` — Your MySQL username.
- `MYSQL_DB_PASSWORD` — Your MySQL password.

Example for the current PowerShell session:

```powershell
$env:MYSQL_DB_USER = "<your_mysql_username>"
$env:MYSQL_DB_PASSWORD = "<your_mysql_password>"
```

Create the database if it does not already exist:

```sql
CREATE DATABASE online_reservation;
```

**Important:** Creating the database does not create the required tables. The `reservations` and `users` tables must also exist with columns compatible with the SQL used by the DAO classes. A database schema script was not included in the inspected source archive.

Never commit actual database credentials to GitHub.

## 🚀 Build and Run

### 1. Clone the repository

```bash
git clone https://github.com/devrana9720/OIBSIP.git
```

### 2. Navigate to the Maven project

```bash
cd OIBSIP/online-reservation-system
```

Adjust the path if the repository has a different local layout.

### 3. Compile the project

```bash
mvn clean compile
```

### 4. Run tests

```bash
mvn test
```

### 5. Package the application

```bash
mvn package
```

### 6. Launch the application

The Maven configuration sets `com.oibsip.App` as the main class for the packaged JAR. After packaging, inspect the `target` directory for the generated JAR and launch the appropriate artifact:

```bash
java -jar target/<generated-jar-name>.jar
```

Replace `<generated-jar-name>.jar` with the actual filename. Ensure the database is running and the environment variables are configured.

## 🧪 Testing

The project uses JUnit 5 and Maven Surefire for automated testing.

Run the test suite with:

```bash
mvn test
```

The project also includes `DBConnectionTest.java`, a standalone diagnostic class for checking database connectivity.

The current `AppTest.java` contains a basic placeholder assertion. Comprehensive automated tests for booking, login, reservation retrieval, and cancellation can be added in future improvements.

## 🔄 Application Workflow

1. Launch the desktop application.
2. Register a user account or log in.
3. Open the train search screen.
4. Enter the source, destination, journey date, and travel class.
5. Review the simulated train results.
6. Enter the required passenger details and submit a reservation.
7. Store the reservation in MySQL.
8. View saved reservations and their status.
9. Look up a reservation using its PNR reference.
10. Confirm cancellation to change a confirmed reservation's status to `CANCELLED`.

## 🔐 Security Considerations

- Database credentials are read from environment variables.
- SQL values are passed through prepared statements.
- User passwords are stored using salted PBKDF2-HMAC-SHA256 password hashing.
- Database credentials should never be committed to source control.
- This project is an educational application and has not undergone a production security audit.

## 🔮 Future Improvements

- Improve the Swing interface and accessibility.
- Add more comprehensive automated and integration tests.
- Add reservation search, filtering, and sorting.
- Improve validation and user-facing error messages.
- Document and version database schema changes.
- Add simulated flight and bus search modules.
- Improve reservation history and cancellation auditing.
- Refactor the application into smaller UI and service classes.

These are potential improvements, not currently claimed features.

## 🎯 Learning Outcomes

This project provides practical experience with:

- Java object-oriented programming.
- Desktop GUI development using Swing.
- Event-driven programming.
- JDBC and relational database operations.
- DAO and model design patterns.
- SQL queries and prepared statements.
- Maven dependency management and packaging.
- JUnit testing fundamentals.
- Git and GitHub version control.

## 👨‍💻 Developer

**Dev Rana**

- GitHub: [devrana9720](https://github.com/devrana9720)
- Repository: [OIBSIP — Online Reservation System](https://github.com/devrana9720/OIBSIP)

---

**Project:** Online Reservation System (RailPlan)  
**Language:** Java 21  
**Type:** Desktop Application  
**Purpose:** Educational project and internship demonstration

