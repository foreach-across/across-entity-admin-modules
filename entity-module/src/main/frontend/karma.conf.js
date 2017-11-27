const webpack = require( "karma-webpack" );
const config = require( "./gulp/config" ).default.js;
const webpackConfig = require( "./webpack.config" ).default;

module.exports = function( karmaConfig ) {
  const configObject = {
    "frameworks": ["jasmine"],
    "files": [
      "./node_modules/babel-polyfill/dist/polyfill.js",
      "./node_modules/phantomjs-polyfill/bind-polyfill.js",
      //config.dest + "common.bundle.js"
    ],
    "plugins": [
      "karma-babel-preprocessor",
      "webpack",
      "karma-jasmine",
      'karma-chrome-launcher',
      "karma-phantomjs-launcher",
      "karma-webpack",
    ],
    "browsers": ["PhantomJS"], /* production browsers: ["PhantomJS", "Chrome", "Firefox", "IE", "IE9", "Safari"], */
    preprocessors: {
      'src/**/*.js': ['babel'],
      'test/**/*.js': ['babel']
    },
    babelPreprocessor: {
      options: {
        presets: ['es2015'],
        sourceMap: 'inline'
      },
      filename: function( file ) {
        return file.originalPath.replace( /\.js$/, '.es5.js' );
      },
      sourceFileName: function( file ) {
        return file.originalPath;
      }
    },
    "webpack": webpackConfig,
    "webpackMiddleware": {
      "noInfo": true /* we already log in js task */
    }
  };

  configObject.files.push( "https://cdnjs.cloudflare.com/ajax/libs/lodash.js/4.17.4/lodash.min.js" );
  configObject.files.push( config.test + "**/*.test.js" );

  configObject.preprocessors[config.src + "**/*.js"] = ["webpack"];
  configObject.preprocessors[config.test + "**/*.test.js"] = ["webpack"];

  karmaConfig.set( configObject );
};
