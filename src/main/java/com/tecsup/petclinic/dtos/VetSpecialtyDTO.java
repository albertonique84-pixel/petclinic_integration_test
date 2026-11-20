package com.tecsup.petclinic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de la relacion veterinario - especialidad
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VetSpecialtyDTO {

	private Integer vetId;

	private Integer specialtyId;

	// formato yyyy-MM-dd
	private String certificationDate;

	private Integer yearsExperience;

	private Boolean primary;

	private String notes;

}
