#!/bin/bash

set -e

echo "🧹 Cleaning up E-commerce Microservices from Kubernetes"

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed. Please install kubectl first."
    exit 1
fi

# Confirm deletion
echo "⚠️  This will delete all E-commerce microservices and infrastructure from Kubernetes."
read -p "Are you sure you want to continue? (y/N): " -r
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Cleanup cancelled."
    exit 1
fi

echo "🗑️  Deleting ingress..."
kubectl delete -f base/ingress.yaml --ignore-not-found=true

echo "🗑️  Deleting microservices..."
kubectl delete -f base/services/ --ignore-not-found=true

echo "🗑️  Deleting Datadog agent..."
kubectl delete -f base/datadog.yaml --ignore-not-found=true

echo "🗑️  Deleting infrastructure services..."
kubectl delete -f infrastructure/ --ignore-not-found=true

echo "🗑️  Deleting secrets and configmaps..."
kubectl delete -f base/secrets.yaml --ignore-not-found=true

echo "🗑️  Deleting namespaces..."
kubectl delete -f base/namespace.yaml --ignore-not-found=true

echo "✅ Cleanup completed successfully!"
echo ""
echo "📝 Note: Persistent volumes may still exist. To delete them manually:"
echo "  kubectl get pv | grep ecommerce"
echo "  kubectl delete pv <pv-name>"