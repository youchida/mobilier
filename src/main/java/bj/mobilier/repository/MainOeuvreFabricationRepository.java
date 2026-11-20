package bj.mobilier.repository;

import bj.mobilier.model.MainOeuvreFabrication;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MainOeuvreFabricationRepository extends JpaRepository<MainOeuvreFabrication, Long> {

    List<MainOeuvreFabrication> findByFabricationIdOrderByIdAsc(Long fabricationId);

    boolean existsByFabricationId(Long fabricationId);

    @Query("SELECT SUM(m.montant) FROM MainOeuvreFabrication m WHERE m.dateSaisie BETWEEN :debut AND :fin")
    BigDecimal sumMontantDansPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
