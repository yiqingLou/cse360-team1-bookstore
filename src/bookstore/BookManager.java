package bookstore;

import java.io.*;
import java.util.*;

public class BookManager {
    private static final String FILE_NAME = "books.txt";
    public static void addBook(Book book) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {

            bw.write(book.getTitle() + "|" + book.getAuthor() + "|" + book.getCategory() + "|" +
                     book.getCondition() + "|" + book.getPrice());
            bw.newLine();
        }
    }


    public static List<Book> getAllBooks() throws IOException {
        List<Book> books = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return books;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split("\\|");
                if (arr.length == 5) {
                    Book book = new Book(arr[0], arr[1], arr[2], arr[3], Double.parseDouble(arr[4]));
                    books.add(book);
                }
            }
        }
        return books;
    }


    public static List<Book> filterByCategory(String category) throws IOException {
        List<Book> result = new ArrayList<>();
        for (Book b : getAllBooks()) {
            if (b.getCategory().equalsIgnoreCase(category)) {
                result.add(b);
            }
        }
        return result;
    }

    public static List<Book> filterByCondition(String condition) throws IOException {
        List<Book> result = new ArrayList<>();
        for (Book b : getAllBooks()) {
            if (b.getCondition().equalsIgnoreCase(condition)) {
                result.add(b);
            }
        }
        return result;
    }
}
