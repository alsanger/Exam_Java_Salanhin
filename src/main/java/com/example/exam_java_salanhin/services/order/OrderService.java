package com.example.exam_java_salanhin.services.order;

import com.example.exam_java_salanhin.enums.OrderStatus;
import com.example.exam_java_salanhin.models.*;
import com.example.exam_java_salanhin.repositories.*;
import com.example.exam_java_salanhin.services.admin.AdminService;
import com.example.exam_java_salanhin.services.product.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private HttpSession httpSession;


    public List<BasketItem> getUserBasket(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        Basket basket = basketRepository.findByUser(user).orElse(null);
        List<BasketItem> basketItems = new ArrayList<BasketItem>();
        if (basket != null) {
            basketItems = basket.getItems();
        }
        return basketItems;
    }

    public void updateQuantity(Long basketItemId, int quantity) {
        BasketItem basketItem = basketItemRepository.findById(basketItemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid basket item ID"));

        basketItem.setQuantity(quantity);
        basketItemRepository.save(basketItem);
    }

    public Basket getCurrentUserBasket() {
        User user = (User) httpSession.getAttribute("user");

        if (user == null) {
            return null;
        }

        return basketRepository.findByUser(user).orElse(null);
    }

    public void removeItemFromBasket(Long basketItemId) {
        BasketItem basketItem = basketItemRepository.findById(basketItemId).orElse(null);
        if (basketItem == null) {
            return;
        }

        Basket basket = basketItem.getBasket();
        basketItemRepository.deleteById(basketItemId);
        List<BasketItem> remainingItems = basketItemRepository.findByBasket(basket);

        if (remainingItems.isEmpty()) {
            basketRepository.delete(basket);
        }
    }

    public void checkout() {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) return;

        Basket basket = basketRepository.findByUser(user).orElse(null);
        if (basket == null || basket.getItems().isEmpty()) return;

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(basket.getTotalPrice());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);

        for (BasketItem basketItem : basket.getItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(basketItem.getProduct());
            orderDetail.setQuantity(basketItem.getQuantity());
            orderDetail.setPrice(basketItem.getProduct().getPrice());
            BigDecimal total = basketItem.getProduct().getPrice().multiply(BigDecimal.valueOf(basketItem.getQuantity()));
            orderDetail.setTotal(total);
            orderDetailsRepository.save(orderDetail);
        }

        basketItemRepository.deleteAll(basket.getItems());
        basketRepository.delete(basket);
    }

    public void addToBasket(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        Basket basket = basketRepository.findByUser(user).orElse(new Basket());
        if (basket.getId() == null) {
            basket.setUser(user);
            basketRepository.save(basket);
        }

        Product product = productService.findProductById(productId);

        Optional<BasketItem> basketItemOptional = basketItemRepository.findByBasketAndProduct(basket, product);

        if (basketItemOptional.isPresent()) {
            BasketItem basketItem = basketItemOptional.get();
            basketItem.setQuantity(basketItem.getQuantity() + 1);
        } else {
            BasketItem newBasketItem = new BasketItem();
            newBasketItem.setBasket(basket);
            newBasketItem.setProduct(product);
            newBasketItem.setQuantity(1);
            basketItemRepository.save(newBasketItem);
        }
    }

}