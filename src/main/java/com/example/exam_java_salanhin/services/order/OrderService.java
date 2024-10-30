package com.example.exam_java_salanhin.services.order;

import com.example.exam_java_salanhin.models.Basket;
import com.example.exam_java_salanhin.models.BasketItem;
import com.example.exam_java_salanhin.models.Product;
import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.repositories.BasketItemRepository;
import com.example.exam_java_salanhin.repositories.BasketRepository;
import com.example.exam_java_salanhin.repositories.UserRepository;
import com.example.exam_java_salanhin.services.admin.AdminService;
import com.example.exam_java_salanhin.services.product.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        // Обновляем количество
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

    public void removeItem(Long basketItemId) {
        basketItemRepository.deleteById(basketItemId);
    }

    public void checkout(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        Basket basket = basketRepository.findByUser(user).orElse(null);
        List<BasketItem> basketItems = basketItemRepository.findByBasket(basket);
        basketItemRepository.deleteAll(basketItems);
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