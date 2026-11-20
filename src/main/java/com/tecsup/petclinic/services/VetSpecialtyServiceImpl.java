package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.entities.VetSpecialtyId;
import com.tecsup.petclinic.exceptions.VetSpecialtyAlreadyExistsException;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;
import com.tecsup.petclinic.mapper.VetSpecialtyMapper;
import com.tecsup.petclinic.repositories.VetSpecialtyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class VetSpecialtyServiceImpl implements VetSpecialtyService {

	VetSpecialtyRepository repository;
	VetSpecialtyMapper mapper;

	public VetSpecialtyServiceImpl(VetSpecialtyRepository repository, VetSpecialtyMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public VetSpecialtyDTO assign(VetSpecialtyDTO dto) throws VetSpecialtyAlreadyExistsException {

		VetSpecialtyId id = new VetSpecialtyId(dto.getVetId(), dto.getSpecialtyId());
		if (repository.existsById(id))
			throw new VetSpecialtyAlreadyExistsException("Specialty already assigned to vet...!");

		if (dto.getYearsExperience() == null)
			dto.setYearsExperience(0);
		if (dto.getPrimary() == null)
			dto.setPrimary(false);

		VetSpecialty saved = repository.save(mapper.mapToEntity(dto));
		return mapper.mapToDto(saved);
	}

	@Override
	public void remove(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {
		repository.delete(get(vetId, specialtyId));
	}

	@Override
	public VetSpecialtyDTO findById(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {
		return mapper.mapToDto(get(vetId, specialtyId));
	}

	@Override
	public List<VetSpecialtyDTO> findByVetId(Integer vetId) {
		return mapper.mapToDtoList(repository.findByVetIdOrderBySpecialtyId(vetId));
	}

	@Override
	public List<VetSpecialtyDTO> findBySpecialtyId(Integer specialtyId, Integer minExperience) {
		List<VetSpecialty> list = (minExperience == null)
				? repository.findBySpecialtyIdOrderByVetId(specialtyId)
				: repository.findBySpecialtyIdAndYearsExperienceGreaterThanEqualOrderByVetId(specialtyId, minExperience);
		return mapper.mapToDtoList(list);
	}

	/**
	 * Marca una especialidad como principal; las demas del mismo vet quedan en false
	 */
	@Override
	@Transactional
	public VetSpecialtyDTO setPrimary(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {

		VetSpecialty target = get(vetId, specialtyId);

		repository.findByVetIdOrderBySpecialtyId(vetId).forEach(vs -> {
			vs.setPrimary(vs.getSpecialtyId().equals(specialtyId));
			repository.save(vs);
		});

		return mapper.mapToDto(target);
	}

	@Override
	public VetSpecialtyDTO updateYearsExperience(Integer vetId, Integer specialtyId, Integer years)
			throws VetSpecialtyNotFoundException {
		VetSpecialty vs = get(vetId, specialtyId);
		vs.setYearsExperience(years);
		return mapper.mapToDto(repository.save(vs));
	}

	@Override
	public VetSpecialtyDTO updateCertificationDate(Integer vetId, Integer specialtyId, String date)
			throws VetSpecialtyNotFoundException {
		VetSpecialty vs = get(vetId, specialtyId);
		vs.setCertificationDate(LocalDate.parse(date));
		return mapper.mapToDto(repository.save(vs));
	}

	private VetSpecialty get(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {
		return repository.findById(new VetSpecialtyId(vetId, specialtyId))
				.orElseThrow(() -> new VetSpecialtyNotFoundException("Record not found...!"));
	}

}
