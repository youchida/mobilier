package bj.mobilier.controller;

import bj.mobilier.model.CategorieChargeGenerale;
import bj.mobilier.service.ChargeGeneraleService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/charges-generales")
public class ChargeGeneraleController {

    private static final ZoneId ZONE_BENIN = ZoneId.of("Africa/Porto-Novo");

    private final ChargeGeneraleService chargeGeneraleService;

    public ChargeGeneraleController(ChargeGeneraleService chargeGeneraleService) {
        this.chargeGeneraleService = chargeGeneraleService;
    }

    @GetMapping
    public String lister(@RequestParam(required = false) Long modifierId, Model model) {
        model.addAttribute("pageActive", "charges");
        model.addAttribute("charges", chargeGeneraleService.listerToutes());
        model.addAttribute("categories", CategorieChargeGenerale.values());
        model.addAttribute("dateAujourdhui", LocalDate.now(ZONE_BENIN));

        if (modifierId != null) {
            try {
                model.addAttribute("chargeEdition", chargeGeneraleService.trouverParId(modifierId));
            } catch (IllegalArgumentException e) {
                model.addAttribute("erreur", e.getMessage());
            }
        }
        return "charges-generales";
    }

    @PostMapping
    public String creer(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateCharge,
            @RequestParam(required = false) CategorieChargeGenerale categorie,
            @RequestParam(required = false) BigDecimal montant,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        try {
            chargeGeneraleService.creer(dateCharge, categorie, montant, description);
            redirectAttributes.addFlashAttribute("succes", "Charge générale enregistrée.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/charges-generales";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateCharge,
            @RequestParam(required = false) CategorieChargeGenerale categorie,
            @RequestParam(required = false) BigDecimal montant,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        try {
            chargeGeneraleService.modifier(id, dateCharge, categorie, montant, description);
            redirectAttributes.addFlashAttribute("succes", "Charge générale mise à jour.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/charges-generales?modifierId=" + id;
        }
        return "redirect:/charges-generales";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            chargeGeneraleService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Charge générale supprimée.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/charges-generales";
    }
}
