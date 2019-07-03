const webpack = require( "webpack" );
const path = require( "path" );

const workingDirectory = process.env.INIT_CWD;

const cssEntries = [
  "entity-module"
];

const jsEntries = [
  "entity-module",
  "entity-query",
  "expandable"
];

const outputDir = "../resources/views/static/entity";

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

const CopyWebpackPlugin = require( 'copy-webpack-plugin' );
const MiniCssExtractPlugin = require( "mini-css-extract-plugin" );
const FixStyleOnlyEntriesPlugin = require( "webpack-fix-style-only-entries" );

module.exports = {
  "target": "web",
  "devtool": "inline-source-map",
  "cache": false,
  "entry": resolveEntries(),
  "output": {
    "path": path.join( workingDirectory, outputDir ),
    "filename": "[name].js"
  },
  "resolve": {
    "modules": [
      path.join( workingDirectory, outputDir ),
      "node_modules",
      "lib/",
      "polyfills/",
      "app/utils"
    ],
    "alias": {
      // Bind version of jquery
      "jquery": "jquery-1.12.0"
    },
    "extensions": ['.js', '.ts', '.tsx', '.scss']
  },
  "externals": {
    "jquery": "jQuery",
    "lodash": "_"
  },
  "module": {
    "rules": [
      // {
      //   "enforce": "pre",
      //   "test": /\.js$/,
      //   "exclude": /node_modules/,
      //   "loader": "eslint-loader",
      //   "options": {
      //     "failOnError": true
      //   }
      // },
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
                              } ),
    new CopyWebpackPlugin( [
                             {from: path.join( workingDirectory, "src/js/lib/dependson.js" ), to: path.join( workingDirectory, outputDir ) + '/js/'}
                           ], {} )
  ],
  "watchOptions":
    {
      "ignored":
        "/node_modules/"
    }
};
