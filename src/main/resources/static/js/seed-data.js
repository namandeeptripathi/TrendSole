/*
 * ================================================
 * seed-data.js — Product Catalog Seeder
 * ================================================
 *
 * Run this ONCE from the browser console when the server is running.
 * It will POST 60 products (12 per category) to /api/products.
 *
 * Usage:
 *   1. Open http://localhost:8080 in your browser
 *   2. Open DevTools Console (F12 → Console)
 *   3. Copy-paste this entire file and press Enter
 *   4. Wait for "SEEDING COMPLETE" message
 *   5. Refresh the page to see all products
 */

(async function seedProducts() {
    const API = 'http://localhost:8080/api/products';

    const products = [
        // ============ FOOTWEAR (12) ============
        { name: 'Nike Air Max 90', description: 'Iconic Air Max design with visible Air cushioning for all-day comfort and street-ready style.', price: 12999, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400', stock: 25 },
        { name: 'Adidas Ultraboost 22', description: 'Responsive Boost midsole with Primeknit+ upper delivers incredible energy return on every step.', price: 16999, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400', stock: 18 },
        { name: 'Puma RS-X Reinvention', description: 'Retro-inspired chunky sneakers with bold colorways and Running System technology.', price: 8999, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=400', stock: 30 },
        { name: 'Converse Chuck 70 High', description: 'Premium heritage canvas high-top with vintage styling and OrthoLite cushioning.', price: 6499, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=400', stock: 35 },
        { name: 'New Balance 574 Classic', description: 'Timeless lifestyle sneaker with ENCAP midsole technology and suede/mesh upper.', price: 9499, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400', stock: 22 },
        { name: 'Vans Old Skool', description: 'The iconic sidestripe shoe in durable canvas and suede. A skateboarding legend.', price: 5499, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=400', stock: 40 },
        { name: 'Reebok Classic Leather', description: 'Clean and simple leather sneaker with soft garment leather upper and EVA midsole.', price: 7499, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=400', stock: 28 },
        { name: 'Asics Gel-Kayano 30', description: 'Premium stability running shoe with GEL technology and FF BLAST+ cushioning.', price: 14999, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=400', stock: 15 },
        { name: 'Jordan 1 Retro High OG', description: 'The shoe that started it all. Premium leather construction with Nike Air cushioning.', price: 16499, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1597045566677-8cf032ed6634?w=400', stock: 10 },
        { name: 'Timberland 6-Inch Premium', description: 'Waterproof leather boots with padded collar for comfort and iconic wheat nubuck finish.', price: 13999, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1638247025967-b4e38f787b76?w=400', stock: 20 },
        { name: 'Clarks Desert Boot', description: 'Iconic crepe-soled chukka boot in premium suede. A British footwear classic since 1950.', price: 8999, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=400', stock: 18 },
        { name: 'Dr. Martens 1460 Boot', description: 'The original 8-eye boot with AirWair bouncing sole and smooth leather upper.', price: 15499, category: 'Footwear', imageUrl: 'https://images.unsplash.com/photo-1605733160314-4fc7dac4bb16?w=400', stock: 12 },

        // ============ CLOTHING (12) ============
        { name: "Levi's 501 Original Jeans", description: "The original straight-fit jeans that started it all. Premium denim with signature riveted pockets.", price: 4999, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400', stock: 45 },
        { name: 'Nike Dri-FIT Crew Tee', description: 'Moisture-wicking performance tee with classic Nike Swoosh. Perfect for training and everyday wear.', price: 2499, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400', stock: 50 },
        { name: 'Zara Slim Fit Blazer', description: 'Tailored slim-fit blazer with notched lapels and two-button fastening. Elevate any outfit.', price: 5999, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1507679799987-c73b1266a04e?w=400', stock: 20 },
        { name: 'H&M Oversized Hoodie', description: 'Relaxed oversized hoodie in soft cotton blend with kangaroo pocket and drawstring hood.', price: 2999, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400', stock: 55 },
        { name: 'Uniqlo Merino Sweater', description: 'Extra fine merino wool crew neck sweater. Lightweight warmth with a premium soft feel.', price: 3499, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=400', stock: 30 },
        { name: 'Ralph Lauren Polo Shirt', description: 'Classic fit cotton mesh polo with signature embroidered Pony. A wardrobe essential.', price: 7999, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=400', stock: 25 },
        { name: 'Calvin Klein Logo Tee', description: 'Minimalist crew neck tee with iconic CK logo. Made from organic cotton for everyday comfort.', price: 3299, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=400', stock: 40 },
        { name: 'Tommy Hilfiger Puffer Jacket', description: 'Warm quilted puffer jacket with signature flag logo. Water-resistant nylon shell.', price: 12999, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1544923246-77307dd270cb?w=400', stock: 15 },
        { name: 'Gap Classic Chinos', description: 'Straight-fit chinos in stretch cotton twill. A polished look from office to weekend.', price: 3999, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=400', stock: 35 },
        { name: 'Mango Linen Shirt', description: 'Relaxed-fit linen shirt with a regular collar. Cool and breathable for summer styling.', price: 3499, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=400', stock: 28 },
        { name: 'Adidas Track Pants', description: 'Classic 3-Stripes track pants in doubleknit fabric. From training to streetwear.', price: 3999, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400', stock: 42 },
        { name: 'Zara Trench Coat', description: 'Belted trench coat with lapel collar and epaulettes. Water-repellent finish for rainy days.', price: 8999, category: 'Clothing', imageUrl: 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=400', stock: 12 },

        // ============ ACCESSORIES (12) ============
        { name: 'Ray-Ban Aviator Classic', description: 'Iconic pilot-shaped sunglasses with crystal green lenses and gold-tone metal frame.', price: 8999, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=400', stock: 30 },
        { name: 'Casio G-Shock GA-2100', description: 'Tough digital-analog watch with carbon core guard structure and 200m water resistance.', price: 12999, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=400', stock: 20 },
        { name: 'Nike Dri-FIT Club Cap', description: 'Lightweight structured cap with moisture-wicking sweatband and embroidered Swoosh.', price: 1999, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1588850561407-ed78c334e67a?w=400', stock: 60 },
        { name: 'Titan Raga Women Watch', description: 'Elegant analog watch with mother of pearl dial and rose gold stainless steel bracelet.', price: 6999, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1612817159949-195b6eb9e31a?w=400', stock: 15 },
        { name: 'Fossil Leather Belt', description: 'Classic leather belt with signature buckle. Hand-stitched edges with vintage finish.', price: 3499, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1624222247344-550fb60583dc?w=400', stock: 35 },
        { name: 'Tommy Hilfiger Bifold Wallet', description: 'Premium leather bifold wallet with embossed logo and multiple card slots.', price: 4499, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=400', stock: 25 },
        { name: 'Oakley Holbrook Sunglasses', description: 'Sport-performance sunglasses with Plutonite lenses and lightweight O Matter frame.', price: 9999, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=400', stock: 18 },
        { name: 'Daniel Wellington Classic Watch', description: 'Ultra-slim watch with interchangeable leather strap and minimalist Scandinavian design.', price: 11999, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1539874754764-5a96559165b0?w=400', stock: 14 },
        { name: 'Puma Evercat Baseball Cap', description: 'Curved-brim baseball cap with embroidered Puma cat logo and adjustable strap.', price: 1499, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1521369909029-2afed882baee?w=400', stock: 50 },
        { name: 'Ray-Ban Wayfarer', description: 'The cultural icon since 1956. Acetate frame with UV-protective polarized lenses.', price: 10499, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1473496169904-658ba7c44d8a?w=400', stock: 22 },
        { name: 'Swatch Originals Gent', description: 'Swiss-made watch with vibrant colorful dial and silicone strap. Bold and playful design.', price: 5999, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1509048191080-d2984bad6ae5?w=400', stock: 28 },
        { name: 'Calvin Klein Reversible Belt', description: 'Smooth leather reversible belt with sleek CK plaque buckle. Two looks in one.', price: 4999, category: 'Accessories', imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400', stock: 32 },

        // ============ BAGS (12) ============
        { name: 'Samsonite Laptop Backpack', description: 'Business-ready laptop backpack with padded compartment, USB port, and TSA-friendly design.', price: 5999, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400', stock: 20 },
        { name: 'Nike Brasilia Training Duffel', description: 'Spacious training duffel with ventilated shoe compartment and water-resistant bottom.', price: 3499, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=400', stock: 30 },
        { name: 'Wildcraft Supernova Backpack', description: 'Adventure-ready backpack with rain cover, padded straps, and 40L capacity.', price: 2999, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1581605405669-fcdf81165afa?w=400', stock: 35 },
        { name: 'Herschel Little America', description: 'Classic mountaineering-inspired backpack with magnetic strap closures and laptop sleeve.', price: 7999, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=400', stock: 15 },
        { name: 'American Tourister Urban Bag', description: 'Lightweight casual backpack with colorful design, multiple pockets, and padded back panel.', price: 2499, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1622560480654-1e0aadd0e3e2?w=400', stock: 45 },
        { name: 'Safari Polyester Daypack', description: 'Everyday daypack with organizer pocket, bottle holder, and reflective tape for visibility.', price: 1999, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1585916420730-d7f95e942d43?w=400', stock: 50 },
        { name: 'Tommy Hilfiger Crossbody', description: 'Compact crossbody bag with signature stripe and adjustable strap. Perfect for on-the-go.', price: 5499, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1566150905458-1bf1fc113f0d?w=400', stock: 22 },
        { name: 'Puma Phase Backpack', description: 'Sleek sporty backpack with zip main compartment, front pocket, and Puma branding.', price: 2299, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1491637639811-60e2756cc1c7?w=400', stock: 40 },
        { name: 'Fossil Leather Tote', description: 'Spacious leather tote with zipper closure, interior pockets, and vintage brass hardware.', price: 9999, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=400', stock: 12 },
        { name: 'Skybags Casual Daypack', description: 'Trendy casual backpack with rain cover, laptop compartment, and ergonomic design.', price: 1799, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=400', stock: 55 },
        { name: 'Adidas Linear Duffel', description: 'Versatile training duffel with adjustable shoulder strap and front zip pocket.', price: 2999, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400', stock: 28 },
        { name: "Levi's Canvas Tote", description: "Durable canvas tote bag with iconic Red Tab logo and reinforced handles for everyday use.", price: 2499, category: 'Bags', imageUrl: 'https://images.unsplash.com/photo-1594223274512-ad4803739b7c?w=400', stock: 32 },

        // ============ ACTIVEWEAR (12) ============
        { name: 'Nike Dri-FIT Training Shorts', description: 'Lightweight training shorts with moisture-wicking fabric and internal brief for support.', price: 2499, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=400', stock: 40 },
        { name: 'Adidas Tiro Track Pants', description: 'Slim-fit training pants with AEROREADY moisture management and iconic 3-Stripes.', price: 3999, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400', stock: 35 },
        { name: 'Puma Active Training Tee', description: 'dryCELL moisture-wicking training tee with flatlock stitching for comfort during workouts.', price: 1999, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=400', stock: 48 },
        { name: 'Under Armour Tech Polo', description: 'Quick-drying anti-odor polo with UPF 30+ sun protection. From the course to the office.', price: 4499, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=400', stock: 25 },
        { name: 'Reebok Training Tank', description: 'Breathable mesh tank top with Speedwick technology for high-intensity training sessions.', price: 1799, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400', stock: 38 },
        { name: 'Nike Pro Training Leggings', description: 'Compression-fit leggings with Dri-FIT technology and mesh ventilation panels.', price: 3499, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=400', stock: 30 },
        { name: 'Adidas Own The Run Tee', description: 'Lightweight running tee with AEROREADY and reflective elements for visibility.', price: 2299, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=400', stock: 42 },
        { name: 'Puma Training Joggers', description: 'Comfortable training joggers with dryCELL technology and tapered leg for a modern fit.', price: 3299, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1552902865-b72c031ac5ea?w=400', stock: 32 },
        { name: 'Under Armour HeatGear Tee', description: 'Ultra-tight second-skin fit with HeatGear fabric that wicks sweat and dries fast.', price: 2999, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400', stock: 28 },
        { name: 'Nike Windrunner Jacket', description: 'The original running jacket with chevron design, packable hood, and water-resistant finish.', price: 7999, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1544923246-77307dd270cb?w=400', stock: 18 },
        { name: 'Reebok CrossFit Shorts', description: 'Durable CrossFit shorts with Speedwick technology and reinforced seams for heavy lifts.', price: 2799, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=400', stock: 35 },
        { name: 'Adidas Aeroready Tank', description: 'Moisture-absorbing tank top with mesh back panel for maximum airflow during training.', price: 1999, category: 'Activewear', imageUrl: 'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=400', stock: 45 }
    ];

    console.log('🚀 Starting TrendSole Product Seeding...');
    console.log('📦 Total products to seed: ' + products.length);

    var success = 0;
    var failed = 0;

    for (var i = 0; i < products.length; i++) {
        try {
            var response = await fetch(API, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(products[i])
            });
            if (response.ok) {
                success++;
                console.log('✅ [' + (i+1) + '/' + products.length + '] ' + products[i].name);
            } else {
                failed++;
                console.error('❌ [' + (i+1) + '] Failed: ' + products[i].name);
            }
        } catch (err) {
            failed++;
            console.error('❌ [' + (i+1) + '] Error: ' + products[i].name, err);
        }
    }

    console.log('');
    console.log('🎉 SEEDING COMPLETE!');
    console.log('✅ Success: ' + success);
    console.log('❌ Failed: ' + failed);
    console.log('🔄 Refresh the page to see your new products!');
})();
