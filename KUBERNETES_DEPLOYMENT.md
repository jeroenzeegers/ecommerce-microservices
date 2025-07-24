# Kubernetes Deployment Guide

This guide covers deploying the e-commerce microservices platform to Kubernetes with full Datadog APM monitoring.

## Prerequisites

- Kubernetes cluster (v1.20+)
- kubectl configured
- Docker images built and pushed to registry
- Datadog account and API key
- NGINX Ingress Controller installed

## Architecture Overview

### Namespaces
- `ecommerce`: Main application services
- `ecommerce-infrastructure`: Database and messaging services

### Services Deployed
- **Infrastructure**: MySQL, MongoDB, Kafka + Zookeeper
- **Discovery**: Eureka Service Registry  
- **Gateway**: API Gateway (main entry point)
- **Microservices**: 13 business services
- **Monitoring**: Datadog Agent (DaemonSet)

## Quick Start

### 1. Prepare Environment

```bash
# Clone repository
git clone <your-repo-url>
cd ecommerce-microservices/k8s

# Update Datadog API key
# Edit base/secrets.yaml and replace 'your-datadog-api-key' with your actual key
vim base/secrets.yaml
```

### 2. Deploy Everything

```bash
# Deploy all services
./deploy.sh
```

### 3. Access Application

```bash
# Get ingress IP
kubectl get ingress ecommerce-ingress -n ecommerce

# Add to /etc/hosts
echo "<INGRESS_IP> ecommerce.local" | sudo tee -a /etc/hosts

# Access application
curl http://ecommerce.local/api/products
```

## Manual Deployment Steps

### 1. Create Namespaces and Secrets

```bash
kubectl apply -f base/namespace.yaml
kubectl apply -f base/secrets.yaml
```

### 2. Deploy Infrastructure

```bash
# MySQL
kubectl apply -f infrastructure/mysql.yaml

# MongoDB  
kubectl apply -f infrastructure/mongodb.yaml

# Kafka & Zookeeper
kubectl apply -f infrastructure/kafka.yaml
```

### 3. Deploy Monitoring

```bash
kubectl apply -f base/datadog.yaml
```

### 4. Deploy Services

```bash
# Discovery service first (others depend on it)
kubectl apply -f base/services/discovery-service.yaml

# Wait for discovery service to be ready
kubectl wait --for=condition=available deployment/discovery-service -n ecommerce

# Deploy all other services
kubectl apply -f base/services/
```

### 5. Deploy Ingress

```bash
kubectl apply -f base/ingress.yaml
```

## Service Ports and URLs

| Service | Internal Port | External Path |
|---------|---------------|---------------|
| API Gateway | 8080 | `/api/*` |
| Discovery Service | 8761 | `/eureka/*` |
| User Service | 8081 | `/users/*` |
| Product Service | 8086 | `/products/*` |
| Order Service | 8082 | `/orders/*` |
| Payment Service | 8083 | `/payments/*` |
| Inventory Service | 8084 | `/inventory/*` |
| Notification Service | 8085 | `/notifications/*` |
| Shipping Service | 8087 | `/shipping/*` |
| Favourite Service | 8088 | `/favourites/*` |
| Promotion Service | 8089 | `/promotions/*` |
| Rating Service | 8090 | `/ratings/*` |
| Tax Service | 8091 | `/tax/*` |
| Search Service | 8092 | `/search/*` |
| Media Service | 8093 | `/media/*` |

## Configuration

### Environment Variables

Services are configured via ConfigMap `ecommerce-config`:

```yaml
DD_SITE: "datadoghq.com"
DD_ENV: "development"  
DD_VERSION: "1.0.0"
DD_AGENT_HOST: "datadog-agent.ecommerce.svc.cluster.local"
EUREKA_URI: "http://discovery-service.ecommerce.svc.cluster.local:8761/eureka"
MYSQL_HOST: "mysql.ecommerce-infrastructure.svc.cluster.local"
MONGODB_HOST: "mongodb.ecommerce-infrastructure.svc.cluster.local"
KAFKA_BOOTSTRAP_SERVERS: "kafka.ecommerce-infrastructure.svc.cluster.local:9092"
```

### Secrets

Sensitive data stored in secrets:
- `mysql-secret`: Database credentials
- `datadog-secret`: Datadog API key

## Scaling

```bash
# Scale individual services
kubectl scale deployment/product-service --replicas=5 -n ecommerce

# Scale multiple services
kubectl scale deployment/product-service deployment/order-service --replicas=3 -n ecommerce
```

## Monitoring & Observability

### Datadog APM
- Traces: https://app.datadoghq.com/apm/traces
- Service Map: https://app.datadoghq.com/apm/map
- Metrics: https://app.datadoghq.com/metric/explorer

### Kubernetes Native

```bash
# View all pods
kubectl get pods -n ecommerce

# Check service health
kubectl get deployment -n ecommerce

# View logs
kubectl logs -f deployment/product-service -n ecommerce

# Describe problematic pods
kubectl describe pod <pod-name> -n ecommerce
```

## Troubleshooting

### Common Issues

1. **Services not starting**
   ```bash
   kubectl describe pod <pod-name> -n ecommerce
   kubectl logs <pod-name> -n ecommerce
   ```

2. **Database connection issues**
   ```bash
   # Check if MySQL is running
   kubectl get pods -n ecommerce-infrastructure
   
   # Test connectivity
   kubectl exec -it <app-pod> -n ecommerce -- nc -zv mysql.ecommerce-infrastructure.svc.cluster.local 3306
   ```

3. **Eureka registration problems**
   ```bash
   # Check Eureka dashboard
   kubectl port-forward svc/discovery-service 8761:8761 -n ecommerce
   # Visit http://localhost:8761
   ```

4. **Datadog not receiving traces**
   ```bash
   # Check Datadog agent logs
   kubectl logs daemonset/datadog-agent -n ecommerce
   
   # Verify agent connectivity
   kubectl exec -it <datadog-pod> -n ecommerce -- curl localhost:8126/v0.3/traces
   ```

### Resource Issues

```bash
# Check resource usage
kubectl top pods -n ecommerce
kubectl top nodes

# Increase resources if needed
kubectl patch deployment product-service -n ecommerce -p='{"spec":{"template":{"spec":{"containers":[{"name":"product-service","resources":{"limits":{"memory":"2Gi","cpu":"1000m"}}}]}}}}'
```

## Updates & Rollbacks

### Rolling Updates

```bash
# Update image
kubectl set image deployment/product-service product-service=myregistry/product-service:v2 -n ecommerce

# Check rollout status
kubectl rollout status deployment/product-service -n ecommerce
```

### Rollbacks

```bash
# View rollout history
kubectl rollout history deployment/product-service -n ecommerce

# Rollback to previous version
kubectl rollout undo deployment/product-service -n ecommerce

# Rollback to specific revision
kubectl rollout undo deployment/product-service --to-revision=2 -n ecommerce
```

## Cleanup

```bash
# Remove everything
./cleanup.sh

# Or manually
kubectl delete namespace ecommerce ecommerce-infrastructure
```

## Production Considerations

### Security
- Use proper RBAC policies
- Network policies for service isolation
- Secrets management with external systems (Vault, etc.)
- Image vulnerability scanning

### High Availability
- Multi-zone deployment
- Database clustering/replication
- Load balancer configuration
- Backup strategies

### Performance
- Resource limits and requests tuning
- Horizontal Pod Autoscaling (HPA)
- Vertical Pod Autoscaling (VPA)
- Cluster autoscaling

### Monitoring
- Set up alerts in Datadog
- Log aggregation
- Custom metrics
- SLI/SLO monitoring