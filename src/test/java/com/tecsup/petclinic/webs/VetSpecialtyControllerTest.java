package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Ejercicio 6: pruebas de integracion de la entidad Vet_specialties.
 * Datos semilla: vet 2 -> radiology (1), vet 3 -> surgery (2) y dentistry (3),
 * vet 4 -> surgery (2), vet 5 -> radiology (1). El vet 1 no tiene especialidades.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Slf4j
public class VetSpecialtyControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	// ---------- Asignacion ----------

	@Test
	public void testAssignSpecialtyToVet() throws Exception {

		VetSpecialtyDTO dto = VetSpecialtyDTO.builder()
				.vetId(1)
				.specialtyId(3)
				.certificationDate("2020-01-15")
				.yearsExperience(4)
				.notes("Dental cleaning")
				.build();

		mockMvc.perform(post("/vet-specialties")
						.content(om.writeValueAsString(dto))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.vetId", is(1)))
				.andExpect(jsonPath("$.specialtyId", is(3)))
				.andExpect(jsonPath("$.certificationDate", is("2020-01-15")))
				.andExpect(jsonPath("$.yearsExperience", is(4)))
				.andExpect(jsonPath("$.primary", is(false)));
	}

	@Test
	public void testRemoveSpecialtyFromVet() throws Exception {

		mockMvc.perform(delete("/vet-specialties/3/3"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/vet-specialties/3/3"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testRemoveSpecialtyFromVetKO() throws Exception {

		mockMvc.perform(delete("/vet-specialties/1/1"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testAssignDuplicate_ShouldFail() throws Exception {

		VetSpecialtyDTO dto = VetSpecialtyDTO.builder()
				.vetId(1).specialtyId(2).yearsExperience(1)
				.build();

		mockMvc.perform(post("/vet-specialties")
						.content(om.writeValueAsString(dto))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// La misma combinacion por segunda vez
		mockMvc.perform(post("/vet-specialties")
						.content(om.writeValueAsString(dto))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	// ---------- Consultas ----------

	@Test
	public void testFindSpecialtiesByVet() throws Exception {

		// vet 3 (Linda Douglas): surgery (2) y dentistry (3)
		mockMvc.perform(get("/vets/3/specialties"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].specialtyId", contains(2, 3)));
	}

	@Test
	public void testFindVetsBySpecialty() throws Exception {

		// radiology (1): vets 2 y 5
		mockMvc.perform(get("/specialties/1/vets"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].vetId", contains(2, 5)));
	}

	@Test
	public void testFindSpecialtiesByVet_Empty() throws Exception {

		// vet 1 (James Carter) no tiene especialidades
		mockMvc.perform(get("/vets/1/specialties"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	// ---------- Especialidad principal ----------

	@Test
	public void testSetPrimarySpecialty() throws Exception {

		// dentistry (3) del vet 3 no es principal
		mockMvc.perform(put("/vet-specialties/3/3/primary"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.vetId", is(3)))
				.andExpect(jsonPath("$.specialtyId", is(3)))
				.andExpect(jsonPath("$.primary", is(true)));
	}

	@Test
	public void testSetPrimarySpecialty_OnlyOnePrimary() throws Exception {

		mockMvc.perform(put("/vet-specialties/3/3/primary"))
				.andExpect(status().isOk());

		// surgery (2) deja de ser principal y dentistry (3) pasa a serlo
		mockMvc.perform(get("/vets/3/specialties"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].specialtyId", is(2)))
				.andExpect(jsonPath("$[0].primary", is(false)))
				.andExpect(jsonPath("$[1].specialtyId", is(3)))
				.andExpect(jsonPath("$[1].primary", is(true)));
	}

	@Test
	public void testSetPrimarySpecialty_NotAssigned() throws Exception {

		// el vet 1 no tiene radiology asignada
		mockMvc.perform(put("/vet-specialties/1/1/primary"))
				.andExpect(status().isNotFound());
	}

	// ---------- Experiencia y certificaciones ----------

	@Test
	public void testFindVetsByMinExperience() throws Exception {

		// radiology: vet 2 (8 anios) y vet 5 (7 anios)
		mockMvc.perform(get("/specialties/1/vets").param("minExperience", "7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].vetId", contains(2, 5)));

		// con minimo 8 solo queda el vet 2
		mockMvc.perform(get("/specialties/1/vets").param("minExperience", "8"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].vetId", is(2)));
	}

	@Test
	public void testUpdateYearsExperience() throws Exception {

		VetSpecialtyDTO dto = VetSpecialtyDTO.builder().yearsExperience(12).build();

		mockMvc.perform(put("/vet-specialties/4/2/experience")
						.content(om.writeValueAsString(dto))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.yearsExperience", is(12)));

		mockMvc.perform(get("/vet-specialties/4/2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.yearsExperience", is(12)));
	}

	@Test
	public void testUpdateCertificationDate() throws Exception {

		VetSpecialtyDTO dto = VetSpecialtyDTO.builder().certificationDate("2024-07-01").build();

		mockMvc.perform(put("/vet-specialties/5/1/certification")
						.content(om.writeValueAsString(dto))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.certificationDate", is("2024-07-01")));

		mockMvc.perform(get("/vet-specialties/5/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.certificationDate", is("2024-07-01")));
	}

}
