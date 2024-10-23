package com.example.exam_java_salanhin.services.user;

import com.example.exam_java_salanhin.models.BlockedUser;
import com.example.exam_java_salanhin.models.Role;
import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.repositories.BlockedUserRepository;
import com.example.exam_java_salanhin.repositories.RoleRepository;
import com.example.exam_java_salanhin.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


import javax.swing.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    BlockedUserRepository blockedUserRepository;

    @Autowired
    private UserValidationService userValidationService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User authenticateUser(String login, String password) {
        Optional<User> optionalUser = userRepository.findByLogin(login);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public String saveUser(User user, String confirmPassword) {

        if (userValidationService.isUserLoginExists(user)) {
            return "User with this login already exists";
        }

        if (userValidationService.isUserEmailExists(user)) {
            return "User with this email already exists";
        }

        if (userValidationService.isUserPhoneExists(user)) {
            return "User with this phone number already exists";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            return "The passwords entered do not match";
        }

        String validationError = userValidationService.validateUserData(user);
        if (validationError != null) {
            return validationError;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "";
    }

    public String updateUser(User user, String confirmPassword, boolean blockStatus) {
        User currentUser = userRepository.findById(user.getId()).orElse(null);

        if (currentUser == null) {
            return "User not found";
        }

        if (!currentUser.getEmail().equals(user.getEmail()) &&
                userValidationService.isUserEmailExists(user)) {
            return "User with this email already exists";
        }

        if (!currentUser.getPhone().equals(user.getPhone()) &&
                userValidationService.isUserPhoneExists(user)) {
            return "User with this phone number already exists";
        }

        if (!user.getPassword().isEmpty()) {
            if (!user.getPassword().equals(confirmPassword)) {
                return "The passwords entered do not match";
            }

            if (!passwordEncoder.matches(user.getPassword(), currentUser.getPassword())) {
                String validationError = userValidationService.validateUserData(user);
                if (validationError != null) {
                    return validationError;
                }
            }
        }

        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhone(user.getPhone());
        currentUser.setCity(user.getCity());
        currentUser.setCountry(user.getCountry());
        currentUser.setRole((user.getRole()));

        if (!user.getPassword().isEmpty() &&
                !passwordEncoder.matches(user.getPassword(), currentUser.getPassword())) {
            currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(currentUser);

        if (blockStatus && this.isBlocked(currentUser.getId()).isEmpty()) {
            blockUser(currentUser.getId());
        }
        if (!blockStatus && this.isBlocked(currentUser.getId()).isPresent()) {
            unblockUser(currentUser.getId());
        }

        return "";
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void assignRoleToUser(User user, Role role) {
        Optional<Role> existingRole = roleRepository.findByName(role.getName());
        if (existingRole.isPresent()) {
            user.setRole(existingRole.get());
        } else {
            roleRepository.save(role);
            user.setRole(role);
        }
    }

    public Optional<BlockedUser> isBlocked(Long userId) {
        return blockedUserRepository.findByUserId(userId);
    }

    public boolean blockUser(Long userId) {
        return blockUser(userId, "");
    }

    public boolean blockUser(Long userId, String reason) {
        Optional<User> userOptional = this.getUserById(userId);

        if (userOptional.isPresent()) {
            BlockedUser blockedUser = new BlockedUser();
            blockedUser.setUser(userOptional.get());
            blockedUser.setReason(reason);
            blockedUser.setBlockedAt(LocalDateTime.now());
            blockedUserRepository.save(blockedUser);
            return true;
        }
        return false;
    }

    public boolean unblockUser(Long userId) {
        Optional<BlockedUser> blockedUserOptional = blockedUserRepository.findByUserId(userId);

        if (blockedUserOptional.isPresent()) {
            blockedUserRepository.delete(blockedUserOptional.get());
            return true;
        }
        return false;
    }
}