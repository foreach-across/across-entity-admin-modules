#!/bin/bash

if [[ $1 == scss ]]; then
    echo "Running gulp scss ${@:2}"
    docker-compose run --rm frontend sh -c "gulp scss ${@:2}"
elif [[ $1 == js ]]; then
    echo "Running gulp js ${@:2}"
    docker-compose run --rm frontend sh -c "gulp js ${@:2}"
else
    docker-compose up
fi