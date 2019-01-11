#!/bin/bash

if ! docker-compose run --rm frontend sh -c "yarn run scss:prod && chmod 777 -R /build"; then
    exit 1
fi
if ! docker-compose run --rm frontend sh -c "yarn run js:prod && chmod 777 -R /build"; then
    exit 1
fi