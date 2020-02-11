package com.ipiecoles.java.mdd050.repository;

import com.ipiecoles.java.mdd050.model.Employe;

public interface EmployeRepository extends BaseEmployeRepository<Employe> {
    Employe findByMatricule(String matricule);
}
