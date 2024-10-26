package com.example.exam_java_salanhin.controllers;

import com.example.exam_java_salanhin.models.Brand;
import com.example.exam_java_salanhin.models.Category;
import com.example.exam_java_salanhin.models.Product;
import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.services.admin.AdminService;
import com.example.exam_java_salanhin.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;


    @ModelAttribute("username")
    public String getUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = null;

        if (session != null) {
            user = (User) session.getAttribute("user");
        }

        if (user != null && "ROLE_ADMIN".equals(user.getRole().getName())) {
            return user.getFirstName();
        } else {
            response.sendRedirect("/user/login");
            return null;
        }
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

        Category category = adminService.getCategoryById(categoryId);
        adminService.assignCategoryToProduct(product, category);

        if (brandId != null) {
            Brand brand = adminService.getBrandById(brandId);
            adminService.assignBrandToProduct(product, brand);
        }

        adminService.saveProductWithImages(product, images);

        return "redirect:/admin/manageProducts";
    }
}