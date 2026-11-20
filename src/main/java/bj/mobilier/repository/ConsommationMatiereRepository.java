package bj.mobilier.repository;

import bj.mobilier.model.ConsommationMatiere;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsommationMatiereRepository extends JpaRepository<ConsommationMatiere, Long> {

    List<ConsommationMatiere> findByFabricationIdOrderByIdAsc(Long fabricationId);

    boolean existsByFabricationId(Long fabricationId);

    @Query("SELECT SUM(c.coutTotal) FROM ConsommationMatiere c WHERE c.dateConsommation BETWEEN :debut AND :fin")
    BigDecimal sumCoutDansPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
