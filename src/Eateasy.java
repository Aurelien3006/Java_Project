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
    private List<Recipe> recipes; // List to store recipes

    // Database connection parameters
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/Eateasy";
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

        recipes = new ArrayList<>(); // Initialize the list of Recipes

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
        viewRecipePanel.setLayout(new GridLayout(recipes.size() + 2, 4)); // Set the layout based on the number of recipes

        // Create labels for the table headers
        JLabel titleHeader = new JLabel("Title");
        JLabel descriptionHeader = new JLabel("Description");
        JLabel actionHeader = new JLabel("Action");

        // Add headers to the view recipes panel
        viewRecipePanel.add(titleHeader);
        viewRecipePanel.add(descriptionHeader);
        viewRecipePanel.add(actionHeader);

        // Iterate through the recipes and create labels and buttons for each recipe
        for (Recipe recipe : recipes) {
            JLabel titleLabel = new JLabel(recipe.getTitle());
            JLabel descriptionLabel = new JLabel(recipe.getDescription());
            JButton markCompleteButton = new JButton("Mark as Complete");
            JButton markUncompletedButton = new JButton("Mark as Uncompleted");

            // Disable buttons for completed recipe and change text color
            if (recipe.isCompleted()) {
                markCompleteButton.setEnabled(false);
                titleLabel.setForeground(Color.GRAY);
                descriptionLabel.setForeground(Color.GRAY);
            }

            markCompleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Implement logic to mark the recipe as complete
                        recipe.setCompleted(true);

                        // Update the UI to visually differentiate completed recipes
                        markCompleteButton.setEnabled(false);
                        titleLabel.setForeground(Color.GRAY);
                        descriptionLabel.setForeground(Color.GRAY);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(viewRecipePanel, "An error occurred while marking the recipe as complete.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Add labels and buttons to the view recipes panel
            viewRecipePanel.add(titleLabel);
            viewRecipePanel.add(descriptionLabel);
            viewRecipePanel.add(markCompleteButton);
        }

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
