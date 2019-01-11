/*
* Copyright 2018 the original author or authors
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

const webpack = require("webpack");
const argv = require( "minimist");
const path = require( "path");
const settings = require('./settings');

const env = argv( process.argv.slice( 2 ) );


module.exports = {
    //"mode": 'development',
    "cache": false,
    "entry": {/* determined by settings.js */},
    "output": {
        "path": path.join( settings.workingDirectory, settings.js.outputDir ),
        "publicPath": "/across/resources/static/theta/js/",
        "filename": "[name].js",
        "chunkFilename": "[name].chunk.[chunkhash].js"
    },
    "resolve": {
        "extensions": ['.js', '.jsx'],
        "modules": [
            "node_modules",
            "lib/",
            "polyfills/",
            "app/utils"
        ],
        "alias": {
            // Bind version of jquery
            "jquery": "jquery-1.12.0"
        }
    },
    "externals": {/* determined by settings.js */},
    "module": {
        "rules": [
            {
                "test": /\.jsx?$/,
                "include":  path.join( settings.workingDirectory, "src" ),
                "loader": "babel-loader",
                "enforce": "post",
                "query": {
                    "presets": ["env"]
                }
            }
        ]
    },
    "plugins": [
        new webpack.ProvidePlugin( {
            // Automatically detect jQuery and $ as free var in modules
            // and inject the jquery library
            // This is required by many jquery plugins
            "jQuery": "jquery",
            "$": "jquery",
            "window.jQuery": "jquery",
            _: "lodash"
        } )
    ],
    "watchOptions": {
        "ignored": "/node_modules/"
    },
    // "optimization": {
    //     "minimize": false
    // }
};