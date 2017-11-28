import {createCookie, eraseCookie, readCookie} from "./cookie";

if ( window.location.search.length > 0 ) {
  if ( window.location.search.match( /[?&]debugMode=true/ ) ) {
    /* eslint-disable */
    console.debug( "Turning debug mode on and creating cookie" );
    /* eslint-enable */
    createCookie( "debugMode", "true", 31 );
  }
  else if ( window.location.search.match( /[?&]debugMode=false/ ) ) {
    /* eslint-disable */
    console.debug( "Deleting debug cookie" );
    /* eslint-enable */
    eraseCookie( "debugMode" );
  }
}

const cookieValue = readCookie( "debugMode" );
const loggerEnabled = (cookieValue !== null);

function _log( debugText ) {
  if ( loggerEnabled ) {
    /* eslint-disable */
    console.log( `Logger ${new Date().toLocaleString()} : ${debugText}` );
    /* eslint-enable */
  }
}

export default ({

  "debug": function( debugText ) {
    if ( loggerEnabled ) {
      /* eslint-disable */
      console.debug( "Logger " + new Date().toLocaleString() + " : " + debugText );
      /* eslint-enable */
    }
  },

  "log": _log,

  "warn": function( debugText ) {
    if ( loggerEnabled ) {
      /* eslint-disable */
      console.warn( "Logger " + new Date().toLocaleString() + " : " + debugText );
      /* eslint-enable */
    }
  },

  "error": function( debugText ) {
    if ( loggerEnabled ) {
      /* eslint-disable */
      console.error( "Logger " + new Date().toLocaleString() + " : " + debugText );
      /* eslint-enable */
    }
  }
});
