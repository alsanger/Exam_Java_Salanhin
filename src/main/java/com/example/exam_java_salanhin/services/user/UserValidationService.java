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

    public boolean isUserLoginExists(User user) {
        if (user.getId() == null) {
            return userRepository.existsByLogin(user.getLogin());
        }
        return userRepository.existsByLoginAndIdNot(user.getLogin(), user.getId());
    }

    public boolean isUserEmailExists(User user) {
        if (user.getId() == null) {
            return userRepository.existsByEmail(user.getEmail());
        }
        return userRepository.existsByEmailAndIdNot(user.getEmail(), user.getId());
    }

    public boolean isUserPhoneExists(User user) {
        if (user.getId() == null) {
            return userRepository.existsByPhone(user.getPhone());
        }
        return userRepository.existsByPhoneAndIdNot(user.getPhone(), user.getId());
    }

    public String validateUserData(User user) {
        if (user.getLogin().length() < 3) {
            return "Login must contain at least 3 characters.";
        }

        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        if (!Pattern.matches(passwordRegex, user.getPassword())) {
            return "The password must contain at least 6 characters";
        }

        return null;
    }
}
