package com.example.exam_java_salanhin.controllers;

import com.example.exam_java_salanhin.models.Basket;
import com.example.exam_java_salanhin.models.BasketItem;
import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.services.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

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

    @GetMapping("/basket")
    public ModelAndView viewBasket(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("order/basket");
        Basket basket = orderService.getCurrentUserBasket();

        // Добавляем проверку на null
        if (basket == null) {
            modelAndView.addObject("basketTotal", BigDecimal.ZERO); // Устанавливаем 0 для общей суммы
            modelAndView.addObject("basketItems", List.of()); // Устанавливаем пустой список для элементов
        } else {
            modelAndView.addObject("basketTotal", basket.getTotalPrice() != null ? basket.getTotalPrice() : BigDecimal.ZERO);
            modelAndView.addObject("basketItems", basket.getItems()); // Передаем элементы корзины
        }

        return modelAndView;
    }

    @PostMapping("/addToBasket")
    public String addToBasket(@RequestParam Long userId, @RequestParam Long productId) {
        orderService.addToBasket(userId, productId);
        return "redirect:/";
    }

    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam Long basketItemId, @RequestParam int quantity) {
        orderService.updateQuantity(basketItemId, quantity);
        return "redirect:/basket";
    }

    @PostMapping("/removeItemFromBasket")
    public String removeItemFromBasket(@RequestParam Long basketItemId) {
        orderService.removeItemFromBasket(basketItemId);
        return "redirect:/basket";
    }

    @PostMapping("/checkout")
    public String checkout() {
        orderService.checkout();
        return "redirect:/orderPlaced";
    }

    @GetMapping("/orderPlaced")
    public String orderPlaced() {
        return "order/orderPlaced";
    }
}