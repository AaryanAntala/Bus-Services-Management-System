# Bus Services Management System

A Java-based database application for managing bus services, including meal orders, linen services, and seat upgrades. This system provides separate interfaces for passengers and conductors to interact with the bus service database.

## Features

### For Passengers
- **Order Meals**: Browse available meals and place orders with quantity selection
- **Request Linen**: Order linen services for your seat (one-time per booking)
- **Upgrade Seats**: Change to a different available seat on the bus

### For Conductors  
- **View Pending Orders**: See all pending meal and linen orders for a specific bus
- **Serve Orders**: Mark orders as completed and remove completed meal orders

## Database Schema

The system uses MySQL database with the following tables:

- **meals**: Stores meal information (ID, name, category, price, stock)
- **orders**: Tracks passenger orders (meals, linen, status)
- **linen**: Manages linen stock per bus
- **seats**: Bus seat information and booking status
- **booking**: Passenger booking records

## Prerequisites

- Java 8 or higher
- MySQL Server
- MySQL Connector/J 8.0.32 (included in `lib/` directory)

## Database Setup

1. Create a MySQL database named `bus_services`
2. Run the SQL scripts in the following order:
   ```bash
   mysql -u root -p bus_services < sql/createTables.sql
   mysql -u root -p bus_services < sql/alterTables.sql
   mysql -u root -p bus_services < sql/bookingTrigger.sql
   mysql -u root -p bus_services < sql/populateTables.sql
   ```

## Configuration

Update the database connection details in `src/services.java`:
```java
static final String DB_URL = "jdbc:mysql://localhost:3306/bus_services";
static final String USER = "your_username";
static final String PASSWORD = "your_password";
```

## Compilation and Execution

### Using Command Line

1. **Compile the Java program:**
   ```bash
   javac -cp "lib/mysql-connector-j-8.0.32.jar:src" src/services.java -d bin/
   ```

2. **Run the application:**
   ```bash
   java -cp "lib/mysql-connector-j-8.0.32.jar:bin" services
   ```

### Using IDE
1. Add `lib/mysql-connector-j-8.0.32.jar` to your project's classpath
2. Compile and run `src/services.java`

## Usage

### Passenger Interface
1. Select "passenger" when prompted
2. Enter your booking details (bus ID, passenger ID, seat ID)
3. Choose from available services:
   - **Order Meal (1)**: View menu, select meals, and specify quantities
   - **Order Linen (2)**: Request linen service for your seat
   - **Upgrade Seat (3)**: View available seats and upgrade

### Conductor Interface
1. Select "conductor" when prompted
2. Enter the bus ID you're serving
3. View pending orders for your bus
4. Choose to serve orders by entering the order ID
5. Orders are automatically marked as completed

## Transaction Management

The application uses database transactions with proper rollback mechanisms:
- **Auto-commit is disabled** for better transaction control
- **Automatic rollback** occurs on errors (e.g., insufficient stock)
- **Manual commits** after successful operations
- **Proper cleanup** of database connections in finally blocks

## Key Features

- **Stock Management**: Automatic stock updates when meals are ordered
- **Duplicate Prevention**: Prevents multiple linen orders for the same seat
- **Seat Availability**: Real-time seat status updates during upgrades
- **Order Tracking**: Complete order lifecycle from pending to completed
- **Data Integrity**: Foreign key constraints and triggers maintain consistency


## Error Handling

- **Database Connection Errors**: Proper exception handling with rollback
- **Invalid Input Validation**: Checks for valid IDs and availability
- **Stock Management**: Prevents ordering more than available stock
- **Transaction Safety**: Ensures data consistency during operations


