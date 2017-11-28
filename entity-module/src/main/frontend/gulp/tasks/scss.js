import gulp from "gulp";
import gutil from "gulp-util";
import handleErrors from "../util/handleErrors";
import config from "../config";
import sass from "gulp-sass";
import sourcemaps from "gulp-sourcemaps";
import autoprefixer from "gulp-autoprefixer";
import gulpif from "gulp-if";
import scssLint from "gulp-scss-lint";
import stylishCSS from "gulp-scss-lint-stylish2";
import touch from "gulp-touch";

// pretty layout

gulp.task( "scss:lint", function( cb ) {
  const stylishCSSReporter = stylishCSS();
  cb();

  return gulp.src( config.scss.lint )
    .pipe( scssLint( {"config": "scss-lint.yml", "customReport": stylishCSSReporter.issues} ) )
    .pipe( stylishCSSReporter.printSummary );
} );

const AUTOPREFIXER_BROWSERS = [
  "ie >= 10",
  "ie_mob >= 10",
  "ff >= 30",
  "chrome >= 34",
  "safari >= 7",
  "opera >= 23",
  "ios >= 7",
  "android >= 4.4",
  "bb >= 10"
];

function singleRun() {
  //console.log( "starting to compile " + config.scss.src + " to " + config.dest);

  return gulp.src( config.scss.src )
    .pipe( gulpif( !gutil.env.production, sourcemaps.init() ) )
    .pipe( sass( {
                   outputStyle: gutil.env.production ? "compressed" : "expanded",
                   errLogToConsole: true,
                   sourceComments: gutil.env.production ? false : true
                 } ) )
    .on( "error", handleErrors )
    .pipe( autoprefixer( AUTOPREFIXER_BROWSERS ) )
    .pipe( gulpif( !gutil.env.production, sourcemaps.write() ) )
    .pipe( gulp.dest( config.scss.dest ) )
    .pipe( touch() );
}

gulp.task( "scss", gulp.series( function() {
  if ( gutil.env.watch ) {
    console.log( "WATCH COMPILE" );

    singleRun(); // always compile once
    console.log( "WATCHER - first compile done" );

    // scss
    const scssWatcher = gulp.watch( config.scss.src, singleRun );

    scssWatcher.on( "change", function( event ) {
      console.log( "event: " + event );
      gutil.log( gutil.colors.yellow( "WATCHER: changed - " + event ) );
    } );

    scssWatcher.on( "error", function( event ) {
      gutil.log( gutil.colors.yellow( "WATCHER: error - " + event ) );
      process.emit( "end" );
    } );

  }
  else {

    console.log( "SINGLE COMPILE (you can also use --watch)" );

    return singleRun();
  }
} ) );
