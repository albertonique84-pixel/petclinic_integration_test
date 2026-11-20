package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.exceptions.VetSpecialtyAlreadyExistsException;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;

import java.util.List;

public interface VetSpecialtyService {

	VetSpecialtyDTO assign(VetSpecialtyDTO dto) throws VetSpecialtyAlreadyExistsException;

	void remove(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException;

	VetSpecialtyDTO findById(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException;

	List<VetSpecialtyDTO> findByVetId(Integer vetId);

	List<VetSpecialtyDTO> findBySpecialtyId(Integer specialtyId, Integer minExperience);

	VetSpecialtyDTO setPrimary(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException;

	VetSpecialtyDTO updateYearsExperience(Integer vetId, Integer specialtyId, Integer years)
			throws VetSpecialtyNotFoundException;

	VetSpecialtyDTO updateCertificationDate(Integer vetId, Integer specialtyId, String date)
			throws VetSpecialtyNotFoundException;

}
