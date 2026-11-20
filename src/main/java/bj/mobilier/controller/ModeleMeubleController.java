package bj.mobilier.controller;

import bj.mobilier.model.ModeleMeuble;
import bj.mobilier.service.ModeleMeubleService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/modeles")
public class ModeleMeubleController {

    private final ModeleMeubleService modeleMeubleService;

    public ModeleMeubleController(ModeleMeubleService modeleMeubleService) {
        this.modeleMeubleService = modeleMeubleService;
    }

    @GetMapping
    public String lister(@RequestParam(required = false) Long modifierId, Model model) {
        List<ModeleMeuble> modeles = modeleMeubleService.listerTous();
        model.addAttribute("pageActive", "modeles");
        model.addAttribute("modeles", modeles);
        model.addAttribute("utilisations", modeleMeubleService.carteUtilisationDansFabrications(modeles));

        if (modifierId != null) {
            try {
                model.addAttribute("modeleEdition", modeleMeubleService.trouverParId(modifierId));
            } catch (IllegalArgumentException e) {
                model.addAttribute("erreur", e.getMessage());
            }
        }
        return "modeles";
    }

    @PostMapping
    public String creer(
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal prixVenteHabituel,
            RedirectAttributes redirectAttributes) {
        try {
            modeleMeubleService.creer(code, nom, description, prixVenteHabituel);
            redirectAttributes.addFlashAttribute("succes", "Modèle de meuble ajouté avec succès.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/modeles";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal prixVenteHabituel,
            RedirectAttributes redirectAttributes) {
        try {
            modeleMeubleService.modifier(id, code, nom, description, prixVenteHabituel);
            redirectAttributes.addFlashAttribute("succes", "Modèle de meuble mis à jour.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/modeles?modifierId=" + id;
        }
        return "redirect:/modeles";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            modeleMeubleService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Modèle de meuble supprimé.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/modeles";
    }
}
