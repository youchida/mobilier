package bj.mobilier.controller;

import bj.mobilier.service.AchatMatiereService;
import bj.mobilier.service.MatierePremiereService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/achats")
public class AchatMatiereController {

    private static final ZoneId ZONE_BENIN = ZoneId.of("Africa/Porto-Novo");

    private final AchatMatiereService achatMatiereService;
    private final MatierePremiereService matierePremiereService;

    public AchatMatiereController(AchatMatiereService achatMatiereService, MatierePremiereService matierePremiereService) {
        this.achatMatiereService = achatMatiereService;
        this.matierePremiereService = matierePremiereService;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("pageActive", "achats");
        model.addAttribute("achats", achatMatiereService.listerTous());
        model.addAttribute("matieres", matierePremiereService.lister(null));
        model.addAttribute("dateAujourdhui", LocalDate.now(ZONE_BENIN));
        return "achats";
    }

    @PostMapping
    public String enregistrer(
            @RequestParam Long matiereId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateAchat,
            @RequestParam BigDecimal quantite,
            @RequestParam BigDecimal prixUnitaire,
            @RequestParam(required = false) String fournisseur,
            RedirectAttributes redirectAttributes) {
        try {
            achatMatiereService.enregistrerAchat(matiereId, dateAchat, quantite, prixUnitaire, fournisseur);
            redirectAttributes.addFlashAttribute("succes", "Achat enregistré et stock mis à jour.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/achats";
    }
}
