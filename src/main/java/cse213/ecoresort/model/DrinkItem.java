package cse213.ecoresort.model;

public class DrinkItem extends MenuItem {
    private boolean isAlcoholic;
    private String temperature; // Hot, Cold, Room

    public DrinkItem(String name, double price, int stockQty, boolean isAlcoholic, String temperature) {
        super(name, price, stockQty, ItemType.DRINK);
        this.isAlcoholic = isAlcoholic;
        this.temperature = temperature;
    }

    public boolean isAlcoholic() { return isAlcoholic; }
    public void setAlcoholic(boolean alcoholic) { isAlcoholic = alcoholic; }
    
    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }

    @Override
    public String getDescription() {
        String desc = getName() + " (" + getTemperature() + ")";
        if (isAlcoholic) {
            desc += " [Alcoholic]";
        }
        return desc;
    }
}
