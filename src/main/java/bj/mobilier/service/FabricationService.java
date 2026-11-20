package bj.mobilier.service;

import bj.mobilier.model.CategorieChargeFabrication;
import bj.mobilier.model.ChargeFabrication;
import bj.mobilier.model.ConsommationMatiere;
import bj.mobilier.model.Fabrication;
import bj.mobilier.model.MainOeuvreFabrication;
import bj.mobilier.model.MatierePremiere;
import bj.mobilier.model.ModeleMeuble;
import bj.mobilier.model.StatutFabrication;
import bj.mobilier.repository.ChargeFabricationRepository;
import bj.mobilier.repository.ConsommationMatiereRepository;
import bj.mobilier.repository.FabricationRepository;
import bj.mobilier.repository.MainOeuvreFabricationRepository;
import bj.mobilier.repository.MatierePremiereRepository;
import bj.mobilier.repository.ModeleMeubleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FabricationService {

    private final FabricationRepository fabricationRepository;
    private final ModeleMeubleRepository modeleMeubleRepository;
    private final MatierePremiereRepository matierePremiereRepository;
    private final ConsommationMatiereRepository consommationMatiereRepository;
    private final MainOeuvreFabricationRepository mainOeuvreFabricationRepository;
    private final ChargeFabricationRepository chargeFabricationRepository;

    public FabricationService(
            FabricationRepository fabricationRepository,
            ModeleMeubleRepository modeleMeubleRepository,
            MatierePremiereRepository matierePremiereRepository,
            ConsommationMatiereRepository consommationMatiereRepository,
            MainOeuvreFabricationRepository mainOeuvreFabricationRepository,
            ChargeFabricationRepository chargeFabricationRepository) {
        this.fabricationRepository = fabricationRepository;
        this.modeleMeubleRepository = modeleMeubleRepository;
        this.matierePremiereRepository = matierePremiereRepository;
        this.consommationMatiereRepository = consommationMatiereRepository;
        this.mainOeuvreFabricationRepository = mainOeuvreFabricationRepository;
        this.chargeFabricationRepository = chargeFabricationRepository;
    }

    public List<Fabrication> listerToutes() {
        return fabricationRepository.findAllByOrderByDateDebutDescIdDesc();
    }

    public List<LigneFabrication> listerResumes() {
        List<Fabrication> fabrications = listerToutes();
        List<LigneFabrication> resumes = new java.util.ArrayList<>();
        for (Fabrication fabrication : fabrications) {
            BigDecimal total = calculerCoutMatieres(fabrication.getId())
                    .add(calculerCoutMainOeuvre(fabrication.getId()))
                    .add(calculerCoutCharges(fabrication.getId()));
            resumes.add(new LigneFabrication(fabrication, total, peutEtreSupprimee(fabrication)));
        }
        return resumes;
    }

    public Map<Long, BigDecimal> carteCoutsTotaux(List<Fabrication> fabrications) {
        Map<Long, BigDecimal> map = new HashMap<>();
        for (Fabrication fabrication : fabrications) {
            BigDecimal total = calculerCoutMatieres(fabrication.getId())
                    .add(calculerCoutMainOeuvre(fabrication.getId()))
                    .add(calculerCoutCharges(fabrication.getId()));
            map.put(fabrication.getId(), total);
        }
        return map;
    }

    public Map<Long, Boolean> carteSuppressionsPossibles(List<Fabrication> fabrications) {
        Map<Long, Boolean> map = new HashMap<>();
        for (Fabrication fabrication : fabrications) {
            map.put(fabrication.getId(), peutEtreSupprimee(fabrication));
        }
        return map;
    }

    public static class LigneFabrication {

        private final Fabrication fabrication;
        private final BigDecimal coutTotal;
        private final boolean peutSupprimer;

        public LigneFabrication(Fabrication fabrication, BigDecimal coutTotal, boolean peutSupprimer) {
            this.fabrication = fabrication;
            this.coutTotal = coutTotal;
            this.peutSupprimer = peutSupprimer;
        }

        public Fabrication getFabrication() {
            return fabrication;
        }

        public BigDecimal getCoutTotal() {
            return coutTotal;
        }

        public boolean isPeutSupprimer() {
            return peutSupprimer;
        }
    }

    public Fabrication trouverParId(Long id) {
        return fabricationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fabrication introuvable."));
    }

    public List<ConsommationMatiere> listerConsommations(Long fabricationId) {
        return consommationMatiereRepository.findByFabricationIdOrderByIdAsc(fabricationId);
    }

    public List<MainOeuvreFabrication> listerMainOeuvres(Long fabricationId) {
        return mainOeuvreFabricationRepository.findByFabricationIdOrderByIdAsc(fabricationId);
    }

    public List<ChargeFabrication> listerCharges(Long fabricationId) {
        return chargeFabricationRepository.findByFabricationIdOrderByIdAsc(fabricationId);
    }

    public BigDecimal calculerCoutMatieres(Long fabricationId) {
        BigDecimal total = BigDecimal.ZERO;
        for (ConsommationMatiere consommation : listerConsommations(fabricationId)) {
            total = total.add(consommation.getCoutTotal());
        }
        return total;
    }

    public BigDecimal calculerCoutMainOeuvre(Long fabricationId) {
        BigDecimal total = BigDecimal.ZERO;
        for (MainOeuvreFabrication mo : listerMainOeuvres(fabricationId)) {
            total = total.add(mo.getMontant());
        }
        return total;
    }

    public BigDecimal calculerCoutCharges(Long fabricationId) {
        BigDecimal total = BigDecimal.ZERO;
        for (ChargeFabrication charge : listerCharges(fabricationId)) {
            total = total.add(charge.getMontant());
        }
        return total;
    }

    public boolean aAuMoinsUneLigneDeCout(Long fabricationId) {
        return consommationMatiereRepository.existsByFabricationId(fabricationId)
                || mainOeuvreFabricationRepository.existsByFabricationId(fabricationId)
                || chargeFabricationRepository.existsByFabricationId(fabricationId);
    }

    public boolean peutEtreSupprimee(Fabrication fabrication) {
        return fabrication.getStatut() == StatutFabrication.EN_COURS && !aAuMoinsUneLigneDeCout(fabrication.getId());
    }

    @Transactional
    public Fabrication creerFabrication(Long modeleId, Integer quantitePrevue, LocalDate dateDebut, String observation) {
        if (modeleId == null) {
            throw new IllegalArgumentException("Le modèle de meuble est obligatoire.");
        }
        if (quantitePrevue == null || quantitePrevue < 1) {
            throw new IllegalArgumentException("La quantité à produire doit être au moins égale à 1.");
        }
        if (dateDebut == null) {
            throw new IllegalArgumentException("La date de début est obligatoire.");
        }

        ModeleMeuble modele = modeleMeubleRepository.findById(modeleId)
                .orElseThrow(() -> new IllegalArgumentException("Modèle de meuble introuvable."));

        Fabrication fabrication = new Fabrication();
        fabrication.setModele(modele);
        fabrication.setQuantitePrevue(quantitePrevue);
        fabrication.setDateDebut(dateDebut);
        fabrication.setStatut(StatutFabrication.EN_COURS);
        fabrication.setObservation(observation != null && !observation.trim().isEmpty() ? observation.trim() : null);
        return fabricationRepository.save(fabrication);
    }

    @Transactional
    public void ajouterConsommation(Long fabricationId, Long matiereId, BigDecimal quantite, LocalDate dateConsommation) {
        Fabrication fabrication = trouverParId(fabricationId);
        verifierEnCours(fabrication);

        if (matiereId == null) {
            throw new IllegalArgumentException("La matière première est obligatoire.");
        }
        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La quantité consommée doit être strictement supérieure à zéro.");
        }
        if (dateConsommation == null) {
            throw new IllegalArgumentException("La date de consommation est obligatoire.");
        }

        MatierePremiere matiere = matierePremiereRepository.findById(matiereId)
                .orElseThrow(() -> new IllegalArgumentException("Matière première introuvable."));

        if (matiere.getStockActuel().compareTo(quantite) < 0) {
            throw new IllegalArgumentException("Stock insuffisant pour " + matiere.getNom()
                    + " (disponible : " + matiere.getStockActuel().stripTrailingZeros().toPlainString()
                    + " " + matiere.getUnite().getLibelle() + ").");
        }

        BigDecimal coutUnitaireFige = matiere.getCoutUnitaireMoyen();
        BigDecimal coutTotal = quantite.multiply(coutUnitaireFige).setScale(2, RoundingMode.HALF_UP);

        matiere.setStockActuel(matiere.getStockActuel().subtract(quantite));
        matierePremiereRepository.save(matiere);

        ConsommationMatiere consommation = new ConsommationMatiere();
        consommation.setFabrication(fabrication);
        consommation.setMatiere(matiere);
        consommation.setQuantite(quantite);
        consommation.setCoutUnitaireFige(coutUnitaireFige);
        consommation.setCoutTotal(coutTotal);
        consommation.setDateConsommation(dateConsommation);
        consommationMatiereRepository.save(consommation);
    }

    @Transactional
    public void supprimerConsommation(Long fabricationId, Long consommationId) {
        Fabrication fabrication = trouverParId(fabricationId);
        verifierEnCours(fabrication);

        ConsommationMatiere consommation = consommationMatiereRepository.findById(consommationId)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de consommation introuvable."));

        if (!consommation.getFabrication().getId().equals(fabricationId)) {
            throw new IllegalArgumentException("Cette consommation n'appartient pas à cette fabrication.");
        }

        MatierePremiere matiere = consommation.getMatiere();
        matiere.setStockActuel(matiere.getStockActuel().add(consommation.getQuantite()));
        matierePremiereRepository.save(matiere);

        consommationMatiereRepository.delete(consommation);
    }

    @Transactional
    public void ajouterMainOeuvre(Long fabricationId, String nomOuvrier, BigDecimal duree, String uniteDuree, BigDecimal montant, LocalDate dateSaisie) {
        Fabrication fabrication = trouverParId(fabricationId);
        verifierEnCours(fabrication);

        if (nomOuvrier == null || nomOuvrier.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'ouvrier est obligatoire.");
        }
        if (duree == null || duree.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La durée doit être strictement supérieure à zéro.");
        }
        if (!"jour(s)".equals(uniteDuree) && !"heure(s)".equals(uniteDuree)) {
            throw new IllegalArgumentException("L'unité de durée est invalide.");
        }
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de la main-d'œuvre doit être strictement supérieur à zéro.");
        }
        if (dateSaisie == null) {
            throw new IllegalArgumentException("La date est obligatoire.");
        }

        MainOeuvreFabrication mo = new MainOeuvreFabrication();
        mo.setFabrication(fabrication);
        mo.setNomOuvrier(nomOuvrier.trim());
        mo.setDuree(duree);
        mo.setUniteDuree(uniteDuree);
        mo.setMontant(montant);
        mo.setDateSaisie(dateSaisie);
        mainOeuvreFabricationRepository.save(mo);
    }

    @Transactional
    public void supprimerMainOeuvre(Long fabricationId, Long mainOeuvreId) {
        Fabrication fabrication = trouverParId(fabricationId);
        verifierEnCours(fabrication);

        MainOeuvreFabrication mo = mainOeuvreFabricationRepository.findById(mainOeuvreId)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de main-d'œuvre introuvable."));

        if (!mo.getFabrication().getId().equals(fabricationId)) {
            throw new IllegalArgumentException("Cette ligne n'appartient pas à cette fabrication.");
        }

        mainOeuvreFabricationRepository.delete(mo);
    }

    @Transactional
    public void ajouterCharge(Long fabricationId, CategorieChargeFabrication categorie, BigDecimal montant, String description, LocalDate dateSaisie) {
        Fabrication fabrication = trouverParId(fabricationId);
        verifierEnCours(fabrication);

        if (categorie == null) {
            throw new IllegalArgumentException("La catégorie de charge est obligatoire.");
        }
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de la charge doit être strictement supérieur à zéro.");
        }
        if (dateSaisie == null) {
            throw new IllegalArgumentException("La date est obligatoire.");
        }

        ChargeFabrication charge = new ChargeFabrication();
        charge.setFabrication(fabrication);
        charge.setCategorie(categorie);
        charge.setMontant(montant);
        charge.setDescription(description != null && !description.trim().isEmpty() ? description.trim() : null);
        charge.setDateSaisie(dateSaisie);
        chargeFabricationRepository.save(charge);
    }

    @Transactional
    public void supprimerCharge(Long fabricationId, Long chargeId) {
        Fabrication fabrication = trouverParId(fabricationId);
        verifierEnCours(fabrication);

        ChargeFabrication charge = chargeFabricationRepository.findById(chargeId)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de charge introuvable."));

        if (!charge.getFabrication().getId().equals(fabricationId)) {
            throw new IllegalArgumentException("Cette charge n'appartient pas à cette fabrication.");
        }

        chargeFabricationRepository.delete(charge);
    }

    @Transactional
    public void terminerFabrication(Long fabricationId, LocalDate dateFin, Integer quantiteProduite) {
        Fabrication fabrication = trouverParId(fabricationId);
        verifierEnCours(fabrication);

        if (dateFin == null) {
            throw new IllegalArgumentException("La date de fin est obligatoire.");
        }
        if (dateFin.isBefore(fabrication.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début.");
        }
        if (quantiteProduite == null || quantiteProduite < 1) {
            throw new IllegalArgumentException("La quantité réellement produite doit être au moins égale à 1.");
        }
        if (!aAuMoinsUneLigneDeCout(fabricationId)) {
            throw new IllegalArgumentException("Impossible de terminer la fabrication sans au moins une ligne de coût (matière, main-d'œuvre ou charge).");
        }

        fabrication.setDateFin(dateFin);
        fabrication.setQuantiteProduite(quantiteProduite);
        fabrication.setStatut(StatutFabrication.TERMINEE);
        fabricationRepository.save(fabrication);
    }

    @Transactional
    public void supprimerFabrication(Long fabricationId) {
        Fabrication fabrication = trouverParId(fabricationId);
        if (fabrication.getStatut() == StatutFabrication.TERMINEE) {
            throw new IllegalArgumentException("Une fabrication terminée ne peut pas être supprimée.");
        }
        if (aAuMoinsUneLigneDeCout(fabricationId)) {
            throw new IllegalArgumentException("Impossible de supprimer une fabrication contenant déjà des lignes de coût.");
        }
        fabricationRepository.delete(fabrication);
    }

    private void verifierEnCours(Fabrication fabrication) {
        if (fabrication.getStatut() != StatutFabrication.EN_COURS) {
            throw new IllegalArgumentException("Cette fabrication est terminée et verrouillée : aucune modification n'est possible.");
        }
    }
}
