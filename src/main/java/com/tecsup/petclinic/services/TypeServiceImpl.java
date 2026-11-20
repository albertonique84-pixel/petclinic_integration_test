package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.PetCountByTypeDTO;
import com.tecsup.petclinic.dtos.TypeDTO;
import com.tecsup.petclinic.entities.PetType;
import com.tecsup.petclinic.exceptions.TypeNotFoundException;
import com.tecsup.petclinic.mapper.TypeMapper;
import com.tecsup.petclinic.repositories.TypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TypeServiceImpl implements TypeService {

	TypeRepository typeRepository;
	TypeMapper typeMapper;

	public TypeServiceImpl(TypeRepository typeRepository, TypeMapper typeMapper) {
		this.typeRepository = typeRepository;
		this.typeMapper = typeMapper;
	}

	@Override
	public TypeDTO create(TypeDTO typeDTO) {
		if (typeDTO.getActive() == null)
			typeDTO.setActive(true);
		PetType newType = typeRepository.save(typeMapper.mapToEntity(typeDTO));
		return typeMapper.mapToDto(newType);
	}

	@Override
	public TypeDTO update(TypeDTO typeDTO) throws TypeNotFoundException {
		findById(typeDTO.getId());
		PetType type = typeRepository.save(typeMapper.mapToEntity(typeDTO));
		return typeMapper.mapToDto(type);
	}

	@Override
	public void delete(Integer id) throws TypeNotFoundException {
		TypeDTO type = findById(id);
		typeRepository.delete(typeMapper.mapToEntity(type));
	}

	@Override
	public TypeDTO findById(Integer id) throws TypeNotFoundException {
		PetType type = typeRepository.findById(id)
				.orElseThrow(() -> new TypeNotFoundException("Record not found...!"));
		return typeMapper.mapToDto(type);
	}

	@Override
	public List<TypeDTO> findAll() {
		return typeMapper.mapToDtoList(typeRepository.findAll());
	}

	@Override
	public List<TypeDTO> findByActive(Boolean active) {
		return typeMapper.mapToDtoList(typeRepository.findByActive(active));
	}

	@Override
	public List<PetCountByTypeDTO> getPetCountByType() {
		List<PetCountByTypeDTO> report = typeRepository.countPetsByType();
		report.forEach(row -> log.info("" + row));
		return report;
	}

}
