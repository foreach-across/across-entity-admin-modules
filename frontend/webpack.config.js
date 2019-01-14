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

const webpack = require( "webpack" );
const argv = require( "minimist" );
const path = require( "path" );
const settings = require( './settings' );

const env = argv( process.argv.slice( 2 ) );
const MiniCssExtractPlugin = require( "mini-css-extract-plugin" );
const CopyWebpackPlugin = require( 'copy-webpack-plugin' );
const FixStyleOnlyEntriesPlugin = require( "webpack-fix-style-only-entries" );

module.exports = {
    //"mode": 'development',
    "cache": false,
    "entry": {/* determined by settings.js */},
    "output": {
        "path": path.join( settings.workingDirectory, settings.js.outputDir ),
        "publicPath": "/across/resources/static/theta/js/",
        "filename": "js/[name].js"
    },
    "resolve": {
        "extensions": ['.js', '.jsx', '.ts', '.tsx', '.scss'],
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
                "include": path.join( settings.workingDirectory, "src" ),
                "loader": "babel-loader",
                "enforce": "post",
                "query": {
                    "presets": ["env"]
                }
            },
            {
                "test": /\.tsx?$/,
                "include": path.join( settings.workingDirectory, "src" ),
                "use": "ts-loader"
            },
            {
                "test": /\.scss$/,
                "include": path.join( settings.workingDirectory, "scss" ),
                "use": [
                    MiniCssExtractPlugin.loader,
                    "css-loader", // translates CSS into CommonJS
                    "sass-loader" // compiles Sass to CSS, using Node Sass by default
                ]
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
        } ),
        // new CopyWebpackPlugin( [{from: "src/bootstrapui-formelements.js", to: "js/bootstrapui-formelements.js"}], {debug: true} ),
        new FixStyleOnlyEntriesPlugin(),
        new MiniCssExtractPlugin( {
            filename: 'css/[name].css'
        } ),
    ],
    "watchOptions":
            {
                "ignored":
                        "/node_modules/"
            }
    ,
// "optimization": {
//     "minimize": false
// }
}
;