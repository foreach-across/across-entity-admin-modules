= Bootstrap 4

To start customizing the bootstrap styling, please start the docker-compose script and then the demo application.

For watching the filesystem for changes, use `docker-compose run --rm frontend yarn run build:watch`.
This will build the static resources (adminweb-bootstrap.css) to your static resources folder (bootstrap-demo-app/src/main/resources) and recompile on file changes.

For building the resources once, use `docker-compose up frontend`.

**NOTE**: when building the static resources via docker, the installed node_modules will not be present on your local file system. 