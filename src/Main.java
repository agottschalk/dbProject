import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Main {
    public static void main(String[] args) {
        final String URL = "jdbc:mysql://localhost:3306/ics311";
        final String USERNAME = "java";
        final String PASSWORD = "javapass";

        MysqlDataSource source = new MysqlDataSource();
        source.setUser(USERNAME);
        source.setPassword(PASSWORD);
        source.setUrl(URL);

        System.out.println("Connecting to database...");

        try (Connection conn = source.getConnection()) {
            System.out.println("Connected!\n");


            //Report 1  ---------------------------------------------------
            //all fencers from club "BLADEARTS"
            printReportHeader("Report 1");

            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT " +
                            "fen_first_name, " +
                            "fen_last_name, " +
                            "fen_club, " +
                            "fen_hand, " +
                            "fen_rating_letter, " +
                            "fen_rating_year " +
                        "FROM fencer " +
                        "WHERE fen_club = \"BLADEARTS\";");


            System.out.printf("%n%-10s %-12s %-9s %-5s %s", "FIRST NAME", "LAST NAME", "CLUB", "HAND", "RATING");
            while(!rs.isLast()){
                rs.next();
                System.out.printf("%n%-10s %-12s %-9s %-5s %s %tY",
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getDate(6));
            }


            //Report 2  -------------------------------------------------------------



            //Report 3  ------------------------------------------------------------
            //all notes on fencer, specified by user input

            System.out.println("\nExiting database.");

        } catch (SQLException e) {
            System.out.println("Uh-oh, something went wrong.");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints a report header to the console with the format:
     * <p>
     * Name: Alex Gottschalk
     * <code>reportName</code>
     * Date: <code>current system date</code>
     * @param reportName displayed name of report
     */
    static void printReportHeader(String reportName){
        System.out.printf("%nName: Alex Gottschalk%n%s%nDate: %s%n",
                reportName, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}