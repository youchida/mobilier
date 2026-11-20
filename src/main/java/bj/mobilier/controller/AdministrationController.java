package bj.mobilier.controller;

import bj.mobilier.model.RoleUtilisateur;
import bj.mobilier.service.UtilisateurService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administration")
public class AdministrationController {

    private final UtilisateurService utilisateurService;

    public AdministrationController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String afficher(Model model) {
        model.addAttribute("pageActive", "administration");
        model.addAttribute("utilisateurs", utilisateurService.listerTous());
        model.addAttribute("roles", RoleUtilisateur.values());
        return "administration";
    }

    @PostMapping("/utilisateurs")
    public String creerUtilisateur(
            @RequestParam String email,
            @RequestParam String nomComplet,
            @RequestParam String motDePasse,
            @RequestParam RoleUtilisateur role,
            RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.creerUtilisateur(email, nomComplet, motDePasse, role);
            redirectAttributes.addFlashAttribute("succes", "Utilisateur créé avec succès.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/administration";
    }

    @PostMapping("/utilisateurs/{id}/statut")
    public String basculerStatut(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.basculerActivation(id);
            redirectAttributes.addFlashAttribute("succes", "Statut du compte mis à jour.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/administration";
    }

    @PostMapping("/utilisateurs/{id}/mot-de-passe")
    public String modifierMotDePasse(
            @PathVariable Long id,
            @RequestParam String nouveauMotDePasse,
            RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.modifierMotDePasseAdmin(id, nouveauMotDePasse);
            redirectAttributes.addFlashAttribute("succes", "Mot de passe modifié.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/administration";
    }
}
