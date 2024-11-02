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

import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @ModelAttribute("role")
    public String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        return (user != null) ? user.getRole().getName() : null;
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

    @GetMapping("/images/{productId}/{imageName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable Long productId,
                                              @PathVariable String imageName) throws IOException {
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
        } catch (Exception ignored) {
        }
        return ResponseEntity.notFound().build();
    }
}
