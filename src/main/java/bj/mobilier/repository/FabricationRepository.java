package bj.mobilier.repository;

import bj.mobilier.model.Fabrication;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FabricationRepository extends JpaRepository<Fabrication, Long> {

    List<Fabrication> findAllByOrderByDateDebutDescIdDesc();

    boolean existsByModeleId(Long modeleId);

    @Query("SELECT COUNT(f) FROM Fabrication f WHERE f.statut = bj.mobilier.model.StatutFabrication.TERMINEE AND f.dateFin BETWEEN :debut AND :fin")
    long countTermineesDansPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT SUM(f.quantiteProduite) FROM Fabrication f WHERE f.statut = bj.mobilier.model.StatutFabrication.TERMINEE AND f.dateFin BETWEEN :debut AND :fin")
    Long sumQuantiteProduiteDansPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
