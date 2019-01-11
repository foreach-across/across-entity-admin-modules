#!/bin/bash

docker-compose run --rm frontend sh -c "yarn run js:prod && yarn run scss:prod && chmod 777 -R /build"
