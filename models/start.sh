#!/bin/env bash

port=5000

# Train the initial model
gunicorn -w 4 -b 0.0.0.0:$port recomenders_app:app