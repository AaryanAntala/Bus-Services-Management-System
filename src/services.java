import java.sql.*;
import java.util.Scanner;

public class services { 

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/bus_services";

    static final String USER = "root";
    static final String PASSWORD = "123456";
    public static void main(String[] args) throws Exception {
        Connection conn = null;
        Statement stmt= null;
        Scanner scanner = new Scanner(System.in);

        try{
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASSWORD);
            conn.setAutoCommit(false);
            boolean queries = true;
            
            while(queries){
                System.out.print("Are you a passenger or conductor :");
                String user = scanner.nextLine();

                if(user.equals("passenger")){
                    System.out.print("Enter bus_id:");
                    String bus_id = scanner.nextLine();
                    System.out.print("Enter passenger_id:");
                    String passenger_id = scanner.nextLine();
                    System.out.print("Enter seat_id:");
                    String seat_id = scanner.nextLine();
                    //now we will check if the given details are valid or not
                    String check = String.format("""
                        SELECT bus_id,passenger_id,seat_id FROM booking WHERE bus_id='%s' AND passenger_id='%s' AND seat_id='%s'
                        """,bus_id,passenger_id,seat_id);
                    
                    stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(check);
                    if(rs.next()){
                        //if deatils are valid, we will show him the availabe services option
                        System.out.println("Select the service :");
                        System.out.println("1. Order Meal\n2. Order Linen\n3. Upgrade Seat");
                        System.out.print("Enter the service id you wish to avail:");
                        int service = scanner.nextInt();
                        scanner.nextLine(); 
                        if(service==1){
                            //we will first show the passenger the available meal options
                            System.out.println("Select the meal option you want to order:");
                            String get_meals = "select * from meals where stock>0 ";
                            ResultSet rs1 = stmt.executeQuery(get_meals);
                            System.out.printf("%-5s %-25s %-10s %-10s%n", "ID", "Meal Name", "Price", "Stock");
                            System.out.println("----------------------------------------------------------");
                            while (rs1.next()) {
                                String meal_id = rs1.getString("meal_id");
                                String meal_name = rs1.getString("meal_name");
                                String meal_price = rs1.getString("meal_price");
                                String meal_stock = rs1.getString("stock");

                                System.out.printf("%-5s %-25s %-10s %-10s%n", meal_id, meal_name, meal_price, meal_stock);
                            }
                            System.out.print("Enter the meal id you want to order:");
                            String meal_id = scanner.nextLine();
                            System.out.print("Enter the quantity you want to order:");
                            int quantity = scanner.nextInt();
                            scanner.nextLine();
                            String insert_order = String.format("""
                                insert into orders (bus_id,passenger_id,seat_id, meal_id,quantity) values ('%s','%s','%s', '%s','%d')"""
                                ,bus_id,passenger_id,seat_id,meal_id,quantity);
                            stmt.executeUpdate(insert_order);

                            String check_stock = String.format("""
                                SELECT stock FROM meals WHERE meal_id='%s'
                                """, meal_id);
                            ResultSet rs2 = stmt.executeQuery(check_stock);

                            if (rs2.next()) {
                                int stock = rs2.getInt("stock");
                                if (quantity > stock) {
                                    System.out.println("Quantity exceeds available stock. Rolling back transaction...");
                                    conn.rollback(); // Rollback the transaction
                                    continue;
                                } 
                                else {
                                    // Update the stock of the meal
                                    String update_stock = String.format("""
                                        update meals set stock=stock-%d WHERE meal_id='%s'
                                        """, quantity, meal_id);
                                    stmt.executeUpdate(update_stock);
                                    System.out.println("Order placed successfully.");
                                }
                            } 
                            else {
                                System.out.println("Invalid meal id, please try again.");
                                conn.rollback(); // Rollback the transaction if meal_id is invalid
                                continue;
                            }
                        }
                        else if(service==2){
                            //we will first show if the linen are available or not
                            String get_linen = String.format("select * from linen where bus_id=%s and linen_stock>0",bus_id);
                            ResultSet rs3 = stmt.executeQuery(get_linen);
                            if(rs3.next()){
                                //we had already asked the bus_id, seat_id and passenger_id, so we will just edit the order table
                                //if an entry didn't already exist, otherwise we will just update the linen field
                                String check_order = String.format("""
                                    select order_id, status, linen from orders where bus_id='%s' and passenger_id='%s' and seat_id='%s'
                                    """, bus_id, passenger_id, seat_id);

                                ResultSet rs4 = stmt.executeQuery(check_order);

                                if (rs4.next()) {
                                    boolean linen = rs4.getBoolean("linen");
                                    if (linen) {
                                        // If linen is already ordered, notify the customer and prevent further action
                                        System.out.println("Linen has already been ordered for this seat. You cannot order it again.");
                                        continue;
                                    } 
                                    else {
                                        String status = rs4.getString("status");
                                        if (status.equals("pending")) {
                                            // If order is pending and linen is not ordered yet, update the order table
                                            String order_id = rs4.getString("order_id");
                                            String update_order = String.format("""
                                                update orders set linen=true where order_id='%s'
                                                """, order_id);
                                            stmt.executeUpdate(update_order);
                                            System.out.println("Linen ordered successfully.");
                                            String update_linen = String.format("""
                                                update linen set linen_stock=linen_stock-1 where bus_id='%s'
                                                """, bus_id);
                                            stmt.executeUpdate(update_linen);
                                        } else if (status.equals("completed")) {
                                            // If order is completed and linen is not ordered yet, add a new entry in the order table
                                            String insert_order = String.format("""
                                                insert into orders (bus_id,passenger_id,seat_id,linen) values ('%s','%s','%s',1)
                                                """, bus_id, passenger_id, seat_id);
                                            stmt.executeUpdate(insert_order);
                                            System.out.println("Linen ordered successfully.");
                                            String update_linen = String.format("""
                                                update linen set linen_stock=linen_stock-1 where bus_id='%s'
                                                """, bus_id);
                                            stmt.executeUpdate(update_linen);
                                        }
                                    }
                                } 
                                else {
                                    // If no existing order, add a new entry in the order table
                                    String insert_order = String.format("""
                                        insert into orders (bus_id,passenger_id,seat_id,linen) values ('%s','%s','%s',1)
                                        """, bus_id, passenger_id, seat_id);
                                    stmt.executeUpdate(insert_order);
                                    System.out.println("Linen ordered successfully.");
                                    String update_linen = String.format("""
                                        update linen set linen_stock=linen_stock-1 where bus_id='%s'
                                        """, bus_id);
                                    stmt.executeUpdate(update_linen);
                                }
                                
                            }
                            else{
                                System.out.println("All linen are booked, Sorry for the inconvenience.");
                                continue;
                            }
                        }
                        else if(service==3){
                            //we will first show the passenger the available seat options
                            String get_seats = String.format("""
                                    select * from seats where bus_id='%s' order by seat_id
                                    """,bus_id);
                            ResultSet rs5 = stmt.executeQuery(get_seats);
                            System.out.println("The following are the seats in bus "+bus_id+" :");
                            System.out.printf("%-10s %-15s%n", "Seat ID", "Booking Status");
                            System.out.println("----------------------------------------------------------");
                            while(rs5.next()){
                                String seat_id1 = rs5.getString("seat_id");
                                String booking_status = rs5.getString("booking_status");
                                System.out.println(seat_id1+" "+booking_status);
                            }
                            System.out.print("Enter the seat id you want to upgrade to:");
                            String new_seat_id = scanner.nextLine();
                            //now we will check if the given seat id is valid or not
                            String check_seat = String.format("""
                                    select seat_id from seats where bus_id='%s' AND seat_id='%s' and booking_status='available'
                                    """,bus_id,new_seat_id);
                            ResultSet rs6 = stmt.executeQuery(check_seat);
                            if(rs6.next()){
                                //update the booking status of old seat to available 
                                //and new seat to booked
                                String update_old_seat = String.format("""
                                        update seats set booking_status='available' where bus_id='%s' and seat_id='%s'
                                        """,bus_id,seat_id);
                                stmt.executeUpdate(update_old_seat);
                                String update_new_seat = String.format("""
                                        update seats set booking_status='booked' where bus_id='%s' and seat_id='%s'
                                        """,bus_id,new_seat_id);
                                stmt.executeUpdate(update_new_seat);
                                //update the booking table with new seat id
                                String update_booking = String.format("""
                                        update booking set seat_id='%s' where bus_id='%s' and passenger_id='%s' and seat_id='%s'
                                        """,new_seat_id,bus_id,passenger_id,seat_id);
                                stmt.executeUpdate(update_booking);
                                System.out.println("Seat upgraded successfully.");
                            }
                            else{
                                System.out.println("The seat is not available, please try again.");
                                continue;
                            }
                        }
                        else{
                            System.out.println("Invalid service id, please try again.");
                            continue;
                        }
                    }
                    else{
                        System.out.println("Invalid details, please try again.");
                        continue;
                    }
                }
                else if(user.equals("conductor")){
                    //we will show the pending orders to the conductor by asking him his bus_id
                    System.out.print("Enter bus_id:");
                    String bus_id = scanner.nextLine();
                    //check if the bus_id is valid or not
                    String check_bus = String.format("""
                        select bus_id from seats where bus_id='%s'
                        """,bus_id);
                    stmt = conn.createStatement();
                    ResultSet rs7 = stmt.executeQuery(check_bus);
                    if(rs7.next()){
                        //if bus_id is valid, we will show the pending orders to the conductor
                        String get_orders = String.format("""
                            select * from orders where bus_id='%s' and status='pending'
                            """,bus_id);
                        
                        boolean serve = true;
                        while(serve){
                            ResultSet rs8 = stmt.executeQuery(get_orders);
                            System.out.println("The following are the pending orders for bus " + bus_id + " :");
                            System.out.printf("%-10s %-15s %-10s %-10s %-10s %-10s%n", "Order ID", "Passenger ID", "Seat ID", "Meal ID", "Quantity", "Linen");
                            System.out.println("--------------------------------------------------------------------------");

                            while (rs8.next()) {
                                String order_id = rs8.getString("order_id");
                                String passenger_id = rs8.getString("passenger_id");
                                String seat_id = rs8.getString("seat_id");
                                String meal_id = rs8.getString("meal_id");
                                String quantity = rs8.getString("quantity");
                                String linen = rs8.getString("linen");

                                System.out.printf("%-10s %-15s %-10s %-10s %-10s %-10s%n", order_id, passenger_id, seat_id, meal_id, quantity, linen);
                            }
                            System.out.print("Do you want to serve any order? (yes/no)");
                            String serve_order = scanner.nextLine();
                            if(serve_order.equals("yes")){
                                System.out.print("Enter order_id:");
                                String order_id = scanner.nextLine();
                                //check if the order_id is valid or not
                                String check_order = String.format("""
                                    select order_id from orders where order_id='%s' and bus_id='%s'
                                    """,order_id,bus_id);
                                ResultSet rs9 = stmt.executeQuery(check_order);
                                if(rs9.next()){
                                    //if order_id is valid, we will update the status of the order to completed
                                    String update_order = String.format("""
                                        update orders set status='completed' where order_id='%s'
                                        """,order_id);
                                    stmt.executeUpdate(update_order);
                                    System.out.println("Order "+order_id+" served successfully.");
                                    //we will remove the order from the orders table
                                    String delete_order = String.format("""
                                        delete from orders where order_id='%s' and linen=false
                                        """,order_id);
                                    stmt.executeUpdate(delete_order);
                                    conn.commit();
                                }
                                else{
                                    System.out.println("Invalid order id, please try again.");
                                    continue;
                                }
                            }
                            else{
                                serve = false;
                            }
                        }
                    }
                    else{
                        System.out.println("Invalid bus id, please try again.");
                        continue;
                    }
                }
                else{
                    System.out.println("Invalid user type, please try again.");
                }
                conn.commit();
                System.out.println("Commit successful!");
                System.out.print("Do you want to try again? (yes/no)");
                String continue_query = scanner.nextLine();
                if(continue_query.equals("no")){
                    queries = false;
                }
            }
           
            stmt.close();
            conn.close();
            scanner.close();
        }
        catch(SQLException se){
            try{
                if(conn!=null){
                    conn.rollback();
                    System.out.println("Failed and Rolled back!");
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
            se.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(stmt!=null)
                    stmt.close();
            }
            catch(SQLException se2){
            }
            try{
                if(conn!=null){
                    conn.close();
                }
            }
            catch(SQLException se){
                se.printStackTrace();
            }
        }
    }
}
