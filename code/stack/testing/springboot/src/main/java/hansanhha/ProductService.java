package hansanhha;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(String name, int quantity, int amount) {
        Product product = new Product(name, quantity, amount);
        return productRepository.save(product);
    }

    public void updateName(Long id, String newName) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product를 찾을 수 없습니다"));
        product.setName(newName);
    }

    public Product get(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product를 찾을 수 없습니다"));
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
