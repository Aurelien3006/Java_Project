import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class Eateasy extends JFrame {

    // Components
    private final JPanel mainMenuPanel; // Panel for the main menu
    private JPanel addRecipePanel; // Panel for adding recipes
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
        JButton exitButton = new JButton("Exit");

        // Add action listeners to the buttons
        addRecipeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openAddRecipePanel(); // Call the method to open the add recipe panel
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        viewRecipeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openViewRecipePanel(); // Call the method to open the view Recipes panel
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit the application when the exit button is clicked
            }
        });

        // Add buttons to the main menu panel
        mainMenuPanel.add(addRecipeButton);
        mainMenuPanel.add(viewRecipeButton);
        mainMenuPanel.add(exitButton);



        getContentPane().add(mainMenuPanel); // Add the main menu panel to the content pane
    }

    // Method to open the add task panel
    private void openAddRecipePanel() throws SQLException {
        mainMenuPanel.setVisible(false); // Hide the main menu panel

        // Establish a database connection
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        // Initialize and configure the add Recipe panel
        addRecipePanel = new JPanel();
        addRecipePanel.setLayout(new GridLayout(5, 2)); // Set the layout to a 5x2 grid

        // Create labels and text fields for title, description
        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField(20);

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField(300);

        // Create buttons for saving and going back to the main menu
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String title = titleField.getText();
                    String description = descriptionField.getText();

                    // Validate input
                    if (title.isEmpty() || description.isEmpty()) {
                        throw new IllegalArgumentException("All fields must be filled out.");
                    }

                    // SQL query for inserting a new record into the Students table
                    String sql = "INSERT INTO Recipes (title, description, Tags) VALUES (?, ?, ?)";

                    // Close the add recipe panel and return to the main menu
                    addRecipePanel.setVisible(false);
                    mainMenuPanel.setVisible(true);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(addRecipePanel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRecipePanel.setVisible(false);
                mainMenuPanel.setVisible(true);
            }
        });

        // Add components to the add recipe panel
        addRecipePanel.add(titleLabel);
        addRecipePanel.add(titleField);
        addRecipePanel.add(descriptionLabel);
        addRecipePanel.add(descriptionField);
        addRecipePanel.add(saveButton);
        addRecipePanel.add(backButton);

        getContentPane().add(addRecipePanel); // Add the add task panel to the content pane
    }

    // Method to open the view recipes panel
    private void openViewRecipePanel() throws SQLException {
        mainMenuPanel.setVisible(false); // Hide the main menu panel

        // Establish a database connection
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

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
        Statement statement = connection.createStatement();
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
        connection.close();

        // Create a button to go back to the main menu
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewRecipePanel.setVisible(false);
                mainMenuPanel.setVisible(true);
            }
        });

        // Add an empty cell for alignment, and the back button to the view recipes panel
        viewRecipePanel.add(new JLabel());
        viewRecipePanel.add(new JLabel());
        viewRecipePanel.add(new JLabel());
        viewRecipePanel.add(new JLabel());
        viewRecipePanel.add(backButton);

        getContentPane().add(viewRecipePanel); // Add the view recipes panel to the content pane
    }


    // Static inner class representing a Recipe
    private static class Recipe {
        private String title;
        private String description;
        private boolean completed;

        // Constructor for Recipe
        public Recipe(String title, String description) {
            this.title = title;
            this.description = description;
            this.completed = false;
        }

        // Getter methods for Recipe attributes
        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public boolean isCompleted() {
            return completed;
        }

        // Setter method for marking Recipe as complete
        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Eateasy app = new Eateasy();
            app.setVisible(true);
        });
    }
}
