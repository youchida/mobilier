package bj.mobilier.repository;

import bj.mobilier.model.ModeleMeuble;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeleMeubleRepository extends JpaRepository<ModeleMeuble, Long> {

    List<ModeleMeuble> findAllByOrderByNomAsc();

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
