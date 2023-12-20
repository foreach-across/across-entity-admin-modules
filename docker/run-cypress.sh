#!/usr/bin/env bash

./wait-for-it.sh -t 360 web:8080 -- run

function run_cypress_file(){
     NEW_UUID=$(LC_CTYPE=C tr -d -c '[:alnum:]' </dev/urandom | head -c 15)
    ./node_modules/.bin/cypress run --record --key 920eb36a-3b08-48d5-b4c8-561353d13e3c --project ./cypress --reporter junit --spec $1 --reporter-options "mochaFile=cypress/${NEW_UUID}.xml,toConsole=true";
}

function run(){
    du -a cypress/cypress/integration | awk '{print $2}' | grep '\.js$' | while read -r line ;
        do run_cypress_file ${line};
    done
}

run
