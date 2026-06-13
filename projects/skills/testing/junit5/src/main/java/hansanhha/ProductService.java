package hansanhha;

import java.util.HashMap;
import java.util.Map;

public class ProductService {

    private final Map<Long, Product> productRepository = new HashMap<>();

    public Product create(String name, int quantity, int amount) {
        Product product = new Product((long) (productRepository.size() + 1), name, quantity, amount);
        productRepository.put(product.getId(), product);
        return product;
    }

    public Product get(Long id) {
        return productRepository.get(id);
    }

    public void delete(Long id) {
        productRepository.remove(id);
    }
}
