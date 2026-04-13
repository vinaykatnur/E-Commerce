package com.electromart.config;

import com.electromart.entity.Category;
import com.electromart.entity.Product;
import com.electromart.repository.CategoryRepository;
import com.electromart.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        Map<String, Category> categories = new LinkedHashMap<>();
        categories.put("phones", createCategory("Phones", "phones", "5G smartphones, camera phones, and premium flagships."));
        categories.put("laptops", createCategory("Laptops", "laptops", "Ultrabooks, gaming laptops, and workstations."));
        categories.put("earphones", createCategory("Earphones", "earphones", "TWS earbuds, neckbands, and wired audio gear."));

        List<Product> products = List.of(
                product(categories.get("phones"), "Apple iPhone 15", "Apple", 79900, 18, true, "https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=900&q=80", "A17-powered iPhone with brilliant OLED display and all-day battery."),
                product(categories.get("phones"), "Samsung Galaxy S24", "Samsung", 74999, 20, true, "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?auto=format&fit=crop&w=900&q=80", "Compact flagship Android phone with Galaxy AI and vibrant AMOLED panel."),
                product(categories.get("phones"), "OnePlus 12", "OnePlus", 64999, 14, true, "https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80", "Snapdragon flagship with blazing fast charging and curved LTPO display."),
                product(categories.get("phones"), "Google Pixel 8", "Google", 75999, 12, true, "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?auto=format&fit=crop&w=900&q=80", "Camera-first Pixel experience with clean Android and smart AI features."),
                product(categories.get("phones"), "Xiaomi 14", "Xiaomi", 69999, 16, false, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80", "Leica-tuned cameras and flagship performance in a compact body."),
                product(categories.get("phones"), "Nothing Phone (2)", "Nothing", 44999, 19, false, "https://images.unsplash.com/photo-1580910051074-3eb694886505?auto=format&fit=crop&w=900&q=80", "Distinctive Glyph design paired with smooth Nothing OS software."),
                product(categories.get("phones"), "Realme GT 6", "Realme", 40999, 22, false, "https://images.unsplash.com/photo-1567581935884-3349723552ca?auto=format&fit=crop&w=900&q=80", "Value flagship with bright display, fast charging, and strong thermals."),
                product(categories.get("phones"), "Vivo X100", "Vivo", 63999, 11, false, "https://images.unsplash.com/photo-1583573636246-18cb2246697d?auto=format&fit=crop&w=900&q=80", "ZEISS-backed cameras and flagship MediaTek chipset for creators."),
                product(categories.get("phones"), "Oppo Reno 11 Pro", "Oppo", 39999, 17, false, "https://images.unsplash.com/photo-1585060544812-6b45742d762f?auto=format&fit=crop&w=900&q=80", "Slim premium phone with portrait camera tuning and vivid AMOLED."),
                product(categories.get("phones"), "Motorola Edge 50 Pro", "Motorola", 31999, 24, false, "https://images.unsplash.com/photo-1605236453806-6ff36851218e?auto=format&fit=crop&w=900&q=80", "Near-stock Android, 125W charging, and Pantone-validated display."),
                product(categories.get("phones"), "iQOO 12", "iQOO", 52999, 15, false, "https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?auto=format&fit=crop&w=900&q=80", "Performance-focused flagship with advanced cooling and gaming boost."),
                product(categories.get("phones"), "Samsung Galaxy A55", "Samsung", 39999, 25, false, "https://images.unsplash.com/photo-1606041011872-596597976b25?auto=format&fit=crop&w=900&q=80", "Premium mid-range phone with metal frame and dependable cameras."),
                product(categories.get("laptops"), "Apple MacBook Air M3", "Apple", 114900, 10, true, "https://images.unsplash.com/photo-1517336714739-489689fd1ca8?auto=format&fit=crop&w=900&q=80", "Thin and silent laptop with class-leading battery life and M3 performance."),
                product(categories.get("laptops"), "Dell XPS 13", "Dell", 129999, 8, true, "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80", "Premium ultrabook with edge-to-edge display and excellent build."),
                product(categories.get("laptops"), "HP Spectre x360", "HP", 124999, 9, true, "https://images.unsplash.com/photo-1511385348-a52b4a160dc2?auto=format&fit=crop&w=900&q=80", "Convertible laptop with OLED touch display and premium craftsmanship."),
                product(categories.get("laptops"), "Lenovo Yoga Slim 7i", "Lenovo", 89999, 13, false, "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=900&q=80", "Balanced Intel Evo laptop for office work, media, and portability."),
                product(categories.get("laptops"), "ASUS ROG Zephyrus G14", "ASUS", 159999, 7, true, "https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?auto=format&fit=crop&w=900&q=80", "Compact gaming powerhouse with RTX graphics and premium display."),
                product(categories.get("laptops"), "Acer Swift Go 14", "Acer", 74999, 15, false, "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80", "OLED productivity laptop with strong performance at a sharp price."),
                product(categories.get("laptops"), "MSI Katana 15", "MSI", 104999, 6, false, "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?auto=format&fit=crop&w=900&q=80", "Gaming laptop with high-refresh display and RTX graphics."),
                product(categories.get("laptops"), "Samsung Galaxy Book4 Pro", "Samsung", 131999, 8, false, "https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?auto=format&fit=crop&w=900&q=80", "AMOLED Windows laptop that pairs beautifully with Galaxy devices."),
                product(categories.get("laptops"), "LG Gram 16", "LG", 127999, 7, false, "https://images.unsplash.com/photo-1504707748692-419802cf939d?auto=format&fit=crop&w=900&q=80", "Large-screen ultra-light laptop built for mobility and battery life."),
                product(categories.get("laptops"), "Lenovo Legion Slim 5", "Lenovo", 119999, 9, false, "https://images.unsplash.com/photo-1603302576837-37561b2e2302?auto=format&fit=crop&w=900&q=80", "Gaming and creator laptop with efficient thermals and QHD display."),
                product(categories.get("laptops"), "ASUS Vivobook S 15 OLED", "ASUS", 82999, 12, false, "https://images.unsplash.com/photo-1587614382346-4ec70e388b28?auto=format&fit=crop&w=900&q=80", "Style-forward OLED laptop for students and everyday creators."),
                product(categories.get("laptops"), "HP Omen 16", "HP", 139999, 6, false, "https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?auto=format&fit=crop&w=900&q=80", "Serious gaming laptop with spacious cooling and immersive display."),
                product(categories.get("laptops"), "Dell Inspiron 14 Plus", "Dell", 94999, 10, false, "https://images.unsplash.com/photo-1611078489935-0cb964de46d6?auto=format&fit=crop&w=900&q=80", "Reliable all-rounder for work, study, and light creative workloads."),
                product(categories.get("earphones"), "Apple AirPods Pro (2nd Gen)", "Apple", 24900, 22, true, "https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?auto=format&fit=crop&w=900&q=80", "Flagship ANC earbuds with seamless Apple ecosystem pairing."),
                product(categories.get("earphones"), "Sony WF-1000XM5", "Sony", 24990, 18, true, "https://images.unsplash.com/photo-1606741965562-7f5c0b6d0d44?auto=format&fit=crop&w=900&q=80", "Top-tier noise cancellation and richly tuned audio for commuters."),
                product(categories.get("earphones"), "Samsung Galaxy Buds2 Pro", "Samsung", 17999, 20, false, "https://images.unsplash.com/photo-1625948515291-69613efd103f?auto=format&fit=crop&w=900&q=80", "Comfortable premium earbuds optimized for Galaxy users."),
                product(categories.get("earphones"), "OnePlus Buds Pro 2", "OnePlus", 11999, 25, false, "https://images.unsplash.com/photo-1658578905031-6f074d30f12e?auto=format&fit=crop&w=900&q=80", "Dynaudio-tuned earbuds with spatial audio and robust ANC."),
                product(categories.get("earphones"), "Nothing Ear", "Nothing", 11999, 28, true, "https://images.unsplash.com/photo-1588423771073-b8903fbb85b5?auto=format&fit=crop&w=900&q=80", "Transparent design language with lively sound and polished app controls."),
                product(categories.get("earphones"), "JBL Tune Beam", "JBL", 5999, 35, false, "https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=900&q=80", "Punchy bass-forward TWS with ANC and long battery backup."),
                product(categories.get("earphones"), "Realme Buds Air 6 Pro", "Realme", 4999, 32, false, "https://images.unsplash.com/photo-1577174881658-0f30ed549adc?auto=format&fit=crop&w=900&q=80", "Feature-rich budget ANC buds with LHDC support."),
                product(categories.get("earphones"), "Boat Airdopes 141 ANC", "boAt", 2499, 45, false, "https://images.unsplash.com/photo-1606400082777-ef05f3c5cde2?auto=format&fit=crop&w=900&q=80", "Affordable daily-use earbuds with bass-heavy signature and ENx tech."),
                product(categories.get("earphones"), "Oppo Enco Air3 Pro", "Oppo", 4999, 30, false, "https://images.unsplash.com/photo-1627989580309-bfaf3e58af6f?auto=format&fit=crop&w=900&q=80", "Balanced sound, lightweight fit, and solid value for music lovers."),
                product(categories.get("earphones"), "Vivo TWS 3e", "Vivo", 1899, 40, false, "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=900&q=80", "Entry-level TWS option with low-latency gaming mode."),
                product(categories.get("earphones"), "CMF Buds Pro", "CMF", 3499, 27, false, "https://images.unsplash.com/photo-1621274147744-cfb903ad50f5?auto=format&fit=crop&w=900&q=80", "Stylish ANC earbuds with warm tuning and generous battery life."),
                product(categories.get("earphones"), "Sony WI-C100 Neckband", "Sony", 1999, 38, false, "https://images.unsplash.com/photo-1518444028785-8f5f61ab3fbe?auto=format&fit=crop&w=900&q=80", "Reliable Bluetooth neckband with strong battery endurance."),
                product(categories.get("earphones"), "Sennheiser CX Plus", "Sennheiser", 12990, 14, false, "https://images.unsplash.com/photo-1583394838336-acd977736f90?auto=format&fit=crop&w=900&q=80", "Audiophile-friendly TWS earbuds with balanced detail and ANC."),
                product(categories.get("earphones"), "Jabra Elite 4", "Jabra", 9999, 16, false, "https://images.unsplash.com/photo-1620207418302-439b387441b0?auto=format&fit=crop&w=900&q=80", "Secure fit earbuds ideal for calls, workouts, and everyday use."),
                product(categories.get("earphones"), "Bose QuietComfort Earbuds II", "Bose", 25900, 12, true, "https://images.unsplash.com/photo-1668649178854-4d43afdc7cb9?auto=format&fit=crop&w=900&q=80", "Premium ANC earbuds with deeply immersive isolation and comfort."),
                product(categories.get("earphones"), "Anker Soundcore Liberty 4 NC", "Anker", 8999, 19, false, "https://images.unsplash.com/photo-1631867675167-90a456a90863?auto=format&fit=crop&w=900&q=80", "Feature-packed ANC earbuds with app EQ and extended battery.")
        );

        productRepository.saveAll(products);
    }

    private Category createCategory(String name, String slug, String description) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    private Product product(Category category, String name, String brand, int price, int stock, boolean featured, String imageUrl, String description) {
        Product product = new Product();
        product.setCategory(category);
        product.setName(name);
        product.setSlug(name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", ""));
        product.setBrand(brand);
        product.setPrice(BigDecimal.valueOf(price));
        product.setStock(stock);
        product.setFeatured(featured);
        product.setImageUrl(imageUrl);
        product.setDescription(description);
        product.setActive(true);
        return product;
    }
}
