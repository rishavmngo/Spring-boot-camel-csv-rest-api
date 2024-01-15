package com.rishavmngo.UserAssignment.routes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.service.CustomerService;

import lombok.Getter;
import lombok.Setter;

@Component
public class CustomerCsvRoutes extends RouteBuilder {

	@Autowired
	private CustomerService customerService;

	@Override
	public void configure() throws Exception {
		DataFormat bind = new BindyCsvDataFormat(CustomerEntity.class);

		// from("file:data/inbox?move=.done&moveFailed=.failed")
		// .setHeader("CamelFileName", simple("${file:name.noext}"))
		// .unmarshal(bind)
		// .process(exchange -> {
		// // Access the unmarshalled data
		// List<CustomerEntity> customers = exchange.getIn().getBody(List.class);
		//
		// // Set the filename for each customer
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// for (CustomerEntity customer : customers) {
		// customer.setFileName(filename);
		// }
		//
		// // Replace the body with the modified data
		// exchange.getIn().setBody(customers);
		// })
		// .bean(customerService, "SaveAll")
		// .end();

		// from("file:data/inbox?move=.done&moveFailed=.failed")
		// .setHeader("CamelFileName", simple("${file:name.noext}"))
		// .unmarshal(bind)
		// .process(exchange -> {
		// // Access the unmarshalled data
		// List<CustomerEntity> customers = exchange.getIn().getBody(List.class);
		//
		// // Set the filename for each customer
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// for (CustomerEntity customer : customers) {
		// customer.setFileName(filename);
		// }
		//
		// // Replace the body with the modified data
		// exchange.getIn().setBody(customers);
		// })
		// .bean(customerService, "SaveAll")
		// .end();

		// onException(BadRequestException.class)
		// .process(exhange -> {
		// System.out.println("" +
		// exhange.getIn().getBody(CustomerEntity.class).toString());
		// })
		// .handled(true)
		// .end();

		// from("file:data/inbox?move=.done&moveFailed=.failed")
		// .setHeader("CamelFileName", simple("${file:name.noext}"))
		// .unmarshal(bind)
		// .split(body()) // Split the list of customers
		// .process(exchange -> {
		// CustomerEntity customer = exchange.getIn().getBody(CustomerEntity.class);
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// customer.setFileName(filename);
		// exchange.getIn().setBody(customer);
		// })
		// .bean(customerService, "addCustomer")
		// .end();

		// onException(IllegalArgumentException.class).handled(true).log("Not a csv
		// file");

		// from("file:data/inbox?move=.done&moveFailed=.failed")
		// .routeId("csv-input")
		// .setHeader("CamelFileName", simple("${file:name.noext}"))
		// .unmarshal(bind)
		// .split(body()) // Split the list of customers
		// .to("direct:csvFileProcessor");
		//
		// from("direct:csvFileProcessor")
		// .process(exchange -> {
		// CustomerEntity customer = exchange.getIn().getBody(CustomerEntity.class);
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// customer.setFileName(filename);
		// try {
		// customerService.addCustomer(customer);
		// System.out.println("Customer with email " + customer.getEmail() + " added
		// successfully");
		//
		// } catch (Exception e) {
		//
		// System.out.println("Customer with email " + customer.getEmail() + " added
		// unsucessfully");
		//
		// throw e;
		// }
		// })
		// .end();

		// onException(IllegalArgumentException.class)
		// .handled(true)
		// .log("Invalid csv file");

		// onException(BadRequestException.class)
		// .handled(true)
		// .to("file:data/inbox/.new");

		SharedState state = new SharedState();
		// from("file:data/inbox?move=.done&moveFailed=.failed")
		// .log("deteced a file")
		// .routeId("csv-input")
		// .setHeader("CamelFileName", simple("${file:name.noext}"))
		// .doTry()
		// .unmarshal(bind)
		// .to("direct:csvFileProcessor")
		// .endDoTry()
		// .doCatch(IllegalArgumentException.class)
		// .log("Failed to parse csv file, invalid csv file given")
		// .throwException(new IllegalArgumentException("Not a csv file"));
		//
		// from("direct:csvFileProcessor")
		// .process(e -> state.reset())
		// .split(body())
		// .process(exchange -> {
		// CustomerEntity customer = exchange.getIn().getBody(CustomerEntity.class);
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// customer.setFileName(filename);
		// try {
		// customerService.addCustomer(customer);
		// state.incrementSuccssFul();
		// // System.out.println("Customer with email " + customer.getEmail() + " added
		// // successfully");
		//
		// } catch (Exception e) {
		//
		// state.incrementUnSuccssFul();
		// state.setErrorFlag(true);
		// // System.out.println("Customer with email " + customer.getEmail() + "
		// // addedunsucessfully");
		// }
		// }).end()
		// .process(exchange -> {
		//
		// if (state.isErrorFlag()) {
		// throw new BadRequestException("Failed to add " + state.getUnSuccessFul() + "
		// customers");
		// }
		//
		// System.out.println(state.getSuccessFul() + " customers added successfully");
		//
		// });

		from("file:data/inbox?noop=true")
				.log("deteced a file")
				.routeId("csv-input")
				.process(e -> state.reset())
				.doTry()
				.unmarshal(bind)
				.to("direct:csvFileProcessor")
				.endDoTry()
				.doCatch(IllegalArgumentException.class)
				.log("Failed to parse csv file, invalid csv file given")
				.process(e -> state.setErrorFlag(true))
				.to("direct:moveFile");

		from("direct:csvFileProcessor")
				.split(body())
				.process(exchange -> {
					CustomerEntity customer = exchange.getIn().getBody(CustomerEntity.class);
					String filename = exchange.getIn().getHeader("CamelFileName", String.class);
					customer.setFileName(filename);
					try {
						customerService.addCustomer(customer);
						state.incrementSuccssFul();
						// System.out.println("Customer with email " + customer.getEmail() + " added
						// successfully");

					} catch (Exception e) {

						state.incrementUnSuccssFul();
						state.setErrorFlag(true);
						// System.out.println("Customer with email " + customer.getEmail() + "
						// addedunsucessfully");
					}
				})
				.end()
				.to("direct:moveFile");

		from("direct:moveFile")
				.process(e -> {

					String filename = e.getIn().getHeader("CamelFileName", String.class);
					Path sourcePath = Paths
							.get("data/inbox/" + filename);
					Path destinationPath = Paths
							.get("data/inbox/" + (state.isErrorFlag() ? ".failed/" : ".done/") + filename);

					Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
					System.out.println("file moved to " + destinationPath.toString());
					System.out.println("Successfully added customers: " + state.getSuccessFul());
					System.out.println("Failed to added customers: " + state.getUnSuccessFul());
				}

				);
		// .process(exchange -> {
		// CustomerEntity customer = exchange.getIn().getBody(CustomerEntity.class);
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// customer.setFileName(filename);
		// exchange.getIn().setHeader("UserEmail", customer.getEmail());
		// }).doTry()
		// .bean(customerService, "addCustomer")
		// .log("Email added successfully: ${header.UserEmail}")
		// .process(exchange -> {
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// Path sourcePath = Paths.get("data/inbox/" + filename + ".csv");
		// Path destinationPath = Paths.get("data/inbox/.done/" + filename + ".csv");
		//
		// if (Files.exists(sourcePath)) {
		//
		// try {
		// Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// })
		// .endDoTry()
		// .doCatch(BadRequestException.class)
		// .log("Error occured Email taken: ${header.UserEmail}")
		// .process(exchange -> {
		// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
		// Path sourcePath = Paths.get("data/inbox/" + filename + ".csv");
		// Path destinationPath = Paths.get("data/inbox/.failed/" + filename + ".csv");
		// // System.out.println("hello " + sourcePath.toString());
		// // System.out.println(Files.exists(sourcePath));
		//
		// if (Files.exists(sourcePath)) {
		//
		// try {
		// Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// });
	}

}

@Getter
@Setter
class SharedState {
	private boolean errorFlag;
	private boolean csvParsingErrorFlag;
	private int successFul = 0;
	private int unSuccessFul = 0;

	public void reset() {
		setErrorFlag(false);
		setSuccessFul(0);
		setUnSuccessFul(0);
		setCsvParsingErrorFlag(false);
	}

	public void incrementSuccssFul() {
		setSuccessFul(getSuccessFul() + 1);
	}

	public void incrementUnSuccssFul() {
		setUnSuccessFul(getUnSuccessFul() + 1);
	}

}

// .process(exchange -> {
// String filename = exchange.getIn().getHeader("CamelFileName", String.class);
// Path sourcePath = Paths.get("data/inbox/" + filename + ".csv");
// Path destinationPath = Paths.get("data/inbox/.failed/" + filename + ".csv");
// // System.out.println("hello " + sourcePath.toString());
// // System.out.println(Files.exists(sourcePath));
//
// if (Files.exists(sourcePath)) {
//
// try {
// Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
//
// });
