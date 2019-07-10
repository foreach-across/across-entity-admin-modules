#!/bin/bash

docker-compose run --rm frontend sh -c "yarn --modules-folder /node_modules run test --ci  --reporters=default --reporters=jest-junit && yarn --modules-folder /node_modules run build:prod"
