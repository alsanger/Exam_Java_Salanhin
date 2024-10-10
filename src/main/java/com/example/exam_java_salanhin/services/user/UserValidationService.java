package com.example.exam_java_salanhin.services.user;

import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserValidationService {
    @Autowired
    UserRepository userRepository;

    public boolean isUserExists(Long id) {
        return userRepository.existsById(id);
    }

    public boolean isUserExists(User user) {
        return userRepository.existsByLogin(user.getLogin()) ||
                userRepository.existsByEmail(user.getEmail()) ||
                userRepository.existsByPhone(user.getPhone());
    }

    public String validateUserData(User user) {
        if (user.getLogin().length() < 3) {
            return "Login must contain at least 3 characters.";
        }

        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        if (!Pattern.matches(passwordRegex, user.getPassword())) {
            return "The password must contain at least 6 characters, including uppercase letters, lowercase letters and numbers.";
        }

        return null;
    }
}
