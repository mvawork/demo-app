package ru.menshevva.demoapp.contollers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticPageController {

    @GetMapping("/logged-out")
    public String loggedOut() {
        return "logged-out";
    }

    @GetMapping("/session-expired")
    public String sessionExpired() {
        return "session-expired";
    }

    @GetMapping("/auth-error")
    public String authError() {
        return "auth-error";
    }

    @GetMapping("/no-access")
    public String noAccess() {
        return "no-access";
    }

    @GetMapping("/no-such-user")
    public String noSuchUser() {
        return "no-such-user";
    }

    @GetMapping("/blocked-user")
    public String blockedUser() {
        return "blocked-user";
    }

}
