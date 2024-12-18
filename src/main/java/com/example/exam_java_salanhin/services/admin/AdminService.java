package com.example.exam_java_salanhin.services.admin;

import com.example.exam_java_salanhin.models.*;
import com.example.exam_java_salanhin.repositories.BrandRepository;
import com.example.exam_java_salanhin.repositories.CategoryRepository;
import com.example.exam_java_salanhin.repositories.ProductImageRepository;
import com.example.exam_java_salanhin.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

    public void saveCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            return;
        }

        categoryRepository.save(category);
    }

    public void updateCategory(Long categoryId, String categoryName) {
        if (categoryRepository.existsByNameAndIdNot(categoryName, categoryId)) {
            return;
        }

        Category category = getCategoryById(categoryId);
        if (category != null) {
            category.setName(categoryName);
            categoryRepository.save(category);
        }
    }

    public void deleteCategoryById(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Long brandId) {
        return brandRepository.findById(brandId).orElse(null);
    }

    public void saveBrand(Brand brand) {
        if (brandRepository.existsByName(brand.getName())) {
            return;
        }

        brandRepository.save(brand);
    }

    public void updateBrand(Long brandId, String brandName) {
        if (brandRepository.existsByNameAndIdNot(brandName, brandId)) {
            return;
        }

        Brand brand = getBrandById(brandId);
        if (brand != null) {
            brand.setName(brandName);
            brandRepository.save(brand);
        }
    }

    public void deleteBrandById(Long brandId) {
        brandRepository.deleteById(brandId);
    }

    @Transactional
    public void deleteProductImage(Long imageId) {
        Optional<ProductImage> imageOptional = productImageRepository.findById(imageId);

        if (imageOptional.isPresent()) {
            ProductImage image = imageOptional.get();
            Product product = image.getProduct();

            if (product != null) {
                product.getProductImages().remove(image);
            }

            String relativePath = image.getImagePath();
            String absolutePath = "src/main/resources/static/" + relativePath;
            File file = new File(absolutePath);

            if (file.exists()) {
                file.delete();
            }

            productImageRepository.deleteById(imageId);
        }
    }

    public void createProduct(Product product, MultipartFile[] images) {
        product.setCreatedAt(LocalDateTime.now());
        productRepository.save(product);

        if (images != null && images.length > 0) {
            saveImages(product, images);
        }
    }

    public void updateProduct(Product product, MultipartFile[] images) {
        Optional<Product> currentOptionalProduct = productRepository.findByIdWithImages(product.getId());
        if (currentOptionalProduct.isEmpty()) {
            return;
        }

        Product currentProduct = currentOptionalProduct.get();
        currentProduct.setName(product.getName());
        currentProduct.setPrice(product.getPrice());
        currentProduct.setSize(product.getSize());
        currentProduct.setColor(product.getColor());
        currentProduct.setDescription(product.getDescription());
        currentProduct.setStockQuantity(product.getStockQuantity());
        currentProduct.setBrand(product.getBrand());
        currentProduct.setCategory(product.getCategory());
        currentProduct.setCreatedAt(product.getCreatedAt());
        currentProduct.setUpdatedAt(LocalDateTime.now());

        if (images != null && images.length > 0) {
            saveImages(product, images);
        }
        productRepository.save(currentProduct);
    }

    public void saveImages(Product product, MultipartFile[] images) {
        String folderPath = "src/main/resources/static/images/" + product.getId();
        File directory = new File(folderPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        int index = getNextAvailableIndex(product.getId());

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String extension = getFileExtension(image.getOriginalFilename());

                String newFileName = product.getId() + "_" + index + extension;
                String newFilePath = folderPath + "/" + newFileName;
                String dbPath = "images/" + product.getId() + "/" + newFileName;

                try {
                    Files.write(Paths.get(newFilePath), image.getBytes());

                    ProductImage productImage = new ProductImage();
                    productImage.setImagePath(dbPath);
                    productImage.setProduct(product);
                    productImageRepository.save(productImage);

                    index++;
                } catch (IOException ignored) {
                }
            }
        }
    }

    private int getNextAvailableIndex(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);

        int maxIndex = 0;
        for (ProductImage image : productImages) {
            String fileName = image.getImagePath();
            String[] parts = fileName.split("_");
            if (parts.length > 1) {
                try {
                    int index = Integer.parseInt(parts[1].split("\\.")[0]);
                    if (index > maxIndex) {
                        maxIndex = index;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return maxIndex + 1;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    public void deleteProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            String productImagesFolderPath = "src/main/resources/static/images/" + product.getId();
            deleteFolder(new File(productImagesFolderPath));

            productRepository.delete(product);
        }
    }

    private void deleteFolder(File folder) {
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
            folder.delete();
        }
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public void setProductCategoryAndBrand(Product product, Long categoryId, Long brandId) {
        Category category = this.getCategoryById(categoryId);
        this.assignCategoryToProduct(product, category);

        if (brandId != null) {
            Brand brand = this.getBrandById(brandId);
            this.assignBrandToProduct(product, brand);
        }
    }

    public void assignCategoryToProduct(Product product, Category category) {
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
        if (existingCategory.isPresent()) {
            product.setCategory(existingCategory.get());
        } else {
            categoryRepository.save(category);
            product.setCategory(category);
        }
    }

    public void assignBrandToProduct(Product product, Brand brand) {
        Optional<Brand> existingBrand = brandRepository.findByName(brand.getName());
        if (existingBrand.isPresent()) {
            product.setBrand(existingBrand.get());
        } else {
            brandRepository.save(brand);
            product.setBrand(brand);
        }
    }
}
