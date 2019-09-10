#!/bin/bash

if [[ $1 == "watch" ]]; then
  docker-compose up --build frontend && docker-compose run --rm frontend ./build-local.sh watch:webjar
else
  docker-compose up --build frontend && docker-compose run --rm frontend ./build-local.sh build:webjar
fi