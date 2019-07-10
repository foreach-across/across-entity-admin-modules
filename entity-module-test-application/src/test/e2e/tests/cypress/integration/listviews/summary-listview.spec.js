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

describe( 'List view: summary views', () => {

    beforeEach( function() {
        cy.login( 'admin' );
    } );

    it( 'Clicking on the rows opens up the summary view', () => {
        cy.goToMenuItem( "User" );

        cy.get('[data-ax-dev-view-element="formGroup-name"] > .control-label').should("not.be.visible");
        cy.get('[data-summary-url]').first().click();

        cy.get('[data-ax-dev-view-element="formGroup-name"] > .control-label').should("be.visible");
    } );
} );
