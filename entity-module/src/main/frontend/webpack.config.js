import path from "path";
import webpack from "webpack";
import config from "./gulp/config";
import gutil from "gulp-util";

export default {
  "target": "web",
  "devtool": "#inline-source-map",
  "cache": true,
  "context": path.join( __dirname, config.js.src ),
  "entry": {
    // "main": ["./main"],
    "entity-query": ["./entity-query"],
    // "common": ["jquery"]
  },
  "output": {
    "path": path.join( __dirname, config.js.dest ),
    "publicPath": "http://localhost:3000/js/",
    "filename": "[name].js",
    "chunkFilename": "[name].chunk.[chunkhash].js"
  },
  "resolve": {
    "modules": [
      path.join( __dirname, config.js.src ),
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

  // IF YOU USE a CDN (attach script tag with src="//"//cdn.jsdelivr.net/jquery/1.12.0/jquery.min""):
  "externals": {
    // require("jquery") is external and available
    //  on the global var jQuery
    "jquery": "jQuery",
    "lodash": "_"
  },
  "module": {
    "rules": [
      {
        "test": /\.js?$/,
        "include": path.join( __dirname, config.js.src ),
        "loader": "babel-loader",
        "enforce": "post",
        "query": {
          "presets": ["es2015"]
        }
      }
    ]
  },
  "plugins": [
    gutil.env.hot
      ? new webpack.HotModuleReplacementPlugin() // Enable HMR
      : function() {
      },
    gutil.env.hot
      ? new webpack.NoEmitOnErrorsPlugin() // needed for HMR
      : function() {
      },
    /*new webpack.optimize.CommonsChunkPlugin({
     "name": "common" // Specify the common bundle's name.
     }),*/
    new webpack.ProvidePlugin( {
      // Automatically detect jQuery and $ as free var in modules
      // and inject the jquery library
      // This is required by many jquery plugins
      "jQuery": "jquery",
      "$": "jquery",
      "window.jQuery": "jquery",
      _: "lodash"
    } ),
    //new webpack.optimize.LimitChunkCountPlugin({maxChunks: (gutil.env.production ?  "15" : "1")}), /* max 15 chunks in production, only 1 for dev */
    //new webpack.optimize.MinChunkSizePlugin({minChunkSize: 2000}) /* minimum 2KB*/
  ]
};
