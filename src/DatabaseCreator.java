import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseCreator {

    public static void createDatabase() throws SQLException {
        Connection databaseConnection = Eateasy.establishDatabaseConnection();
        try (databaseConnection) {
            // Check if the table exists, if not, create it
            if (!tableExists(databaseConnection)) {
                createTable(databaseConnection);
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

    public static void insertRecipe(Connection connection, String name, String category, String tags, String fileLink) throws SQLException {
        String insertSQL = "INSERT INTO recipes (name, category, tags, file_link) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, category);
            preparedStatement.setString(3, tags);
            preparedStatement.setString(4, fileLink);
            preparedStatement.executeUpdate();
        }
    }
}
