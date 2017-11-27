try {
  const gulp = require( "gulp" );
  const svgSprite = require( "gulp-svg-sprite" );
  const config = require( "../config" );
  const path = require( "path" );
  const glob = require( "glob" );
  const size = require( "gulp-size" );

  gulp.task( "svg", function() {
    const svgDest = config.svg.dest;

    function makeSvgSpriteOptions( dirPath ) {
      return {
        mode: {
          symbol: {
            dest: ".",
            example: true,
            sprite: "main.svg"
          },
        }
      };
    }

    return glob( config.svg.src, function( err, dirs ) {
      dirs.forEach( function( dir ) {
        gulp.src( path.join( dir, "*.svg" ) )
          .pipe( svgSprite( makeSvgSpriteOptions( dir ) ) )
          .pipe( size( {showFiles: true, title: svgDest} ) )
          .pipe( gulp.dest( svgDest ) )
      } )
    } );

  } );
}
catch ( e ) {
  console.log( "gulp svg task DISABLED" );

  // to enable again:
  // npm install gulp-svg-sprite@1.2.19 --save
  // && npm install path@0.12.7 --save
  // && npm install glob@7.0.3 --save
  // && npm install gulp-size@2.1.0 --save
}

