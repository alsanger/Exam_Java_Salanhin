package com.example.exam_java_salanhin.controllers;

import com.example.exam_java_salanhin.models.BlockedUser;
import com.example.exam_java_salanhin.models.Role;
import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

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

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        return "index";
    }

    @GetMapping("/user/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/user/login")
    public ModelAndView loginUser(@RequestParam("login") String login,
                                  @RequestParam("password") String password,
                                  HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.authenticateUser(login, password);
        if (user == null) {
            modelAndView.addObject("error", "Invalid login or password");
            modelAndView.setViewName("user/login");
            return modelAndView;
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @GetMapping("/user/logout")
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/user/login";
    }

    @GetMapping("/user/createUser")
    public ModelAndView createUser(@RequestParam(value = "error", defaultValue = "") String error,
                                   @RequestParam(value = "fromAdmin", defaultValue = "false") boolean fromAdmin) {
        ModelAndView modelAndView = new ModelAndView();

        List<Role> roles = userService.getAllRoles();

        modelAndView.addObject("roles", roles);
        modelAndView.addObject("fromAdmin", fromAdmin);
        modelAndView.addObject("error", error);
        modelAndView.setViewName("user/createUser");
        return modelAndView;
    }

    @PostMapping("/user/createUser")
    public ModelAndView createUser(@ModelAttribute User user,
                                   @RequestParam(value = "confirmPassword") String confirmPassword,
                                   @RequestParam(value = "newUserRoleString") String newUserRoleString,
                                   @RequestParam(value = "fromAdmin") boolean fromAdmin,
                                   HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView();

        Role userRole = new Role();
        if (userService.getAllUsers().isEmpty()) {
            userRole.setName("ROLE_ADMIN");
        } else {
            if (newUserRoleString != null && !newUserRoleString.isEmpty()) {
                userRole.setName(newUserRoleString);
            } else {
                userRole.setName("ROLE_USER");
            }
        }
        userService.assignRoleToUser(user, userRole);

        String error = userService.saveUser(user, confirmPassword);

        if (error == "") {
            if (fromAdmin) {
                modelAndView.setViewName("admin/workAreaAdmin");
            } else {
                modelAndView.setViewName("redirect:/");
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
            }
        } else {
            modelAndView.addObject("error", error);
            modelAndView.addObject("fromAdmin", fromAdmin);

            modelAndView.setViewName("redirect:/user/createUser");
        }
        return modelAndView;
    }

    @GetMapping("/user/updateUser")
    public ModelAndView updateUser(
            @RequestParam(value = "updatedUserID", defaultValue = "-1") Long updatedUserID,
            @RequestParam(value = "error", defaultValue = "") String error,
            @RequestParam(value = "fromAdmin", defaultValue = "false") boolean fromAdmin,
            HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();

        User user;
        if (updatedUserID == -1) {
            user = (User) session.getAttribute("user");
        } else {
            Optional<User> optionalUser = userService.getUserById(updatedUserID);
            user = optionalUser.orElse(null);
        }

        if (user == null) {
            modelAndView.setViewName("user/login");
            return modelAndView;
        }

        modelAndView.addObject("user", user);

        List<Role> roles = userService.getAllRoles();
        modelAndView.addObject("roles", roles);

        Optional<BlockedUser> blockedUser = userService.isBlocked(user.getId());
        boolean isBlocked = blockedUser.isPresent();
        modelAndView.addObject("isBlocked", isBlocked);

        modelAndView.addObject("fromAdmin", fromAdmin);
        modelAndView.addObject("error", error);

        modelAndView.setViewName("user/updateUser");
        return modelAndView;
    }

    @PostMapping("/user/updateUser")
    public ModelAndView updateUser(@ModelAttribute User user,
                                   @RequestParam(value = "newUserRoleString", defaultValue = "ROLE_USER") String newUserRoleString,
                                   @RequestParam(value = "blockStatus") boolean blockStatus,
                                   @RequestParam(value = "confirmPassword") String confirmPassword,
                                   @RequestParam(value = "fromAdmin") boolean fromAdmin,
                                   HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();

        Role userRole = new Role(newUserRoleString);
        userService.assignRoleToUser(user, userRole);

        String error = userService.updateUser(user, confirmPassword, blockStatus);
        if (error == "") {
            if (fromAdmin) {
                modelAndView.setViewName("admin/workAreaAdmin");
            } else {
                modelAndView.setViewName("redirect:/");
                session.setAttribute("user", user);
            }
        } else {
            modelAndView.addObject("updatedUserID", user.getId());
            modelAndView.addObject("error", error);
            modelAndView.addObject("fromAdmin", fromAdmin);

            modelAndView.setViewName("redirect:/user/updateUser");
        }
        return modelAndView;
    }

    @PostMapping("/user/deleteUser")
    public ModelAndView deleteUser(@RequestParam("id") Long id,
                                   @RequestParam(value = "fromAdmin") boolean fromAdmin,
                                   HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            modelAndView.setViewName("redirect:/user/login");
            return modelAndView;
        }

        userService.deleteUser(id);

        if (fromAdmin) {
            modelAndView.setViewName("redirect:/admin/usersList");
        } else {
            modelAndView.setViewName("redirect:/");
            session.invalidate();
        }
        return modelAndView;
    }
}