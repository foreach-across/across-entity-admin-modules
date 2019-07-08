#!/bin/bash

if [[ $1 == "lockfile" ]]; then
  docker-compose up update-lockfile
elif [[ $1 == "" ]]; then
  docker-compose build frontend && docker-compose run --rm frontend sh -c "yarn --modules-folder /node_modules run build:watch"
else
  docker-compose build frontend && docker-compose run --rm frontend sh -c "yarn --modules-folder /node_modules run $1"
fi
