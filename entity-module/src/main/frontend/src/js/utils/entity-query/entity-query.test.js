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
import EntityQuery from "./entity-query";
import EntityQueryCondition from "./entity-query-condition";
import {EntityQueryOps} from "./entity-query-ops";
import Sort from "../sort/sort";
import SortOrder, {Direction} from "../sort/sort-order";

describe( "EntityQuery", function() {
  it( "single property toString()", function() {
    const query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.EQ, "myName" ) );
    expect( query.toString() ).toEqual( "name = 'myName'" );
  } );

  it( "Top level 'and' should have no braces", function() {
    const innerQuery = EntityQuery.and( new EntityQueryCondition( "city", EntityQueryOps.EQ, 213 ), new EntityQueryCondition( "age", EntityQueryOps.GT, 18 ) );
    const query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.EQ, "myName" ), innerQuery );

    expect( query.toString() ).toEqual( "name = 'myName' and (city = 213 and age > 18)" );

    const other = EntityQuery.and( new EntityQueryCondition( "year", EntityQueryOps.NEQ, 2015 ), query );
    expect( other.toString() ).toEqual( "year != 2015 and (name = 'myName' and (city = 213 and age > 18))" );
  } );

  it( "Top level 'or' should have no braces", function() {
    const innerQuery = EntityQuery.and( new EntityQueryCondition( "city", EntityQueryOps.EQ, 213 ), new EntityQueryCondition( "age", EntityQueryOps.GT, 18 ) );
    const query = EntityQuery.or( new EntityQueryCondition( "name", EntityQueryOps.EQ, "myName" ), innerQuery );

    expect( query.toString() ).toEqual( "name = 'myName' or (city = 213 and age > 18)" )
  } );

  it( "nested expressions toString()", function() {
    const query = new EntityQuery( EntityQueryOps.AND );
    query.add( new EntityQueryCondition( "name", EntityQueryOps.EQ, "someName" ) );
    query.add( new EntityQueryCondition( "city", EntityQueryOps.NEQ, 217 ) );

    expect( query.toString() ).toEqual( "name = 'someName' and city != 217" );

    const subQuery = EntityQuery.or(
      new EntityQueryCondition( "email", EntityQueryOps.CONTAINS, "emailOne" ),
      new EntityQueryCondition( "email", EntityQueryOps.EQ, "emailTwo" )
    );
    query.add( subQuery );

    const eql = "name = 'someName' and city != 217 and (email contains 'emailOne' or email = 'emailTwo')";
    expect( query.toString() ).toEqual( eql );
  } );

  it( "all toString()", function() {
    const sort = new Sort( new SortOrder( "name", Direction.ASC ), new SortOrder( "city", Direction.DESC ) );
    expect( EntityQuery.all().toString() ).toEqual( "" );
    expect( EntityQuery.all( sort ).toString() ).toEqual( "order by name ASC, city DESC" )
  } );

  it( "query with ordering toString()", function() {
    const sort = new Sort(
      new SortOrder( "name", Direction.ASC ),
      new SortOrder( "city", Direction.DESC )
    );

    const query = new EntityQuery( EntityQueryOps.AND );
    query.setSort( sort );
    query.add( new EntityQueryCondition( "name", EntityQueryOps.EQ, "someName" ) );
    query.add( new EntityQueryCondition( "city", EntityQueryOps.NEQ, 217 ) );

    expect( query.toString() ).toEqual( "name = 'someName' and city != 217 order by name ASC, city DESC" );

    sort.add( new SortOrder( "city", Direction.DESC ) );
    expect( query.toString() ).toEqual( "name = 'someName' and city != 217 order by city DESC, name ASC" )
  } );

  it( "equals takes the sortorder into account", function() {
    const query = new EntityQuery( EntityQueryOps.AND );
    query.add( new EntityQueryCondition( "name", EntityQueryOps.EQ, "someName" ) );

    const other = new EntityQuery( EntityQueryOps.AND );
    other.add( new EntityQueryCondition( "name", EntityQueryOps.EQ, "someName" ) );

    expect( query ).toEqual( other );

    other.setSort( new Sort( "name", Direction.ASC ) );
    expect( query ).not.toEqual( other );
  } )

} );
