#!/bin/bash

if [[ $1 =~ "npm" ]]; then
  OUTPUT_PATH=""
elif [[ $1 =~ "webjar" ]]; then
  OUTPUT_PATH="../resources/META-INF/resources/webjars/bootstrap-ui/0.0.1"
elif [[ $1 =~ "static" ]]; then
  OUTPUT_PATH=""
fi

echo Building resources for $1, writing files to $OUTPUT_PATH

cd ./src/main/frontend

if [[ $1 != *":"* ]]; then
  yarn run build -- --output=$OUTPUT_PATH
else
  yarn run ${1%:*} -- --output=$OUTPUT_PATH
fi

cd ../../../
