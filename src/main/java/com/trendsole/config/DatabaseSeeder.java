package com.trendsole.config;

import com.trendsole.model.Product;
import com.trendsole.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * DatabaseSeeder - Automatically populates the database with initial
 * product data if it is empty. This works for both MySQL and H2 databases.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            Product p1 = new Product(null, "Nike Air Max 90", "Classic Nike sneakers with Air cushioning for all-day comfort.", 12999.00, "Sneakers", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400", 25);
            Product p2 = new Product(null, "Adidas Ultraboost", "Responsive Boost midsole for energy return on every step.", 15999.00, "Running", "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400", 18);
            Product p3 = new Product(null, "Puma RS-X", "Retro-inspired chunky sneakers with bold colors.", 8999.00, "Sneakers", "https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=400", 30);
            Product p4 = new Product(null, "Woodland Boots", "Durable leather boots for outdoor adventures.", 5499.00, "Boots", "https://images.unsplash.com/photo-1638247025967-b4e38f787b76?w=400", 15);
            Product p5 = new Product(null, "Bata Formal Shoes", "Elegant formal shoes for office and special occasions.", 3299.00, "Formal", "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=400", 40);
            Product p6 = new Product(null, "Reebok Classic", "Timeless Reebok design with soft leather upper.", 6999.00, "Sneakers", "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400", 22);

            productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6));
            System.out.println("🌱 Database seeded with mock products successfully!");
        }
    }
}
