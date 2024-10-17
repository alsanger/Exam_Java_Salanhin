package com.example.exam_java_salanhin.services.user;

import com.example.exam_java_salanhin.models.Role;
import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.repositories.RoleRepository;
import com.example.exam_java_salanhin.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserValidationService userValidationService;

    @Autowired
    private AuthenticationManager authenticationManager;

//    public User authenticateUser(String login, String password) {
//        Optional<User> optionalUser = userRepository.findByLogin(login);
//
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//
//            if (passwordEncoder.matches(password, user.getPassword())) {
//                List<GrantedAuthority> authorities = new ArrayList<>();
//                authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
//
//                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                return user;
//            }
//        }
//        return null;
//    }



        public User authenticateUser(String login, String password) {
            try {
                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(login, password);
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (authentication.getPrincipal() instanceof UserDetails) {
                    return (User) authentication.getPrincipal();
                }
            } catch (AuthenticationException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }


    public ModelAndView saveUser(User user) {
        ModelAndView modelAndView = new ModelAndView();

        if (userValidationService.isUserExists(user)) {
            modelAndView.addObject("error", "A user with this login, email or phone number already exists.");
            modelAndView.setViewName("user/createUser");
            return modelAndView;
        }

        String validationError = userValidationService.validateUserData(user);
        if (validationError != null) {
            modelAndView.addObject("error", validationError);
            modelAndView.setViewName("registration");
            return modelAndView;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public ModelAndView updateUser(Long id, User updatedUser) {
        ModelAndView modelAndView = new ModelAndView();

        if (userValidationService.isUserExists(updatedUser)) {
            modelAndView.addObject("error", "A user with this email or phone number already exists.");
            modelAndView.setViewName("user/updateUser");
            return modelAndView;
        }

        String validationError = userValidationService.validateUserData(updatedUser);
        if (validationError != null) {
            modelAndView.addObject("error", validationError);
            modelAndView.setViewName("user/updateUser");
            return modelAndView;
        }

        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setCity(updatedUser.getCity());
            existingUser.setCountry(updatedUser.getCountry());

            if (!updatedUser.getPassword().isEmpty() && !passwordEncoder.matches(updatedUser.getPassword(), existingUser.getPassword())) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            userRepository.save(existingUser);
            modelAndView.setViewName("redirect:/user/profileUser");
        } else {
            modelAndView.addObject("error", "Failed to update user.");
            modelAndView.setViewName("user/updateUser");
        }

        return modelAndView;
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByLogin(login);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with login: " + login);
        }

        return optionalUser.get();
    }
}