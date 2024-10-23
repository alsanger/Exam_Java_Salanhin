package com.example.exam_java_salanhin.services.admin;

import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.repositories.RoleRepository;
import com.example.exam_java_salanhin.repositories.UserRepository;
import com.example.exam_java_salanhin.services.user.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Service
public class AdminService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private UserValidationService userValidationService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


//    public ModelAndView saveUser(User user) {
//        ModelAndView modelAndView = new ModelAndView();
//
//        if (userValidationService.isUserLoginExists(user)) {
//            modelAndView.addObject("error", "A user with this login already exists.");
//            modelAndView.setViewName("admin/createUser");
//            return modelAndView;
//        }
//
//        if (userValidationService.isUserEmailExists(user)) {
//            modelAndView.addObject("error", "A user with this email already exists.");
//            modelAndView.setViewName("admin/createUser");
//            return modelAndView;
//        }
//
//        if (userValidationService.isUserPhoneExists(user)) {
//            modelAndView.addObject("error", "A user with this phone number already exists.");
//            modelAndView.setViewName("admin/createUser");
//            return modelAndView;
//        }
//
//        String validationError = userValidationService.validateUserData(user);
//        if (validationError != null) {
//            modelAndView.addObject("error", validationError);
//            modelAndView.setViewName("admin/createUser");
//            return modelAndView;
//        }
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setCreatedAt(LocalDateTime.now());
//        userRepository.save(user);
//        modelAndView.setViewName("admin/workAreaAdmin");
//        return modelAndView;
//    }
}
