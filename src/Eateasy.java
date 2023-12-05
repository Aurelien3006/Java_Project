import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class Eateasy extends JFrame {

    // Components
    private final JPanel mainMenuPanel; // Panel for the main menu
    private JPanel viewRecipePanel; // Panel for viewing recipes

    // Database connection parameters
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/eateasy";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";


    // Constructor for the Eateasy App class
    public Eateasy() {
        setTitle("Eateasy"); // Set the title of the application window
        setSize(600, 400); // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set default close operation
        setLocationRelativeTo(null); // Center the window on the screen

        // Initialize the main menu panel and set its layout to FlowLayout
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new FlowLayout());

        // Create buttons for adding recipes, viewing recipes, and exiting
        JButton addRecipeButton = new JButton("Add Recipe");
        JButton viewRecipeButton = new JButton("View Recipes");
        JButton searchRecipeButton = new JButton("Search Recipes");
        JButton exitButton = new JButton("Exit");

        // Add action listeners to the buttons
        addRecipeButton.addActionListener(e -> {
            openAddRecipePanel(); // Call the method to open the add recipe panel
        });

        viewRecipeButton.addActionListener(e -> {
            try {
                openViewRecipePanel(); // Call the method to open the view Recipes panel
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        searchRecipeButton.addActionListener(e -> {
            try{
                openSearchRecipePanel();
            }
            catch (SQLException ex){
                throw new RuntimeException(ex);
            }
        });

        exitButton.addActionListener(e -> {
            System.exit(0); // Exit the application when the exit button is clicked
        });

        // Add buttons to the main menu panel
        mainMenuPanel.add(addRecipeButton);
        mainMenuPanel.add(viewRecipeButton);
        mainMenuPanel.add(searchRecipeButton);
        mainMenuPanel.add(exitButton);



        getContentPane().add(mainMenuPanel); // Add the main menu panel to the content pane

    }
    private void openSearchRecipePanel() throws SQLException {
        new Recipesearch();
    }

    // Method to open the add recipe panel
    private void openAddRecipePanel() {
        new RecipeForm();
    }

    // Method to open the view recipes panel
    private void openViewRecipePanel() throws SQLException {
        mainMenuPanel.setVisible(false); // Hide the main menu panel

        // Initialize and configure the view recipes panel
        viewRecipePanel = new JPanel();
        GridLayout gridLayout = new GridLayout(0, 3);
        gridLayout.setHgap(5); // Add horizontal gap between cells
        gridLayout.setVgap(5); // Add vertical gap between cells
        viewRecipePanel.setLayout(gridLayout);

        // Create labels for the table headers
        JLabel titleHeader = new JLabel("Name");
        JLabel categoryHeader = new JLabel("Category");
        JLabel tagsHeader = new JLabel("Tags");

        // Add headers to the view recipes panel
        viewRecipePanel.add(titleHeader);
        viewRecipePanel.add(categoryHeader);
        viewRecipePanel.add(tagsHeader);

        // Retrieve recipes from the database
        Statement statement = establishDatabaseConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM recipes");

        // Iterate through the recipes and create labels for each recipe
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            String category = resultSet.getString("category");
            String tags = resultSet.getString("tags");

            // Create labels for each recipe
            JLabel nameLabel = new JLabel(name);
            JLabel categoryLabel = new JLabel(category);
            JLabel tagsLabel = new JLabel(tags);

            // Set borders to make the grid visible
            nameLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            categoryLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            tagsLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            // Add labels to the view recipes panel
            viewRecipePanel.add(nameLabel);
            viewRecipePanel.add(categoryLabel);
            viewRecipePanel.add(tagsLabel);
        }

        // Close the database resources
        resultSet.close();
        statement.close();
        establishDatabaseConnection().close();

        // Create a button to go back to the main menu
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            viewRecipePanel.setVisible(false);
            mainMenuPanel.setVisible(true);
        });

        // Add an empty cell for alignment, and the back button to the view recipes panel
        viewRecipePanel.add(new JLabel());
        viewRecipePanel.add(new JLabel());
        viewRecipePanel.add(new JLabel());
        viewRecipePanel.add(new JLabel());
        viewRecipePanel.add(backButton);

        getContentPane().add(viewRecipePanel); // Add the view recipes panel to the content pane
    }

    public static Connection establishDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(Eateasy.DB_URL, Eateasy.DB_USER, Eateasy.DB_PASSWORD);
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseCreator.createDatabase();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Eateasy app = new Eateasy();
            app.setVisible(true);
        });
    }
}
