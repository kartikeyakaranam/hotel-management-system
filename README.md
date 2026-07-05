# 🏨 Hotel Management System

A JavaFX desktop application for managing hotel rooms, customers, bookings, and billing.
Built with **JavaFX 21**, **PostgreSQL (JDBC)**, **Maven**, and **Scene Builder FXML**.

---

## Tech Stack

| Layer       | Technology              |
|-------------|-------------------------|
| UI          | JavaFX 21 + Scene Builder FXML |
| Styling     | Custom Dark CSS Theme   |
| Database    | PostgreSQL via JDBC     |
| Build Tool  | Maven                   |
| Language    | Java 17                 |

---

## Features

- **Room Management** — Add, update, delete rooms; filter by availability
- **Customer Management** — Add, update, delete customers
- **Booking Management** — Book rooms, checkout guests, auto-update availability
- **Billing** — Generate bills with 12% GST, view billing history
- **CSV Export** — Export all bills to a CSV file
- **Persistent Storage** — All data stored in PostgreSQL via JDBC

---

## Prerequisites

1. **Java 17+** — [Download](https://adoptium.net)
2. **Maven 3.8+** — [Download](https://maven.apache.org)
3. **PostgreSQL 14+** — [Download](https://www.postgresql.org/download/)
4. **IntelliJ IDEA** (recommended) with JavaFX plugin

---

## Database Setup

1. Open **pgAdmin** or **psql** and run:

```sql
CREATE DATABASE hotel_db;
```

2. The application **automatically creates all tables** on first launch.
   No manual schema import needed.

3. *(Optional)* If your PostgreSQL username/password differs from `postgres/postgres`,
   update `src/main/java/com/hotel/db/DatabaseConnection.java`:

```java
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

---

## Running in IntelliJ IDEA

1. **Open** the project: `File → Open → select the HMS folder`
2. **Wait** for Maven to download dependencies (first run may take a minute)
3. **Run** the app:
   - Right-click `MainApp.java` → **Run 'MainApp'**
   - OR via Maven: `mvn javafx:run` in the terminal

---

## Running via Maven (terminal)

```bash
cd HMS
mvn clean javafx:run
```

---

## Project Structure

```
HMS/
├── pom.xml
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── com/hotel/
    │       ├── MainApp.java              ← Entry point
    │       ├── db/
    │       │   └── DatabaseConnection.java
    │       ├── model/
    │       │   ├── Room.java
    │       │   ├── Customer.java
    │       │   ├── Booking.java
    │       │   └── Bill.java
    │       ├── dao/
    │       │   ├── RoomDAO.java
    │       │   ├── CustomerDAO.java
    │       │   ├── BookingDAO.java
    │       │   └── BillDAO.java
    │       ├── controller/
    │       │   ├── MainController.java
    │       │   ├── RoomController.java
    │       │   ├── CustomerController.java
    │       │   ├── BookingController.java
    │       │   └── BillingController.java
    │       └── util/
    │           └── AlertUtil.java
    └── resources/com/hotel/
        ├── fxml/
        │   ├── MainView.fxml
        │   ├── RoomView.fxml
        │   ├── CustomerView.fxml
        │   ├── BookingView.fxml
        │   └── BillingView.fxml
        └── css/
            └── dark-theme.css
```

---

## Rubric Coverage

| Criterion                        | Status |
|----------------------------------|--------|
| Basic System (Rooms + Bookings)  | ✅     |
| GUI design with styled layouts   | ✅ Dark Modern Theme |
| Scene Builder & FXML components  | ✅     |
| Permanent storage via JDBC       | ✅ PostgreSQL |
| Billing management               | ✅ with 12% GST |
| CSV Export                       | ✅     |
| Maven build tool                 | ✅     |

---

## Marking Scheme

- Basic System → **5M**
- GUI design + Additional features → **5M**
