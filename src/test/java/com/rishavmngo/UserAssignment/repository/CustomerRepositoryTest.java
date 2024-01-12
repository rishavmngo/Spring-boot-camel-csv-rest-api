package com.rishavmngo.UserAssignment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import com.rishavmngo.UserAssignment.CustomersUtils;
import com.rishavmngo.UserAssignment.domain.CustomerEntity;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository underTest;

	@Test
	void testFindByFileNameReturnExpectedCustomer() {

		CustomerEntity customerA = CustomersUtils.createTestCustomerEntityA();

		underTest.save(customerA);

		List<CustomerEntity> customers = underTest.findByFilename(customerA.getFileName());

		assertEquals(customerA, customers.get(0));

	}

	@Test
	void testFindByFileNameReturnExpectedNumberOfCustomers() {

		CustomerEntity customerA = CustomersUtils.createTestCustomerEntityA();

		CustomerEntity customerB = CustomersUtils.createTestCustomerEntityB();

		underTest.save(customerA);
		underTest.save(customerB);

		List<CustomerEntity> customers = underTest.findByFilename("first_batch");

		assertEquals(2, customers.size());
	}

	@Test
	void testDeleteByFilenameIsDeletingCutomers() {

		CustomerEntity customerA = CustomersUtils.createTestCustomerEntityA();

		CustomerEntity customerB = CustomersUtils.createTestCustomerEntityB();

		underTest.save(customerA);
		underTest.save(customerB);
		underTest.deleteByFilename("first_batch");

		List<CustomerEntity> customers = underTest.findByFilename("first_batch");

		assertEquals(0, customers.size());
	}

	void testWetherDeleteByFilenameIsOnlyDeletingWhoHaveFileName() {

		CustomerEntity customerA = CustomersUtils.createTestCustomerEntityA();

		CustomerEntity customerC = CustomersUtils.createTestCustomerEntityC();

		underTest.save(customerA);
		underTest.save(customerC);
		underTest.deleteByFilename("first_batch");

		List<CustomerEntity> customers = underTest.findByFilename("first_batch");

		assertEquals(1, customers.size());
	}
}
