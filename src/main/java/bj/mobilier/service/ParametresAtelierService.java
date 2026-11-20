package bj.mobilier.service;

import bj.mobilier.model.ParametresAtelier;
import bj.mobilier.repository.ParametresAtelierRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParametresAtelierService {

    private final ParametresAtelierRepository parametresAtelierRepository;

    public ParametresAtelierService(ParametresAtelierRepository parametresAtelierRepository) {
        this.parametresAtelierRepository = parametresAtelierRepository;
    }

    @Transactional
    public ParametresAtelier obtenirParametres() {
        List<ParametresAtelier> liste = parametresAtelierRepository.findAll();
        if (!liste.isEmpty()) {
            return liste.get(0);
        }
        ParametresAtelier defaut = new ParametresAtelier();
        defaut.setNomAtelier("Atelier de Menuiserie");
        defaut.setCommune("Cotonou");
        defaut.setTelephone("");
        defaut.setAdresse("");
        return parametresAtelierRepository.save(defaut);
    }

    @Transactional
    public ParametresAtelier mettreAJour(String nomAtelier, String commune, String telephone, String adresse) {
        if (nomAtelier == null || nomAtelier.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'atelier est obligatoire.");
        }
        if (commune == null || commune.trim().isEmpty()) {
            throw new IllegalArgumentException("La commune est obligatoire.");
        }

        ParametresAtelier parametres = obtenirParametres();
        parametres.setNomAtelier(nomAtelier.trim());
        parametres.setCommune(commune.trim());
        parametres.setTelephone(telephone != null ? telephone.trim() : "");
        parametres.setAdresse(adresse != null ? adresse.trim() : "");
        return parametresAtelierRepository.save(parametres);
    }
}
