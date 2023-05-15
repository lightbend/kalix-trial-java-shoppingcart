package com.example.shoppingcart;

import kalix.javasdk.testkit.ValueEntityResult;
import kalix.javasdk.testkit.ValueEntityTestKit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class ProductEntityTest {
    @Test
    public void testCreate()throws Exception{
        var productId = UUID.randomUUID().toString();
        Product product = new Product("apple",10);

        ValueEntityTestKit<Product,ProductEntity> testKit = ValueEntityTestKit.of(productId,ProductEntity::new);

        ValueEntityResult<String> res = testKit.call(entity -> entity.create(product));
        assertFalse(res.isError());
        assertEquals("OK",res.getReply());
        Product persistedProduct = (Product)res.getUpdatedState();
        assertEquals(product.name(),persistedProduct.name());
        assertEquals(product.quantity(),persistedProduct.quantity());
    }
}
