# Installing cypress

* navigate to `entity-module-test-application/src/test/e2e`
* install the cypress dependency by using `yarn`
* Optional: install npx globally by using `npm i -g npx`

# Starting Cypress

Navigate to `entity-module-test-application/src/test/e2e/tests`

## Open the Cypress Test Runner

* Start the EntityModuleTestApplication: e2e (start with profile `e2e`)
* `"../node_modules/.bin/cypress" open` (or `npx cypress open`) opens the GUI through which you can run all or specific tests 

## Running tests

Press `Run all specs` in the Cypress GUI or by using npx `npx cypress run`.
By adding the `--headed` option, an electron browser will be used instead of running headlessly.

To run specific files, provide the spec option with a quoted path or comma separated file paths: `npx cypress run --spec './cypress/integration/**/*'`

See the [cypress command line](https://docs.cypress.io/guides/guides/command-line.html#Installation) for more information.


