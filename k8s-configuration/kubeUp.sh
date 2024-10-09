#!/bin/bash

# Start Minikube
minikube start

# Set up Docker environment to use Minikube's Docker daemon
eval $(minikube docker-env)

# Build the Docker image
docker build -f ../DockerfileDomainService -t stockanalyzer-domain ../
docker build -f ../DockerfileGw -t stockanalyzer-gateway ../

# Enable Ingress addon in Minikube
minikube addons enable ingress

# Wait for the Ingress controller to be ready
echo "Waiting for the Ingress controller to be ready..."
kubectl rollout status deployment/ingress-nginx-controller -n ingress-nginx

# Apply Kubernetes configurations
kubectl apply -f mongo-configmap.yaml
kubectl apply -f mongo-secret.yaml
kubectl apply -f domain-secret.yaml
kubectl apply -f pv-mongo.yaml
kubectl apply -f pvc-mongo.yaml

# Wait for the services to be ready before applying the Ingress
echo "Waiting for MongoDB and StockAnalyzer and Nats services to be ready..."
kubectl apply -f nats.yaml
kubectl wait --for=condition=ready pod -l app=natsServer --timeout=300s
kubectl apply -f mongo.yaml
kubectl wait --for=condition=ready pod -l app=mongodb --timeout=300s
kubectl apply -f domain.yaml
kubectl wait --for=condition=ready pod -l app=domain --timeout=300s
kubectl apply -f gateway.yaml
kubectl wait --for=condition=ready pod -l app=gateway --timeout=300s

# Apply Ingress configuration
kubectl apply -f ingress.yaml

# Optional: Start Minikube tunnel to expose services
sudo minikube tunnel
