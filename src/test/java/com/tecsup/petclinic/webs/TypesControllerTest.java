package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.TypeDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Ejercicio 5: pruebas de integracion de la entidad Types.
 * Se levanta un contexto nuevo (BD H2 recien cargada con data.sql), porque el reporte
 * cuenta mascotas y otras pruebas (PetControllerTest) agregan mascotas de tipo cat.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Slf4j
public class TypesControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testFindAllTypes() throws Exception {

		final int NRO_RECORD = 8;
		final int ID_FIRST_RECORD = 1;

		this.mockMvc.perform(get("/types"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(NRO_RECORD)))
				.andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
	}

	@Test
	public void testFindTypeOK() throws Exception {

		this.mockMvc.perform(get("/types/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("cat")))
				.andExpect(jsonPath("$.description", is("Domestic feline")))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testFindTypeKO() throws Exception {

		mockMvc.perform(get("/types/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testCreateType() throws Exception {

		TypeDTO newTypeTO = TypeDTO.builder()
				.name("ferret")
				.description("Domestic ferret")
				.sizeCategory("small")
				.averageLifespan(7)
				.careLevel("high")
				.build();

		this.mockMvc.perform(post("/types")
						.content(om.writeValueAsString(newTypeTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is("ferret")))
				.andExpect(jsonPath("$.sizeCategory", is("small")))
				.andExpect(jsonPath("$.averageLifespan", is(7)))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testGetPetCountByType() throws Exception {

		// cat (id 1) tiene 4 mascotas: Leo, Samantha, Max y Sly
		mockMvc.perform(get("/types/report/pet-count"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].typeId", is(1)))
				.andExpect(jsonPath("$[0].typeName", is("cat")))
				.andExpect(jsonPath("$[0].petCount", is(4)));
	}

	@Test
	public void testDeleteType() throws Exception {

		TypeDTO newTypeTO = TypeDTO.builder()
				.name("iguana")
				.description("Tipo temporal")
				.build();

		ResultActions mvcActions = mockMvc.perform(post("/types")
						.content(om.writeValueAsString(newTypeTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		Integer id = JsonPath.parse(mvcActions.andReturn().getResponse().getContentAsString()).read("$.id");

		mockMvc.perform(delete("/types/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/types/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindActiveTypes() throws Exception {

		// 7 tipos activos, se excluye snake (id 6)
		mockMvc.perform(get("/types").param("active", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(7)))
				.andExpect(jsonPath("$[*].active", everyItem(is(true))))
				.andExpect(jsonPath("$[*].name", not(hasItem("snake"))));
	}

	@Test
	public void testGetPetCountByType_ExcludeInactive() throws Exception {

		// snake esta inactivo y tiene a Basil, pero no debe salir en el reporte
		mockMvc.perform(get("/types/report/pet-count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].typeName", not(hasItem("snake"))))
				.andExpect(jsonPath("$[*].typeId", not(hasItem(6))));
	}

	@Test
	public void testUpdateType() throws Exception {

		TypeDTO newTypeTO = TypeDTO.builder()
				.name("parrot").description("Bird").careLevel("medium")
				.build();

		ResultActions mvcActions = mockMvc.perform(post("/types")
						.content(om.writeValueAsString(newTypeTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		Integer id = JsonPath.parse(mvcActions.andReturn().getResponse().getContentAsString()).read("$.id");

		TypeDTO upTypeTO = TypeDTO.builder()
				.name("parrot").description("Talking bird").careLevel("high").active(true)
				.build();

		mockMvc.perform(put("/types/" + id)
						.content(om.writeValueAsString(upTypeTO))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		mockMvc.perform(get("/types/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.description", is("Talking bird")))
				.andExpect(jsonPath("$.careLevel", is("high")));
	}

	@Test
	public void testDeleteTypeKO() throws Exception {

		mockMvc.perform(delete("/types/1000"))
				.andExpect(status().isNotFound());
	}

}
