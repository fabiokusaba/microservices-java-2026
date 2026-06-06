package br.edu.atitus.productservice.controllers;

import br.edu.atitus.productservice.clients.CurrencyClient;
import br.edu.atitus.productservice.clients.CurrencyResponse;
import br.edu.atitus.productservice.dtos.ProductDTO;
import br.edu.atitus.productservice.entities.ProductEntity;
import br.edu.atitus.productservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Value("${server.port}")
    private String port;

    private final ProductRepository productRepository;
    private final CurrencyClient currencyClient;

    public ProductController(ProductRepository productRepository, CurrencyClient currencyClient) {
        this.productRepository = productRepository;
        this.currencyClient = currencyClient;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(
            @PathVariable Long id,
            @RequestParam String targetCurrency
    ) throws Exception {
        targetCurrency = targetCurrency.toUpperCase();

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product not found"));

        Double convertedPrice;
        String environment = "Product-Service running on port " + port;
        String requestCurrency = targetCurrency;

        if (targetCurrency.equals(product.getCurrency())) {
            convertedPrice = product.getPrice();
        } else {
            CurrencyResponse currency = currencyClient.getCurrency(product.getCurrency(),  targetCurrency);
            convertedPrice = currency.conversionRate() * product.getPrice();
            environment = environment + " - " + currency.environment();
        }

        ProductDTO dto = new ProductDTO(
                product.getId(),
                product.getDescription(),
                product.getBrand(),
                product.getModel(),
                product.getCurrency(),
                product.getPrice(),
                product.getStock(),
                convertedPrice,
                environment,
                requestCurrency
        );

        return ResponseEntity.ok(dto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        String message = exception.getMessage().replace("/r/n", "");
        return ResponseEntity.badRequest().body(message);
    }
}
