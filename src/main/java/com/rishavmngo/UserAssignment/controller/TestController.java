package com.rishavmngo.UserAssignment.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rishavmngo.UserAssignment.domain.CustomerEntity;
import com.rishavmngo.UserAssignment.exceptions.UniqueConstraintException;
import com.rishavmngo.UserAssignment.service.CustomerService;

@Component
public class TestController extends RouteBuilder {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private HandleConstraintException handleConstraintException;

	@Override
	public void configure() throws Exception {
		DataFormat bind = new BindyCsvDataFormat(CustomerEntity.class);

		// onException(UniqueConstraintException.class)
		// .log("Unique constraint exception")
		// .end();

		from("file:data/inbox?move=.done&moveFailed=.failed")
				.setHeader("CamelFileName", simple("${file:name.noext}"))
				.unmarshal(bind)
				.process(exchange -> {
					// Access the unmarshalled data
					List<CustomerEntity> customers = exchange.getIn().getBody(List.class);

					// Set the filename for each customer
					String filename = exchange.getIn().getHeader("CamelFileName", String.class);
					for (CustomerEntity customer : customers) {
						customer.setFileName(filename);
					}

					// Replace the body with the modified data
					exchange.getIn().setBody(customers);
				})
				.bean(customerService, "SaveAll")
				.end();

		rest("/customers")
				.produces("application/json")
				.get("/getAll")
				.to("direct:getAllCustomers");

		// Route to handle the REST endpoint
		from("direct:getAllCustomers")
				.bean(customerService, "getAll")
				.marshal().json()
				.end();

		// from("rest:delete:deleteByFileName/{filename}")
		// .bean("getCurrentTimeBean");
	}

}

// .onException(ConstraintViolationException.class)
// .handled(true)
// .maximumRedeliveries(0)
@Component
class HandleConstraintException {
	public void process(Exchange exhange) {
		System.out.println("exception: " + exhange.getIn());
	}
}

class GetCurrentTimeBean {
	public void getCurrentTime(Exchange exhange) {
		System.out.println(exhange.getIn().getHeader("filename", String.class));
	}
}
