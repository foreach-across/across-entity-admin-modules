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

import {exampleAddMethod, exampleSubstractMethod} from "./app/modules/example-module";

/*
 * syntax: Jasmine
 * https://jasmine.github.io/
 *
 * Testrunner: Karma
 * http://karma-runner.github.io
 * */

describe( "main initialisation", function() {
  it( "should run a unit test", function() {
    expect( true ).toBe( true );
  } );
} );

describe( "module initialisation", function() {
  it( "should init a  module succesfully", function() {

    expect( exampleAddMethod( 1, 2 ) ).toEqual( 3 );
    expect( exampleSubstractMethod( 2, 1 ) ).toEqual( 1 );

  } );
} );
