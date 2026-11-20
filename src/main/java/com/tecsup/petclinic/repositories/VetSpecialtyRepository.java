package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.entities.VetSpecialtyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VetSpecialtyRepository extends JpaRepository<VetSpecialty, VetSpecialtyId> {

	List<VetSpecialty> findByVetIdOrderBySpecialtyId(Integer vetId);

	List<VetSpecialty> findBySpecialtyIdOrderByVetId(Integer specialtyId);

	List<VetSpecialty> findBySpecialtyIdAndYearsExperienceGreaterThanEqualOrderByVetId(Integer specialtyId,
			Integer minExperience);

}
