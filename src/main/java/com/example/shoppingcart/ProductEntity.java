package com.example.shoppingcart;

import io.grpc.Status;
import kalix.javasdk.annotations.EntityKey;
import kalix.javasdk.annotations.EntityType;
import kalix.javasdk.valueentity.ValueEntity;
import kalix.javasdk.valueentity.ValueEntityContext;
import org.springframework.web.bind.annotation.*;

@EntityKey("productId")
@EntityType("product")
@RequestMapping("/product/{productId}")
public class ProductEntity extends ValueEntity<Product>{
    private final String productId;

    public ProductEntity(ValueEntityContext context) {
        this.productId = context.entityId();
    }

    @Override
    public Product emptyState() {
        return Product.empty();
    }

    @PostMapping("/create")
    public Effect<String> create(@RequestBody  Product product){
        if(currentState().isEmpty())
            return effects().updateState(product).thenReply("OK");
        else
            return effects().error("Already created");
    }
    @GetMapping("/get")
    public Effect<Product> get(){
        if(currentState().isEmpty())
            return effects().error("Not found", Status.Code.NOT_FOUND);
        else
            return effects().reply(currentState());
    }
    @PutMapping("/update")
    public Effect<String> update(@RequestBody Product product){
        if(currentState().isEmpty())
            return effects().error("Not found", Status.Code.NOT_FOUND);
        else
            return effects().updateState(product).thenReply("OK");
    }
    @DeleteMapping("/delete")
    public Effect<String> delete(){
        if(currentState().isEmpty())
            return effects().error("Not found", Status.Code.NOT_FOUND);
        else
            return effects().deleteEntity().thenReply("OK");
    }
}
