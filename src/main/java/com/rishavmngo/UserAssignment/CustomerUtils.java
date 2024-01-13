
package com.rishavmngo.UserAssignment;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;

public final class CustomerUtils {
	private CustomerUtils() {
	}

	public static CustomerEntity createTestCustomerEntityA() {

		return CustomerEntity.builder().id(1L).firstName("rishav").lastName("raj")
				.email("rishav@gmail.com")
				.fileName("first_batch").build();
	}

	public static CustomerEntity createTestCustomerEntityB() {

		return CustomerEntity.builder().id(2L).firstName("joe").lastName("biden")
				.email("biden@gmail.com")
				.fileName("first_batch").build();
	}

	public static CustomerEntity createTestCustomerEntityC() {

		return CustomerEntity.builder().id(3L).firstName("donald").lastName("trump")
				.email("donald@gmail.com")
				.fileName("second_batch").build();
	}

	public static CustomerEntity createTestCustomerEntityD() {

		return CustomerEntity.builder().id(4L).firstName("risi").lastName("sunak")
				.email("rishi@gmail.com")
				.fileName("second_batch").build();
	}

	public static CustomerEntity createTestCustomerEntityE() {

		return CustomerEntity.builder().id(5L).firstName("ranbir").lastName("kapoor")
				.email("ranbir@gmail.com")
				.fileName("third_batch").build();
	}

	public static CustomerEntity createTestCustomerEntityF() {

		return CustomerEntity.builder().id(6L).firstName("vivek").lastName("ramasyamy")
				.email("vivek@gmail.com")
				.fileName("third_batch").build();
	}
}
