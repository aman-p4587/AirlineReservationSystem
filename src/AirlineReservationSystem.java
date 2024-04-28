import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class AirlineReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/airline_reservation_system";
    private static final String username = "root";
    private static final String password = "Felicity@4587";
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Statement statement = connection.createStatement();
            Scanner sc = new Scanner(System.in);
            while(true){
                System.out.println();
                System.out.println("AIRLINE RESERVATION SYSTEM");
                System.out.println("1. Check all flight timings and their destinations");
                System.out.println("2. Book Ticket");
                System.out.println("3. Get Ticket");
                System.out.println("4. See who all are travelling at a particular time");
                System.out.println("5. See who all are travelling from a particular city");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                switch(choice){
                    case 1:
                        checkAllFlights(statement);
                        break;
                    case 2:
                        bookTicket(statement,sc);
                        break;
                    case 3:
                        viewTicket(statement,sc);
                        break;
                    case 4:
                        checkTime(statement,sc);
                        break;
                    case 5:
                        checkCity(statement,sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice!! Try again.");
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    public static void checkAllFlights(Statement statement) throws SQLException{
        String query = "SELECT * FROM flight_db";
        try(ResultSet resultSet = statement.executeQuery(query)){
            System.out.println("All the Flights Schedule:");
            System.out.println("+-----------------+------------+--------------+------------+-----------+");
            System.out.println("|   Flight name   | flight from|  flight to   |  Dep. time | Arr. time |");
            System.out.println("+-----------------+------------+--------------+------------+-----------+");
        while (resultSet.next()){
                String fl_name = resultSet.getString("flight_name");
                String fl_from = resultSet.getString("fl_from");
                String fl_to = resultSet.getString("fl_to");
                String dep_time = resultSet.getString("departure_time");
                String arr_time = resultSet.getString("arrival_time");

                System.out.printf("| %-15s | %-10s | %-12s | %-10s | %-10s|\n",fl_name,fl_from,fl_to,dep_time,arr_time);
            }
                System.out.println("+-----------------+------------+--------------+------------+-----------+");
        }catch(SQLException e){
            e.getStackTrace();
        }
    }
    public static void bookTicket(Statement statement,Scanner sc) throws SQLException {
        System.out.println("*****OFFER: BOOKING 5 OR MORE TICKETS WILL GET YOU 10% DISCOUNT*****");

        System.out.println("Where you wish travel from: ");
        String fl_from = sc.next();
        System.out.println("Where you wish travel to: ");
        String fl_to = sc.next();
        int if_available = checkAvailability(statement,fl_from,fl_to);
        if(if_available<0){
            System.out.println("Sorry No flight is Scheduled for this route!!");
            return ;
        }

        System.out.print("How many ticket you wish to book:");
        int no_of_ticket = sc.nextInt();
        int passenger = 0;
        int ticket_cost=0;
        String fl_name = "";
        String dep_time ="";
        String arr_time ="";

        String query = "SELECT * FROM flight_db WHERE flno = "+if_available;
        try(ResultSet resultSet = statement.executeQuery(query)){
            if(resultSet.next()){
                fl_name = resultSet.getString("flight_name");
                dep_time = resultSet.getString("departure_time");
                arr_time = resultSet.getString("arrival_time");
                ticket_cost = resultSet.getInt("fl_cost");
            }else{
                System.out.println("no flight match found");
            }
        }

        while(no_of_ticket!=0){
            passenger++;
            System.out.println("============Enter Passenger"+passenger+" details============");
            System.out.print("Enter first name: ");
            String first_name = sc.next();
            System.out.print("Enter last name: ");
            String last_name = sc.next();
            System.out.print("Enter Date of Birth (format: yyyy/mm/dd): ");
            String dob = sc.next();
            System.out.print("Enter Age: ");
            int age = sc.nextInt();
            System.out.print("Enter sex (M/F): ");
            String sex = sc.next();
            System.out.print("Enter nationality: ");
            String nationality = sc.next();

            query = "INSERT INTO ticket_table(name,dob,age,sex,nationality,fl_no,fl_name,fl_from,fl_to,dep_time,arr_time,ticket_cost) VALUES" +
                    "('"+first_name+" "+last_name+"','"+dob+"',"+age+",'"+sex+"','"+nationality+"',"+if_available+
                    ",'"+fl_name+"','"+fl_from+"','"+fl_to+"','"+dep_time+"','"+arr_time+"',"+ticket_cost+")";

            try{
                statement.executeUpdate(query);
                System.out.println("Booking Confirmed!!!");

                System.out.println("=================================================");
            }catch(SQLException e){
                e.getStackTrace();
            }
            no_of_ticket--;
        }

    }
    public static void viewTicket(Statement statement, Scanner sc){
        try{
            System.out.print("Enter your first name: ");
            String first_name = sc.next();
            System.out.print("Enter your last name: ");
            String last_name = sc.next();
            System.out.print("Enter your D.O.B (format: yyyy-mm-dd) : ");
            String dob = sc.next();

            String query = "SELECT * FROM ticket_table WHERE name='"+first_name+" "+last_name+"' AND dob = '"+dob+"'";
            try(ResultSet resultSet = statement.executeQuery(query)){

                if(resultSet.next()){
                    String name = resultSet.getString("name");
                    String date_of_birth = String.valueOf(resultSet.getDate("dob"));
                    int age = resultSet.getInt("age");
                    String sex = resultSet.getString("sex");
                    String nationality = resultSet.getString("nationality");
                    int fl_no = resultSet.getInt("fl_no");
                    int price = resultSet.getInt("ticket_cost");
                    query = "SELECT * FROM flight_db WHERE flno = "+fl_no;
                    try(ResultSet resultSet2 = statement.executeQuery(query)){
                        if(resultSet2.next()){
                            String fl_name = resultSet2.getString("flight_name");
                            String fl_from = resultSet2.getString("fl_from");
                            String fl_to = resultSet2.getString("fl_to");
                            String dep_time = resultSet2.getString("departure_time");
                            String arr_time = resultSet2.getString("arrival_time");
                            System.out.println("\t\t\t\t\t===================YOUR TICKET DETAILS====================");
                            System.out.println("+-----------------+---------------+--------+------+-------------+-----------------+------------+--------------+------------+-----------+---------------+");
                            System.out.println("|      Name       | Date of Birth |  Age   |  Sex | Nationality |   Flight name   | flight from|  flight to   |  Dep. time | Arr. time |  Ticket Price |");
                            System.out.println("+-----------------+---------------+--------+------+-------------+-----------------+------------+--------------+------------+-----------+---------------+");
                            System.out.printf("| %-15s | %-13s | %-6s | %-4s | %-11s | %-15s | %-10s | %-12s | %-10s | %-10s| %-13d |\n", name, date_of_birth, age, sex, nationality,fl_name,fl_from,fl_to,dep_time,arr_time,price);
                            System.out.println("+-----------------+---------------+--------+------+-------------+-----------------+------------+--------------+------------+-----------+---------------+");

                        }
                    }
                } else{
                    System.out.println("Reservation not found for the given ID");
                }
            }
        }catch(SQLException e){
            e.getStackTrace();
        }
    }
    public static void checkTime(Statement statement,Scanner sc){
        System.out.print("Enter the time(format: HH:MM[am/pm] | eg->07:00pm,09:00am)  : ");
        String time = sc.next();
        String query = "SELECT * FROM ticket_table where dep_time = '"+time+"'";
        System.out.println("=================LIST OF THE PASSENGERS===============");
        System.out.println("+-----------------+--------+------+-------------+-----------------+------------+------------+--------------+");
        System.out.println("|      Name       |  Age   |  Sex | Nationality |   Flight name   |  Dep. time | flight from|  flight to   |");
        System.out.println("+-----------------+--------+------+-------------+-----------------+------------+------------+--------------+");
        try(ResultSet resultSet = statement.executeQuery(query)){

            while(resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String sex = resultSet.getString("sex");
                String nationality = resultSet.getString("nationality");
                String fl_name = resultSet.getString("fl_name");
                String dep_time = resultSet.getString("dep_time");
                String dep_city = resultSet.getString("fl_from");
                String arr_city = resultSet.getString("fl_to");
                System.out.printf("| %-15s | %-6s | %-4s | %-11s | %-15s | %-10s | %-12s | %-10s |\n",name,age,sex,nationality,fl_name,dep_time,dep_city,arr_city);
                System.out.println("+-----------------+--------+------+-------------+-----------------+------------+------------+--------------+");
            }
        }catch(SQLException e){
            e.getStackTrace();
        }
    }
    public static void checkCity(Statement statement,Scanner sc){
        System.out.print("Enter city name: ");
        String city = sc.next();
        String query = "SELECT * FROM ticket_table where fl_from = '"+city+"'";
        System.out.println("=================LIST OF THE PASSENGERS===============");
        System.out.println("+-----------------+--------+------+-------------+-----------------+------------+------------+");
        System.out.println("|      Name       |  Age   |  Sex | Nationality |   Flight name   |  Dep. time | flight to  |");
        System.out.println("+-----------------+--------+------+-------------+-----------------+------------+------------+");
        try(ResultSet resultSet = statement.executeQuery(query)){

            while(resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String sex = resultSet.getString("sex");
                String nationality = resultSet.getString("nationality");
                String fl_name = resultSet.getString("fl_name");
                String dep_time = resultSet.getString("dep_time");
                String dep_city = resultSet.getString("fl_to");
                System.out.printf("| %-15s | %-6s | %-4s | %-11s | %-15s | %-10s | %-10s |\n",name,age,sex,nationality,fl_name,dep_time,dep_city);
                System.out.println("+-----------------+--------+------+-------------+-----------------+------------+------------+");
            }
        }catch(SQLException e){
            e.getStackTrace();
        }
    }
    public static int checkAvailability(Statement statement,String fl_from,String fl_to){
        String query = "SELECT * FROM flight_db WHERE fl_from = '"+fl_from+ "' AND fl_to = '"+fl_to+"'";
        try(ResultSet resultSet = statement.executeQuery(query)){
            if(resultSet.next()){
                return resultSet.getInt("flno");
            }
            return -1;
        }catch(SQLException e){
            e.getStackTrace();
            return -1;
        }
    }
    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i=5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank you For Using Our Airline Reservation System!!");
    }
}
