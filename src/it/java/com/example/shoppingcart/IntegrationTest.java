package com.example.shoppingcart;

import com.example.shoppingcart.Main;
import kalix.spring.testkit.KalixIntegrationTestKitSupport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


/**
 * This is a skeleton for implementing integration tests for a Kalix application built with the Java SDK.
 *
 * This test will initiate a Kalix Proxy using testcontainers and therefore it's required to have Docker installed
 * on your machine. This test will also start your Spring Boot application.
 *
 * Since this is an integration tests, it interacts with the application using a WebClient
 * (already configured and provided automatically through injection).
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class IntegrationTest extends KalixIntegrationTestKitSupport {
  @Autowired
  private WebClient webClient;
  private Duration timeout = Duration.of(5, ChronoUnit.SECONDS);
  @Test
  public void test() throws Exception {
    var productId = UUID.randomUUID().toString();
    Product product = new Product("apple",10);
    var res = webClient.post()
                .uri("/product/%s/create".formatted(productId))
                .bodyValue(product)
                .retrieve()
                .toEntity(String.class)
                .block(timeout);
    assertEquals("OK",res);
    var getProduct = webClient.get()
            .uri("/product/%s/get".formatted(productId))
            .retrieve()
            .toEntity(Product.class)
            .block(timeout)
            .getBody();
    assertEquals(product.name(),getProduct.name());
    assertEquals(product.quantity(),getProduct.quantity());
  }
}