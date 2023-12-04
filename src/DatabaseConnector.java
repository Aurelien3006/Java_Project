import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {
    private static final String JDBC_URL = "jdbc:mariadb://localhost:3300/eateasy";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static List<Recipe> searchRecipesByCategoryAndTags(String category, List<String> tags) {
        List<Recipe> recipes = new ArrayList<>();

        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM recipes WHERE category = ? AND tags LIKE ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, category);
                String tagsPattern = "%" + String.join("%", tags) + "%";
                preparedStatement.setString(2, tagsPattern);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Recipe recipe = new Recipe(
                                resultSet.getInt("recipe_id"),
                                resultSet.getString("name"),
                                resultSet.getString("category"),
                                resultSet.getString("tags"),
                                resultSet.getString("file_link")
                        );
                        recipes.add(recipe);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recipes;
    }
    public static Recipe getRecipeByName(String name) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM recipes WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new Recipe(
                                resultSet.getInt("recipe_id"),
                                resultSet.getString("name"),
                                resultSet.getString("category"),
                                resultSet.getString("tags"),
                                resultSet.getString("file_link")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the recipe is not found
    }
}
