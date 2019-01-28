# Installing cypress

* navigate to `bootstrap-ui-module:src/main/frontend/e2e`
* install the cypress dependency by using `yarn install`
* Optional: install npx globally by using `npm install -g npx`

# Starting Cypress

Navigate to `bootstrap-ui-module/src/main/frontend/e2e/tests`

## Open the Cypress Test Runner

* Start the DynamicFormsTestApplication
* `"../node_modules/.bin/cypress" open` (or `npx cypress open`) opens the GUI through which you can run all or specific tests 

## Running tests

Press `Run all specs` in the Cypress GUI  or by using npx `npx cypress run`.
By adding the `--headed` option, an electron browser will be used instead of running headlessly.

To run specific files, provide the spec option with a quoted path or comma separated file paths: `npx cypress run --spec './cypress/integration/**/*'`

See the [cypress command line](https://docs.cypress.io/guides/guides/command-line.html#Installation) for more information.


 