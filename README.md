# BootstrapUiModule / AdminWebModule / EntityModule / PropertiesModule / ApplicationInfoModule / Bootstrap 4

Please refer to the [module page][] for all information regarding documentation, issue tracking and support.

## Building from source

The source can be built using [Maven][] with JDK 8.

### Configuring the frontend setup

The javascript is compiled using [webpack](https://github.com/webpack/webpack) and [node-sass](https://github.com/sass/node-sass).
For ease of use, this configuration is split up over various file, so that minimal knowledge is required for basic configuration.

Setting | Description | File 
--- | --- | ---
Entry files | Configured by providing the  | `package.json` 
Output paths | The output path for scss and js can be configured by specifying the `scssOutputPath` and respectively `jsOutputPath` as config variables | `package.json` 
Files to keep | Configured by listing these files in the `keepFiles` property | `settings.js` 
Libraries that are loaded externally | Configured by specifying a dependency to global variable mapping for the dependency in the `externals` object.  | `settings.js` 

See the `entity-module/src/main/frontend` folder.
`webpack.config.js` contains the main configuration. 

### Building

First of all, the docker image needs to be created for building the frontend resources.
Execute `docker-compose build` to build the required image(s)

Builds can be compiled locally by running `build-local.sh`, this will also watch the files by default.
An additional `build-prod.sh` is provided for build agents.

Description | command
--- | ---
Building only | `build-local.sh build`
Building and watching | `build-local.sh build:watch` or `build-local.sh`
Building in production mode | `build-local.sh build:prod`

## Contributing
Contributions in the form of pull requests are greatly appreciated.  Please refer to the [contributor guidelines][] for more details.

# Bootstrap 4

To start customizing the bootstrap styling, please start the docker-compose script and then the demo application.

For watching the filesystem for changes, use `docker-compose run --rm frontend yarn run build:watch`.
This will build the static resources (adminweb-bootstrap.css) to your static resources folder (bootstrap-demo-app/src/main/resources) and recompile on file changes.

For building the resources once, use `docker-compose up frontend`.

**NOTE**: when building the static resources via docker, the installed node_modules will not be present on your local file system.

## Contents

This project contains the following:

### Entity management

- configured entities which are rendered using EntityModule & BootstrapUiModule
- required properties are suffixed with '*'

#### Test entities

- Default entities from the across starter app (Author / Blogpost) with a very basic entity configuration
- associations

#### Car

- extended entity admin menu (navigation bar within a specific entity)
- embedded collections of objects
- Currency field
- Selectbox
- File upload (via FileManagerModule)
- date
- radio buttons 

#### Library

- summary view
- embedded collections of numbers, strings, checkbox lists, objects 
- datepicker
- currency field

#### Manufacturer

- embedded association to cars
  - the association also has a sub menu on the detail page
- advanced option menu with 2 links

### Functionality Demos / Fieldsets

- A demo page with various fieldset options

### License
Licensed under version 2.0 of the [Apache License][].

[module page]: https://foreach-across.github.io/modules/bootstrap-ui-module
[contributor guidelines]: https://foreach-across.github.io/contributing
[Maven]: https://maven.apache.org
[Apache License]: https://www.apache.org/licenses/LICENSE-2.0

[module page]: https://foreach-across.github.io/modules/admin-web-module
[contributor guidelines]: https://foreach-across.github.io/contributing
[Maven]: https://maven.apache.org
[Apache License]: https://www.apache.org/licenses/LICENSE-2.0

[module page]: https://foreach-across.github.io/modules/entity-module
[contributor guidelines]: https://foreach-across.github.io/contributing
[Maven]: https://maven.apache.org
[Apache License]: https://www.apache.org/licenses/LICENSE-2.0
[e2e readme]: ./entity-module-test-application/src/test/e2e/README.md

[module wiki]: https://foreach-across.github.io/modules/properties-module
[contributor guidelines]: https://foreach-across.github.io/contributing
[Maven]: https://maven.apache.org
[Apache License]: https://www.apache.org/licenses/LICENSE-2.0

[module wiki]: https://foreach-across.github.io/modules/application-info-module
[contributor guidelines]: https://foreach-across.github.io/contributing
[Maven]: https://maven.apache.org
[Apache License]: https://www.apache.org/licenses/LICENSE-2.0
