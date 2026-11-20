package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.exceptions.VetSpecialtyAlreadyExistsException;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;
import com.tecsup.petclinic.services.VetSpecialtyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de la relacion veterinario - especialidad
 */
@RestController
@Slf4j
public class VetSpecialtyController {

	private VetSpecialtyService service;

	public VetSpecialtyController(VetSpecialtyService service) {
		this.service = service;
	}

	/**
	 * Asigna una especialidad a un veterinario
	 */
	@PostMapping(value = "/vet-specialties")
	ResponseEntity<VetSpecialtyDTO> assign(@RequestBody VetSpecialtyDTO dto) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(service.assign(dto));
		} catch (VetSpecialtyAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	@GetMapping(value = "/vet-specialties/{vetId}/{specialtyId}")
	ResponseEntity<VetSpecialtyDTO> findById(@PathVariable Integer vetId, @PathVariable Integer specialtyId) {
		try {
			return ResponseEntity.ok(service.findById(vetId, specialtyId));
		} catch (VetSpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/vet-specialties/{vetId}/{specialtyId}")
	ResponseEntity<String> remove(@PathVariable Integer vetId, @PathVariable Integer specialtyId) {
		try {
			service.remove(vetId, specialtyId);
			return ResponseEntity.ok(" Delete vet " + vetId + " - specialty " + specialtyId);
		} catch (VetSpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Especialidades de un veterinario
	 */
	@GetMapping(value = "/vets/{vetId}/specialties")
	ResponseEntity<List<VetSpecialtyDTO>> findSpecialtiesByVet(@PathVariable Integer vetId) {
		return ResponseEntity.ok(service.findByVetId(vetId));
	}

	/**
	 * Veterinarios de una especialidad, opcionalmente con experiencia minima
	 */
	@GetMapping(value = "/specialties/{specialtyId}/vets")
	ResponseEntity<List<VetSpecialtyDTO>> findVetsBySpecialty(@PathVariable Integer specialtyId,
			@RequestParam(required = false) Integer minExperience) {
		return ResponseEntity.ok(service.findBySpecialtyId(specialtyId, minExperience));
	}

	@PutMapping(value = "/vet-specialties/{vetId}/{specialtyId}/primary")
	ResponseEntity<VetSpecialtyDTO> setPrimary(@PathVariable Integer vetId, @PathVariable Integer specialtyId) {
		try {
			return ResponseEntity.ok(service.setPrimary(vetId, specialtyId));
		} catch (VetSpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping(value = "/vet-specialties/{vetId}/{specialtyId}/experience")
	ResponseEntity<VetSpecialtyDTO> updateExperience(@PathVariable Integer vetId, @PathVariable Integer specialtyId,
			@RequestBody VetSpecialtyDTO dto) {
		try {
			return ResponseEntity.ok(service.updateYearsExperience(vetId, specialtyId, dto.getYearsExperience()));
		} catch (VetSpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping(value = "/vet-specialties/{vetId}/{specialtyId}/certification")
	ResponseEntity<VetSpecialtyDTO> updateCertification(@PathVariable Integer vetId, @PathVariable Integer specialtyId,
			@RequestBody VetSpecialtyDTO dto) {
		try {
			return ResponseEntity.ok(service.updateCertificationDate(vetId, specialtyId, dto.getCertificationDate()));
		} catch (VetSpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
