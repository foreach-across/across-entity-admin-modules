const root = "";
let dest;
import gutil from "gulp-util";

const slicingConfig = {
  "dest": "./slicing/",
  "URL": "./slicing/",
  "templates": ["./slicing/**/*.html", "./slicing/css/main.css"]
};

const devConfig = {
  "dest": "../resources/views/static/entity/",
  "URL": "../resources/views/static/entity/",
  "templates": ["../foreach-boilerplate-examplesite/**/*.html", "../foreach-boilerplate-examplesite/css/main.css"]
};

if ( gutil.env.slicing ) {
  dest = slicingConfig.dest;
}
else {
  dest = devConfig.dest
}

export default {
  "root": root,
  "dest": dest,
  "devURL":
    (gutil.env.slicing
      ? slicingConfig.URL
      : devConfig.URL),
  "templates":
    (gutil.env.slicing
      ? slicingConfig.templates
      : devConfig.templates),
  "scss": {
    "src": root + (gutil.env.path ? gutil.env.path : "") + "scss/**/*.scss",
    "lint": root + (gutil.env.path ? gutil.env.path : "") + "scss/**/*.scss",
    "dest": dest + (gutil.env.path ? gutil.env.path : "") + "css/"
  },
  "css": {
    "src": root + (gutil.env.path ? gutil.env.path : "") + "css/**/*.css"
  },
  "js": {
    "src": root + "js-src/",
    "lint": [root + "js-src/app/**/*.js"],
    "test": root + "js-src/",
    "dest": dest + "js/"
  },
  "svg": {
    "src": root + "svg-src/**",
    "dest": dest + "svg/"
  },
  "images_src": "static/**/*",
  "images_dest": "static/"
};
