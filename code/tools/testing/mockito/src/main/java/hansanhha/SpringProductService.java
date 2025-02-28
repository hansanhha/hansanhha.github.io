package hansanhha;

import org.springframework.stereotype.Service;

@Service
public class SpringProductService {

    private final SpringProductRepository productRepository;

    public SpringProductService(SpringProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(String name, int quantity, int amount) {
        Product product = new Product(name, quantity, amount);
        return productRepository.save(product);
    }

    public void updateName(Product product, String newName) {
        Product newProduct = new Product(newName, product.getQuantity(), product.getAmount());
        productRepository.update(product, newProduct);
    }

    public Product get(Long id) {
        return productRepository.findById(id);
    }

    public void delete(Long id) {
        productRepository.remove(id);
    }
}
