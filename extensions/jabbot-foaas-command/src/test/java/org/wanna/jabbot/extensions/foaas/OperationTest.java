package org.wanna.jabbot.extensions.foaas;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wanna.jabbot.extensions.foaas.binding.Field;
import org.wanna.jabbot.extensions.foaas.binding.Operation;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-07
 */
public class OperationTest {
	ObjectMapper mapper;
	private Field from, target, company;

	final String json = "[\n" +
			"  {\n" +
			"    \"name\": \"Ballmer\",\n" +
			"    \"url\": \"/ballmer/:name/:company/:from\",\n" +
			"    \"fields\": [\n" +
			"      {\n" +
			"        \"name\": \"Name\",\n" +
			"        \"field\": \"name\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"name\": \"Company\",\n" +
			"        \"field\": \"company\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"name\": \"From\",\n" +
			"        \"field\": \"from\"\n" +
			"      }\n" +
			"    ]\n" +
			"  },\n" +
			"  {\n" +
			"    \"name\": \"Because\",\n" +
			"    \"url\": \"/because/:from\",\n" +
			"    \"fields\": [\n" +
			"      {\n" +
			"        \"name\": \"From\",\n" +
			"        \"field\": \"from\"\n" +
			"      }\n" +
			"    ]\n" +
			"  },\n" +
			"  {\n" +
			"    \"name\": \"Bus\",\n" +
			"    \"url\": \"/bus/:name/:from\",\n" +
			"    \"fields\": [\n" +
			"      {\n" +
			"        \"name\": \"Name\",\n" +
			"        \"field\": \"name\"\n" +
			"      },\n" +
			"      {\n" +
			"        \"name\": \"From\",\n" +
			"        \"field\": \"from\"\n" +
			"      }\n" +
			"    ]\n" +
			"  }]";
	@Before
	public void before(){
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		from = new Field();
		from.setField("from");
		from.setValue("vincent");

		target = new Field();
		target.setField("name");
		target.setValue("someone");

		company = new Field();
		company.setField("company");
		company.setValue("a_company");
	}

	@Test
	public void parseOperationList() throws Exception{
		TypeFactory typeFactory = mapper.getTypeFactory();
		CollectionType collectionType = typeFactory.constructCollectionType(
				List.class, Operation.class);
		List<Operation> operations = mapper.readValue(json, collectionType);
		Assert.assertThat(operations.size(),is(3));
		for (Operation operation : operations) {
			System.out.println(operation.getName());
			System.out.println(operation.getUrl());
			Assert.assertThat(operation.getFields(), notNullValue());
			Assert.assertFalse(operation.getFields().isEmpty());
		}
	}

	@Test
	public void populateUrl(){
		Operation operation = new Operation();
		operation.setUrl("/test/:from/:name");

		List<Field> fields = new ArrayList<>();
		fields.add(from);
		fields.add(target);
		operation.setFields(fields);

		Assert.assertThat(operation.getPopulatedUrl(),is("/test/"+from.getValue()+"/"+target.getValue()));
	}

	@Test
	public void execute() throws Exception{
		Operation operation = new Operation();
		operation.setUrl("/because/:from");

		List<Field> fields = new ArrayList<>();
		fields.add(from);
		operation.setFields(fields);

		String response = operation.execute();

		Assert.assertThat(response,is("Why? Because Fuck you, that's why. - "+from.getValue()));
	}
}
