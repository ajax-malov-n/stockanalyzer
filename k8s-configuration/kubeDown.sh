#!/bin/bash

kubectl delete -f ingress.yaml
kubectl delete -f mongo.yaml
kubectl delete -f stockanalyzer.yaml
kubectl delete -f mongo-configmap.yaml
kubectl delete -f mongo-secret.yaml
kubectl delete -f stockanalyzer-secret.yaml
kubectl delete -f stockanalyzer-configmap.yaml

minikube addons disable ingress

minikube stop
