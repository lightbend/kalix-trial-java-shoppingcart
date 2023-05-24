# Kalix Trial - eCommerce - Java
## Designing Kalix Services
### Use case 
![Use case](images/ecommerce-design_kalix_services.png)<br>
eCommerce use case is a simple shopping cart example consisting of product and shopping cart.
Product models items that are being sold and Shopping Cart models list of items customer wants to buy.<br>
#### Product
Data model:
- productId
- name
- quantity

Operations:
- CREATE/READ/UPDATE/DELETE product

#### Shopping cart 
Data model:
- cartId
- list of productId + quantity

Operations:
- Add product
- Check out

### Kalix components
Kalix components are building blocks used to abstract functionalities.<br> 
In this use case we are going to use Kalix component called `Entity`.<br>
![Entity](images/e-commerce-kalix-component-entity.png)<br>
Entity:
- component for modeling of data and data business logic operations
- removes complexities around data:
  - caching
  - concurrency
  - distributed locking
- simplifies data modeling, business logic implementation with out-of-the box scalability, resilience
Kalix provides other components that are not used in this use case and more details can be found in [Kalix documentation](https://docs.kalix.io/):<br>
![Other Kalix components](images/ecommerce-other_components.png)<br>

### Design Product Kalix service
**Product service**
- implements Product functionalities
- Kalix component used: Entity

![Product Kalix Service](images/ecommerce-designing-product-service.png)

**Product Entity**
- models one product instance and business logic operations over that one instance

**Data model**
- productId
- name
- quantity

**API**
- HTTP/RES
- Endpoints:
  - **Create**
    `POST /product/{productId}/create`
    
    Request (JSON):
    - name (string)
    - quantity (int)
    
    Response (String): "OK"
  - **Read**
    `GET /product/{productId}/get`
    Request (JSON): No body
    
    Response (JSON):
    - name (string)
    - quantity (int)
  - **Update**
    `PUT /product/{productId}/update`
  
    Request (JSON):
    - name (string)
    - quantity (int)
  
    Response (String): "OK"
  - **Delete**
    `DELETE /product/{productId}/delete`
  
    Request (JSON): No body
  
    Response (String): "OK"

## Kickstart Kalix development project
### Prerequisite
Java 17 or later<br>
Maven 3.6 or later<br>
Docker 20.10.14 or higher (to run locally)<br>
### Kalix Maven ArchType
Kalix [Maven ArchType](https://maven.apache.org/archetype/index.html) generates a new Maven development project from Kalix template
### Create shopping cart Maven project from Kalix template 
Execute in command line:
```
mvn archetype:generate \
  -DarchetypeGroupId=io.kalix \
  -DarchetypeArtifactId=kalix-spring-boot-archetype \
  -DarchetypeVersion=1.2.0
```
Use this setup:
```
Define value for property 'groupId': com.example
Define value for property 'artifactId': kalix-trial-shoppingcart
Define value for property 'version' 1.0-SNAPSHOT: :
Define value for property 'package' com.example: : com.example.shoppingcart
```
Maven ArchType generates Maven project:

![Maven project structure](images/ecommerce-maven-project-strcuture.png)

- `pom.xml` with all pre-configured Maven plugins and dependencies required development, testing and packaging of Kalix service code
- `Main`, Java Class for bootstrapping Kalix service
- `resources` directory with minimal required configuration
- `it` directory with integration test example

## Define data structure
Create Product `Java record` in `com.example.shoppingcart package`.<br> 
Add helper methods for creating `empty` product structure and to validate if `isEmpty`.
```
public record Product(String name, int quantity) {
   public static Product empty(){
       return new Product(null,0);
   }
   public boolean isEmpty(){
       return name == null && quantity == 0;
   }
}
```
## Define API - Product Entity API
1. Create `ProductEntity` Java class in `com.example.shoppingcart` package that `extends` `kalix.javasdk.valueentity.ValueEntity` with inner type `Product` type
2. Add `productId` class parameter
3. Add constructor for Kalix to inject `ValueEntityContext` from which `entityId` is used set `productId`
4. Annotate `ProductEntity` class with spring web bind annotation `@RequestMapping` and configure path with `productId` as in-path parameter
5. Annotate class with `@EntityKey(“productId”)` to configure entity key to `productId`
6. Annotate class with `@EntityType` to assign reference name to the entity
7. Override `emptyState` method and return `empty` `Product` value

```
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
}
```
8. Annotate `ProductEntity` class with spring web bind annotation `@RequestMapping` and configure path with `productId` as in-path parametar
9. For each endpoints:
   - To `ProductEntity` class add method per endpoint (`create`, `get`, `update`, `delete`)
   - Each method:
     - input: HTTP request data structure (using spring web annotations)
     - return: `ValueEntity.Effect`  with HTTP response data structure as an inner type
     - using spring web annotation mappings for REST method and path mapping (`@PostMapping`,...)
```
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
   public Effect<String> create(@RequestBody  Product product){}
   
   @GetMapping("/get")
   public Effect<Product> get(){}
   
   @PutMapping("/update")
   public Effect<String> update(@RequestBody Product product){}
   
   @DeleteMapping("/delete")
   public Effect<String> delete(){}
}
```
## Implementing business logic
Helper methods from `ValueEntity` class:
- `currentState()` facilitates access to current value of the data for that product instance (e.g. productId: 111)
- `effects()` facilitates actions that Kalix needs to perform
- `updateState()` - to persist data
- `thenReply()` - to send response after persistence is successful
- `error()` - to send error response back
- `deleteEntity` - facilitates data deletion
- Kalix ensures that each method (create, get, update, delete) is executed in sequence for one product instance (e.g. productId: 111) ensuring consistency and resolving concurrent access

### `create` endpoint
Business logic for create is to persist data if not yet persistent. In other cases returns an ERROR. 
```
@PostMapping("/create")
public Effect<String> create(@RequestBody  Product product){
   if(currentState().isEmpty())
       return effects().updateState(product).thenReply("OK");
   else
       return effects().error("Already created");
}
```
### `get` endpoint
Business logic for get is to product data if exists and if not return not found error.
```
@GetMapping("/get")
public Effect<Product> get(){
   if(currentState().isEmpty())
       return effects().error("Not found", Status.Code.NOT_FOUND);
   else
       return effects().reply(currentState());
}
```
### `update` endpoint
Business logic for update is to update product data if product was already created. If product is not found, return NOT FOUND error.
```
@PutMapping("/update")
public Effect<String> update(@RequestBody Product product){
   if(currentState().isEmpty())
       return effects().error("Not found", Status.Code.NOT_FOUND);
   else
       return effects().updateState(product).thenReply("OK");
}
```
### `delete` endpoint
Business logic for delete is delete data if product exists and return NOT FOUND error if not
```
@DeleteMapping("/delete")
public Effect<String> delete(){
   if(currentState().isEmpty())
       return effects().error("Not found", Status.Code.NOT_FOUND);
   else
       return effects().deleteEntity().thenReply("OK");
}
```
## Test
Kalix comes with very rich test kit for unit and integration testing of Kalix code

`Test kit` provides help (custom assertions, mocks,...) with:
- unit testing of individual Kalix components (e.g `Entity`) in isolation
- integration testing in Kalix Platform simulated environment in isolation

- Allows easy test automation with very high test coverage

### Unit test
1. Create a new `test/java` directories in `src` directory
2. Create `com.example.shoppingcart` package in `test/java`
3. Create `ProductEntityTest` class created package
4. Create `testCreate` method with `JUnit Jupiter Test` annotation
5. `ValueEntityTestKit` class is used for unit testing `Value Entity` component. Inner types are `Product` and `ProductEntity`. 
    It is for unit testing one product instance so `productId` needs to be provided
6. `Testkit` call method is used for triggering each entity endpoint and result is `ValueEntityResult` with inner type as a HTTP result  
7. result can be used for test assertion
   - `isError` - assert error
   - `getReply` - assert reply
   - `getUpdatedState` - assert persistent data
```
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
```
Run the unit test:
```
mvn test
```
### Integration test
Kalix test kit for integration testing runs code using test containers to simulate Kalix Platform runtime environment.
Integration test uses spring reactive WebClient to interact with running code.
IntegrationTest class is created during development project kick start and is pre-configured with WebClient
Each endpoint is tested from the client perspective
```
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
```
Run the integration test:
```
mvn -Pit verify
```
### Run locally in prod-like environment
TBD
### Deploy and run on Kalix Platform on Cloud Provider of your choice 
1. Register for Kalix
   - FREE
   - https://console.kalix.io/register
   - download & install CLI through registration process
2. Create a project in cloud provider and region of choice
 ```
kalix projects new ecommerce --region=gcp-us-east1
```
3. Deploy:
```
mvn deploy
```
### Exercises 
TBD










