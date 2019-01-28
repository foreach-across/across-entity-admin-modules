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
const path = require( "path" );

const workingDirectory = process.env.INIT_CWD;

const cssEntries = [
    "bootstrapui"
];

const jsEntries = [
    "bootstrapui",
    "bootstrapui-formelements"
];

const outputDir = "../resources/views/static/BootstrapUiModule";

function resolveFileIdentifier( type, file ) {
    switch ( type ) {
        case "js":
            return path.join( "js", file );
        case "scss":
            return path.join( "css", file );
        default:
    }
    return file;
}

function resolveFiles( obj, type, files ) {
    files.forEach( file => obj[resolveFileIdentifier( type, file )] = path.join( path.join( workingDirectory, "src/" + type ), file ) );
}

function resolveEntries() {
    const entries = {};
    resolveFiles( entries, "js", jsEntries );
    resolveFiles( entries, "scss", cssEntries );
    return entries;
}

const MiniCssExtractPlugin = require( "mini-css-extract-plugin" );
const FixStyleOnlyEntriesPlugin = require( "webpack-fix-style-only-entries" );

module.exports = {
    "cache": false,
    "entry": resolveEntries(),
    "output": {
        "path": path.join( workingDirectory, outputDir ),
        "filename": "[name].js"
    },
    "resolve": {
        "extensions": ['.js', '.ts', '.tsx', '.scss']
    },
    "externals": {
        "jquery": "jQuery"
    },
    "module": {
        "rules": [
            {
                "enforce": "pre",
                "test": /\.js$/,
                "exclude": /node_modules/,
                "loader": "eslint-loader",
                "options": {
                    "failOnError": true
                }
            },
            {
                "test": /\.ts$/,
                "enforce": 'pre',
                "use": [
                    {
                        "loader": 'tslint-loader',
                        "options": {
                            "failOnHint": true
                        }
                    }
                ]
            },
            {
                "test": /\.jsx?$/,
                "include": path.join( workingDirectory, "src/js" ),
                "loader": "babel-loader",
                "enforce": "post",
                "query": {
                    "presets": ["env"]
                }
            },
            {
                "test": /\.tsx?$/,
                "include": path.join( workingDirectory, "src/js" ),
                "use": "ts-loader"
            },
            {
                "test": /\.scss$/,
                "include": path.join( workingDirectory, "src/scss" ),
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
            "jQuery": "jquery",
            "$": "jquery",
            "window.jQuery": "jquery"
        } ),
        new FixStyleOnlyEntriesPlugin(),
        new MiniCssExtractPlugin( {
            "filename": "[name].css"
        } )
    ],
    "watchOptions":
            {
                "ignored":
                        "/node_modules/"
            }
};
