## EntityModule
Please refer to the [module page][] for all information regarding documentation, issue tracking and support.

### Contributing
Contributions in the form of pull requests are greatly appreciated.  Please refer to the [contributor guidelines][] for more details. 

### Building from source
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

### Building

First of all, the docker image needs to be created for building the frontend resources.
Execute `docker-compose build` to build the required image(s)

Builds can be compiled locally by running `build-local.sh`, this will also watch the files by default.
An additional `build-prod.sh` is provided for build agents.

A complete lockfile is required to build frontend resources, if a dependency does not match the lockfile, the build will fail.
Updating the lockfile must be done manually by executing `build-local.sh lockfile`.

Description | command
--- | ---
Updating lockfile | `build-local.sh lockfile`
Building only | `build-local.sh build`
Building and watching | `build-local.sh build:watch` or `build-local.sh`
Building in production mode | `build-local.sh build:prod`

### License
Licensed under version 2.0 of the [Apache License][].

[module page]: https://across.dev/modules/entitymodule
[contributor guidelines]: https://across.dev/contributing
[Maven]: https://maven.apache.org
[Apache License]: https://www.apache.org/licenses/LICENSE-2.0