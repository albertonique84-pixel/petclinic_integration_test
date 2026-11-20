package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author jgomezm
 *
 */
@NoArgsConstructor
@Entity(name = "specialties")
@Data
public class Specialty {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "office")
	private String office;

	@Column(name = "h_open")
	private Integer hOpen;

	@Column(name = "h_close")
	private Integer hClose;

}
