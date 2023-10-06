#!/bin/env bash

# Train the initial model


# Set the value of n
n=8

# Loop n times and run the command
for i in $(seq 1 $n); do
  curl http://127.0.0.1:5000/fitLightGBMDogs
done
