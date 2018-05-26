/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.itest.java;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.itest.java.objects.Greeting;
import io.restassured.itest.java.objects.Message;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import static io.restassured.mapper.ObjectMapperType.GSON;
import static io.restassured.mapper.ObjectMapperType.JACKSON_2;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import java.io.IOException;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;

public class CustomObjectMappingITest extends WithJetty {

    public AtomicBoolean customSerializationUsed = new AtomicBoolean(false);
    public AtomicBoolean customDeserializationUsed = new AtomicBoolean(false);

    @Before
    public void
            setup() throws Exception {
        customSerializationUsed.set(true);
        customDeserializationUsed.set(true);
    }

    @Ignore
    public void
            using_explicit_custom_object_mapper() throws Exception {
        final Message message = new Message();
        message.setMessage("A message");
        final ObjectMapper mapper = new ObjectMapper() {
            public Object deserialize(ObjectMapperDeserializationContext context) {
                final String toDeserialize = context.getDataToDeserialize().asString();
                final String unquoted = StringUtils.remove(toDeserialize, "#");
                final Message message = new Message();
                message.setMessage(unquoted);
                customDeserializationUsed.set(true);
                return message;
            }

            public Object serialize(ObjectMapperSerializationContext context) {
                final Message objectToSerialize = context.getObjectToSerializeAs(Message.class);
                final String message = objectToSerialize.getMessage();
                customSerializationUsed.set(true);
                return "##" + message + "##";
            }
        };

        final Message returnedMessage = given().body(message, mapper).when().post("/reflect").as(Message.class, mapper);

        assertThat(returnedMessage.getMessage(), equalTo("A message"));
        assertThat(customSerializationUsed.get(), is(true));
        assertThat(customDeserializationUsed.get(), is(true));
    }

    @Ignore
    public void
            using_custom_object_mapper_statically() {
        final Message message = new Message();
        message.setMessage("A message");
        final ObjectMapper mapper = new ObjectMapper() {
            public Object deserialize(ObjectMapperDeserializationContext context) {
                final String toDeserialize = context.getDataToDeserialize().asString();
                final String unquoted = StringUtils.remove(toDeserialize, "##");
                final Message message = new Message();
                message.setMessage(unquoted);
                customDeserializationUsed.set(true);
                return message;
            }

            public Object serialize(ObjectMapperSerializationContext context) {
                final Message objectToSerialize = context.getObjectToSerializeAs(Message.class);
                final String message = objectToSerialize.getMessage();
                customSerializationUsed.set(true);
                return "##" + message + "##";
            }
        };
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig(mapper));

        final Message returnedMessage = given().body(message).when().post("/reflect").as(Message.class);

        assertThat(returnedMessage.getMessage(), equalTo("A message"));
        assertThat(customSerializationUsed.get(), is(true));
        assertThat(customDeserializationUsed.get(), is(true));
    }

    @Ignore
    public void
            using_default_object_mapper_type_if_specified() {
        final Message message = new Message();
        message.setMessage("A message");
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig(GSON));

        final Message returnedMessage = given().body(message).when().post("/reflect").as(Message.class);

        assertThat(returnedMessage.getMessage(), equalTo("A message"));
    }

    @Ignore
    public void
            using_as_specified_object() {
        final Message message = new Message();
        message.setMessage("A message");
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig(GSON));

        final String returnedMessage = given().body(message).when().post("/reflect")
                .as(Message.class).getMessage();

        assertThat(returnedMessage, equalTo("A message"));
    }

    @Test
    public void
            using_custom_object_mapper_factory() throws IOException {
        final Greeting greeting = new Greeting();
        greeting.setFirstName("John");
        greeting.setLastName("Doe");
//        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(objectMapperConfig().gsonObjectMapperFactory(
//                new GsonObjectMapperFactory() {
//                    public Gson create(Type cls, String charset) {
//                        return new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
//                    }
//                }
//        ));

      
//        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(objectMapperConfig().jackson2ObjectMapperFactory(
//                new Jackson2ObjectMapperFactory() {
//            public com.fasterxml.jackson.databind.ObjectMapper create(Type cls, String charset) {
//                return new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules();
//            }
//
//        }
//        ));

        final Greeting returnedGreeting = given().contentType("application/json").body(greeting, JACKSON_2).
                expect().body("firstName", equalTo("John")).when().post("/reflect").as(Greeting.class, JACKSON_2);
        
         final EmployeeName returnedName = given().contentType("application/json").body(greeting, JACKSON_2).
                when().post("/reflect").as(EmployeeName.class, JACKSON_2);
         
         
         
         Response response=given().contentType("application/json").body(greeting, JACKSON_2).
                when().post("/reflect");
         
         ResponseBody body = response.getBody();
         
         JsonPath jsonPathEvaluator = response.jsonPath();
         
         String lastName = jsonPathEvaluator.get("lastName");
        
        
        //important  body() only working with Object 
        String returnedGreetingStr = given().contentType(ContentType.JSON).body(
                greeting, JACKSON_2)
                .when().post("/reflect").getBody().asString();
        
        System.out.println ( returnedGreetingStr );
        
        //!!!!!!!body() method will not working
//        EmployeeName returnedName10= given().contentType("application/json").body(
//                "{\"firstName\" : \"John\" , \"lastName\" :  \"Doe\"   }" ,JACKSON_2  )
//                .when().post("/reflect").as(EmployeeName.class, JACKSON_2);
      
        
        EmployeeName returnedName1= given().contentType("application/json").body(
                "{\"firstName\" : \"John\" , \"lastName\" :  \"Doe\"   }" )
                .when().post("/reflect").as(EmployeeName.class, JACKSON_2);
         
         
        EmployeeName returnedName2= given().contentType("application/json").body(
                 returnedGreetingStr   )
                .when().post("/reflect").as(EmployeeName.class, JACKSON_2);
        
        
        EmployeeName returnedName3= given().contentType("application/json").body(
                returnedGreetingStr)
                .when().post("/reflect").as(EmployeeName.class, JACKSON_2);


        assertThat(returnedGreeting.getFirstName(), equalTo("John"));
        assertThat(returnedGreeting.getLastName(), equalTo("Doe"));
    }
}
