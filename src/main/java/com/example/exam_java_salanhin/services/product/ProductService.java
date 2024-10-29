package com.example.exam_java_salanhin.services.product;

import com.example.exam_java_salanhin.models.Product;
import com.example.exam_java_salanhin.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> findProducts(String search, String sort) {
        List<Product> products = productRepository.findAll();

        if (search != null && !search.isEmpty()) {
            products = products.stream()
                    .filter(product -> product.getName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if ("Sort by Default".equals(sort)) {
            return products;
        } else if ("Price: $-$$$".equals(sort)) {
            products.sort(Comparator.comparing(product -> ((Product) product).getPrice().doubleValue()));
        } else if ("Price: $$$-$".equals(sort)) {
            products.sort(Comparator.comparing(product -> ((Product) product).getPrice().doubleValue()).reversed());
        }

        return products;
    }
}