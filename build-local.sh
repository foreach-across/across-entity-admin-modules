#!/bin/bash

if [[ $1 == "" ]]; then
  docker-compose run --rm frontend sh -c "yarn run build:watch"
fi

docker-compose run --rm frontend sh -c "yarn run $1"