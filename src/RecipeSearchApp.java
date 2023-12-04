import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RecipeSearchApp extends JFrame {
    private JTextField categoryTextField;
    private JTextField tagsTextField;
    private JTextArea resultTextArea;
    private JTextArea detailsTextArea;

    public RecipeSearchApp() {
        setTitle("Recipe Search App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);

        categoryTextField = new JTextField();
        tagsTextField = new JTextField();
        resultTextArea = new JTextArea();
        detailsTextArea = new JTextArea();

        JButton searchButton = new JButton("Search Recipes");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String categoryInput = categoryTextField.getText();
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
            }
        });
        resultTextArea.setPreferredSize(new Dimension(200,50));

        resultTextArea.setEditable(false);
        resultTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int index = resultTextArea.viewToModel(evt.getPoint());
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
        splitPane.setDividerLocation(0.7); // Set the initial divider location (adjust as needed)
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(3, 2));
        searchPanel.add(new JLabel("Enter category:"));
        searchPanel.add(categoryTextField);
        searchPanel.add(new JLabel("Enter ingredients (comma-separated):"));
        searchPanel.add(tagsTextField);
        searchPanel.add(createSearchButton());
        return searchPanel;
    }

    private JButton createSearchButton() {
        JButton searchButton = new JButton("Search Recipes");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String categoryInput = categoryTextField.getText();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RecipeSearchApp().setVisible(true);
            }
        });
    }
}
