import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.util.List;
import java.util.Arrays;

public class Eateasy extends JFrame {

    // Components
    private final JPanel mainMenuPanel; // Panel for the main menu
    private JPanel viewRecipePanel; // Panel for viewing recipes


    // Database connection parameters
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/eateasy";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin";

    private JTextField tagsTextField;
    private JTextArea resultTextArea;
    private JTextArea detailsTextArea;
    private final JComboBox<String> categoryComboBox;

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

        categoryComboBox = new JComboBox<>(new String[]{"Appetizer", "Main", "Dessert"});
    }
    private void openSearchRecipePanel() throws SQLException {
        mainMenuPanel.setVisible(false);

        tagsTextField = new JTextField();
        resultTextArea = new JTextArea();
        detailsTextArea = new JTextArea();

        resultTextArea.setPreferredSize(new Dimension(200,50));

        resultTextArea.setEditable(false);
        resultTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int index = resultTextArea.viewToModel2D(evt.getPoint());
                try {
                    int lineNum = resultTextArea.getLineOfOffset(index);
                    String selectedRecipeName = resultTextArea.getText().split("\n")[lineNum];
                    displayRecipeDetails(selectedRecipeName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        // Panel for searching recipes
        JPanel searchRecipePanel = new JPanel();
        searchRecipePanel.setLayout(new BorderLayout());
        searchRecipePanel.add(createSearchPanel(), BorderLayout.NORTH);
        searchRecipePanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, searchRecipePanel, new JScrollPane(detailsTextArea));
        splitPane.setDividerLocation(0.7); // Set the initial divider location (adjust as needed)
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);
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

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(3, 2));

        searchPanel.add(new JLabel("Select category:"));
        searchPanel.add(categoryComboBox);

        searchPanel.add(new JLabel("Enter ingredients (comma-separated):"));
        searchPanel.add(tagsTextField);
        searchPanel.add(createSearchButton());
        searchPanel.add(createBackButton());

        return searchPanel;
    }


    private JButton createBackButton(){
        //to go back on the menu
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainMenuPanel.setVisible(true));
        return backButton;
    }

    private JButton createSearchButton() {
        JButton searchButton = new JButton("Search Recipes");
        searchButton.addActionListener(e -> {
            String categoryInput = (String) categoryComboBox.getSelectedItem();

            assert categoryInput != null;
            if (categoryInput.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a category.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tagsInput = tagsTextField.getText();

            if (tagsInput.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter at least one ingredient.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<String> tags = Arrays.asList(tagsInput.split(","));
            List<Recipe> recipes = DatabaseConnector.searchRecipesByCategoryAndTags(categoryInput, tags);

            if (recipes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No recipes found.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder resultText = new StringBuilder("Search Results:\n");
                for (Recipe recipe : recipes) {
                    resultText.append(recipe.getName()).append("\n");
                }
                resultTextArea.setText(resultText.toString());
            }
        });
        return searchButton;
    }


    private void displayRecipeDetails(String selectedRecipeName) {
        Recipe selectedRecipe = DatabaseConnector.getRecipeByName(selectedRecipeName);

        if (selectedRecipe != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedRecipe.getFileLink()))) {
                StringBuilder detailsText = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    detailsText.append(line).append("\n");
                }
                detailsTextArea.setText(detailsText.toString());
            } catch (IOException e) {
                e.printStackTrace();
                detailsTextArea.setText("Error reading recipe details.");
            }
        }
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
