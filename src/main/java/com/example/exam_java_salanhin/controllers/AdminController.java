package com.example.exam_java_salanhin.controllers;

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

        List<Category> categories = adminService.getAllCategories(); // Получение списка категорий из сервиса
        categories.sort(Comparator.comparing(Category::getCategoryName)); // Сортировка по алфавиту

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
        newCategory.setCategoryName(categoryName);

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
        return "redirect:/admin/manageCategories"; // Перенаправление на обновленный список категорий
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
        modelAndView.addObject("categories", categories);

        modelAndView.setViewName("admin/product/createProduct");
        return modelAndView;
    }
}
