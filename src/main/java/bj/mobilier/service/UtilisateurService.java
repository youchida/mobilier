package bj.mobilier.service;

import bj.mobilier.model.RoleUtilisateur;
import bj.mobilier.model.Utilisateur;
import bj.mobilier.repository.UtilisateurRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Utilisateur> listerTous() {
        return utilisateurRepository.findAllByOrderByNomCompletAsc();
    }

    @Transactional
    public Utilisateur creerUtilisateur(String email, String nomComplet, String motDePasse, RoleUtilisateur role) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse de connexion est obligatoire.");
        }
        if (nomComplet == null || nomComplet.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom complet est obligatoire.");
        }
        if (motDePasse == null || motDePasse.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Le rôle est obligatoire.");
        }

        String emailNormalise = email.trim().toLowerCase();
        if (utilisateurRepository.existsByEmailIgnoreCase(emailNormalise)) {
            throw new IllegalArgumentException("Cette adresse de connexion est déjà utilisée.");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(emailNormalise);
        utilisateur.setNomComplet(nomComplet.trim());
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        utilisateur.setRole(role);
        utilisateur.setActif(true);
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void basculerActivation(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        if (utilisateur.isActif() && utilisateur.getRole() == RoleUtilisateur.ADMIN) {
            long adminsActifs = utilisateurRepository.countByRoleAndActifTrue(RoleUtilisateur.ADMIN);
            if (adminsActifs <= 1) {
                throw new IllegalArgumentException("Impossible de désactiver le dernier administrateur actif.");
            }
        }

        utilisateur.setActif(!utilisateur.isActif());
        utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void modifierMotDePasseAdmin(Long id, String nouveauMotDePasse) {
        if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 6) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 6 caractères.");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void modifierPropreMotDePasse(String email, String motDePasseActuel, String nouveauMotDePasse, String confirmation) {
        Utilisateur utilisateur = utilisateurRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        if (motDePasseActuel == null || !passwordEncoder.matches(motDePasseActuel, utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect.");
        }
        if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 6) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 6 caractères.");
        }
        if (!nouveauMotDePasse.equals(confirmation)) {
            throw new IllegalArgumentException("La confirmation du nouveau mot de passe ne correspond pas.");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);
    }
}
