package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.PetCountByTypeDTO;
import com.tecsup.petclinic.dtos.TypeDTO;
import com.tecsup.petclinic.exceptions.TypeNotFoundException;
import com.tecsup.petclinic.services.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de tipos de mascota
 */
@RestController
@Slf4j
public class TypeController {

	private TypeService typeService;

	public TypeController(TypeService typeService) {
		this.typeService = typeService;
	}

	/**
	 * Lista todos los tipos o filtra por estado (?active=true|false)
	 */
	@GetMapping(value = "/types")
	public ResponseEntity<List<TypeDTO>> findAllTypes(@RequestParam(required = false) Boolean active) {
		List<TypeDTO> types = (active == null) ? typeService.findAll() : typeService.findByActive(active);
		return ResponseEntity.ok(types);
	}

	/**
	 * Reporte: cantidad de mascotas por tipo (solo tipos activos)
	 */
	@GetMapping(value = "/types/report/pet-count")
	public ResponseEntity<List<PetCountByTypeDTO>> getPetCountByType() {
		return ResponseEntity.ok(typeService.getPetCountByType());
	}

	@PostMapping(value = "/types")
	@ResponseStatus(HttpStatus.CREATED)
	ResponseEntity<TypeDTO> create(@RequestBody TypeDTO typeTO) {
		TypeDTO newTypeTO = typeService.create(typeTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(newTypeTO);
	}

	@GetMapping(value = "/types/{id}")
	ResponseEntity<TypeDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(typeService.findById(id));
		} catch (TypeNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping(value = "/types/{id}")
	ResponseEntity<TypeDTO> update(@RequestBody TypeDTO typeTO, @PathVariable Integer id) {
		try {
			typeTO.setId(id);
			return ResponseEntity.ok(typeService.update(typeTO));
		} catch (TypeNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/types/{id}")
	ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			typeService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (TypeNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
