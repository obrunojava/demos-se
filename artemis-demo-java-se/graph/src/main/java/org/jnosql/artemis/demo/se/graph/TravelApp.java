/*
 * Copyright (c) 2017 Otávio Santana and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 * You may elect to redistribute this code under either of these licenses.
 *
 * Contributors:
 *
 * Otavio Santana
 */
package org.jnosql.artemis.demo.se.graph;

import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jnosql.artemis.graph.GraphTemplate;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.jnosql.artemis.demo.se.graph.Person.builder;

public class TravelApp {


    private static final String GOAL = "type";
    private static final String FUN = "fun";
    private static final String TRAVELS = "travels";
    private static final String WORK = "Work";

    public static void main(String[] args) {

        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            GraphTemplate graph = container.select(GraphTemplate.class).get();

            Person stark = graph.insert(builder().withName("Stark").build());
            Person roges = graph.insert(builder().withName("Rogers").build());
            Person romanoff = graph.insert(builder().withName("Romanoff").build());
            Person banners = graph.insert(builder().withName("Banners").build());

            City sanFrancisco = graph.insert(City.of("San Francisco"));
            City moscow = graph.insert(City.of("Moscow"));
            City newYork = graph.insert(City.of("New York"));
            City saoPaulo = graph.insert(City.of("São Paulo"));
            City casaBlanca = graph.insert(City.of("Casa Blanca"));

            graph.edge(stark, TRAVELS, sanFrancisco).add(GOAL, FUN);
            graph.edge(stark, TRAVELS, moscow).add(GOAL, FUN);
            graph.edge(stark, TRAVELS, newYork).add(GOAL, FUN);
            graph.edge(stark, TRAVELS, saoPaulo).add(GOAL, FUN);
            graph.edge(stark, TRAVELS, casaBlanca).add(GOAL, FUN);

            graph.edge(roges, TRAVELS, newYork).add(GOAL, WORK);

            graph.edge(banners, TRAVELS, casaBlanca).add(GOAL, WORK);
            graph.edge(banners, TRAVELS, saoPaulo).add(GOAL, WORK);

            graph.edge(romanoff, TRAVELS, moscow).add(GOAL, WORK);
            graph.edge(romanoff, TRAVELS, newYork).add(GOAL, WORK);
            graph.edge(romanoff, TRAVELS, saoPaulo).add(GOAL, WORK);
            graph.edge(romanoff, TRAVELS, casaBlanca).add(GOAL, FUN);


            Map<String, Long> mostFunCity = graph.getTraversalVertex()
                    .inE(TRAVELS)
                    .has(GOAL, FUN).inV()
                    .<City>stream()
                    .map(City::getName)
                    .collect((groupingBy(Function.identity(), counting())));

            Map<String, Long> mostBusiness = graph.getTraversalVertex()
                    .inE(TRAVELS)
                    .has(GOAL, WORK).inV()
                    .<City>stream()
                    .map(City::getName)
                    .collect((groupingBy(Function.identity(), counting())));

            Map<String, Long> mostTravelCity = graph.getTraversalVertex()
                    .out(TRAVELS)
                    .<City>stream()
                    .map(City::getName)
                    .collect((groupingBy(Function.identity(), counting())));

            System.out.println("The city most fun: "+ mostFunCity);
            System.out.println("The city most business: "+ mostBusiness);
            System.out.println("The city with more travel: "+ mostTravelCity);




        }
    }
}
