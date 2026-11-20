package bj.mobilier.repository;

import bj.mobilier.model.ChargeGenerale;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChargeGeneraleRepository extends JpaRepository<ChargeGenerale, Long> {

    List<ChargeGenerale> findAllByOrderByDateChargeDescIdDesc();

    @Query("SELECT SUM(c.montant) FROM ChargeGenerale c WHERE c.dateCharge BETWEEN :debut AND :fin")
    BigDecimal sumMontantDansPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
