/*
* This is the "bootstrap" entry point for our application.
* it's purpose is to include any general polyfilles and dependencies as well as any code that should *always* run.
* We keep a separate app/init.js to distinguish between these purposes and the *very* project specific module setup.
* */

import "./app/init";

if ( module.hot ) {
  module.hot.accept();
}
