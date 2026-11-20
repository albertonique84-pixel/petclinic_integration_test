package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.mapper.VetMapper;
import com.tecsup.petclinic.repositories.VetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VetServiceImpl implements VetService {

	VetRepository vetRepository;
	VetMapper vetMapper;

	public VetServiceImpl(VetRepository vetRepository, VetMapper vetMapper) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
	}

	@Override
	public VetDTO create(VetDTO vetDTO) {
		if (vetDTO.getActive() == null)
			vetDTO.setActive(true);
		Vet newVet = vetRepository.save(vetMapper.mapToEntity(vetDTO));
		return vetMapper.mapToDto(newVet);
	}

	@Override
	public VetDTO update(VetDTO vetDTO) throws VetNotFoundException {
		Vet vet = getVet(vetDTO.getId());
		vet.setFirstName(vetDTO.getFirstName());
		vet.setLastName(vetDTO.getLastName());
		vet.setEmail(vetDTO.getEmail());
		vet.setPhone(vetDTO.getPhone());
		if (vetDTO.getActive() != null)
			vet.setActive(vetDTO.getActive());
		return vetMapper.mapToDto(vetRepository.save(vet));
	}

	@Override
	public void delete(Integer id) throws VetNotFoundException {
		Vet vet = getVet(id);
		vetRepository.delete(vet);
	}

	@Override
	public VetDTO findById(Integer id) throws VetNotFoundException {
		return vetMapper.mapToDto(getVet(id));
	}

	@Override
	public List<VetDTO> findAll() {
		return vetMapper.mapToDtoList(vetRepository.findAll());
	}

	@Override
	public List<VetDTO> findByActive(Boolean active) {
		List<Vet> vets = vetRepository.findByActive(active);
		vets.forEach(vet -> log.info("" + vet));
		return vetMapper.mapToDtoList(vets);
	}

	/**
	 * Soft delete: el veterinario no se borra, solo queda inactivo
	 */
	@Override
	public VetDTO deactivate(Integer id) throws VetNotFoundException {
		Vet vet = getVet(id);
		vet.setActive(false);
		return vetMapper.mapToDto(vetRepository.save(vet));
	}

	@Override
	public VetDTO activate(Integer id) throws VetNotFoundException {
		Vet vet = getVet(id);
		vet.setActive(true);
		return vetMapper.mapToDto(vetRepository.save(vet));
	}

	private Vet getVet(Integer id) throws VetNotFoundException {
		return vetRepository.findById(id)
				.orElseThrow(() -> new VetNotFoundException("Record not found...!"));
	}

}
