package hansanhha.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class OAuth2LoginController {

    @GetMapping
    public String index() {
        log.info(this.getClass().getSimpleName());
        log.info("- enter the index page");
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        log.info(this.getClass().getSimpleName());
        log.info("- enter the login page");
        return "login-page";
    }

    @GetMapping("/login/oauth2/success")
    public String succeedLogin() {
        return "login-success";
    }
}
