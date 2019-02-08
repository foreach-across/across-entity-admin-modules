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

    it( 'Listview contains 3 partners', () => {
        cy.goToMenuItem( "Partner" );

        cy.assertListViewResults(3);
    } );

    it( 'After filtering we only see google', () => {
        cy.goToMenuItem( "Partner" );

        cy.get('#partner-name-filter')
                .clear()
                .type("Google");

        cy.get(".form > .btn").click();

        cy.get("tbody").within(() => {
            cy.get('[data-tbl-field="name"]').first().contains("Google");
        });

        cy.assertListViewResults(1);
    } );

    it( 'After filtering nonExistant we get no results', () => {
        cy.goToMenuItem( "Partner" );

        cy.get('#partner-name-filter')
                .clear()
                .type("nonExistant");

        cy.get(".form > .btn").click();

        cy.get('[data-ax-dev-view-element="itemsTable-noresults"]').contains("No partners found");
    } );
} );