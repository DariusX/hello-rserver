# Testing Java client for RServer
This guide walks you through using Maven to build a simple Java project.

## K8s port forwarding
Using local port 6312, mapped to the Pod's port of 6311
`kubectl port-forward -n dev-dqf deployment/r-server 6312:6311`

## Reference
https://www.rforge.net/Rserve/example.html
