package com.example.exam_java_salanhin.controllers;

import com.example.exam_java_salanhin.models.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
public class MainController {
//    @GetMapping("/")
//    public ModelAndView homePage() {
//        ModelAndView modelAndView = new ModelAndView("home");
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.isAuthenticated()) {
//            String username = authentication.getName();
//            modelAndView.addObject("username", username);
//        } else {
//            modelAndView.addObject("username", null);
//        }
//
//        return modelAndView;
//    }


    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            model.addAttribute("username", username);
        } else {
            model.addAttribute("username", null);
        }

        return "index";
    }







//    @GetMapping("/")
//    public String homePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
//        if (userDetails != null) {
//            if (userDetails instanceof User) {
//                User user = (User) userDetails;
//                model.addAttribute("firstName", user.getFirstName());
//            }
//        } else {
//            model.addAttribute("firstName", null);
//        }
//        return "index";
//    }
}
