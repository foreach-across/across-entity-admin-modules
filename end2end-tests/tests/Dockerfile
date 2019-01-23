FROM cypress/browsers:chrome63-ff57
RUN apt-get -y update && apt-get install -y bash
COPY end2end-tests/tests/wait-for-it.sh ./wait-for-it.sh
COPY end2end-tests/tests/run-cypress.sh ./run-cypress.sh
RUN chmod +x ./wait-for-it.sh
RUN npm i cypress
#ENTRYPOINT [ "bash", "-c", "./wait-for-it.sh -t 360 web:8080 -- run; ./node_modules/.bin/cypress run --record --key 146240c5-1ff5-468a-80e8-a4057c04555d --project ./cypress --reporter junit; chmod -R 777 ./cypress" ]
