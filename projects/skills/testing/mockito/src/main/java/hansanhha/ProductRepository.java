package hansanhha;

import java.util.HashMap;
import java.util.Map;

public class ProductRepository {

    private final Map<Long, Product> products = new HashMap<>();

    public Product save(Product product) {
        product.setId((long) (products.size() + 1));
        products.put(product.getId(), product);
        return product;
    }

    public void update(Product oldProduct, Product newProduct) {
        products.replace(oldProduct.getId(), oldProduct, newProduct);
    }

    public Product findById(Long id) {
        return products.get(id);
    }

    public void remove(Long id) {
        products.remove(id);
    }
}
