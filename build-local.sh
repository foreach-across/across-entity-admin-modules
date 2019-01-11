#!/bin/bash

if [[ $1 == scss* ]]; then
    echo "Running scss $1"
    docker-compose run --rm frontend sh -c "yarn run $1"
elif [[ $1 == js* ]]; then
    echo "Running $1"
    docker-compose run --rm frontend sh -c "yarn run $1"
else
    docker-compose up
fi