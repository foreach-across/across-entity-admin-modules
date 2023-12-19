/*
 * Copyright 2019 the original author or authors
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

import adapterUtils from "../../support/utils/control-adapters";

describe( 'ControlAdapter - Numeric', function () {
    const selector = '#ca-numeric';
    const content = '7234.23';
    const label = '$7,234.23';

    before( function () {
        cy.visit( "/utilities/control-adapters" );
    } );

    afterEach( 'reset adapter', function () {
        cy.get( selector )
                .clear()
                .then( ( element ) => {
                    console.log( element.data() );
                    adapterUtils.getAdapterForElement( element ).reset();
                } )
                .closest( 'div.mb-3' )
                .click();
    } );

    it( 'adapter exists', function () {
        adapterUtils.assertThatAdapterExists( selector );
    } );

    it( 'does not have underlying control adapters', function () {
        adapterUtils.assertHasUnderlyingControlAdapters( selector, 0 );
    } );

    it( "value holds the formatted value, value and the textbox", function () {
        cy.get( selector )
                .type( content )
                .then( ( element ) => {
                    adapterUtils.assertAdapterValueSelected( element, 0, label, content, element[0] );
                } );
    } );

    it( 'modifying value', function () {
        cy.get( selector )
                .then( ( element ) => adapterUtils.assertAdapterValueSelected( element, 0, '', '' ) )
                .type( content )
                .then( ( element ) => adapterUtils.assertAdapterValueSelected( element, 0, label, content ) );
    } );

    it( 'adapter reset removes currency descriptor', function () {
        cy.get( selector )
                .then( ( element ) => adapterUtils.assertAdapterHoldsAmountOfValues( element, 1 ) )
                .type( content )
                .then( ( element ) => {
                    adapterUtils.assertAdapterHoldsAmountOfValues( element, 1 );
                    adapterUtils.assertAdapterValueSelected( element, 0, label, content );
                    adapterUtils.getAdapterForElement( element ).reset();
                    adapterUtils.assertAdapterHoldsAmountOfValues( element, 1 );
                    adapterUtils.assertAdapterValueSelected( element, 0, '', '' );
                } );

    } );

    it( 'bootstrapui.change is fired when a change event is emitted on the element', function () {
        cy.get( selector )
                .then( ( element ) => {
                    adapterUtils.assertThatBootstrapUiChangeIsTriggeredOn( element, 'change' );
                } )
    } );
} );
