# Screening Project: Spring Boot Shopping Cart Implementation

This project is designed to evaluate Java 21 and Spring Boot 3.5.x proficiency through practical implementation of a domain-driven shopping cart system.

## Overview

You have a pre-configured Spring Boot application with:
- ✅ **Product Entity** with SKU and PriceInfo (One-to-Many) relationship
- ✅ **PriceInfo** - A value object representing product pricing in different currencies
- ✅ **TaxInfo** - Represents Canadian provincial and federal taxes
- ✅ **Cart Entity** - Shopping cart aggregate root (partially implemented)
- ✅ **Database Schema** - Complete Liquibase migration scripts
- ✅ **Sample Data** - Canadian Tire products and Canadian tax information
- ✅ **DTOs** - CartSaveRequest and CartSaveResponse prepared
- ✅ **Test Suite** - SpringBootTest class with test cases ready for implementation

## Your Tasks

### 1. Implement CartService.addProductToCart()

**Location:** `src/main/java/com/cantire/storetech/evaluation/service/CartService.java`

**Requirements:**
1. **Cart Creation/Retrieval**: 
   - If `cartId` is null, create a new `Cart` with the provided region and currencyCode
   - If `cartId` exists, retrieve the persisted Cart from database

2. **Product Validation**:
   - Validate that the product exists in the database
   - If product doesn't exist, return error response with appropriate message

3. **Add Product to Cart**:
   - Add the product to the cart with the specified quantity
   - Handle duplicate products (may need to update quantity if product already exists)

4. **Calculate Subtotal**:
   - Use the `findCurrentPrice()` helper method to get the current price for the specified currency
   - Calculate subtotal based on all products in the cart
   - Use the `calculateSubtotal()` helper method

5. **Retrieve Applicable Taxes**:
   - Use `getTaxesForRegion()` to get all applicable taxes for the cart's region
   - Assign these to the cart's `applicableTaxes` list

6. **Persist & Response**:
   - Save the cart to database
   - Use `toResponse()` helper method to build the CartSaveResponse
   - Return successful response

### 2. Consider Edge Cases

- Invalid/non-existent product IDs
- Unavailable currency codes (no price info for that currency)
- Invalid region codes
- Null or zero quantities

## Project Structure

```
src/
├── main/
│   ├── java/com/cantire/storetech/evaluation/
│   │   ├── controller/
│   │   │   ├── ProductController.java        (GET /api/products)
│   │   │   └── CartController.java           (POST /api/carts)
│   │   ├── service/
│   │   │   ├── ProductService.java
│   │   │   ├── ProductServiceImpl.java
│   │   │   └── CartService.java              (⭐ IMPLEMENT THIS)
│   │   ├── model/
│   │   │   ├── Product.java                  (Aggregate Root)
│   │   │   ├── PriceInfo.java                (Value Object)
│   │   │   ├── TaxInfo.java
│   │   │   ├── Cart.java                     (Aggregate Root)
│   │   │   └── ProductCategory.java
│   │   ├── repo/
│   │   │   ├── ProductRepository.java
│   │   │   ├── CartRepository.java
│   │   │   ├── TaxInfoRepository.java
│   │   │   └── PriceInfoRepository.java
│   │   └── dto/
│   │       ├── CartSaveRequest.java
│   │       ├── CartSaveResponse.java
│   │       └── ProductResponse.java
│   └── resources/
│       └── db.changelog/
│           ├── changelog-1.0-initial-schema.xml
│           └── changelog-2.0-sample-data.xml
└── test/
    └── java/com/cantire/storetech/evaluation/
        └── service/
            └── CartServiceTest.java          (⭐ TEST YOUR IMPLEMENTATION)
```

## Database Schema

### Tables

- **product** - Product catalog
- **product_category** - Product categories
- **price_info** - Product pricing in different currencies (One-to-Many with Product)
- **tax_info** - Canadian tax rates by province/state
- **cart** - Shopping cart aggregate
- **cart_product** - Many-to-many relationship table
- **cart_product_quantities** - Element collection for cart quantities
- **cart_applicable_taxes** - Taxes applicable to a cart

### Sample Data Included

**Products (5 Canadian Tire items):**
- 16oz Claw Hammer (CAD $24.99)
- Cordless Drill/Driver Kit (CAD $129.99)
- Premium Synthetic Motor Oil 5L (CAD $34.99)
- Deluxe Tent 4-Person (CAD $199.99)
- LED Work Light (CAD $49.99)

**Taxes (Canadian):**
All effective from Jan 1, 2026 to Dec 31, 2099
- **Ontario**: 13% HST
- **British Columbia**: 5% GST + 7% PST
- **Alberta**: 5% GST (no PST or HST)
- **Manitoba**: 5% GST + 8% PST
- **Quebec**: 5% GST + 9.975% QST
- And more for other provinces...

## API Endpoints

### Get Products with Pricing
```
GET /api/products
Response: List<ProductResponse> with current prices
```

### Add Product to Cart
```
POST /api/carts
Request:
{
  "cartId": null,           // null for new cart, or existing cart ID
  "productId": 1,
  "quantity": 2,
  "region": "ON",           // Province abbreviation
  "currencyCode": "CAD"
}

Response:
{
  "cartId": 1,
  "totalItems": 2,
  "subtotal": 59.98,
  "currencyCode": "CAD",
  "region": "ON",
  "items": [...],
  "taxBreakdown": [...],
  "success": true,
  "message": "..."
}
```

## Testing

Run the test suite to verify your implementation:

```bash
mvn test -Dtest=CartServiceTest
```

**Test Cases:**
1. ✅ Add valid product to new cart (no cart ID)
2. ✅ Add valid product to existing cart
3. ✅ Try to add invalid/non-existent product (should fail)
4. ✅ Try to add product with unavailable currency (should fail)

## Domain-Driven Design Notes

- **PriceInfo** is a value object, accessible only through its aggregate root (Product)
- **Cart** is an aggregate root managing its own products and quantities
- **TaxInfo** is immutable data representing tax rules
- Entities should validate their own invariants
- Consider using domain events for important state changes

## Key Classes to Review

Before implementing, review:
1. `Product.java` - See the PriceInfo relationship
2. `CartService.java` - See helper methods and their contracts
3. `CartSaveRequest.java` & `CartSaveResponse.java` - Understand the DTOs
4. `CartServiceTest.java` - Understand test expectations

## Database Connection

The project uses an H2 in-memory database. Configuration in `application.yml`:
- Database is created and seeded on startup
- Liquibase migrations run automatically
- Sample data is loaded from `changelog-2.0-sample-data.xml`

## Hints

1. The `CartRepository` has a `save()` method to persist carts
2. The `ProductRepository` has a `findById()` method to retrieve products
3. Helper methods in `CartService` handle price lookups and calculation
4. Use `@Transactional` for data consistency
5. The test profile (`@ActiveProfiles("test")`) allows test-specific configuration

## Success Criteria

✅ All 4 test cases pass  
✅ Code follows Spring Boot best practices  
✅ Proper exception handling and error responses  
✅ Code is readable and maintainable  
✅ Domain model is respected (DDD principles)  

Good luck! 🚀
