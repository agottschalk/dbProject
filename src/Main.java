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

        String r2Query = "SELECT mat_round, mat_type, " +
                    "r.fen_first_name, r.fen_last_name, " +
                    "l.fen_first_name, l.fen_last_name, " +
                    "mat_right_touches, mat_left_touches, " +
                    "referee.ref_first_name, referee.ref_last_name " +
                "FROM tournament_match " +
                    "INNER JOIN referee ON referee.ref_id = tournament_match.ref_id " +
                    "INNER JOIN fencer r ON r.fen_id = tournament_match.fen_id_right " +
                    "INNER JOIN fencer l ON l.fen_id = tournament_match.fen_id_left " +
                "WHERE eve_id = 3001;";

        String r3Query = "SELECT fen_id, fen_first_name, fen_last_name " +
                "FROM fencer " +
                "WHERE fen_first_name LIKE ? " +
                "OR fen_last_name LIKE ?;";

        String r4Query = "SELECT mat_notes FROM fencer_notes " +
                "WHERE fen_id = ?;";

        MysqlDataSource source = new MysqlDataSource();
        source.setUser(USERNAME);
        source.setPassword(PASSWORD);
        source.setUrl(URL);

        System.out.println("Connecting to database...");

        try (Connection conn = source.getConnection();
            PreparedStatement stmt1 = conn.prepareStatement(r1Query);
            PreparedStatement stmt2 = conn.prepareStatement(r2Query);
            PreparedStatement stmt3 = conn.prepareStatement(r3Query);
            PreparedStatement stmt4 = conn.prepareStatement(r4Query)) {
            System.out.println("Connected!\n");

            ResultSet rs;

            //Report 1  ---------------------------------------------------
            //list of all fencers at event id #3001 (Polar Bear Open MS)
            printReportHeader("Report 1: List of fencers from one event");
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
            printReportHeader("Report 2: All matches in an event");
            rs = stmt2.executeQuery();
            System.out.printf("%n%s %s %-16s %s %-16s %s %s",
                    "ROUND", "TYPE", "RIGHT FENCER", "TOUCHES", "LEFT FENCER", "TOUCHES", "REFEREE");
            while(!rs.isLast()){
                rs.next();
                System.out.printf("%n%5s %4.4s %-16s %-7.7s %-16s %-7.7s %s",
                        rs.getInt("mat_round"),
                        rs.getString("mat_type"),
                        rs.getString("r.fen_first_name") + " " + rs.getString("r.fen_last_name"),
                        rs.getInt("mat_right_touches"),
                        rs.getString("l.fen_first_name") + " " + rs.getString("l.fen_last_name"),
                        rs.getInt("mat_left_touches"),
                        rs.getString("referee.ref_first_name") + " " + rs.getString("referee.ref_last_name"));
            }

            System.out.printf("%n%n%n");

            //Report 3  ------------------------------------------------------------
            //all notes on fencer, specified by user input
            printReportHeader("Report 3: All notes on a fencer");

            //get fencer's name from user
            Scanner in = new Scanner(System.in);
            System.out.println("\nFor the third report, please enter a fencer's name:");
            String fen = in.nextLine();
            stmt3.setString(1, fen + "%");
            stmt3.setString(2, fen + "%");
            rs = stmt3.executeQuery();

            //use name to find fencer's id
            while(!rs.isBeforeFirst()){
                System.out.println("\nNo fencer found, please try again.");
                fen = in.nextLine();
                stmt3.setString(1, fen + "%");
                stmt3.setString(2, fen + "%");
                rs = stmt3.executeQuery();
            }

            //user chooses correct fencer, this is for when there are multiple matches
            System.out.printf("%nChoose a fencer id:%n");
            while(!rs.isLast()){
                rs.next();
                System.out.printf("%s %s%n",
                        rs.getString("fen_id"),
                        rs.getString("fen_first_name") + " " + rs.getString("fen_last_name"));
            }

            stmt4.setInt(1, in.nextInt());
            rs = stmt4.executeQuery();

            //print final list of notes, or message if none are present
            if(rs.isBeforeFirst()){
                System.out.printf("%nNOTES");
                while(!rs.isLast()){
                    rs.next();
                    System.out.printf("%n%s", rs.getString(1));
                }
            }else{
                System.out.println("\nNo notes found.");
            }

            System.out.println("\nExiting database.");

        } catch (SQLException e) {
            System.out.println("\nUh-oh, something went wrong.");
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