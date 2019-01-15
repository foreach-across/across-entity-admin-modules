#!/bin/bash

if [[ $1 == "" ]]; then
  docker-compose build && docker-compose run --rm frontend sh -c "yarn run build:watch"
fi

docker-compose build && docker-compose run --rm frontend sh -c "yarn run $1"