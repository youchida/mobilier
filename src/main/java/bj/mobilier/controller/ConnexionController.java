package bj.mobilier.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConnexionController {

    @GetMapping("/")
    public String accueil() {
        return "redirect:/dashboard";
    }

    @GetMapping("/connexion")
    public String pageConnexion(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "connexion";
    }
}
