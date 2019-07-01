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

describe( 'Partner listview', () => {

    beforeEach( function() {
        cy.login( 'admin', 'admin' );
    } );

    it( 'Advanced filter has the correct terms', () => {
        cy.goToMenuItem( "Note" );
        cy.get( '[name="extensions[eqFilterProperties][text]"]' ).type( "co{enter}" );
        cy.get( '[data-entity-query-filter-form-link="advanced"]' ).click();
        cy.get( '#extensions\\[eqFilter\\]' ).should( 'be.visible' )
                .should( "have.value", "text contains 'co'" );
    } );

    it( 'Advanced filter gives proper errormessage on invalid expression', () => {
        cy.goToMenuItem( "Note" );
        cy.get( '[data-entity-query-filter-form-link="advanced"]' ).click();
        cy.get( '[name="extensions[eqFilter]"]' ).type( "nonexistent{enter}" );
        cy.get( '.alert' ).contains( 'Missing operator for: nonexistent ; position 11' );
    } );

} );