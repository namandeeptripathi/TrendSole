package com.trendsole.config;

import com.trendsole.model.Product;
import com.trendsole.repository.ProductRepository;
import com.trendsole.repository.CartItemRepository;
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

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() != 72) {
            System.out.println("🌱 Database count is " + productRepository.count() + " (not 72). Resetting and re-seeding...");
            cartItemRepository.deleteAll();
            productRepository.deleteAll();
            Product[] products = new Product[] {
                // Category: Footwear (Set A - 12 items)
                new Product(null, "Nike Air Max 90 Premium", "Classic Nike sneakers with Air cushioning for all-day comfort.", 12999.00, "Footwear", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400", 25),
                new Product(null, "Adidas Ultraboost Prime", "Responsive Boost midsole for energy return on every step.", 15999.00, "Footwear", "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400", 18),
                new Product(null, "Puma RS-X Retro", "Retro-inspired chunky sneakers with bold colors.", 8999.00, "Footwear", "https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=400", 30),
                new Product(null, "Woodland Adventure Boots", "Durable leather boots for outdoor adventures.", 5499.00, "Footwear", "https://images.unsplash.com/photo-1638247025967-b4e38f787b76?w=400", 15),
                new Product(null, "Bata Executive Formal Shoes", "Elegant formal shoes for office and special occasions.", 3299.00, "Footwear", "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=400", 40),
                new Product(null, "Reebok Classic Vintage", "Timeless Reebok design with soft leather upper.", 6999.00, "Footwear", "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400", 22),
                new Product(null, "Converse All Star Classic", "Timeless classic canvas high-top sneakers.", 5999.00, "Footwear", "https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=400", 35),
                new Product(null, "Crocs Classic Clogs", "Lightweight, water-friendly, and ventilated clogs.", 2999.00, "Footwear", "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=400", 50),
                new Product(null, "Skechers Go Walk", "Comfortable walking shoes with responsive cushioning.", 4999.00, "Footwear", "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400", 40),
                new Product(null, "Birkenstock Arizona Sandals", "Classic two-strap sandals with cork-latex footbed.", 7999.00, "Footwear", "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=400", 15),
                new Product(null, "Hush Puppies Leather Loafers", "Elegant slip-on leather loafers for comfort and style.", 4499.00, "Footwear", "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=400", 20),
                new Product(null, "Fila Disruptor II", "Chunky retro style sneakers with sawtooth rubber sole.", 6999.00, "Footwear", "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400", 25),

                // Category: Footwear (Set B - 12 items from seed-data.js)
                new Product(null, "Nike Air Max 90", "Iconic Air Max design with visible Air cushioning for all-day comfort and street-ready style.", 12999.00, "Footwear", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400", 25),
                new Product(null, "Adidas Ultraboost 22", "Responsive Boost midsole with Primeknit+ upper delivers incredible energy return on every step.", 16999.00, "Footwear", "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400", 18),
                new Product(null, "Puma RS-X Reinvention", "Retro-inspired chunky sneakers with bold colorways and Running System technology.", 8999.00, "Footwear", "https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=400", 30),
                new Product(null, "Converse Chuck 70 High", "Premium heritage canvas high-top with vintage styling and OrthoLite cushioning.", 6499.00, "Footwear", "https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=400", 35),
                new Product(null, "New Balance 574 Classic", "Timeless lifestyle sneaker with ENCAP midsole technology and suede/mesh upper.", 9499.00, "Footwear", "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400", 22),
                new Product(null, "Vans Old Skool", "The iconic sidestripe shoe in durable canvas and suede. A skateboarding legend.", 5499.00, "Footwear", "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=400", 40),
                new Product(null, "Reebok Classic Leather", "Clean and simple leather sneaker with soft garment leather upper and EVA midsole.", 7499.00, "Footwear", "https://images.unsplash.com/photo-1539185441755-769473a23570?w=400", 28),
                new Product(null, "Asics Gel-Kayano 30", "Premium stability running shoe with GEL technology and FF BLAST+ cushioning.", 14999.00, "Footwear", "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=400", 15),
                new Product(null, "Jordan 1 Retro High OG", "The shoe that started it all. Premium leather construction with Nike Air cushioning.", 16499.00, "Footwear", "https://images.unsplash.com/photo-1597045566677-8cf032ed6634?w=400", 10),
                new Product(null, "Timberland 6-Inch Premium", "Waterproof leather boots with padded collar for comfort and iconic wheat nubuck finish.", 13999.00, "Footwear", "https://images.unsplash.com/photo-1638247025967-b4e38f787b76?w=400", 20),
                new Product(null, "Clarks Desert Boot", "Iconic crepe-soled chukka boot in premium suede. A British footwear classic since 1950.", 8999.00, "Footwear", "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=400", 18),
                new Product(null, "Dr. Martens 1460 Boot", "The original 8-eye boot with AirWair bouncing sole and smooth leather upper.", 15499.00, "Footwear", "https://images.unsplash.com/photo-1605733160314-4fc7dac4bb16?w=400", 12),

                // Category: Clothing (12 items)
                new Product(null, "Levi's 501 Original Jeans", "The original straight-fit jeans that started it all. Premium denim with signature riveted pockets.", 4999.00, "Clothing", "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400", 45),
                new Product(null, "Nike Dri-FIT Crew Tee", "Moisture-wicking performance tee with classic Nike Swoosh. Perfect for training and everyday wear.", 2499.00, "Clothing", "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400", 50),
                new Product(null, "Zara Slim Fit Blazer", "Tailored slim-fit blazer with notched lapels and two-button fastening. Elevate any outfit.", 5999.00, "Clothing", "https://images.unsplash.com/photo-1507679799987-c73b1266a04e?w=400", 20),
                new Product(null, "H&M Oversized Hoodie", "Relaxed oversized hoodie in soft cotton blend with kangaroo pocket and drawstring hood.", 2999.00, "Clothing", "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400", 55),
                new Product(null, "Uniqlo Merino Sweater", "Extra fine merino wool crew neck sweater. Lightweight warmth with a premium soft feel.", 3499.00, "Clothing", "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=400", 30),
                new Product(null, "Ralph Lauren Polo Shirt", "Classic fit cotton mesh polo with signature embroidered Pony. A wardrobe essential.", 7999.00, "Clothing", "https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=400", 25),
                new Product(null, "Calvin Klein Logo Tee", "Minimalist crew neck tee with iconic CK logo. Made from organic cotton for everyday comfort.", 3299.00, "Clothing", "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=400", 40),
                new Product(null, "Tommy Hilfiger Puffer Jacket", "Warm quilted puffer jacket with signature flag logo. Water-resistant nylon shell.", 12999.00, "Clothing", "https://images.unsplash.com/photo-1544923246-77307dd270cb?w=400", 15),
                new Product(null, "Gap Classic Chinos", "Straight-fit chinos in stretch cotton twill. A polished look from office to weekend.", 3999.00, "Clothing", "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=400", 35),
                new Product(null, "Mango Linen Shirt", "Relaxed-fit linen shirt with a regular collar. Cool and breathable for summer styling.", 3499.00, "Clothing", "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=400", 28),
                new Product(null, "Adidas Track Pants", "Classic 3-Stripes track pants in doubleknit fabric. From training to streetwear.", 3999.00, "Clothing", "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400", 42),
                new Product(null, "Zara Trench Coat", "Belted trench coat with lapel collar and epaulettes. Water-repellent finish for rainy days.", 8999.00, "Clothing", "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=400", 12),

                // Category: Accessories (12 items)
                new Product(null, "Ray-Ban Aviator Classic", "Iconic pilot-shaped sunglasses with crystal green lenses and gold-tone metal frame.", 8999.00, "Accessories", "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=400", 30),
                new Product(null, "Casio G-Shock GA-2100", "Tough digital-analog watch with carbon core guard structure and 200m water resistance.", 12999.00, "Accessories", "https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=400", 20),
                new Product(null, "Nike Dri-FIT Club Cap", "Lightweight structured cap with moisture-wicking sweatband and embroidered Swoosh.", 1999.00, "Accessories", "https://images.unsplash.com/photo-1588850561407-ed78c334e67a?w=400", 60),
                new Product(null, "Titan Raga Women Watch", "Elegant analog watch with mother of pearl dial and rose gold stainless steel bracelet.", 6999.00, "Accessories", "https://images.unsplash.com/photo-1612817159949-195b6eb9e31a?w=400", 15),
                new Product(null, "Fossil Leather Belt", "Classic leather belt with signature buckle. Hand-stitched edges with vintage finish.", 3499.00, "Accessories", "https://images.unsplash.com/photo-1624222247344-550fb60583dc?w=400", 35),
                new Product(null, "Tommy Hilfiger Bifold Wallet", "Premium leather bifold wallet with embossed logo and multiple card slots.", 4499.00, "Accessories", "https://images.unsplash.com/photo-1627123424574-724758594e93?w=400", 25),
                new Product(null, "Oakley Holbrook Sunglasses", "Sport-performance sunglasses with Plutonite lenses and lightweight O Matter frame.", 9999.00, "Accessories", "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=400", 18),
                new Product(null, "Daniel Wellington Classic Watch", "Ultra-slim watch with interchangeable leather strap and minimalist Scandinavian design.", 11999.00, "Accessories", "https://images.unsplash.com/photo-1539874754764-5a96559165b0?w=400", 14),
                new Product(null, "Puma Evercat Baseball Cap", "Curved-brim baseball cap with embroidered Puma cat logo and adjustable strap.", 1499.00, "Accessories", "https://images.unsplash.com/photo-1521369909029-2afed882baee?w=400", 50),
                new Product(null, "Ray-Ban Wayfarer", "The cultural icon since 1956. Acetate frame with UV-protective polarized lenses.", 10499.00, "Accessories", "https://images.unsplash.com/photo-1473496169904-658ba7c44d8a?w=400", 22),
                new Product(null, "Swatch Originals Gent", "Swiss-made watch with vibrant colorful dial and silicone strap. Bold and playful design.", 5999.00, "Accessories", "https://images.unsplash.com/photo-1509048191080-d2984bad6ae5?w=400", 28),
                new Product(null, "Calvin Klein Reversible Belt", "Smooth leather reversible belt with sleek CK plaque buckle. Two looks in one.", 4999.00, "Accessories", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400", 32),

                // Category: Bags (12 items)
                new Product(null, "Samsonite Laptop Backpack", "Business-ready laptop backpack with padded compartment, USB port, and TSA-friendly design.", 5999.00, "Bags", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400", 20),
                new Product(null, "Nike Brasilia Training Duffel", "Spacious training duffel with ventilated shoe compartment and water-resistant bottom.", 3499.00, "Bags", "https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=400", 30),
                new Product(null, "Wildcraft Supernova Backpack", "Adventure-ready backpack with rain cover, padded straps, and 40L capacity.", 2999.00, "Bags", "https://images.unsplash.com/photo-1581605405669-fcdf81165afa?w=400", 35),
                new Product(null, "Herschel Little America", "Classic mountaineering-inspired backpack with magnetic strap closures and laptop sleeve.", 7999.00, "Bags", "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=400", 15),
                new Product(null, "American Tourister Urban Bag", "Lightweight casual backpack with colorful design, multiple pockets, and padded back panel.", 2499.00, "Bags", "https://images.unsplash.com/photo-1622560480654-1e0aadd0e3e2?w=400", 45),
                new Product(null, "Safari Polyester Daypack", "Everyday daypack with organizer pocket, bottle holder, and reflective tape for visibility.", 1999.00, "Bags", "https://images.unsplash.com/photo-1585916420730-d7f95e942d43?w=400", 50),
                new Product(null, "Tommy Hilfiger Crossbody", "Compact crossbody bag with signature stripe and adjustable strap. Perfect for on-the-go.", 5499.00, "Bags", "https://images.unsplash.com/photo-1566150905458-1bf1fc113f0d?w=400", 22),
                new Product(null, "Puma Phase Backpack", "Sleek sporty backpack with zip main compartment, front pocket, and Puma branding.", 2299.00, "Bags", "https://images.unsplash.com/photo-1491637639811-60e2756cc1c7?w=400", 40),
                new Product(null, "Fossil Leather Tote", "Spacious leather tote with zipper closure, interior pockets, and vintage brass hardware.", 9999.00, "Bags", "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=400", 12),
                new Product(null, "Skybags Casual Daypack", "Trendy casual backpack with rain cover, laptop compartment, and ergonomic design.", 1799.00, "Bags", "https://images.unsplash.com/photo-1544816155-12df9643f363?w=400", 55),
                new Product(null, "Adidas Linear Duffel", "Versatile training duffel with adjustable shoulder strap and front zip pocket.", 2999.00, "Bags", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400", 28),
                new Product(null, "Levi's Canvas Tote", "Durable canvas tote bag with iconic Red Tab logo and reinforced handles for everyday use.", 2499.00, "Bags", "https://images.unsplash.com/photo-1594223274512-ad4803739b7c?w=400", 32),

                // Category: Activewear (12 items)
                new Product(null, "Nike Dri-FIT Training Shorts", "Lightweight training shorts with moisture-wicking fabric and internal brief for support.", 2499.00, "Activewear", "https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=400", 40),
                new Product(null, "Adidas Tiro Track Pants", "Slim-fit training pants with AEROREADY moisture management and iconic 3-Stripes.", 3999.00, "Activewear", "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400", 35),
                new Product(null, "Puma Active Training Tee", "dryCELL moisture-wicking training tee with flatlock stitching for comfort during workouts.", 1999.00, "Activewear", "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=400", 48),
                new Product(null, "Under Armour Tech Polo", "Quick-drying anti-odor polo with UPF 30+ sun protection. From the course to the office.", 4499.00, "Activewear", "https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=400", 25),
                new Product(null, "Reebok Training Tank", "Breathable mesh tank top with Speedwick technology for high-intensity training sessions.", 1799.00, "Activewear", "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400", 38),
                new Product(null, "Nike Pro Training Leggings", "Compression-fit leggings with Dri-FIT technology and mesh ventilation panels.", 3499.00, "Activewear", "https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=400", 30),
                new Product(null, "Adidas Own The Run Tee", "Lightweight running tee with AEROREADY and reflective elements for visibility.", 2299.00, "Activewear", "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=400", 42),
                new Product(null, "Puma Training Joggers", "Comfortable training joggers with dryCELL technology and tapered leg for a modern fit.", 3299.00, "Activewear", "https://images.unsplash.com/photo-1552902865-b72c031ac5ea?w=400", 32),
                new Product(null, "Under Armour HeatGear Tee", "Ultra-tight second-skin fit with HeatGear fabric that wicks sweat and dries fast.", 2999.00, "Activewear", "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400", 28),
                new Product(null, "Nike Windrunner Jacket", "The original running jacket with chevron design, packable hood, and water-resistant finish.", 7999.00, "Activewear", "https://images.unsplash.com/photo-1544923246-77307dd270cb?w=400", 18),
                new Product(null, "Reebok CrossFit Shorts", "Durable CrossFit shorts with Speedwick technology and reinforced seams for heavy lifts.", 2799.00, "Activewear", "https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=400", 35),
                new Product(null, "Adidas Aeroready Tank", "Moisture-absorbing tank top with mesh back panel for maximum airflow during training.", 1999.00, "Activewear", "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=400", 45)
            };

            productRepository.saveAll(Arrays.asList(products));
            System.out.println("🌱 Database seeded with " + products.length + " mock products successfully!");
        }
    }
}
