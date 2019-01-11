/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Main configuration file for frontend build parameters.
 */
const path = require('path');

/**
 * Global variables - base output directory.
 * Relative directories are relative to working directory, usually the directory containing this config.js.
 */
const outputDir = './resources/views/static/BootstrapUiModule';

/**
 * Export global configuration.
 */
module.exports = {
    "css": {
        "outputDir": path.join( outputDir, '/css' )
    },
    "js": {
        "outputDir": path.join( outputDir, '/js' ),
        "webpack": {
            //
            // Javascript files that should be bundled by webpack and copied to the output.
            // Only use filename of files that are in the root src/js folder.
            //
            "entries": [
                "test"
            ],
            //
            // List of Javascript files in the output directory that should be kept and never deleted
            // when creating the webpack bundles.
            //
            "keepFiles": [
                "autosize.min.js",
                "bootstrapui.js",
                "bootstrapui-formelements.js",
                "moment/locale-nl-BE.js"
            ],
            //
            // External dependencies, usually through provided through CDN and not to be bundled.
            // Tormat:
            //   dependency: globalVariable
            // Example:
            //   "react": "React"
            //   Import 'react' is available as global var React.
            //
            "externals": {
                "jquery": "jQuery",
                "lodash": "_"
                // "react": 'React',
                // "react-dom": 'ReactDOM',
                // "axios": "axios",
                // "react-bootstrap": 'ReactBootstrap'
            }
        }
    }
}