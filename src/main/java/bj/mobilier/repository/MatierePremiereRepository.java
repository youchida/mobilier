package bj.mobilier.repository;

import bj.mobilier.model.MatierePremiere;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MatierePremiereRepository extends JpaRepository<MatierePremiere, Long> {

    List<MatierePremiere> findAllByOrderByNomAsc();

    List<MatierePremiere> findByNomContainingIgnoreCaseOrCodeContainingIgnoreCaseOrderByNomAsc(String nom, String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    @Query("SELECT m FROM MatierePremiere m WHERE m.stockActuel <= m.seuilAlerte ORDER BY m.nom ASC")
    List<MatierePremiere> findMatieresStockBas();

    @Query("SELECT COUNT(m) FROM MatierePremiere m WHERE m.stockActuel <= m.seuilAlerte")
    long countMatieresStockBas();

    @Query("SELECT SUM(m.stockActuel * m.coutUnitaireMoyen) FROM MatierePremiere m")
    BigDecimal sumValeurStock();
}
