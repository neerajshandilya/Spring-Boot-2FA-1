package lol.gilliard.springboot2fa;

import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;

@Controller
public class WebController {

    private static Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ModelAndView showHomepage(Principal principal){
        if (principal == null){
            return new ModelAndView("home");
        } else {
            return new ModelAndView("home", "user", principal);
        }
    }

    @GetMapping("/user/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "registration";
    }

    @PostMapping("/user/registration")
    public ModelAndView registerNewUser(@ModelAttribute("user") @Valid UserDto userDto, BindingResult result, RedirectAttributes redirect){

        try {
            userService.loadUserByUsername(userDto.getUsername());
            logger.warn("Attempt to register existing user username: {}", userDto.getUsername());
            result.rejectValue("username", "already.exists");
            return new ModelAndView("registration", "user", userDto);

        } catch (UsernameNotFoundException e) {
            // Good. Carry on.
        }

        if (result.hasErrors()){
            return new ModelAndView("registration", "user", userDto);
        }

        userService.createNewUser(userDto);

        redirect.addFlashAttribute("user", userDto);
        return new ModelAndView("redirect:/user/2fa");
    }

    @RequestMapping("/user/2fa")
    public String setUpUser2FA(Model model, Principal principal) throws UnsupportedEncodingException {
        UserDto user = (UserDto) (model.asMap().get("user"));
        if (user == null){
            return "redirect:/user/registration";
        }

        String otpUrl = OTP.getURL(user.getTwofakey(), 6, Type.TOTP, "spring-boot-2fa-demo", user.getUsername());

        String twoFaQrUrl = String.format(
            "https://chart.googleapis.com/chart?cht=qr&chs=200x200&chl=%s",
            URLEncoder.encode(otpUrl, "UTF-8"));

        model.addAttribute("twoFaQrUrl", twoFaQrUrl);
        return "2faReg";
    }

    @RequestMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    @RequestMapping("/user/home")
    public ModelAndView showUserHome(Principal principal){
        return new ModelAndView("userhome.html", "user", principal);
    }

}
