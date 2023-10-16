package com.example.shoppingcart;

import io.grpc.Status;
import kalix.javasdk.annotations.EntityKey;
import kalix.javasdk.annotations.EntityType;
import kalix.javasdk.annotations.Id;
import kalix.javasdk.annotations.TypeId;
import kalix.javasdk.valueentity.ValueEntity;
import kalix.javasdk.valueentity.ValueEntityContext;
import org.springframework.web.bind.annotation.*;

@Id("productId")
@TypeId("product-stock")
@RequestMapping("/product-stock/{productId}")
public class ProductStockEntity extends ValueEntity<ProductStock>{
    private final String productId;

    public ProductStockEntity(ValueEntityContext context) {
        this.productId = context.entityId();
    }

    @Override
    public ProductStock emptyState() {
        return ProductStock.empty();
    }

    @PostMapping("/create")
    public Effect<String> create(@RequestBody ProductStock productStock){
        if(currentState().isEmpty())
            return effects().updateState(productStock).thenReply("OK");
        else
            return effects().error("Already created");
    }
    @GetMapping("/get")
    public Effect<ProductStock> get(){
        if(currentState().isEmpty())
            return effects().error("Not found", Status.Code.NOT_FOUND);
        else
            return effects().reply(currentState());
    }
    @PutMapping("/update")
    public Effect<String> update(@RequestBody ProductStock productStock){
        if(currentState().isEmpty())
            return effects().error("Not found", Status.Code.NOT_FOUND);
        else
            return effects().updateState(productStock).thenReply("OK");
    }
    @DeleteMapping("/delete")
    public Effect<String> delete(){
        if(currentState().isEmpty())
            return effects().error("Not found", Status.Code.NOT_FOUND);
        else
            return effects().updateState(ProductStock.empty()).thenReply("OK");
    }
}
