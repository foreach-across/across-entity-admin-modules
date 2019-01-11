#!/bin/bash

docker-compose run --rm frontend sh -c "yarn run $1"