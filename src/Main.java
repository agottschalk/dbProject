import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        final String URL = "jdbc:mysql://localhost:3306/ics311";
        final String USERNAME = "java";
        final String PASSWORD = "javapass";

        String r1Query = "SELECT fen_first_name, " +
                "fen_last_name, " +
                "fen_club, " +
                "fen_rating_letter, " +
                "fen_rating_year " +
                "FROM fencer " +
                "WHERE fen_id in (" +
                "(SELECT fen_id_left FROM tournament_match " +
                "WHERE eve_id = 3001)) " +
                "OR fen_id in (" +
                "(SELECT fen_id_right FROM tournament_match " +
                "WHERE eve_id = 3001));";

        String r2Query = "SELECT tournament_match.mat_id, mat_round, mat_type, " +
                    "r.fen_id AS r_id, r.fen_first_name AS right_fname, r.fen_last_name AS right_lname, " +
                    "l.fen_id AS l_id, l.fen_first_name AS left_fname, l.fen_last_name AS left_lname, " +
                    "mat_right_touches, mat_left_touches, referee.ref_first_name, referee.ref_last_name " +
                "FROM tournament_match " +
                    "INNER JOIN referee ON referee.ref_id = tournament_match.ref_id " +
                    "INNER JOIN fencer r ON r.fen_id = tournament_match.fen_id_right " +
                    "INNER JOIN fencer l ON l.fen_id = tournament_match.fen_id_left " +
                "WHERE eve_id = 3001;";

        MysqlDataSource source = new MysqlDataSource();
        source.setUser(USERNAME);
        source.setPassword(PASSWORD);
        source.setUrl(URL);

        System.out.println("Connecting to database...");

        try (Connection conn = source.getConnection();
            PreparedStatement stmt1 = conn.prepareStatement(r1Query);
            PreparedStatement stmt2 = conn.prepareStatement(r2Query);) {
            System.out.println("Connected!\n");

            ResultSet rs;

            //Report 1  ---------------------------------------------------
            //list of all fencers at event id #3001 (Polar Bear Open MS)
            printReportHeader("Report 1");
            rs = stmt1.executeQuery();

            System.out.printf("%n%-10s %-12s %-9s %s", "FIRST NAME", "LAST NAME", "CLUB", "RATING");
            while(!rs.isLast()){
                rs.next();
                System.out.printf("%n%-10s %-12s %-9s %s %tY",
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getDate(5));
            }

            System.out.printf("%n%n%n");

            //Report 2  ------------------------------------------------------------
            //results of all matches in event 3001, Polar Bear Open MS
            rs = stmt2.executeQuery();

            //Report 3  ------------------------------------------------------------
            //all notes on fencer, specified by user input
            Scanner in = new Scanner(System.in);
            System.out.println("For the third report, please enter a fencer's first and last name");
            String fen = in.nextLine();

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