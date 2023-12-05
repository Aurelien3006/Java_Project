import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class Recipesearch extends JFrame {
    private final JTextField tagsTextField;
    private final JTextArea resultTextArea;
    private final JTextArea detailsTextArea;
    private final JComboBox<String> categoryComboBox;


    public Recipesearch() {
        categoryComboBox = new JComboBox<>(new String[]{"Appetizer", "Main", "Dessert"});
        setTitle("Recipe Search App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        tagsTextField = new JTextField();
        resultTextArea = new JTextArea();
        detailsTextArea = new JTextArea();


        resultTextArea.setPreferredSize(new Dimension(200, 50));

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

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(createSearchPanel(), BorderLayout.NORTH);
        panel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel, new JScrollPane(detailsTextArea));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(3, 2));
        searchPanel.add(new JLabel("Enter category:"));
        searchPanel.add(categoryComboBox);
        searchPanel.add(new JLabel("Enter ingredients (comma-separated):"));
        searchPanel.add(tagsTextField);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        searchPanel.add(createSearchButton());
        searchPanel.add(backButton);
        return searchPanel;
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
}
