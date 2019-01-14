/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
const path = require('path');

const settings = require('./settings');
const webpackConfig = require('./webpack.config');


settings.js.webpack.entries.map( file => webpackConfig.entry[file] = path.join( path.join( settings.workingDirectory, "src" ), file )  );
settings.css.webpack.entries.map( file => webpackConfig.entry[file] = path.join( path.join( settings.workingDirectory, "scss" ), file )  );

webpackConfig.externals = settings.js.webpack.externals;

console.log( webpackConfig );

module.exports = webpackConfig;