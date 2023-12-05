import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

    public static List<Recipe> searchRecipesByCategoryAndTags(String category, List<String> tags) {
        List<Recipe> recipes = new ArrayList<>();

        try (Connection connection = Eateasy.establishDatabaseConnection()) {

            // Append conditions for each tag using LIKE

            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM recipes WHERE category = ?" + " AND tags LIKE ?".repeat(tags.size()))) {
                preparedStatement.setString(1, category);

                // Set parameters for each tag
                for (int i = 0; i < tags.size(); i++) {
                    String tagsPattern = "%" + tags.get(i) + "%";
                    preparedStatement.setString(i + 2, tagsPattern); // +2 because 1 is for category
                }

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
        try (Connection connection = Eateasy.establishDatabaseConnection()) {
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
