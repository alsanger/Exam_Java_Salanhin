package com.example.exam_java_salanhin.controllers;

import com.example.exam_java_salanhin.models.Role;
import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.services.admin.AdminService;
import com.example.exam_java_salanhin.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;


    @ModelAttribute("username")
    public String getUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = null;

        if (session != null) {
            user = (User) session.getAttribute("user");
        }

        if (user != null && "ROLE_ADMIN".equals(user.getRole().getName())) {
            return user.getFirstName();
        } else {
            response.sendRedirect("/user/login");
            return null;
        }
    }

    @GetMapping("/admin/workAreaAdmin")
    public String workAreaAdmin() {
        return "admin/workAreaAdmin";
    }

    @GetMapping("/admin/createUserFromAdmin")
    public ModelAndView createUserFromAdmin() {
        return userController.createUser("", true);
    }

    @GetMapping("/admin/usersList")
    public ModelAndView usersList() {
        ModelAndView modelAndView = new ModelAndView();

        List<User> users = userService.getAllUsers();
        modelAndView.addObject("users", users);

        modelAndView.setViewName("admin/usersList");
        return modelAndView;
    }

    @PostMapping("/admin/updateUserFromAdmin")
    public ModelAndView updateUserFromAdmin(@RequestParam(value = "updatedUserID") Long updatedUserID) {
        return userController.updateUser(updatedUserID, "", true, null);
    }
}
