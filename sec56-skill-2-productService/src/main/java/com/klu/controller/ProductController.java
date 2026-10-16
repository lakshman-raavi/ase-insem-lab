package com.klu.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.model.Product;
import com.klu.repository.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController 
{

    private final ProductRepository repository;

    @Value("${server.port}")
    private String port;

    public ProductController(ProductRepository repository) 
    {
        this.repository = repository;
    }
    
    //http://localhost:8001/products
    @GetMapping
    public Map<String, Object> getAllProducts() 
    {

        List<Product> products = repository.findAll();

        return Map.of(
                "service", "Product Service",
                "instancePort", port,
                "products", products
        );
    }
    
    //http://localhost:8001/products/1
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(product ->
                        ResponseEntity.ok(
                                Map.of(
                                        "service",
                                        "Product Service",
                                        "instancePort",
                                        port,
                                        "product",
                                        product
                                )
                        )
                )
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }
    
    //http://localhost:8001/products
	//    {
	//    	  "name":"Smart Watch",
	//    	  "price":2599.0
	//    }
    @PostMapping
    public Product addProduct(@RequestBody Product product) 
    {

        return repository.save(product);
    }
    
    //http://localhost:8001/products/1
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,@RequestBody Product product) {

        return repository.findById(id)
                .map(existing -> {

                    existing.setName(product.getName());
                    existing.setPrice(product.getPrice());

                    return ResponseEntity.ok(
                            repository.save(existing)
                    );
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }
    
    //http://localhost:8001/products/2
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id) {

        if (!repository.existsById(id)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        repository.deleteById(id);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Product deleted successfully"
                )
        );
    }
}