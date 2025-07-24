# javax to jakarta Migration Status

## Overview
This project uses **Spring Boot 3.3.5** which requires **Jakarta EE** instead of the legacy **Java EE** APIs. Several services still use the old `javax.*` imports and need to be migrated to `jakarta.*`.

## Migration Status

### ✅ Completed Services
- `common-lib` - ✅ Uses `jakarta.annotation.PostConstruct`
- `discovery-service` - ✅ Ready
- `api-gateway` - ✅ Ready  
- `user-service` - ✅ Ready
- `product-service` - ✅ Ready
- `order-service` - ✅ Ready
- `payment-service` - ✅ Ready
- `inventory-service` - ✅ Ready
- `notification-service` - ✅ Ready
- `favourite-service` - ✅ Ready
- `media-service` - ✅ Ready

### ❌ Services Needing Migration
- `shipping-service` - ❌ Uses `javax.validation.*`, `javax.persistence.*`
- `promotion-service` - ❌ Uses `javax.persistence.*`
- `rating-service` - ❌ Uses `javax.persistence.*`  
- `tax-service` - ❌ Uses `javax.validation.*`, `javax.persistence.*`
- `search-service` - ❌ Uses `javax.persistence.*`

## Required Changes

### 1. Import Changes
```java
// OLD (javax)
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.ConstraintViolationException;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.EntityListeners;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import javax.transaction.Transactional;

// NEW (jakarta)  
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.ConstraintViolationException;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import jakarta.transaction.Transactional;
```

### 2. Build Status
Currently, the GitHub Actions workflow excludes the services needing migration to allow successful builds of the completed services.

### 3. Migration Steps for Each Service
1. Replace all `javax.validation.*` → `jakarta.validation.*`
2. Replace all `javax.persistence.*` → `jakarta.persistence.*`  
3. Replace all `javax.transaction.*` → `jakarta.transaction.*`
4. Test compilation: `mvn clean compile`
5. Test full build: `mvn clean package -DskipTests`
6. Add service back to GitHub Actions workflow matrix

### 4. Automated Migration Option
For bulk migration, consider using IDE refactoring tools or sed commands:

```bash
# Example sed commands (run in each service directory)
find src -name "*.java" -exec sed -i 's/javax\.validation/jakarta.validation/g' {} \;
find src -name "*.java" -exec sed -i 's/javax\.persistence/jakarta.persistence/g' {} \;
find src -name "*.java" -exec sed -i 's/javax\.transaction/jakarta.transaction/g' {} \;
```

## Current Build Status
- ✅ 10 services building successfully with Datadog APM integration
- ❌ 5 services excluded from build pending javax/jakarta migration
- ✅ All infrastructure (K8s, Docker, CI/CD) ready for all services

## Next Steps
1. Migrate the 5 remaining services from javax to jakarta
2. Re-enable them in the GitHub Actions workflow
3. Complete full end-to-end testing

This migration is required for Spring Boot 3.x compatibility and modern Jakarta EE standards.