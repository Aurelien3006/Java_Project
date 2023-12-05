public class Recipe {
    private int recipeId;
    private String name;
    private String category;
    private String tags;
    private String fileLink;

    public Recipe(int recipeId, String name, String category, String tags, String fileLink) {
        this.recipeId = recipeId;
        this.name = name;
        this.category = category;
        this.tags = tags;
        this.fileLink = fileLink;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getTags() {
        return tags;
    }

    public String getFileLink() {
        return fileLink;
    }
}
