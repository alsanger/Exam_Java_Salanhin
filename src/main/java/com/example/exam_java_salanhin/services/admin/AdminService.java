package com.example.exam_java_salanhin.services.admin;

import com.example.exam_java_salanhin.models.*;
import com.example.exam_java_salanhin.repositories.BrandRepository;
import com.example.exam_java_salanhin.repositories.CategoryRepository;
import com.example.exam_java_salanhin.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

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

//    public void saveProduct(Product product) {
//        product.setCreatedAt(LocalDateTime.now());
//        productRepository.save(product);
//    }

    public void saveProductWithImages(Product product, MultipartFile[] images) {
        // Сохранить продукт для получения ID
        productRepository.save(product);

        // Обработка и сохранение изображений
        if (images != null && images.length > 0) {
            saveImagesForProduct(product.getId(), images);
        }
    }

    // Метод для сохранения изображений продукта с переименованием файлов
    private void saveImagesForProduct(Long productId, MultipartFile[] images) {
        String folderPath = "src/main/resources/static/images/" + productId;
        File directory = new File(folderPath);

        // Создаем папку, если она не существует
        if (!directory.exists() && !directory.mkdirs()) {
            return; // Не удалось создать папку, просто выходим из метода
        }

        // Счетчик для имен файлов
        int counter = 1;

        // Обработка каждого файла
        for (MultipartFile image : images) {
            if (!image.isEmpty() && isImageFile(image)) {
                String newFileName = productId + "_" + counter + getFileExtension(image);
                saveImageFile(image, folderPath, newFileName);
                counter++;
            }
        }
    }

    // Метод для сохранения одного файла изображения с указанным именем
    private void saveImageFile(MultipartFile image, String folderPath, String fileName) {
        try {
            Path path = Paths.get(folderPath, fileName);

            // Проверяем, если файл уже существует, не перезаписываем его
            if (!Files.exists(path)) {
                Files.write(path, image.getBytes());
            }
        } catch (Exception e) {
            // Логирование ошибки при необходимости, но не выбрасываем исключение
        }
    }

    // Метод для получения расширения файла
    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return ""; // Вернуть пустую строку, если расширение отсутствует
    }

//    private void saveImagesForProduct(Long productId, MultipartFile[] images) {
//        String folderPath = "src/main/resources/static/images/" + productId;
//        File directory = new File(folderPath);
//
//        if (!directory.exists() && !directory.mkdirs()) {
//            return;
//        }
//
//        for (MultipartFile image : images) {
//            if (!image.isEmpty() && isImageFile(image)) {
//                saveImageFile(image, folderPath);
//            }
//        }
//    }
//
//    private void saveImageFile(MultipartFile image, String folderPath) {
//        try {
//            Path path = Paths.get(folderPath, image.getOriginalFilename());
//
//            if (!Files.exists(path)) {
//                Files.write(path, image.getBytes());
//            }
//        } catch (Exception ignored) {}
//    }
//
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
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
