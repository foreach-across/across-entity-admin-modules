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

describe( 'List view: basic features', () => {

    beforeEach( function() {
        cy.login( 'admin' );
    } );

    it( 'List view contains 3 partners', () => {
        cy.goToMenuItem( "Partner" );

        cy.assertListViewResults( 3 );
    } );

    it( 'After filtering we only see google', () => {
        cy.server();
        cy.route( '/admin/entities/partner?*' ).as( "listViewAjax" );
        cy.goToMenuItem( "Partner" );

        cy.get( '#partner-name-filter' )
                .clear()
                .type( "Google" );

        cy.get( ".form > .btn" ).click();
        //cy.wait('@listViewAjax');

        cy.get( "tbody" ).within( () => {
            cy.get( '[data-tbl-field="name"]' ).first().contains( "Google" );
        } );

        cy.assertListViewResults( 1 );
    } );

    it( 'Simple sorting', () => {
        cy.server();
        cy.goToMenuItem( "Partner" );

        cy.get( "tbody" ).within( () => {
            cy.get( '[data-tbl-field="name"]' ).first().contains( "Facebook" );
        } );

        cy.get( '[data-tbl-sort-property="name"]' ).click();

        cy.get( "tbody" ).within( () => {
            cy.get( '[data-tbl-field="name"]' ).first().contains( "Microsoft" );
        } );
    } );

    it( 'After filtering nonExistant we get no results', () => {
        cy.goToMenuItem( "Partner" );

        cy.get( '#partner-name-filter' )
                .clear()
                .type( "nonExistant" );

        cy.get( ".form > .btn" ).click();

        cy.get( '[data-ax-dev-view-element="itemsTable-noresults"]' ).contains( "No partners found" );
    } );

    it( 'Filtering and sorting', () => {
        cy.server();
        cy.route( '/admin/entities/user?*' ).as( "listViewAjax" );
        cy.goToMenuItem( "User" );

        cy.get( "tbody" ).within( () => {
            cy.get( '[data-tbl-field="id"]' ).first().contains( "jane 0" );
        } );

        cy.get( '[name="extensions[eqFilterProperties][name]"]' ).type( "joey{enter}" );
        //cy.wait('@listViewAjax');
        cy.assertListViewResults( 15 );

        cy.get( "tbody" ).within( () => {
            cy.get( '[data-tbl-field="id"]' ).first().contains( "joey 0" );
        } );

        cy.get( '[data-tbl-sort-property="name"]' ).click();
        // configured via AJAX
        cy.wait('@listViewAjax');

        cy.get( "tbody" ).within( () => {
            cy.get( '[data-tbl-field="id"]' ).first().contains( "joey 9" );
        } );
    } );

    it( 'Advanced filter has the correct terms', () => {
        cy.goToMenuItem( "Note" );
        cy.server();
        cy.route( '/admin/entities/note?*' ).as( "listViewAjax" );
        cy.get( '[name="extensions[eqFilterProperties][text]"]' ).type( "co{enter}" );
        cy.get( '[data-entity-query-filter-form-link="advanced"]' ).click();
        //cy.wait('@listViewAjax');

        cy.get( '#extensions\\[eqFilter\\]' ).should( 'be.visible' )
                .should( "have.value", "text contains 'co'" );

    } );
} );
