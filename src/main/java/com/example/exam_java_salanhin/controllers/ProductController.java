package com.example.exam_java_salanhin.controllers;

import com.example.exam_java_salanhin.models.Product;
import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.services.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @ModelAttribute("username")
    public String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        return (user != null) ? user.getFirstName() : null;
    }

    @ModelAttribute("userId")
    public Long getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        return (user != null) ? user.getId() : null;
    }

    @GetMapping("/")
    public ModelAndView getProducts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort,
            Model model) {

        List<Product> products = productService.findProducts(search, sort);

        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("products", products);
        modelAndView.addObject("search", search);
        modelAndView.addObject("sort", sort);

        return modelAndView;
    }

    @GetMapping("/product/details/{id}")
    public ModelAndView getProductDetails(@PathVariable Long id,
                                          @RequestParam(value = "search", required = false) String search,
                                          @RequestParam(value = "sort", required = false) String sort) {

        Product product = productService.findProductById(id);
        ModelAndView modelAndView = new ModelAndView("product/product_detail");
        modelAndView.addObject("product", product);
        modelAndView.addObject("search", search);
        modelAndView.addObject("sort", sort);
        return modelAndView;
    }

}
