#!/bin/bash

# Start Minikube
minikube start

# Set up Docker environment to use Minikube's Docker daemon
eval $(minikube docker-env)

# Build the Docker image
docker build -t stockanalyzer-app ../

# Enable Ingress addon in Minikube
minikube addons enable ingress

# Wait for the Ingress controller to be ready
echo "Waiting for the Ingress controller to be ready..."
kubectl rollout status deployment/ingress-nginx-controller -n ingress-nginx

# Apply Kubernetes configurations
kubectl apply -f mongo-configmap.yaml
kubectl apply -f mongo-secret.yaml
kubectl apply -f stockanalyzer-secret.yaml
kubectl apply -f stockanalyzer-configmap.yaml

# Wait for the services to be ready before applying the Ingress
echo "Waiting for MongoDB and StockAnalyzer services to be ready..."
kubectl apply -f mongo.yaml
kubectl wait --for=condition=available --timeout=300s deployment/mongodb-deployment
kubectl apply -f stockanalyzer.yaml
kubectl wait --for=condition=available --timeout=300s deployment/stockanalyzer

# Apply Ingress configuration
kubectl apply -f ingress.yaml

# Optional: Start Minikube tunnel to expose services
sudo minikube tunnel
