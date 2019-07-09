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

describe( 'List view: pagination', () => {

    beforeEach( function() {
        cy.login( 'admin' );
    } );

    it( 'Pagination without sorting & filtering', () => {
        cy.visit( '/admin/entities/note?page=0&size=10' );
        cy.get( '.total-pages-link' ).contains( "3" );

        cy.assertListViewResults( 10, 22 );
        cy.get( "tbody" ).within( () => {
            cy.get( '[data-tbl-field="name"]' ).first().contains( "adipiscing" );
        } );

        cy.server();
        cy.route('/admin/entities/note?*').as("listViewAjax");

        cy.get( '.pager-form .glyphicon-step-forward' ).click();
        //cy.wait('@listViewAjax');

        cy.assertListViewResults( 10, 22 );
        cy.get( "tbody" ).within( () => {
            cy.get( '[data-tbl-field="name"]' ).first().contains( "ipsum" );
        } );

        cy.get( '[data-tbl-page-selector]' ).type( "3{enter}" );
//        cy.wait('@listViewAjax');

        cy.assertListViewResults( 2, 22 );
        cy.get( "tbody" ).within( () => {
            cy.get( '[data-tbl-field="name"]' ).first().contains( "sit" );
        } );
    } );
} );
