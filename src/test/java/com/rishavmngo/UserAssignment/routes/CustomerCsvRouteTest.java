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
// @UseAdviceWith
public class CustomerCsvRouteTest {

	private ProducerTemplate producerTemplate;

	@Autowired
	private CamelContext context;

	@MockBean
	private CustomerService customerService;

	// @EndpointInject("mock:result") // Assuming a mock endpoint for testing
	// private MockEndpoint resultEndpoint;

	// @EndpointInject("mock:file:data/inbox")
	// private MockEndpoint fileEndpoint;
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

		// AdviceWith.adviceWith(context, "csv-input", a -> {
		// a.mockEndpoints("file:*");
		// a.interceptFrom("file:data/inbox?move=.done&moveFailed=.failed") // Intercept
		// messages sent to the specified
		// // file endpoint
		// .process(e -> {
		// // Rethrow any BadRequestException to trigger file movement to .failed
		// if (e.getIn().getBody() instanceof BadRequestException) {
		// throw (BadRequestException) e.getIn().getBody();
		// }
		// });
		// });
		//
		// context.start();

		String file = "customerId,firstName,lastName,email\n1,bill,clinton,bill@gmail.com\n2,elon,musk,musk@gmail.com\n";

		// File newFile = createCsv
		// producerTemplate.sendBody("file:data/inbox", new
		// File("data/input/test.csv"));

		producerTemplate.send("file:data/inbox", exchange -> {
			exchange.getIn().setBody(new File("data/input/test.csv"));
			exchange.getIn().setHeader("CamelFileName", "test.csv");
		});

		// resultEndpoint.assertIsSatisfied();
		Thread.sleep(1000L);
		assertFileMovedTo("data/inbox/.done/test.csv");
	}

	void testreal() {

	}

	void testAnother() throws Exception {

		doThrow(new BadRequestException("hello")).when(customerService).addCustomer(any(CustomerEntity.class));

		producerTemplate.send("file:data/inbox", exchange -> {
			exchange.getIn().setBody(new File("data/input/test.csv"));
			exchange.getIn().setHeader("CamelFileName", "test.csv");
		});

		Thread.sleep(1000L);

		assertFileMovedTo("data/inbox/.failed/test.csv");

	}

	private File createTestCsvFile(String path, String content) throws IOException {
		File file = new File(path);
		file.getParentFile().mkdirs(); // Create parent directories if needed
		Files.write(file.toPath(), content.getBytes());
		return file;
	}

	private void assertFileMovedTo(String expectedPath) {
		File expectedFile = new File(expectedPath);
		assertTrue(expectedFile.exists(), "File not found at expected path: " + expectedPath);
	}
}
