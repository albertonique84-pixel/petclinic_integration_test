package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.services.VetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de veterinarios
 */
@RestController
@Slf4j
public class VetController {

	private VetService vetService;

	public VetController(VetService vetService) {
		this.vetService = vetService;
	}

	/**
	 * Lista todos los veterinarios o filtra por estado (?active=true|false)
	 */
	@GetMapping(value = "/vets")
	public ResponseEntity<List<VetDTO>> findAllVets(@RequestParam(required = false) Boolean active) {
		List<VetDTO> vets = (active == null) ? vetService.findAll() : vetService.findByActive(active);
		return ResponseEntity.ok(vets);
	}

	@PostMapping(value = "/vets")
	@ResponseStatus(HttpStatus.CREATED)
	ResponseEntity<VetDTO> create(@RequestBody VetDTO vetTO) {
		VetDTO newVetTO = vetService.create(vetTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(newVetTO);
	}

	@GetMapping(value = "/vets/{id}")
	ResponseEntity<VetDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(vetService.findById(id));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping(value = "/vets/{id}")
	ResponseEntity<VetDTO> update(@RequestBody VetDTO vetTO, @PathVariable Integer id) {
		try {
			vetTO.setId(id);
			return ResponseEntity.ok(vetService.update(vetTO));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping(value = "/vets/{id}/deactivate")
	ResponseEntity<VetDTO> deactivate(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(vetService.deactivate(id));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping(value = "/vets/{id}/activate")
	ResponseEntity<VetDTO> activate(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(vetService.activate(id));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/vets/{id}")
	ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			vetService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
