import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class Product {
    private int id; 
    private String name; 
    private double price; 
    private Instant creationDatetime; 
    private List<Category> categories; 


    public Product(int id, String name, double price, Instant creationDatetime, List<Category> categories) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.creationDatetime = creationDatetime;
        this.categories = categories;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public List<Category> getCategories() {
        return categories;
    }


    public String getCategoryName() {
        if (categories == null || categories.isEmpty()) {
            return "";
        }
        
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", creationDatetime=" + creationDatetime +
                ", categories=[" + getCategoryName() + ']' +
                '}';
    }
}