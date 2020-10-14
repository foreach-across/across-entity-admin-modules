FROM cypress/browsers:chrome65-ff57
COPY bootstrap-ui-module/src/main/frontend/tests/e2e/tests/wait-for-it.sh ./wait-for-it.sh
COPY bootstrap-ui-module/src/main/frontend/tests/e2e/tests/run-cypress.sh ./run-cypress.sh
ENV CI=1
RUN chmod +x ./wait-for-it.sh
RUN npm i cypress@4.8.0
