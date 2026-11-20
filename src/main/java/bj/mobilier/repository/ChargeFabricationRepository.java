package bj.mobilier.repository;

import bj.mobilier.model.ChargeFabrication;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChargeFabricationRepository extends JpaRepository<ChargeFabrication, Long> {

    List<ChargeFabrication> findByFabricationIdOrderByIdAsc(Long fabricationId);

    boolean existsByFabricationId(Long fabricationId);

    @Query("SELECT SUM(c.montant) FROM ChargeFabrication c WHERE c.dateSaisie BETWEEN :debut AND :fin")
    BigDecimal sumMontantDansPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
