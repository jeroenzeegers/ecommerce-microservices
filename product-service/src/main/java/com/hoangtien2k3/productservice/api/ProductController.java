package com.hoangtien2k3.productservice.api;

import com.hoangtien2k3.productservice.dto.ProductDto;
import com.hoangtien2k3.productservice.dto.response.collection.DtoCollectionResponse;
import com.hoangtien2k3.productservice.service.ProductService;
import datadog.trace.api.Trace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private final ProductService productService;

    // Get a list of all products
    @GetMapping
    @Trace(operationName = "product.findAll")
    public Flux<List<ProductDto>> findAll() {
        log.info("ProductDto List, controller; fetch all categories");
        return productService.findAll();
    }

    // Get detailed information of a specific product
    @GetMapping("/{productId}")
    @Trace(operationName = "product.findById")
    public ResponseEntity<ProductDto> findById(@PathVariable("productId")
                                               @NotBlank(message = "Input must not be blank!")
                                               @Valid final String productId) {
        log.info("ProductDto, resource; fetch product by id");
        return ResponseEntity.ok(productService.findById(Integer.parseInt(productId)));
    }

    // Create a new product
    @PostMapping
    @Trace(operationName = "product.save")
    public ResponseEntity<ProductDto> save(@RequestBody
                                           @NotNull(message = "Input must not be NULL!")
                                           @Valid final ProductDto productDto) {
        log.info("ProductDto, resource; save product");
        return ResponseEntity.ok(productService.save(productDto));
    }

    // Update information of all product
    @PutMapping
    public ResponseEntity<ProductDto> update(@RequestBody
                                             @NotNull(message = "Input must not be NULL!")
                                             @Valid final ProductDto productDto) {
        log.info("ProductDto, resource; update product");
        return ResponseEntity.ok(productService.update(productDto));
    }

    // Update information of a product:
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> update(@PathVariable("productId")
                                             @NotBlank(message = "Input must not be blank!")
                                             @Valid final String productId,
                                             @RequestBody
                                             @NotNull(message = "Input must not be NULL!")
                                             @Valid final ProductDto productDto) {
        log.info("ProductDto, resource; update product with productId");
        return ResponseEntity.ok(productService.update(Integer.parseInt(productId), productDto));
    }

    // Delete a product
    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("productId") final String productId) {
        log.info("Boolean, resource; delete product by id");
        productService.deleteById(Integer.parseInt(productId));
        return ResponseEntity.ok(true);
    }

}
