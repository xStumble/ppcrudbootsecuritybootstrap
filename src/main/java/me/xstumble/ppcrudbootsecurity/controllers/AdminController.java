package me.xstumble.ppcrudbootsecurity.controllers;

import jakarta.validation.Valid;
import me.xstumble.ppcrudbootsecurity.exceptions.EntityExistsExceptionWithType;
import me.xstumble.ppcrudbootsecurity.models.User;
import me.xstumble.ppcrudbootsecurity.security.UserDetailsImpl;
import me.xstumble.ppcrudbootsecurity.services.RoleService;
import me.xstumble.ppcrudbootsecurity.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public String showAdminPanel(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("currentUser", userDetails.getUser());
        return "admin/adminpage";
    }

    @PostMapping("/adduser")
    public String createUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/adminpage";
        }

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.addUser(user);
        } catch (EntityExistsExceptionWithType e) {
            return "admin/adminpage";
        }

        return "redirect:/admin";
    }

    @PostMapping("/edituser/edit")
    public String updateUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model,
                             @RequestParam long id) {

        List<FieldError> errors = bindingResult.getFieldErrors().stream()
                .filter(fer -> !fer.getField().equals("password")).toList();
        BindingResult sortedBindingResult = new BeanPropertyBindingResult(user, "user");
        errors.forEach(sortedBindingResult::addError);

        if (sortedBindingResult.hasErrors()) {
            return "admin/adminpage";
        }

        if (user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().length() < 4) {
            user.setPassword(userService.getUser(id).getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        try {
            userService.updateUser(id, user);
        } catch (EntityExistsExceptionWithType e) {
            return "admin/adminpage";
        }

        return "redirect:/admin";
    }

    @PostMapping("/deleteuser")
    public String deleteUser(@RequestParam long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
