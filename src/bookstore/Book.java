package bookstore;

public class Book {
    private String title;
    private String author;
    private String category;
    private String condition;
    private double price;

    public Book(String title, String author, String category, String condition, double price) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.condition = condition;
        this.price = price;
    }

    // getters/setters 省略
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getCondition() { return condition; }
    public double getPrice() { return price; }
}
