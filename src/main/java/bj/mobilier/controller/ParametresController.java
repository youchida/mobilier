package bj.mobilier.controller;

import bj.mobilier.service.ParametresAtelierService;
import bj.mobilier.service.UtilisateurService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/parametres")
public class ParametresController {

    private final ParametresAtelierService parametresAtelierService;
    private final UtilisateurService utilisateurService;

    public ParametresController(
            ParametresAtelierService parametresAtelierService,
            UtilisateurService utilisateurService) {
        this.parametresAtelierService = parametresAtelierService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String afficher(Model model) {
        model.addAttribute("pageActive", "parametres");
        return "parametres";
    }

    @PostMapping("/atelier")
    public String modifierAtelier(
            @RequestParam String nomAtelier,
            @RequestParam String commune,
            @RequestParam(required = false) String telephone,
            @RequestParam(required = false) String adresse,
            RedirectAttributes redirectAttributes) {
        try {
            parametresAtelierService.mettreAJour(nomAtelier, commune, telephone, adresse);
            redirectAttributes.addFlashAttribute("succes", "Informations de l'atelier enregistrées.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/parametres";
    }

    @PostMapping("/mot-de-passe")
    public String modifierMonMotDePasse(
            Authentication authentication,
            @RequestParam String motDePasseActuel,
            @RequestParam String nouveauMotDePasse,
            @RequestParam String confirmationMotDePasse,
            RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.modifierPropreMotDePasse(
                    authentication.getName(),
                    motDePasseActuel,
                    nouveauMotDePasse,
                    confirmationMotDePasse
            );
            redirectAttributes.addFlashAttribute("succes", "Votre mot de passe a été modifié.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/parametres";
    }
}
