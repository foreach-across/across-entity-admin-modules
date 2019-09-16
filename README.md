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


