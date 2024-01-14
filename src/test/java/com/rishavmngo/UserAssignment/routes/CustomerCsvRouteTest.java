package com.rishavmngo.UserAssignment.routes;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Configuration;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.rishavmngo.UserAssignment.CustomersUtils;
import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.exceptions.BadRequestException;
import com.rishavmngo.UserAssignment.service.CustomerService;

@CamelSpringBootTest
@SpringBootTest
@EnableAutoConfiguration
@ExtendWith(MockitoExtension.class)
@MockEndpoints("direct:*")
// @UseAdviceWith
public class CustomerCsvRouteTest {

	private ProducerTemplate producerTemplate;

	@Autowired
	private CamelContext context;

	@MockBean
	private CustomerService customerService;

	// @EndpointInject("mock:result") // Assuming a mock endpoint for testing
	// private MockEndpoint resultEndpoint;

	// private MockEndpoint fileEndpoint;

	@EndpointInject("mock:direct:csvFileProcessor")
	private MockEndpoints csvFileProcessor;
	//
	// @EndpointInject("mock:file:data/inbox.done")
	// private MockEndpoint doneEndpoint;
	//
	// @EndpointInject("mock:file:data/inbox.failed")
	// private MockEndpoint failedEndpoint;

	@Configuration
	static class TestConfig {

		RoutesBuilder route() {
			return new CustomerCsvRoutes();
		}
	}

	@BeforeEach
	public void setup() {

		producerTemplate = context.createProducerTemplate();

	}

	void test() throws Exception {
		List<CustomerEntity> customers = Arrays.asList(
				CustomersUtils.createTestCustomerEntityA(),
				CustomersUtils.createTestCustomerEntityB());

		// doAnswer(invocation -> {
		// throw new BadRequestException(
		// "Email " + customers.get(0).getEmail() + " taken");
		// }).when(customerService).addCustomer(customers.get(0));

		String file = "customerId,firstName,lastName,email\n1,bill,clinton,bill@gmail.com\n2,elon,musk,musk@gmail.com\n";

		// producerTemplate.request("file:data/inbox", exhange -> {
		//
		// exhange.getIn().setHeader("CamelFileName", "first_batch");
		// exhange.getIn().setBody(file);
		// });

		// ArgumentCaptor<CustomerEntity> customerArgumentCaptor =
		// ArgumentCaptor.forClass(CustomerEntity.class);

		// CustomerEntity capturedCustomer = customerArgumentCaptor.getValue();
		// assertThat(capturedCustomer).isEqualTo(customers.get(0));
	}

	@Test
	void testOne() throws Exception {

		doNothing().when(customerService).addCustomer(any());

		producerTemplate.send("file:data/inbox", exchange -> {
			exchange.getIn().setBody(new File("data/input/test.csv"));
			exchange.getIn().setHeader("CamelFileName", "test.csv");
		});

		Thread.sleep(1000L);
		assertFileMovedTo("data/inbox/.done/test.csv");
	}

	@Test
	void testAnother() throws Exception {

		doThrow(new BadRequestException("hello")).when(customerService).addCustomer(any(CustomerEntity.class));

		producerTemplate.send("direct:csvFileProcessor", exchange -> {
			exchange.getIn().setBody(CustomersUtils.createTestCustomerEntityA());
			exchange.getIn().setHeader("CamelFileName", "test.csv");
		});

		verify(customerService).addCustomer(any(CustomerEntity.class));
	}

	@Test
	void testAnotherOne() throws Exception {

		doNothing().when(customerService).addCustomer(any());

		producerTemplate.send("direct:csvFileProcessor", exchange -> {
			exchange.getIn().setBody(CustomersUtils.createTestCustomerEntityA());
			exchange.getIn().setHeader("CamelFileName", "test.csv");
		});

		verify(customerService).addCustomer(any(CustomerEntity.class));
	}

	private void assertFileMovedTo(String expectedPath) {
		File expectedFile = new File(expectedPath);
		assertTrue(expectedFile.exists(), "File not found at expected path: " + expectedPath);
	}
}
