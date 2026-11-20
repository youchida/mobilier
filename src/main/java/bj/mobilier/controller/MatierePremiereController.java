package bj.mobilier.controller;

import bj.mobilier.model.CategorieMatiere;
import bj.mobilier.model.UniteMesure;
import bj.mobilier.service.MatierePremiereService;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/matieres")
public class MatierePremiereController {

    private final MatierePremiereService matierePremiereService;

    public MatierePremiereController(MatierePremiereService matierePremiereService) {
        this.matierePremiereService = matierePremiereService;
    }

    @GetMapping
    public String lister(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) Long modifierId,
            Model model) {
        model.addAttribute("pageActive", "matieres");
        model.addAttribute("recherche", recherche != null ? recherche : "");
        model.addAttribute("matieres", matierePremiereService.lister(recherche));
        model.addAttribute("categories", CategorieMatiere.values());
        model.addAttribute("unites", UniteMesure.values());

        if (modifierId != null) {
            try {
                model.addAttribute("matiereEdition", matierePremiereService.trouverParId(modifierId));
            } catch (IllegalArgumentException e) {
                model.addAttribute("erreur", e.getMessage());
            }
        }
        return "matieres";
    }

    @PostMapping
    public String creer(
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam CategorieMatiere categorie,
            @RequestParam UniteMesure unite,
            @RequestParam BigDecimal seuilAlerte,
            RedirectAttributes redirectAttributes) {
        try {
            matierePremiereService.creer(code, nom, categorie, unite, seuilAlerte);
            redirectAttributes.addFlashAttribute("succes", "Matière première ajoutée avec succès.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/matieres";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam CategorieMatiere categorie,
            @RequestParam UniteMesure unite,
            @RequestParam BigDecimal seuilAlerte,
            RedirectAttributes redirectAttributes) {
        try {
            matierePremiereService.modifier(id, code, nom, categorie, unite, seuilAlerte);
            redirectAttributes.addFlashAttribute("succes", "Matière première mise à jour avec succès.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/matieres?modifierId=" + id;
        }
        return "redirect:/matieres";
    }
}
