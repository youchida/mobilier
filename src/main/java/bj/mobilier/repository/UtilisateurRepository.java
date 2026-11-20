package bj.mobilier.repository;

import bj.mobilier.model.RoleUtilisateur;
import bj.mobilier.model.Utilisateur;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByRoleAndActifTrue(RoleUtilisateur role);

    List<Utilisateur> findAllByOrderByNomCompletAsc();
}
