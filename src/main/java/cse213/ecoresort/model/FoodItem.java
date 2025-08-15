package cse213.ecoresort.model;

public class FoodItem extends MenuItem {
    private String cuisine;
    private boolean isVegetarian;

    public FoodItem(String name, double price, int stockQty, String cuisine, boolean isVegetarian) {
        super(name, price, stockQty, ItemType.FOOD);
        this.cuisine = cuisine;
        this.isVegetarian = isVegetarian;
    }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    
    public boolean isVegetarian() { return isVegetarian; }
    public void setVegetarian(boolean vegetarian) { isVegetarian = vegetarian; }

    @Override
    public String getDescription() {
        String desc = getName() + " (" + getCuisine() + ")";
        if (isVegetarian) {
            desc += " [Veg]";
        }
        return desc;
    }
}
