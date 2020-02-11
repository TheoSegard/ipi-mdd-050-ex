package com.ipiecoles.java.mdd050.service;

import com.ipiecoles.java.mdd050.exception.EmployeException;
import com.ipiecoles.java.mdd050.model.Employe;
import com.ipiecoles.java.mdd050.repository.EmployeRepository;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeService {

    public static final Integer PAGE_SIZE_MIN = 10;
    public static final Integer PAGE_SIZE_MAX = 100;
    public static final Integer PAGE_MIN = 0;

    @Autowired
    private EmployeRepository employeRepository;

    public Employe findById(Long id){
        Employe employe = employeRepository.findOne(id);
        if(employe == null){
            throw new EntityNotFoundException("L'employé d'identifiant " + id + " n'a pas été trouvé.");
        }
        return employe;
    }

    public Long countAllEmploye() {
        return employeRepository.count();
    }

    public void deleteEmploye(Long id) throws EmployeException {
        try {
            employeRepository.delete(id);
        } catch (DataIntegrityViolationException e){
            //Cas particulier de la suppression d'un manager avec equipe
            if(e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException myException = (ConstraintViolationException) e.getCause();
                if (myException.getSQLException().getSQLState().equals("23000")) {
                    throw new EmployeException("Pour supprimer un manager, il faut que son équipe soit vide.");
                }
            }
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public <T extends Employe> T creerEmploye(T e) {
        return employeRepository.save(e);
    }

    public <T extends Employe> T updateEmploye(Long id, T employe) {
        if(!employeRepository.exists(id)) {
            throw new EntityNotFoundException("L'employé d'identifiant " + id + " n'existe pas !");
        }
        return employeRepository.save(employe);
    }

    public Page<Employe> findAllEmployes(Integer page, Integer size, String sortProperty, String sortDirection) {
        //Vérification du paramètre page
        if (page == null) {
            page = PAGE_MIN;
        } else if(page < 0) {
            throw new IllegalArgumentException("Le numéro de page ne peut être inférieur à 0");
        }

        //Vérification du paramètre size
        if (size == null) {
            size = PAGE_SIZE_MIN;
        } else if(size < 0 || size > PAGE_SIZE_MAX) {
            throw new IllegalArgumentException("La taille de la page doit être comprise entre 1 et " + PAGE_SIZE_MAX);
        }

        //Vérification du paramètre sortDirection
        Sort sort = null;
        try {
            sort = new Sort(new Sort.Order(Sort.Direction.fromString(sortDirection),sortProperty));
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Le sens du tri peut valoir 'ASC' pour un tri ascendant ou 'DESC' pour un tri descendant (insensible à la casse");
        }
        Pageable pageable = new PageRequest(page,size,sort);
        Page<Employe> employes = employeRepository.findAll(pageable);
        if(page >= employes.getTotalPages()){
            throw new IllegalArgumentException("Le numéro de page ne peut être supérieur à " + employes.getTotalPages());
        } else if(employes.getTotalElements() == 0){
            throw new EntityNotFoundException("Il n'y a aucun employés dans la base de données");
        }
        return employes;
    }

    public Employe findByMatricule(String matricule) {

        if(!matricule.matches("^[MCT][0-9]{5}$")){
            throw new IllegalArgumentException("Mauvais matricule.");
        }

        Employe employe =  this.employeRepository.findByMatricule(matricule);
        if(employe == null){
            throw new EntityNotFoundException("L'employé de matricule '" + matricule + "' n'a pas été trouvé.");
        }
        return employe;
    }

}
