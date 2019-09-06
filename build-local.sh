#!/bin/bash

if [[ $1 == "" ]]; then
  docker-compose up --build frontend2 && docker-compose run --rm frontendv2 sh -c "yarn run $1"
elif [[ $1 == build ]]; then
  docker-compose up --build frontend2