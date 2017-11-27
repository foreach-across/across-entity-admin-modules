try {
  const gulp = require( "gulp" );
  const size = require( "gulp-size" );
  const imagemin = require( "gulp-imagemin" );
  const config = require( "../config" );
  const pngquant = require( "imagemin-pngquant" );

  // Optimize images
  gulp.task( "images", function() {
               console.log( "processing folder " + config.images_src );

               return gulp.src( config.images_src )
                 .pipe( imagemin( {
                                    progressive: true,
                                    interlaced: true,
                                    use: [pngquant( {quality: "65-80", speed: 4} )]
                                  } ) )
                 .pipe( gulp.dest( config.images_dest ) )
                 .pipe( size( {title: "images"} ) );
             }
  );
}
catch ( e ) {
  console.log( "gulp images task DISABLED" );

  // to enable again:
  // npm install gulp-imagemin@2.4.0 --save
  // && npm install imagemin-pngquant@4.2.2 --save
  // && npm install gulp-size@2.1.0 --save
}

