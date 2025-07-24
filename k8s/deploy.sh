#!/bin/bash

set -e

echo "🚀 Deploying E-commerce Microservices to Kubernetes"

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed. Please install kubectl first."
    exit 1
fi

# Check if we're connected to a cluster
if ! kubectl cluster-info &> /dev/null; then
    echo "❌ Not connected to a Kubernetes cluster. Please configure kubectl."
    exit 1
fi

echo "✅ Connected to Kubernetes cluster: $(kubectl config current-context)"

# Function to wait for deployment to be ready
wait_for_deployment() {
    local namespace=$1
    local deployment=$2
    local timeout=${3:-300}
    
    echo "⏳ Waiting for deployment $deployment in namespace $namespace to be ready..."
    kubectl wait --for=condition=available --timeout=${timeout}s deployment/$deployment -n $namespace
}

# Function to wait for pods to be running
wait_for_pods() {
    local namespace=$1
    local label=$2
    local timeout=${3:-300}
    
    echo "⏳ Waiting for pods with label $label in namespace $namespace to be running..."
    kubectl wait --for=condition=ready --timeout=${timeout}s pod -l $label -n $namespace
}

# 1. Create namespaces
echo "📦 Creating namespaces..."
kubectl apply -f base/namespace.yaml

# 2. Create secrets and configmaps
echo "🔐 Creating secrets and configmaps..."
echo "⚠️  Make sure to update the Datadog API key in base/secrets.yaml before deploying!"
read -p "Have you updated the Datadog API key? (y/N): " -r
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Please update the DD_API_KEY in k8s/base/secrets.yaml and run this script again."
    exit 1
fi

kubectl apply -f base/secrets.yaml

# 3. Deploy infrastructure services
echo "🏗️  Deploying infrastructure services..."

echo "  📊 Deploying MySQL..."
kubectl apply -f infrastructure/mysql.yaml
wait_for_deployment ecommerce-infrastructure mysql

echo "  🍃 Deploying MongoDB..."
kubectl apply -f infrastructure/mongodb.yaml
wait_for_deployment ecommerce-infrastructure mongodb

echo "  📨 Deploying Kafka..."
kubectl apply -f infrastructure/kafka.yaml
wait_for_deployment ecommerce-infrastructure zookeeper
wait_for_deployment ecommerce-infrastructure kafka

# 4. Deploy Datadog agent
echo "🐕 Deploying Datadog agent..."
kubectl apply -f base/datadog.yaml
wait_for_pods ecommerce app=datadog-agent

# 5. Deploy discovery service first (other services depend on it)
echo "🗺️  Deploying discovery service..."
kubectl apply -f base/services/discovery-service.yaml
wait_for_deployment ecommerce discovery-service

# Wait a bit for Eureka to be fully ready
echo "⏳ Waiting for Eureka to be fully ready..."
sleep 30

# 6. Deploy all other microservices
echo "🚀 Deploying microservices..."

services=("api-gateway" "user-service" "product-service" "order-service" "payment-service" 
          "inventory-service" "notification-service" "shipping-service" "favourite-service" 
          "promotion-service" "rating-service" "tax-service" "search-service" "media-service")

for service in "${services[@]}"; do
    echo "  🔧 Deploying $service..."
    kubectl apply -f base/services/$service.yaml
done

# Wait for all services to be ready
echo "⏳ Waiting for all microservices to be ready..."
for service in "${services[@]}"; do
    wait_for_deployment ecommerce $service
done

# 7. Deploy ingress
echo "🌐 Deploying ingress..."
kubectl apply -f base/ingress.yaml

# 8. Show status
echo "📊 Deployment Status:"
echo ""
echo "Namespaces:"
kubectl get namespaces | grep ecommerce

echo ""
echo "Infrastructure Services:"
kubectl get pods -n ecommerce-infrastructure

echo ""
echo "Microservices:"
kubectl get pods -n ecommerce

echo ""
echo "Services:"
kubectl get svc -n ecommerce

echo ""
echo "Ingress:"
kubectl get ingress -n ecommerce

echo ""
echo "🎉 Deployment completed successfully!"
echo ""
echo "📝 Next Steps:"
echo "1. Add 'ecommerce.local' to your /etc/hosts file pointing to your ingress IP"
echo "2. Get the ingress IP: kubectl get ingress ecommerce-ingress -n ecommerce"
echo "3. Access the application at http://ecommerce.local/api"
echo "4. Monitor with Datadog at https://app.datadoghq.com/apm/traces"
echo ""
echo "🔍 Useful commands:"
echo "  - View logs: kubectl logs -f deployment/<service-name> -n ecommerce"
echo "  - Scale service: kubectl scale deployment/<service-name> --replicas=3 -n ecommerce"
echo "  - Port forward: kubectl port-forward svc/<service-name> 8080:8080 -n ecommerce"