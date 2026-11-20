package com.tecsup.petclinic.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Fila del reporte: cantidad de mascotas por tipo
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PetCountByTypeDTO {

	private Integer typeId;

	private String typeName;

	private Long petCount;

}
