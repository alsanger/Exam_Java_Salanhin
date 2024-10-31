package com.example.exam_java_salanhin.controllers;

import com.example.exam_java_salanhin.models.Brand;
import com.example.exam_java_salanhin.models.Category;
import com.example.exam_java_salanhin.models.Product;
import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.services.admin.AdminService;
import com.example.exam_java_salanhin.services.user.UserService;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    @ModelAttribute("username")
    public String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        return (user != null) ? user.getFirstName() : null;
    }

//    @ModelAttribute("username")
//    public String getUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        HttpSession session = request.getSession(false);
//        User user = null;
//
//        if (session != null) {
//            user = (User) session.getAttribute("user");
//        }
//
//        if (user != null && "ROLE_ADMIN".equals(user.getRole().getName())) {
//            return user.getFirstName();
//        } else {
//            response.sendRedirect("/user/login");
//            return null;
//        }
//    }

    @ModelAttribute("role")
    public String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        return (user != null) ? user.getRole().getName() : null;
    }

    @GetMapping("/images/{productId}/{imageName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable Long productId, @PathVariable String imageName) {
        try {
            Path filePath = Paths.get("src/main/resources/static/images/" + productId + "/" + imageName);

            if (Files.exists(filePath) && Files.isReadable(filePath)) {
                Resource file = new UrlResource(filePath.toUri());

                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(file);
            }
        } catch (Exception ignored) {}
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/workAreaAdmin")
    public String workAreaAdmin() {
        return "admin/workAreaAdmin";
    }

    @GetMapping("/admin/createUserFromAdmin")
    public ModelAndView createUserFromAdmin() {
        return userController.createUser("", true);
    }

    @GetMapping("/admin/usersList")
    public ModelAndView usersList() {
        ModelAndView modelAndView = new ModelAndView();

        List<User> users = userService.getAllUsers();
        modelAndView.addObject("users", users);

        modelAndView.setViewName("admin/usersList");
        return modelAndView;
    }

    @PostMapping("/admin/updateUserFromAdmin")
    public ModelAndView updateUserFromAdmin(@RequestParam(value = "updatedUserID") Long updatedUserID) {
        return userController.updateUser(updatedUserID, "", true, null);
    }

    @GetMapping("/admin/manageCategories")
    public ModelAndView manageCategories() {
        ModelAndView modelAndView = new ModelAndView();

        List<Category> categories = adminService.getAllCategories();
        categories.sort(Comparator.comparing(Category::getName));

        modelAndView.addObject("categories", categories);
        modelAndView.setViewName("admin/category/categoriesList");
        return modelAndView;
    }

    @GetMapping("/admin/createCategory")
    public ModelAndView createCategory() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/category/createCategory");
        return modelAndView;
    }

    @PostMapping("/admin/createCategory")
    public String createCategory(@RequestParam("categoryName") String categoryName) {
        Category newCategory = new Category();
        newCategory.setName(categoryName);

        adminService.saveCategory(newCategory);

        return "redirect:/admin/manageCategories";
    }

    @GetMapping("/admin/editCategory")
    public ModelAndView editCategory(@RequestParam("categoryId") Long categoryId) {
        ModelAndView modelAndView = new ModelAndView();

        Category category = adminService.getCategoryById(categoryId);
        modelAndView.addObject("category", category);
        modelAndView.setViewName("admin/category/editCategory");
        return modelAndView;
    }

    @PostMapping("/admin/updateCategory")
    public String updateCategory(@RequestParam("categoryId") Long categoryId, @RequestParam("categoryName") String categoryName) {
        adminService.updateCategory(categoryId, categoryName);
        return "redirect:/admin/manageCategories";
    }

    @PostMapping("/admin/deleteCategory")
    public String deleteCategory(@RequestParam("categoryId") Long categoryId) {
        adminService.deleteCategoryById(categoryId);
        return "redirect:/admin/manageCategories";
    }

    @GetMapping("/admin/manageBrands")
    public ModelAndView manageBrands() {
        ModelAndView modelAndView = new ModelAndView();

        List<Brand> brands = adminService.getAllBrands();
        brands.sort(Comparator.comparing(Brand::getName));

        modelAndView.addObject("brands", brands);
        modelAndView.setViewName("admin/brand/brandsList");
        return modelAndView;
    }

    @GetMapping("/admin/createBrand")
    public ModelAndView createBrand() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/brand/createBrand");
        return modelAndView;
    }

    @PostMapping("/admin/createBrand")
    public String createBrand(@RequestParam("brandName") String brandName) {
        Brand newBrand = new Brand();
        newBrand.setName(brandName);

        adminService.saveBrand(newBrand);

        return "redirect:/admin/manageBrands";
    }

    @GetMapping("/admin/editBrand")
    public ModelAndView editBrand(@RequestParam("brandId") Long brandId) {
        ModelAndView modelAndView = new ModelAndView();

        Brand brand = adminService.getBrandById(brandId);
        modelAndView.addObject("brand", brand);
        modelAndView.setViewName("admin/brand/editBrand");
        return modelAndView;
    }

    @PostMapping("/admin/updateBrand")
    public String updateBrand(@RequestParam("brandId") Long brandId,
                              @RequestParam("brandName") String brandName) {
        adminService.updateBrand(brandId, brandName);
        return "redirect:/admin/manageBrands";
    }

    @PostMapping("/admin/deleteBrand")
    public String deleteBrand(@RequestParam("brandId") Long brandId) {
        adminService.deleteBrandById(brandId);
        return "redirect:/admin/manageBrands";
    }

    @GetMapping("/admin/manageProducts")
    public ModelAndView manageProducts() {
        ModelAndView modelAndView = new ModelAndView();

        List<Product> products = adminService.getAllProducts();
        products.sort(Comparator.comparing(Product::getName));

        modelAndView.addObject("products", products);
        modelAndView.setViewName("admin/product/manageProducts");
        return modelAndView;
    }

    @GetMapping("/admin/createProduct")
    public ModelAndView createProduct() {
        ModelAndView modelAndView = new ModelAndView();

        List<Category> categories = adminService.getAllCategories();
        List<Brand> brands = adminService.getAllBrands();

        modelAndView.addObject("categories", categories);
        modelAndView.addObject("brands", brands);

        modelAndView.setViewName("admin/product/createProduct");
        return modelAndView;
    }

    @PostMapping("/admin/createProduct")
    public String createProduct(@ModelAttribute Product product,
                                @RequestParam("categoryId") Long categoryId,
                                @RequestParam(value = "brandId", required = false) Long brandId,
                                @RequestParam(value = "images", required = false) MultipartFile[] images) {

        adminService.setProductCategoryAndBrand(product, categoryId, brandId);
        adminService.createProduct(product, images);

        return "redirect:/admin/manageProducts";
    }

    @PostMapping("/admin/updateProduct")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("categoryId") Long categoryId,
                                @RequestParam(value = "brandId", required = false) Long brandId,
                                @RequestParam(value = "images", required = false) MultipartFile[] images) {

        adminService.setProductCategoryAndBrand(product, categoryId, brandId);
        adminService.updateProduct(product, images);

        return "redirect:/admin/manageProducts";
    }

    @GetMapping("/admin/editProduct")
    public ModelAndView editProduct(@RequestParam("productId") Long productId) {
        ModelAndView modelAndView = new ModelAndView();

        Optional<Product> optionalProduct = adminService.getProductById(productId);
        if (optionalProduct.isEmpty()) {
            modelAndView.setViewName("redirect:/admin/manageProducts");
            return modelAndView;
        }

        Product product = optionalProduct.get();

        List<Category> categories = adminService.getAllCategories();
        List<Brand> brands = adminService.getAllBrands();

        modelAndView.addObject("product", product);
        modelAndView.addObject("categories", categories);
        modelAndView.addObject("brands", brands);
        modelAndView.setViewName("admin/product/editProduct");

        return modelAndView;
    }

//    @PostMapping("/admin/addProductImage")
//    public String addProductImage(@RequestParam("productId") Long productId,
//                                  @RequestParam("images") MultipartFile[] images) {
//        adminService.addNewImagesToProduct(productId, images);
//
//        return "redirect:/admin/editProduct?productId=" + productId;
//    }


    @PostMapping("/admin/deleteProductImage")
    public String deleteProductImage(@RequestParam("imageId") Long imageId,
                                     @RequestParam("productId") Long productId) {
        adminService.deleteProductImage(imageId);

        return "redirect:/admin/editProduct?productId=" + productId;
    }

    @PostMapping("/admin/deleteProduct")
    public String deleteProduct(@RequestParam("productId") Long productId) {
        adminService.deleteProductById(productId);
        return "redirect:/admin/manageProducts";
    }
}