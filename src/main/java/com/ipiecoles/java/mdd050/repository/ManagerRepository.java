package com.ipiecoles.java.mdd050.repository;

import com.ipiecoles.java.mdd050.model.Manager;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface ManagerRepository extends BaseEmployeRepository<Manager> {
    @EntityGraph(attributePaths = "equipe")
    Manager findOneWithEquipeById(Long id);

    Manager findById(Long idManager);

    Optional<Manager> findByMatricule(String matriculeManager);
}
