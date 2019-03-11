FROM cypress/browsers:chrome63-ff57
RUN apt-get -y update && apt-get install -y bash
COPY bootstrap-ui-module/src/main/frontend/tests/e2e/tests/wait-for-it.sh ./wait-for-it.sh
COPY bootstrap-ui-module/src/main/frontend/tests/e2e/tests/run-cypress.sh ./run-cypress.sh
ENV CI=1
RUN chmod +x ./wait-for-it.sh
RUN npm i cypress
