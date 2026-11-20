package bj.mobilier.service;

import bj.mobilier.model.CategorieMatiere;
import bj.mobilier.model.MatierePremiere;
import bj.mobilier.model.UniteMesure;
import bj.mobilier.repository.MatierePremiereRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatierePremiereService {

    private final MatierePremiereRepository matierePremiereRepository;

    public MatierePremiereService(MatierePremiereRepository matierePremiereRepository) {
        this.matierePremiereRepository = matierePremiereRepository;
    }

    public List<MatierePremiere> lister(String recherche) {
        if (recherche == null || recherche.trim().isEmpty()) {
            return matierePremiereRepository.findAllByOrderByNomAsc();
        }
        String terme = recherche.trim();
        return matierePremiereRepository.findByNomContainingIgnoreCaseOrCodeContainingIgnoreCaseOrderByNomAsc(terme, terme);
    }

    public MatierePremiere trouverParId(Long id) {
        return matierePremiereRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matière première introuvable."));
    }

    @Transactional
    public MatierePremiere creer(String code, String nom, CategorieMatiere categorie, UniteMesure unite, BigDecimal seuilAlerte) {
        validerChamps(code, nom, categorie, unite, seuilAlerte);
        String codeNormalise = code.trim().toUpperCase();

        if (matierePremiereRepository.existsByCodeIgnoreCase(codeNormalise)) {
            throw new IllegalArgumentException("Ce code de matière existe déjà.");
        }

        MatierePremiere matiere = new MatierePremiere();
        matiere.setCode(codeNormalise);
        matiere.setNom(nom.trim());
        matiere.setCategorie(categorie);
        matiere.setUnite(unite);
        matiere.setSeuilAlerte(seuilAlerte);
        matiere.setStockActuel(BigDecimal.ZERO);
        matiere.setCoutUnitaireMoyen(BigDecimal.ZERO);
        return matierePremiereRepository.save(matiere);
    }

    @Transactional
    public MatierePremiere modifier(Long id, String code, String nom, CategorieMatiere categorie, UniteMesure unite, BigDecimal seuilAlerte) {
        validerChamps(code, nom, categorie, unite, seuilAlerte);
        String codeNormalise = code.trim().toUpperCase();

        MatierePremiere matiere = trouverParId(id);
        if (matierePremiereRepository.existsByCodeIgnoreCaseAndIdNot(codeNormalise, id)) {
            throw new IllegalArgumentException("Ce code de matière est déjà utilisé.");
        }

        matiere.setCode(codeNormalise);
        matiere.setNom(nom.trim());
        matiere.setCategorie(categorie);
        matiere.setUnite(unite);
        matiere.setSeuilAlerte(seuilAlerte);
        return matierePremiereRepository.save(matiere);
    }

    private void validerChamps(String code, String nom, CategorieMatiere categorie, UniteMesure unite, BigDecimal seuilAlerte) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code de la matière est obligatoire.");
        }
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la matière est obligatoire.");
        }
        if (categorie == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire.");
        }
        if (unite == null) {
            throw new IllegalArgumentException("L'unité de mesure est obligatoire.");
        }
        if (seuilAlerte == null || seuilAlerte.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le seuil d'alerte doit être supérieur ou égal à zéro.");
        }
    }
}
