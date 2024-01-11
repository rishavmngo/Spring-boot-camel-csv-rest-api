package com.rishavmngo.UserAssignment.domain;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CsvRecord(separator = ",", skipFirstLine = true, generateHeaderColumns = true)
@Entity
@Table(name = "customers")
public class CustomerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
	@DataField(pos = 1, columnName = "customerId")
	private Long id;

	@DataField(pos = 2, columnName = "firstName")
	private String firstName;

	@DataField(pos = 3, columnName = "lastName")
	private String lastName;

	private String fileName;

	@Column(unique = true)
	@DataField(pos = 4, columnName = "email")
	private String email;
}
