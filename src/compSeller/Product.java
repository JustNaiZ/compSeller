package compSeller;

public class Product {
    private final int productId;               // идентификатор товара
    private final String productName;
    private final double sellingPrice;
    private final double buyingPrice;
    private final int stockQuantity;    // количество на складе

    // Конструктор с тремя параметрами
    public Product(int id, String productName, double buyingPrice, double sellingPrice, int stockQuantity) {
        this.productId = id;
        this.productName = productName;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
    }

    // Геттеры
    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    // Сеттеры (опционально)
    /*public void setId(int id) {
        this.id = id;
    }*/

    /*public void setProductName(String productName) {
        this.productName = productName;
    }*/

    /*public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }*/

    /*public int getProductId() {
        return productId;
    }*/
}
