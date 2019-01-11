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

const settings = require('../settings');
const argv = require('minimist');
const path = require('path');

const env = argv( process.argv.slice( 2 ) );
const workingDirectory = process.env.INIT_CWD;

let dest;

const gutil = {
    env: {
        slicing: true,
        path: ''
    }
};

const slicingConfig = {
    "dest": "./slicing/",
    "URL": "./slicing/",
    "templates": ["./slicing/**/*.html", "./slicing/css/main.css"]
};

const devConfig = {
    "dest": "../resources/views/static/entity/",
    "URL": "../resources/views/static/entity/",
    "templates": ["../foreach-boilerplate-examplesite/**/*.html", "../foreach-boilerplate-examplesite/css/main.css"]
};

if ( gutil.env.slicing ) {
    dest = slicingConfig.dest;
}
else {
    dest = devConfig.dest
}

module.exports ={
    "root": workingDirectory,
    "dest": dest,
    "scss": {
        "lintConfig": '.sass-lint.yml',
        "src": path.join( workingDirectory, "scss/**/*.scss" ),
        "dest": path.join( workingDirectory, settings.css.outputDir )
    },
    "js": {
        "src": path.join( workingDirectory, "src" ),
        "lint": [
            path.join( workingDirectory, "src/**/*.js" )
            // "!" + path.join( workingDirectory, "src/lib/**/*.js" ),
            // "!" + path.join( workingDirectory, "src/polyfills/**/*.js" )
        ],
        "test": path.join( workingDirectory, "src" ),
        "dest": path.join( workingDirectory, settings.js.outputDir ),
        "webpack" : settings.js.webpack
    }
};
