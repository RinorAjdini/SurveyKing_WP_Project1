package finki.ukim.mk.surveyKing.web;

import finki.ukim.mk.surveyKing.model.Role;
import finki.ukim.mk.surveyKing.model.User;
import finki.ukim.mk.surveyKing.model.exceptions.InvalidArgumentsException;
import finki.ukim.mk.surveyKing.model.exceptions.PasswordsDoNotMatchException;
import finki.ukim.mk.surveyKing.model.exceptions.UsernameAlreadyExistsException;
import finki.ukim.mk.surveyKing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {
        try {
            userService.register(user.getUsername(), user.getPassword(), confirmPassword, Role.USER,user.getName(),user.getSurname());
            return "redirect:/login";
        } catch (UsernameAlreadyExistsException e) {
            model.addAttribute("error", "Username already exists. Please choose a different username.");
        } catch (PasswordsDoNotMatchException e) {
            model.addAttribute("error", "Passwords do not match. Please try again.");
        } catch (InvalidArgumentsException e) {
            model.addAttribute("error", "Invalid registration details. Please try again.");
        }
        return "register";
    }
    @GetMapping
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
}
