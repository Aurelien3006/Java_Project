import java.sql.*;
import java.util.Scanner;

public class DatabaseCreator {

    public static void main(String[] args) {
        createDatabase();
    }



    private static void createDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3300/eateasy",
                "root", "root")) {

            // Check if the table exists, if not, create it
            if (!tableExists(connection)) {
                createTable(connection);
            }

            // Get user input for recipe details
            Scanner scanner = new Scanner(System.in);
            System.out.println("How many recipes do you want to enter: ");
            int count = scanner.nextInt();

            while(count!=0){
                createRecipe(connection);
                --count;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean tableExists(Connection connection) throws SQLException {
        ResultSet tables = connection.getMetaData().getTables(null, null, "recipes", null);
        return tables.next();
    }

    private static void createTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE recipes " +
                "(recipe_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "category VARCHAR(255)," +
                "tags VARCHAR(255), " +
                "file_link VARCHAR(255))";
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
        }
    }


    private static void insertRecipe(Connection connection, String name,String category, String tags, String fileLink)
            throws SQLException {
        String insertSQL = "INSERT INTO recipes (name, category, tags, file_link) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, category);
            preparedStatement.setString(3, tags);
            preparedStatement.setString(4, fileLink);
            preparedStatement.executeUpdate();
        }
    }

    private static void createRecipe(Connection connection) throws SQLException {
        // Get user input for recipe details
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the recipe: ");
        String name = scanner.nextLine();

        System.out.print("Enter the category of the recipe (appetizer,main,dessert): ");
        String category = scanner.nextLine();

        System.out.print("Enter tags (comma-separated): ");
        String tags = scanner.nextLine();

        System.out.print("Enter the link to file.txt: ");
        String fileLink = scanner.nextLine();

        // Insert into the database
        insertRecipe(connection, name, category, tags, fileLink);

        System.out.println("Recipe added to the database successfully.");
    }
}
