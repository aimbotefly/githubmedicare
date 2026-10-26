package com.medicare.api.performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class GetPatientByEmailSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    Iterator<Map<String, Object>> emailFeeder = Stream
            .generate(() -> Map.<String, Object>of("email", "perf-" + UUID.randomUUID() + "@mail.com"))
            .iterator();

    ScenarioBuilder scn = scenario("Recuperation d'un patient par email")
            .feed(emailFeeder)
            .exec(http("Preparation : POST /patients")
                    .post("/patients")
                    .body(StringBody("{\"nom\":\"Perf\",\"prenom\":\"Test\",\"email\":\"#{email}\",\"telephone\":\"0600000000\"}"))
                    .check(status().is(201)))
            .exec(http("GET /patients/{email}")
                    .get("/patients/#{email}")
                    .check(status().is(200)));

    {
        setUp(scn.injectOpen(rampUsers(100).during(10)))
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().mean().lt(500),
                        global().successfulRequests().percent().gt(99.0));
    }
}
