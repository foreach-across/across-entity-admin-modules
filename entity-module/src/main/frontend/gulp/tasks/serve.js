import "./scss";
import "./js";
import gulp from "gulp";
import browserSync from "browser-sync";
import webpackDevMiddleware from "webpack-dev-middleware";
import webpackHotMiddleware from "webpack-hot-middleware";
import config from "../config";
import webpack from "webpack";
import webpackConfig from "../../webpack.config.js";
import proxy from "http-proxy-middleware";
import gutil from "gulp-util";

const bundler = webpack( webpackConfig );

// Static server
gulp.task( "serve", gulp.series( "scss", "js", function() {

  const browserSyncConfig = {};
  const filter = function( pathname, req ) {
    // true passes
    return (!pathname.match( /__webpack_hmr/g ) && !pathname.match( /hot/g ));
  };

  let jsonPlaceholderProxy = proxy( filter, {
    target: config.devURL,
    logLevel: "debug"
  } );

  browserSyncConfig.server = {
    "notify": true,
    // Customize the Browsersync console logging prefix
    "logPrefix": "FE",
    "baseDir": gutil.env.slicing ? config.dest : "./",
    "path": config.dest + "",
    "middleware": [
      webpackDevMiddleware( bundler, {
        "publicPath": webpackConfig.output.publicPath,
        "watchOptions": {
          "ignored": /node_modules/,
          "poll": true
        },
        "stats": {
          "colors": true,
          "assets": true,
          "version": true,
          "hash": true,
          "timings": true,
          "chunks": true,
          "chunkModules": true
        }
      } ),
      webpackHotMiddleware( bundler )
    ],
    "port": 3000
  };

  if ( gutil.env.slicing ) {
    browserSyncConfig.startPath = "/";
  }
  else {
    browserSyncConfig.server.middleware.push( jsonPlaceholderProxy );
    browserSyncConfig.startPath = "/";
    /* the homepage */
  }

  // watch for changes in markup/templates, css
  browserSyncConfig.files = config.templates;
  browserSync.create().init( browserSyncConfig );

  gulp.watch( config.scss.src, gulp.series( ["scss"] ) );

} ) );
