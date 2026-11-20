package bj.mobilier.repository;

import bj.mobilier.model.AchatMatiere;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchatMatiereRepository extends JpaRepository<AchatMatiere, Long> {

    List<AchatMatiere> findAllByOrderByDateAchatDescIdDesc();
}
