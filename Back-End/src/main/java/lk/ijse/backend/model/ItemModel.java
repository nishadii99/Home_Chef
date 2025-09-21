package lk.ijse.backend.model;

public class ItemModel {
    private int itemCode;
    private String name;
    private int category;
    private String description;
    private double price;
    private String sourceImage;
    private String location;
    private int quantity;
    private String user;

    public ItemModel() {
    }

    public ItemModel(int itemCode, String name, int category, String description, double price, String sourceImage, String location, int quantity, String user) {
        this.itemCode = itemCode;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.sourceImage = sourceImage;
        this.location = location;
        this.quantity = quantity;
        this.user = user;
    }

    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(String sourceImage) {
        this.sourceImage = sourceImage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ItemModel{" +
                "itemCode=" + itemCode +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", sourceImage='" + sourceImage + '\'' +
                ", location='" + location + '\'' +
                ", quantity=" + quantity +
                ", user='" + user + '\'' +
                '}';
    }
}
