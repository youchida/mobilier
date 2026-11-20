package bj.mobilier.controller;

import bj.mobilier.model.ParametresAtelier;
import bj.mobilier.model.Utilisateur;
import bj.mobilier.repository.UtilisateurRepository;
import bj.mobilier.service.ParametresAtelierService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final ParametresAtelierService parametresAtelierService;
    private final UtilisateurRepository utilisateurRepository;

    public GlobalModelAdvice(ParametresAtelierService parametresAtelierService, UtilisateurRepository utilisateurRepository) {
        this.parametresAtelierService = parametresAtelierService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @ModelAttribute("atelier")
    public ParametresAtelier atelier() {
        return parametresAtelierService.obtenirParametres();
    }

    @ModelAttribute("utilisateurCourant")
    public Utilisateur utilisateurCourant() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return utilisateurRepository.findByEmailIgnoreCase(auth.getName()).orElse(null);
    }
}
