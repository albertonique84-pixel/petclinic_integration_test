package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.entities.VetSpecialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VetSpecialtyMapper {

	VetSpecialtyMapper INSTANCE = Mappers.getMapper(VetSpecialtyMapper.class);

	@Mapping(source = "certificationDate", target = "certificationDate", dateFormat = "yyyy-MM-dd")
	VetSpecialty mapToEntity(VetSpecialtyDTO dto);

	@Mapping(source = "certificationDate", target = "certificationDate", dateFormat = "yyyy-MM-dd")
	VetSpecialtyDTO mapToDto(VetSpecialty entity);

	List<VetSpecialtyDTO> mapToDtoList(List<VetSpecialty> list);

}
