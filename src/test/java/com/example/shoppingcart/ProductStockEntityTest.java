package com.example.shoppingcart;

import kalix.javasdk.testkit.ValueEntityResult;
import kalix.javasdk.testkit.ValueEntityTestKit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class ProductStockEntityTest {
    @Test
    public void testCreate()throws Exception{
        var productId = UUID.randomUUID().toString();
        ProductStock productStock = new ProductStock(10);

        ValueEntityTestKit<ProductStock, ProductStockEntity> testKit = ValueEntityTestKit.of(productId, ProductStockEntity::new);

        ValueEntityResult<String> res = testKit.call(entity -> entity.create(productStock));
        assertFalse(res.isError());
        assertEquals("OK", res.getReply());
        ProductStock persistedProductStock = testKit.getState();
        assertEquals(productStock.quantity(), persistedProductStock.quantity());
    }
}
