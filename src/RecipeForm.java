import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

public class RecipeForm extends JFrame {
    private final JTextField recipeNameField;
    //private final JSpinner personSpinner;
    private final JTextArea ingredientsArea;
    private final JTextArea stepsArea;
    private final JComboBox<String> categoryComboBox;
    private static final String RECIPE_FOLDER = "./recipe_files";

    public RecipeForm() {
        // Set up the main frame
        setTitle("Add a recipe to database");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        // Create form components
        JLabel nameLabel = new JLabel("Recipe Name: ");
        recipeNameField = new JTextField();

        /** JLabel personLabel = new JLabel("Number of Persons: ");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 12, 1);
        personSpinner = new JSpinner(spinnerModel);
         **/

        JLabel ingredientsLabel = new JLabel("Ingredients (Separate with a semi-colon): ");
        ingredientsArea = new JTextArea();

        JLabel stepsLabel = new JLabel("Steps (Separate with a semi-colon): ");
        stepsArea = new JTextArea();

        JLabel categoryLabel = new JLabel("Category: ");
        String[] categories = {"Appetizer", "Main", "Dessert"};
        categoryComboBox = new JComboBox<>(categories);

        // Add a "Submit" button
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // Fetch values from the form
            String recipeName = recipeNameField.getText();
            //int persons = (int) personSpinner.getValue();
            String ingredients = ingredientsArea.getText();
            String steps = stepsArea.getText();
            String category = (String) categoryComboBox.getSelectedItem();
            if (validateForm()) {
                String filePath = saveRecipe(recipeName, ingredients, steps);
                try {
                    DatabaseCreator.insertRecipe(Eateasy.establishDatabaseConnection(), recipeName, category, ingredients, filePath);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(RecipeForm.this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add a "Back" button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());

        // Add components to the frame
        add(nameLabel);
        add(recipeNameField);

        //add(personLabel);
        //add(personSpinner);

        add(ingredientsLabel);
        add(new JScrollPane(ingredientsArea));

        add(stepsLabel);
        add(new JScrollPane(stepsArea));

        add(categoryLabel);
        add(categoryComboBox);

        // Add "Submit" and "Back" buttons
        add(submitButton);
        add(backButton);

        // Display the frame
        setLocationRelativeTo(null);
        setVisible(true);

        // Create the recipe folder if it doesn't exist
        File folder = new File(RECIPE_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    private boolean validateForm() {
        // Validate that all fields are completed
        return !recipeNameField.getText().isEmpty() &&
                !ingredientsArea.getText().isEmpty() &&
                !stepsArea.getText().isEmpty();
    }

    private String saveRecipe(String nameOfRecipe, String listOfIngredients, String stepsOfRecipe) {
        // Create a new text file
        String fileName = RECIPE_FOLDER + File.separator + nameOfRecipe + ".txt";
        File recipeFile = new File(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(recipeFile))) {
            // Write recipe details to the file
            writer.write("Ingredients:\n");

            // Split ingredients based on semicolons and write each on a new line
            String[] ingredientsArray = listOfIngredients.split(";");
            for (String ingredient : ingredientsArray) {
                writer.write(ingredient.trim());
                writer.newLine();
            }

            writer.newLine(); // Add an empty line between ingredients and instructions

            writer.write("Instructions:\n");

            // Split steps based on semicolons and write each on a new line with step numbers
            String[] stepsArray = stepsOfRecipe.split(";");
            for (int i = 0; i < stepsArray.length; i++) {
                writer.write("Step " + (i + 1) + ": " + stepsArray[i].trim());
                writer.newLine();
            }

            JOptionPane.showMessageDialog(this, "Recipe saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving recipe", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Optionally, clear the form after saving
        clearForm();

        return fileName;
    }

    private void clearForm() {
        // Clear form fields
        recipeNameField.setText("");
        //personSpinner.setValue(1);
        ingredientsArea.setText("");
        stepsArea.setText("");
        categoryComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        // Launch the Swing application in the Event Dispatch Thread
        SwingUtilities.invokeLater(RecipeForm::new);
    }
}
