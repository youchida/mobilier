package bj.mobilier.service;

import bj.mobilier.model.ModeleMeuble;
import bj.mobilier.repository.FabricationRepository;
import bj.mobilier.repository.ModeleMeubleRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModeleMeubleService {

    private final ModeleMeubleRepository modeleMeubleRepository;
    private final FabricationRepository fabricationRepository;

    public ModeleMeubleService(ModeleMeubleRepository modeleMeubleRepository, FabricationRepository fabricationRepository) {
        this.modeleMeubleRepository = modeleMeubleRepository;
        this.fabricationRepository = fabricationRepository;
    }

    public List<ModeleMeuble> listerTous() {
        return modeleMeubleRepository.findAllByOrderByNomAsc();
    }

    public Map<Long, Boolean> carteUtilisationDansFabrications(List<ModeleMeuble> modeles) {
        Map<Long, Boolean> map = new HashMap<>();
        for (ModeleMeuble modele : modeles) {
            map.put(modele.getId(), fabricationRepository.existsByModeleId(modele.getId()));
        }
        return map;
    }

    public ModeleMeuble trouverParId(Long id) {
        return modeleMeubleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Modèle de meuble introuvable."));
    }

    @Transactional
    public ModeleMeuble creer(String code, String nom, String description, BigDecimal prixVenteHabituel) {
        validerChamps(code, nom, prixVenteHabituel);
        String codeNormalise = code.trim().toUpperCase();

        if (modeleMeubleRepository.existsByCodeIgnoreCase(codeNormalise)) {
            throw new IllegalArgumentException("Ce code de modèle existe déjà.");
        }

        ModeleMeuble modele = new ModeleMeuble();
        modele.setCode(codeNormalise);
        modele.setNom(nom.trim());
        modele.setDescription(description != null && !description.trim().isEmpty() ? description.trim() : null);
        modele.setPrixVenteHabituel(prixVenteHabituel);
        return modeleMeubleRepository.save(modele);
    }

    @Transactional
    public ModeleMeuble modifier(Long id, String code, String nom, String description, BigDecimal prixVenteHabituel) {
        validerChamps(code, nom, prixVenteHabituel);
        String codeNormalise = code.trim().toUpperCase();

        ModeleMeuble modele = trouverParId(id);
        if (modeleMeubleRepository.existsByCodeIgnoreCaseAndIdNot(codeNormalise, id)) {
            throw new IllegalArgumentException("Ce code de modèle est déjà utilisé.");
        }

        modele.setCode(codeNormalise);
        modele.setNom(nom.trim());
        modele.setDescription(description != null && !description.trim().isEmpty() ? description.trim() : null);
        modele.setPrixVenteHabituel(prixVenteHabituel);
        return modeleMeubleRepository.save(modele);
    }

    @Transactional
    public void supprimer(Long id) {
        ModeleMeuble modele = trouverParId(id);
        if (fabricationRepository.existsByModeleId(id)) {
            throw new IllegalArgumentException("Ce modèle est déjà utilisé dans une fabrication et ne peut pas être supprimé.");
        }
        modeleMeubleRepository.delete(modele);
    }

    private void validerChamps(String code, String nom, BigDecimal prixVenteHabituel) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code du modèle est obligatoire.");
        }
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du modèle est obligatoire.");
        }
        if (prixVenteHabituel != null && prixVenteHabituel.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le prix de vente habituel doit être supérieur à zéro.");
        }
    }
}
