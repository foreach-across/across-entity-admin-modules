#!/bin/bash

docker-compose run --rm frontend sh -c "yarn run scss:prod && chmod 777 -R /build"
docker-compose run --rm frontend sh -c "yarn run js:prod && chmod 777 -R /build"
