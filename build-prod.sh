#!/bin/bash

docker-compose run --rm --user "bamboo" frontend sh -c "yarn run scss:prod && yarn run js:prod"