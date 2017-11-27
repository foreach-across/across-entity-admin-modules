/*
 * This is the "main" javascript file for our application.
 * it's purpose is to bundle and/or bootstrap all our modules/ui/utils/ ... logic
 * */

import $ from "jquery";
import moduleInstance from "app/modules/example-module";
import logger from "logger";

const loadElements = $( ".js-condition" ).length;

logger.log( `load module ${moduleInstance}` );

if ( loadElements ) {
  logger.log( "condition was met" );
  // elements is asynchronously (and conditionally) required here

  import(/* webpackChunkName: "webpackChunkNameForElements" */ `app/ui/elements`).then( ( result ) => {
    logger.debug( result );
    $( ".js-condition" ).on( "click", () => {
      logger.log( "click event example" );
    } );
  } ).catch( () => "An error occurred while loading the component" );

  import(/* webpackChunkName: "lodash" */ "lodash").then( ( _ ) => {
    const element = document.createElement( "div" );
    element.innerHTML = _.join( ["Hello", "webpack"], " " );
    return element;
  } ).catch( () => "An error occurred while loading the component" );
}
else {
  logger.warn( "condition was not met" );
}

// Remove any side effects so that when this module gets hotreloaded, they don't occur twice
// source: https://github.com/ahfarmer/webpack-hmr-3-ways/blob/master/server-api/js/box-creator.js
if ( module.hot ) {
  module.hot.dispose( () => {
    $( ".js-condition" ).off( "click" );
  } );
}
