package bj.mobilier.controller;

import bj.mobilier.model.CategorieChargeFabrication;
import bj.mobilier.model.Fabrication;
import bj.mobilier.service.FabricationService;
import bj.mobilier.service.MatierePremiereService;
import bj.mobilier.service.ModeleMeubleService;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
@RequestMapping("/fabrications")
public class FabricationController {

    private static final ZoneId ZONE_BENIN = ZoneId.of("Africa/Porto-Novo");

    private final FabricationService fabricationService;
    private final ModeleMeubleService modeleMeubleService;
    private final MatierePremiereService matierePremiereService;

    public FabricationController(
            FabricationService fabricationService,
            ModeleMeubleService modeleMeubleService,
            MatierePremiereService matierePremiereService) {
        this.fabricationService = fabricationService;
        this.modeleMeubleService = modeleMeubleService;
        this.matierePremiereService = matierePremiereService;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("pageActive", "fabrications");
        model.addAttribute("lignes", fabricationService.listerResumes());
        model.addAttribute("modeles", modeleMeubleService.listerTous());
        model.addAttribute("dateAujourdhui", LocalDate.now(ZONE_BENIN));
        return "fabrications";
    }

    @PostMapping
    public String creer(
            @RequestParam(required = false) Long modeleId,
            @RequestParam(required = false) Integer quantitePrevue,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) String observation,
            RedirectAttributes redirectAttributes) {
        try {
            Fabrication fabrication = fabricationService.creerFabrication(modeleId, quantitePrevue, dateDebut, observation);
            redirectAttributes.addFlashAttribute("succes", "Fabrication créée. Vous pouvez maintenant ajouter les coûts.");
            return "redirect:/fabrications/" + fabrication.getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/fabrications";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Fabrication fabrication = fabricationService.trouverParId(id);
            BigDecimal coutMatieres = fabricationService.calculerCoutMatieres(id);
            BigDecimal coutMainOeuvre = fabricationService.calculerCoutMainOeuvre(id);
            BigDecimal coutCharges = fabricationService.calculerCoutCharges(id);
            BigDecimal coutTotal = coutMatieres.add(coutMainOeuvre).add(coutCharges);

            BigDecimal coutRevientUnitaire = null;
            BigDecimal ecartPrixVente = null;
            if (fabrication.isTerminee() && fabrication.getQuantiteProduite() != null && fabrication.getQuantiteProduite() > 0) {
                coutRevientUnitaire = coutTotal.divide(
                        BigDecimal.valueOf(fabrication.getQuantiteProduite()),
                        2,
                        RoundingMode.HALF_UP
                );
                if (fabrication.getModele().getPrixVenteHabituel() != null) {
                    ecartPrixVente = fabrication.getModele().getPrixVenteHabituel().subtract(coutRevientUnitaire);
                }
            }

            model.addAttribute("pageActive", "fabrications");
            model.addAttribute("fabrication", fabrication);
            model.addAttribute("consommations", fabricationService.listerConsommations(id));
            model.addAttribute("mainOeuvres", fabricationService.listerMainOeuvres(id));
            model.addAttribute("charges", fabricationService.listerCharges(id));
            model.addAttribute("coutMatieres", coutMatieres);
            model.addAttribute("coutMainOeuvre", coutMainOeuvre);
            model.addAttribute("coutCharges", coutCharges);
            model.addAttribute("coutTotal", coutTotal);
            model.addAttribute("coutRevientUnitaire", coutRevientUnitaire);
            model.addAttribute("ecartPrixVente", ecartPrixVente);
            model.addAttribute("peutSupprimer", fabricationService.peutEtreSupprimee(fabrication));
            model.addAttribute("matieres", matierePremiereService.lister(null));
            model.addAttribute("categoriesCharge", CategorieChargeFabrication.values());
            model.addAttribute("dateAujourdhui", LocalDate.now(ZONE_BENIN));
            return "fabrication-detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/fabrications";
        }
    }

    @PostMapping("/{id}/consommations")
    public String ajouterConsommation(
            @PathVariable Long id,
            @RequestParam(required = false) Long matiereId,
            @RequestParam(required = false) BigDecimal quantite,
            RedirectAttributes redirectAttributes) {
        try {
            fabricationService.ajouterConsommation(id, matiereId, quantite, LocalDate.now(ZONE_BENIN));
            redirectAttributes.addFlashAttribute("succes", "Consommation de matière enregistrée.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/fabrications/" + id;
    }

    @PostMapping("/{id}/consommations/{consommationId}/supprimer")
    public String supprimerConsommation(
            @PathVariable Long id,
            @PathVariable Long consommationId,
            RedirectAttributes redirectAttributes) {
        try {
            fabricationService.supprimerConsommation(id, consommationId);
            redirectAttributes.addFlashAttribute("succes", "Consommation supprimée et stock rétabli.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/fabrications/" + id;
    }

    @PostMapping("/{id}/main-oeuvre")
    public String ajouterMainOeuvre(
            @PathVariable Long id,
            @RequestParam String nomOuvrier,
            @RequestParam(required = false) BigDecimal duree,
            @RequestParam String uniteDuree,
            @RequestParam(required = false) BigDecimal montant,
            RedirectAttributes redirectAttributes) {
        try {
            fabricationService.ajouterMainOeuvre(id, nomOuvrier, duree, uniteDuree, montant, LocalDate.now(ZONE_BENIN));
            redirectAttributes.addFlashAttribute("succes", "Ligne de main-d'œuvre ajoutée.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/fabrications/" + id;
    }

    @PostMapping("/{id}/main-oeuvre/{mainOeuvreId}/supprimer")
    public String supprimerMainOeuvre(
            @PathVariable Long id,
            @PathVariable Long mainOeuvreId,
            RedirectAttributes redirectAttributes) {
        try {
            fabricationService.supprimerMainOeuvre(id, mainOeuvreId);
            redirectAttributes.addFlashAttribute("succes", "Ligne de main-d'œuvre supprimée.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/fabrications/" + id;
    }

    @PostMapping("/{id}/charges")
    public String ajouterCharge(
            @PathVariable Long id,
            @RequestParam(required = false) CategorieChargeFabrication categorie,
            @RequestParam(required = false) BigDecimal montant,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        try {
            fabricationService.ajouterCharge(id, categorie, montant, description, LocalDate.now(ZONE_BENIN));
            redirectAttributes.addFlashAttribute("succes", "Charge ajoutée à la fabrication.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/fabrications/" + id;
    }

    @PostMapping("/{id}/charges/{chargeId}/supprimer")
    public String supprimerCharge(
            @PathVariable Long id,
            @PathVariable Long chargeId,
            RedirectAttributes redirectAttributes) {
        try {
            fabricationService.supprimerCharge(id, chargeId);
            redirectAttributes.addFlashAttribute("succes", "Charge supprimée.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/fabrications/" + id;
    }

    @PostMapping("/{id}/terminer")
    public String terminer(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Integer quantiteProduite,
            RedirectAttributes redirectAttributes) {
        try {
            fabricationService.terminerFabrication(id, dateFin, quantiteProduite);
            redirectAttributes.addFlashAttribute("succes", "Fabrication clôturée et verrouillée.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/fabrications/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            fabricationService.supprimerFabrication(id);
            redirectAttributes.addFlashAttribute("succes", "Fabrication supprimée.");
            return "redirect:/fabrications";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/fabrications/" + id;
        }
    }
}
