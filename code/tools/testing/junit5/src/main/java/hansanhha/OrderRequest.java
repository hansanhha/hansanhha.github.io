package hansanhha;

public record OrderRequest(
        Long productId,
        Long quantity,
        int amount) {


}
