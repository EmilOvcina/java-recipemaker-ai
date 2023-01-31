package src.build;

public class Action 
{
    private String name;
    private String container;
    private String ingredient;

    public Action(String name, String container, String ingredient) {
        this.name = name;
        this.container = container;
        this.ingredient = ingredient;
    }

    /**
     * Used for debugging.
     * Prints the action easily readable.
     */
    public void printAction() {
        System.out.println(name + ": " + ingredient + " in " + container);
    }

    public String getName() {
        return name;
    }

    public String getContainer() {
        return container;
    }

    public String getIngredients() {
        return ingredient;
    }
}
