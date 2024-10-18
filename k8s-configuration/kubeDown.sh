#!/bin/bash

kubectl delete -f ingress.yaml
kubectl delete -f domain.yaml
kubectl delete -f gateway.yaml
kubectl delete -f mongo.yaml
kubectl delete -f nats.yaml
kubectl delete -f mongo-configmap.yaml
kubectl delete -f mongo-secret.yaml
kubectl delete -f domain-secret.yaml
kubectl delete -f kafka.yaml

minikube addons disable ingress
