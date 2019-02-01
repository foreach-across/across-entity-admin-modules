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
import {EntityQueryOps} from "../src/js/app/utils/entity-query/entity-query-ops";

describe( "EntityQueryOps", function() {
  let assertExists;
  beforeAll( function() {
    assertExists = function( object ) {
      expect( object ).not.toBeNull();
      expect( object ).not.toBeUndefined();
    };
  } );

  it( "exists", function() {
    assertExists( EntityQueryOps );
  } );

  it( "AND", function() {
    let operand = EntityQueryOps.AND;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "and" );
    expect( operand.toString( null, "Foreach", "Across" ) )
      .toEqual( "'Foreach' and 'Across'" );
    expect( operand.toString( null, "Test", null ) )
      .toEqual( "'Test' and NULL" );
    expect( operand.toString( null, "Test", null ) )
      .toEqual( "'Test' and NULL" );
  } );

  it( "OR", function() {
    let operand = EntityQueryOps.OR;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "or" );
    expect( operand.toString( null, "Foreach", "Across" ) )
      .toEqual( "'Foreach' or 'Across'" );
    expect( operand.toString( null, "Test", null ) )
      .toEqual( "'Test' or NULL" );
    expect( operand.toString( null, "Test", null ) )
      .toEqual( "'Test' or NULL" );
  } );

  it( "EQ", function() {
    let operand = EntityQueryOps.EQ;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "=" );
    expect( operand.toString( "name", "Jos" ) ).toEqual( "name = 'Jos'" );
    expect( operand.toString( "name", "Jan", "Evert" ) ).toEqual( "name = 'Jan'" )

  } );

  it( "NEQ", function() {
    let operand = EntityQueryOps.NEQ;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 2 );
    expect( tokens ).toContain( "!=", "<>" );
    expect( operand.toString( "name", "Jos" ) ).toEqual( "name != 'Jos'" );
    expect( operand.toString( "name", "Jan", "Evert" ) ).toEqual( "name != 'Jan'" )
  } );

  it( "CONTAINS", function() {
    let operand = EntityQueryOps.CONTAINS;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "contains" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id contains -1" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id contains -2" );
  } );

  it( "NOT_CONTAINS", function() {
    let operand = EntityQueryOps.NOT_CONTAINS;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "not contains" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id not contains -1" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id not contains -2" );
  } );

  it( "IN", function() {
    let operand = EntityQueryOps.IN;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "in" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id in (-1)" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id in (-2,-3)" );
    expect( operand.toString( "name", "Jan", "Evert" ) ).toEqual( "name in ('Jan','Evert')" );

  } );

  it( "NOT_IN", function() {
    let operand = EntityQueryOps.NOT_IN;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "not in" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id not in (-1)" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id not in (-2,-3)" );
    expect( operand.toString( "name", "Jan", "Evert" ) ).toEqual( "name not in ('Jan','Evert')" );
  } );

  it( "LIKE", function() {
    let operand = EntityQueryOps.LIKE;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "like" );
    expect( operand.toString( "name", "Jos" ) ).toEqual( "name like 'Jos'" );
    expect( operand.toString( "name", "Jos%" ) ).toEqual( "name like 'Jos%'" );
    expect( operand.toString( "name", "%Jos" ) ).toEqual( "name like '%Jos'" );
    expect( operand.toString( "name", "%Jos%" ) ).toEqual( "name like '%Jos%'" );
    expect( operand.toString( "name", "Jan", "Evert" ) ).toEqual( "name like 'Jan'" );
  } );

  it( "LIKE_IC", function() {
    let operand = EntityQueryOps.LIKE_IC;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "ilike" );
    expect( operand.toString( "name", "Jos" ) ).toEqual( "name ilike 'Jos'" );
    expect( operand.toString( "name", "Jos%" ) ).toEqual( "name ilike 'Jos%'" );
    expect( operand.toString( "name", "%Jos" ) ).toEqual( "name ilike '%Jos'" );
    expect( operand.toString( "name", "%Jos%" ) ).toEqual( "name ilike '%Jos%'" );
    expect( operand.toString( "name", "Jan", "Evert" ) ).toEqual( "name ilike 'Jan'" );
  } );

  it( "GT", function() {
    let operand = EntityQueryOps.GT;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( ">" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id > -1" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id > -2" );

  } );

  it( "GE", function() {
    let operand = EntityQueryOps.GE;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( ">=" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id >= -1" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id >= -2" );
  } );

  it( "LT", function() {
    let operand = EntityQueryOps.LT;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "<" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id < -1" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id < -2" );
  } );

  it( "LE", function() {
    let operand = EntityQueryOps.LE;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "<=" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id <= -1" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id <= -2" );
  } );

  it( "IS_NULL", function() {
    let operand = EntityQueryOps.IS_NULL;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "is" );
    expect( operand.toString( "id", null ) ).toEqual( "id is NULL" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id is NULL" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id is NULL" );
  } );

  it( "IS_NOT_NULL", function() {
    let operand = EntityQueryOps.IS_NOT_NULL;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "is not" );
    expect( operand.toString( "id", null ) ).toEqual( "id is not NULL" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id is not NULL" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id is not NULL" );
  } );

  it( "IS_EMPTY", function() {
    let operand = EntityQueryOps.IS_EMPTY;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "is" );
    expect( operand.toString( "id", null ) ).toEqual( "id is EMPTY" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id is EMPTY" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id is EMPTY" );
  } );

  it( "IS_NOT_EMPTY", function() {
    let operand = EntityQueryOps.IS_NOT_EMPTY;
    assertExists( operand );
    let tokens = operand.tokens;

    expect( tokens.length ).toEqual( 1 );
    expect( tokens ).toContain( "is not" );
    expect( operand.toString( "id", null ) ).toEqual( "id is not EMPTY" );
    expect( operand.toString( "id", -1 ) ).toEqual( "id is not EMPTY" );
    expect( operand.toString( "id", -2, -3 ) ).toEqual( "id is not EMPTY" );
  } );

  it( "forToken", function() {
    expect( EntityQueryOps.forToken( "and" ) ).toEqual( EntityQueryOps.AND );
    expect( EntityQueryOps.forToken( "or" ) ).toEqual( EntityQueryOps.OR );
    expect( EntityQueryOps.forToken( "=" ) ).toEqual( EntityQueryOps.EQ );
    expect( EntityQueryOps.forToken( "!=" ) ).toEqual( EntityQueryOps.NEQ );
    expect( EntityQueryOps.forToken( "<>" ) ).toEqual( EntityQueryOps.NEQ );
    expect( EntityQueryOps.forToken( "contains" ) ).toEqual( EntityQueryOps.CONTAINS );
    expect( EntityQueryOps.forToken( "not contains" ) ).toEqual( EntityQueryOps.NOT_CONTAINS );
    expect( EntityQueryOps.forToken( "in" ) ).toEqual( EntityQueryOps.IN );
    expect( EntityQueryOps.forToken( "not in" ) ).toEqual( EntityQueryOps.NOT_IN );
    expect( EntityQueryOps.forToken( "like" ) ).toEqual( EntityQueryOps.LIKE );
    expect( EntityQueryOps.forToken( "ilike" ) ).toEqual( EntityQueryOps.LIKE_IC );
    expect( EntityQueryOps.forToken( "not like" ) ).toEqual( EntityQueryOps.NOT_LIKE );
    expect( EntityQueryOps.forToken( "not ilike" ) ).toEqual( EntityQueryOps.NOT_LIKE_IC );
    expect( EntityQueryOps.forToken( ">" ) ).toEqual( EntityQueryOps.GT );
    expect( EntityQueryOps.forToken( ">=" ) ).toEqual( EntityQueryOps.GE );
    expect( EntityQueryOps.forToken( "<" ) ).toEqual( EntityQueryOps.LT );
    expect( EntityQueryOps.forToken( "<=" ) ).toEqual( EntityQueryOps.LE );
    expect( EntityQueryOps.forToken( "is" ) ).toEqual( EntityQueryOps.IS_NULL );
    expect( EntityQueryOps.forToken( "is not" ) ).toEqual( EntityQueryOps.IS_NOT_NULL );

  } );

  it( "isGroupOperand", function() {
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.AND ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.OR ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.EQ ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.NEQ ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.NEQ ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.CONTAINS ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.NOT_CONTAINS ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.IN ) ).toBeTruthy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.NOT_IN ) ).toBeTruthy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.LIKE ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.LIKE_IC ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.NOT_LIKE ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.NOT_LIKE_IC ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.GT ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.GE ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.LT ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.LE ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.IS_NULL ) ).toBeFalsy();
    expect( EntityQueryOps.isGroupOperand( EntityQueryOps.IS_NOT_NULL ) ).toBeFalsy();

  } );
} );


