#!/bin/bash

docker-compose run --rm --user "bamboo" frontend sh -c "yarn run scss:prod && run js:prod"