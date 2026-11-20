package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VetDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Ejercicio 1: pruebas de integracion de la entidad Vet.
 * La peticion recorre controlador -> servicio -> repositorio -> H2 (sin mocks).
 * Cada prueba se revierte al terminar (@Transactional) para no alterar los datos semilla.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Slf4j
public class VetControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testFindAllVets() throws Exception {

		final int NRO_RECORD = 6;
		final int ID_FIRST_RECORD = 1;

		this.mockMvc.perform(get("/vets"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(NRO_RECORD)))
				.andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
	}

	@Test
	public void testFindVetOK() throws Exception {

		this.mockMvc.perform(get("/vets/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.firstName", is("James")))
				.andExpect(jsonPath("$.lastName", is("Carter")))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testFindVetKO() throws Exception {

		mockMvc.perform(get("/vets/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testCreateVet() throws Exception {

		VetDTO newVetTO = VetDTO.builder()
				.firstName("Juan")
				.lastName("Sanchez")
				.email("juan.sanchez@petclinic.com")
				.phone("987654321")
				.build();

		this.mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(newVetTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName", is("Juan")))
				.andExpect(jsonPath("$.lastName", is("Sanchez")))
				.andExpect(jsonPath("$.email", is("juan.sanchez@petclinic.com")))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testDeactivateVet() throws Exception {

		// Se desactiva al vet 1 (James Carter), que esta activo
		mockMvc.perform(put("/vets/1/deactivate"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.active", is(false)));

		// Se confirma con un GET
		mockMvc.perform(get("/vets/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active", is(false)));
	}

	@Test
	public void testReactivateVet() throws Exception {

		// El vet 6 (Sharon Jenkins) esta inactivo en los datos semilla
		mockMvc.perform(put("/vets/6/activate"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(6)))
				.andExpect(jsonPath("$.lastName", is("Jenkins")))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testFindActiveVets() throws Exception {

		mockMvc.perform(get("/vets").param("active", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))
				.andExpect(jsonPath("$[*].active", everyItem(is(true))));
	}

	@Test
	public void testFindInactiveVets() throws Exception {

		mockMvc.perform(get("/vets").param("active", "false"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(6)))
				.andExpect(jsonPath("$[0].active", is(false)));
	}

	@Test
	public void testUpdateVet() throws Exception {

		VetDTO newVetTO = VetDTO.builder()
				.firstName("Mario").lastName("Rojas")
				.email("mario.rojas@petclinic.com").phone("911111111")
				.build();

		ResultActions mvcActions = mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(newVetTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		Integer id = JsonPath.parse(mvcActions.andReturn().getResponse().getContentAsString()).read("$.id");

		VetDTO upVetTO = VetDTO.builder()
				.firstName("Mario").lastName("Rojas Paz")
				.email("mrojas@petclinic.com").phone("922222222")
				.build();

		mockMvc.perform(put("/vets/" + id)
						.content(om.writeValueAsString(upVetTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		mockMvc.perform(get("/vets/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.lastName", is("Rojas Paz")))
				.andExpect(jsonPath("$.email", is("mrojas@petclinic.com")));
	}

	@Test
	public void testDeleteVet() throws Exception {

		VetDTO newVetTO = VetDTO.builder()
				.firstName("Temporal").lastName("Borrar")
				.build();

		ResultActions mvcActions = mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(newVetTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		Integer id = JsonPath.parse(mvcActions.andReturn().getResponse().getContentAsString()).read("$.id");

		mockMvc.perform(delete("/vets/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/vets/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteVetKO() throws Exception {

		mockMvc.perform(delete("/vets/1000"))
				.andExpect(status().isNotFound());
	}

}
