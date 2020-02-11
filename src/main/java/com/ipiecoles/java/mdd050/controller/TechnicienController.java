package com.ipiecoles.java.mdd050.controller;

import com.ipiecoles.java.mdd050.exception.ConflictException;
import com.ipiecoles.java.mdd050.exception.EmployeException;
import com.ipiecoles.java.mdd050.model.Employe;
import com.ipiecoles.java.mdd050.model.Manager;
import com.ipiecoles.java.mdd050.model.Technicien;
import com.ipiecoles.java.mdd050.repository.ManagerRepository;
import com.ipiecoles.java.mdd050.repository.TechnicienRepository;
import com.ipiecoles.java.mdd050.service.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;


//@RequestMapping(method = RequestMethod.GET, value = "/{idManager}/equipe/{matricule}/add")
//public Technicien addTechniciens(@PathVariable Long idManager, @PathVariable String matricule) {
//        return this.managerService.addTechniciens(idManager, matricule);
//        }


@RestController
@RequestMapping("/techniciens")
public class TechnicienController {

    @Autowired
    private EmployeService employeService;
    @Autowired
    private TechnicienRepository technicienRepository;
    @Autowired
    private ManagerRepository managerRepository;


    @RequestMapping(method = RequestMethod.GET,
            value = "/{idTechnicien}/manager/{matriculeManager}/add")
    public Technicien addManagerToTechnicien (
            @PathVariable("idTechnicien") Long idTechnicien,
            @PathVariable("matriculeManager") String matriculeManager){

        //Technicien technicien;
        Technicien technicien = technicienRepository.findById(idTechnicien) ;

        if (technicien == null) {
            throw new EntityNotFoundException("Le technicien d'identifiant : " +
                    idTechnicien + " n'a pas été trouvé.");
        }
        if (technicien.getManager() != null) {
            throw new IllegalArgumentException("Le technicien d'identifiant : " +
                    idTechnicien + " possède déjà un manager.");
        }

        Optional<Manager> manager = managerRepository.findByMatricule(matriculeManager);

        if (!manager.isPresent()) {
            throw new EntityNotFoundException("Le manager de matricule : " +
                    matriculeManager + " n'a pas été trouvé.");
        }
        System.out.println("ALLLLLAAAAAAAAAAAAAAAAAAAAAAAA");

        manager.get().getEquipe().add(technicien);
        Manager m = managerRepository.save(manager.get());

        technicien.setManager(manager.get());
        technicienRepository.save(technicien);

        return technicien;
    }
}