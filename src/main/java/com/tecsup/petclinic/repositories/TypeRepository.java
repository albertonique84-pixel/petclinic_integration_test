package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.dtos.PetCountByTypeDTO;
import com.tecsup.petclinic.entities.PetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeRepository extends JpaRepository<PetType, Integer> {

	List<PetType> findByActive(Boolean active);

	/**
	 * Cantidad de mascotas por tipo, solo tipos activos
	 */
	@Query("SELECT new com.tecsup.petclinic.dtos.PetCountByTypeDTO(t.id, t.name, COUNT(p.id)) "
			+ "FROM types t, pets p "
			+ "WHERE p.typeId = t.id AND t.active = true "
			+ "GROUP BY t.id, t.name ORDER BY t.id")
	List<PetCountByTypeDTO> countPetsByType();

}
