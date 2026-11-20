package bj.mobilier.service;

import bj.mobilier.model.AchatMatiere;
import bj.mobilier.model.MatierePremiere;
import bj.mobilier.repository.AchatMatiereRepository;
import bj.mobilier.repository.MatierePremiereRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AchatMatiereService {

    private final AchatMatiereRepository achatMatiereRepository;
    private final MatierePremiereRepository matierePremiereRepository;

    public AchatMatiereService(AchatMatiereRepository achatMatiereRepository, MatierePremiereRepository matierePremiereRepository) {
        this.achatMatiereRepository = achatMatiereRepository;
        this.matierePremiereRepository = matierePremiereRepository;
    }

    public List<AchatMatiere> listerTous() {
        return achatMatiereRepository.findAllByOrderByDateAchatDescIdDesc();
    }

    @Transactional
    public AchatMatiere enregistrerAchat(Long matiereId, LocalDate dateAchat, BigDecimal quantite, BigDecimal prixUnitaire, String fournisseur) {
        if (matiereId == null) {
            throw new IllegalArgumentException("La matière achetée est obligatoire.");
        }
        if (dateAchat == null) {
            throw new IllegalArgumentException("La date d'achat est obligatoire.");
        }
        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La quantité achetée doit être strictement supérieure à zéro.");
        }
        if (prixUnitaire == null || prixUnitaire.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le prix unitaire en FCFA doit être strictement supérieur à zéro.");
        }

        MatierePremiere matiere = matierePremiereRepository.findById(matiereId)
                .orElseThrow(() -> new IllegalArgumentException("Matière première introuvable."));

        BigDecimal valeurAvant = matiere.getStockActuel().multiply(matiere.getCoutUnitaireMoyen());
        BigDecimal valeurAchat = quantite.multiply(prixUnitaire).setScale(2, RoundingMode.HALF_UP);
        BigDecimal nouveauStock = matiere.getStockActuel().add(quantite);
        BigDecimal nouveauCoutMoyen = valeurAvant.add(valeurAchat).divide(nouveauStock, 2, RoundingMode.HALF_UP);

        matiere.setStockActuel(nouveauStock);
        matiere.setCoutUnitaireMoyen(nouveauCoutMoyen);
        matierePremiereRepository.save(matiere);

        AchatMatiere achat = new AchatMatiere();
        achat.setMatiere(matiere);
        achat.setDateAchat(dateAchat);
        achat.setQuantite(quantite);
        achat.setPrixUnitaire(prixUnitaire);
        achat.setMontantTotal(valeurAchat);
        achat.setFournisseur(fournisseur != null && !fournisseur.trim().isEmpty() ? fournisseur.trim() : null);

        return achatMatiereRepository.save(achat);
    }
}
