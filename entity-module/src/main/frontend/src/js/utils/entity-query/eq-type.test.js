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
import EQType from "./eq-type"
import EQString from "./eq-string";
import EQValue from "./eq-value";
import EQGroup from "./eq-group";
import EQFunction from "./eq-function";
import {arrayEquals} from "../utilities";
import {NullOrUndefinedException} from "../exceptions";

describe( "EQType", function() {

  describe( "EQValue", function() {
    it( "exists", function() {
      const eqValue = new EQValue( 1 );
      expect( eqValue instanceof EQValue ).toBeTruthy();
      expect( eqValue instanceof EQType ).toBeTruthy();
    } );

    it( "value may not be null or undefined", function() {
      expect( () => new EQValue() ).toThrowError( new NullOrUndefinedException( "EQValue", "value" ).message );
      expect( () => new EQValue( null ) ).toThrowError( new NullOrUndefinedException( "EQValue", "value", null ).message );
    } );

    it( "value", function() {
      let eqValue = new EQValue( 10 );
      expect( eqValue.getValue() ).toEqual( 10 );
      eqValue = new EQValue( "My string" );
      expect( eqValue.getValue() ).toEqual( "My string" );
    } );

    it( "equal if same value", function() {
      expect( new EQValue( "some value" ) ).toEqual( new EQValue( "some value" ) );
    } );

    it( "not equal if different value", function() {
      expect( new EQValue( "some value" ) ).not.toEqual( new EQValue( "some other value" ) )
    } );

    it( "toString() simply writes value", function() {
      let eqValue = new EQValue( 10 );
      expect( eqValue.toString() ).toEqual( "10" );
      eqValue = new EQValue( "My string" );
      expect( eqValue.toString() ).toEqual( "My string" );
    } );
  } );

  describe( "EQString", function() {
    it( "exists", function() {
      const eqString = new EQString( "my string" );
      expect( eqString instanceof EQString ).toBeTruthy();
      expect( eqString instanceof EQType ).toBeTruthy();
    } );

    it( "value may not be null or undefined", function() {
      expect( () => new EQString() ).toThrowError( new NullOrUndefinedException( "EQString", "value" ).message );
      expect( () => new EQString( null ) ).toThrowError( new NullOrUndefinedException( "EQString", "value", null ).message );
    } );

    it( "value", function() {
      let eqString = new EQString( 10 );
      expect( eqString.getValue() ).toEqual( 10 );
      eqString = new EQValue( "My string" );
      expect( eqString.getValue() ).toEqual( "My string" );
    } );

    it( "equal if same value", function() {
      expect( new EQString( "some value" ) ).toEqual( new EQString( "some value" ) );
    } );

    it( "not equal if different value", function() {
      expect( new EQString( "some value" ) ).not.toEqual( new EQString( "some other value" ) )
    } );

    it( "toString() writes escaped value", function() {
      let eqString = new EQString( "some value" );
      expect( eqString.toString() ).toEqual( "'some value'" );
      eqString = new EQString( "some ' \"value" );
      expect( eqString.toString() ).toEqual( "'some \\' \"value'" );
    } );
  } );

  describe( "EQGroup", function() {
    it( "exists", function() {
      const values = [new EQString( "one" ), new EQValue( 123 )];
      let eqGroup = new EQGroup( values );
      expect( eqGroup instanceof EQGroup ).toBeTruthy();
      expect( eqGroup instanceof EQType ).toBeTruthy();

      eqGroup = new EQGroup( new EQString( "one" ), new EQValue( 123 ) );
      expect( eqGroup instanceof EQGroup ).toBeTruthy();
      expect( eqGroup instanceof EQType ).toBeTruthy();
    } );

    it( "values", function() {
      const values = [new EQString( "one" ), new EQValue( 123 )];
      const eqGroup = new EQGroup( values );

      expect( JSON.stringify( eqGroup.getValues() ) ).toEqual( JSON.stringify( values ) );
      expect( eqGroup.getValues() ).toEqual( values );
      expect( arrayEquals( eqGroup.getValues(), values ) ).toBeTruthy();

      const eqGroup2 = new EQGroup( new EQString( "one" ), new EQValue( 123 ) );
      expect( arrayEquals( eqGroup2.getValues(), values ) ).toBeTruthy();

      const eqGroup3 = new EQGroup( new EQString( "one" ) );
      expect( eqGroup3.getValues() ).toEqual( [new EQString( "one" )] );
      expect( arrayEquals( eqGroup3.getValues(), [new EQString( "one" )] ) ).toBeTruthy();

    } );

    it( "equal if same value", function() {
      const values = [new EQString( "one" ), new EQValue( 123 )];
      expect( new EQGroup( values ) )
        .toEqual( new EQGroup( values ) );
      expect( new EQGroup( values ) )
        .toEqual( new EQGroup( new EQString( "one" ), new EQValue( 123 ) ) );
      expect( new EQGroup( new EQString( "one" ), new EQValue( 123 ) ) )
        .toEqual( new EQGroup( new EQString( "one" ), new EQValue( 123 ) ) );
    } );

    it( "not equal if different value", function() {
      const values = [new EQString( "one" ), new EQValue( 123 )];
      expect( new EQGroup( values ) )
        .not.toEqual( new EQGroup( new EQString( "two" ), new EQValue( 123 ) ) );
      expect( new EQGroup( new EQString( "one" ), new EQValue( 123 ) ) )
        .not.toEqual( new EQGroup( new EQString( "one" ), new EQValue( 789 ) ) );
      expect( new EQGroup( new EQString( "one" ), new EQValue( 123 ), new EQValue( false ) ) )
        .not.toEqual( new EQGroup( new EQString( "one" ), new EQValue( 789 ) ) );
    } );

    it( "changes to original collection have no impact", function() {
      const values = [new EQString( "one" ), new EQValue( 123 )];
      const eqGroup = new EQGroup( values );
      values.push( new EQString( "Jan" ) );
      expect( eqGroup.getValues() ).not.toContain( new EQString( "Jan" ) );
      expect( eqGroup.getValues() ).not.toEqual( values );
    } );

    it( "toString() writes wrapped group", function() {
      expect( new EQGroup( [] ).toString() ).toEqual( "()" );
      expect( new EQGroup( new EQValue( 1 ), new EQString( "test" ), new EQValue( 2 ) ).toString() )
        .toEqual( "(1,'test',2)" );
    } );
  } );

  describe( "EQFunction", function() {
    it( "exists", function() {
      const eqFunction = new EQFunction( "function" );
      expect( eqFunction instanceof EQFunction ).toBeTruthy();
      expect( eqFunction instanceof EQType ).toBeTruthy();
    } );

    it( "name may not be null or undefined", function() {
      expect( () => new EQFunction() ).toThrowError( new NullOrUndefinedException( "EQFunction", "name" ).message );
      expect( () => new EQFunction( null ) ).toThrowError( new NullOrUndefinedException( "EQFunction", "name", null ).message );
    } );

    it( "properties", function() {
      let eqFunction = new EQFunction( "myFunction" );
      expect( eqFunction.getName() ).toEqual( "myFunction" );
      expect( eqFunction.getArguments().length ).toEqual( 0 );

      const values = [new EQString( "one" ), new EQValue( 123 )];
      eqFunction = new EQFunction( "otherFunction", values );
      expect( eqFunction.getName() ).toEqual( "otherFunction" );
      expect( arrayEquals( eqFunction.getArguments(), values ) ).toBeTruthy();
    } );

    it( "equal if same name and arguments", function() {
      expect( new EQFunction( "myFunction" ) ).toEqual( new EQFunction( "myFunction" ) );
      expect( new EQFunction( "myFunction", new EQString( "Joris" ), new EQValue( 123 ) ) )
        .toEqual( new EQFunction( "myFunction", new EQString( "Joris" ), new EQValue( 123 ) ) );
    } );

    it( "not equal if different name or arguments", function() {
      expect( new EQFunction( "myFunction" ) ).not.toEqual( new EQFunction( "otherFunction" ) );
      expect( new EQFunction( "myFunction", new EQString( "Joris" ), new EQValue( 123 ) ) )
        .not.toEqual( new EQFunction( "myFunction", new EQString( "Egert" ), new EQValue( 123 ) ) );
      expect( new EQFunction( "myFunction", new EQString( "Joris" ), new EQValue( 123 ) ) )
        .not.toEqual( new EQFunction( "otherFunction", new EQString( "Joris" ), new EQValue( 123 ) ) );
    } );

    it( "changes to original collection have no impact", function() {
      let values = [new EQString( "one" ), new EQValue( 123 )];
      const eqFunction = new EQFunction( "myFunction", values );
      expect( eqFunction.getArguments() ).toEqual( values );
      values.push( new EQString( "Karel" ) );
      expect( eqFunction.getArguments() ).not.toEqual( values );
    } );

    it( "toString() writes wrapped function", function() {
      expect( new EQFunction( "someFunction" ).toString() ).toEqual( "someFunction()" );
      expect( new EQFunction( "anotherFunc", new EQValue( 1 ), new EQString( "test" ), new EQValue( 2 ) ).toString() )
        .toEqual( "anotherFunc(1,'test',2)" );
    } );

  } );
} );
