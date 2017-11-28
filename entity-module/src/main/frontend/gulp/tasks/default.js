import gulp from "gulp";
import "./serve";

gulp.task( "default", gulp.series( "serve" ) );
