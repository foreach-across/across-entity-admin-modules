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

describe( 'ControlAdapter - Textbox', function () {
    before( function () {
        cy.visit( "/control-adapters" );
    } );

    describe( 'textbox', function () {
        const selector = '#ca-textbox';
        const content = 'now this is a story all about how';

        afterEach( 'reset adapter', function () {
            cy.get( selector )
                    .then( ( element ) => {
                        adapterUtils.getAdapterForElement( element ).reset();
                    } )
        } );
        it( 'adapter exists', function () {
            adapterUtils.assertThatAdapterExists( selector );
        } );

        it( 'does not have underlying control adapters', function () {
            adapterUtils.assertHasUnderlyingControlAdapters( selector, 0 );
        } );

        it( "value holds the textbox and it's content as label and value", function () {
            cy.get( selector )
                    .type( content )
                    .then( ( element ) => {
                        adapterUtils.assertAdapterValueSelected( element, 0, content, content, element[0] );
                    } );
        } );

        it( 'modifying value', function () {
            cy.get( selector )
                    .then( ( element ) => adapterUtils.assertAdapterValueSelected( element, 0, '' ) )
                    .type( content )
                    .then( ( element ) => adapterUtils.assertAdapterValueSelected( element, 0, content ) );
        } );

        it( 'reset elements initial value', function () {
            cy.get( selector )
                    .type( content )
                    .then( ( element ) => {
                        adapterUtils.assertAdapterValueSelected( element, 0, content );
                        adapterUtils.getAdapterForElement( element ).reset();
                        adapterUtils.assertAdapterValueSelected( element, 0, '' );
                    } )
        } );

        it( 'bootstrapui.change is fired when a change event is emitted on the element', function () {
            cy.get( selector )
                    .then( ( element ) => {
                        adapterUtils.assertThatBootstrapUiChangeIsTriggeredOn( element, 'change' );
                    } )
        } );
    } );

    describe( 'autosizing textbox', function () {
        const selector = '#ca-textbox-autosize';
        const content = "my life got flipped turned upside down and I'd like to take a minute just sit right there";

        afterEach( 'reset adapter', function () {
            cy.get( selector )
                    .then( ( element ) => {
                        adapterUtils.getAdapterForElement( element ).reset();
                    } )
        } );
        it( 'adapter exists', function () {
            adapterUtils.assertThatAdapterExists( selector );
        } );

        it( 'does not have underlying control adapters', function () {
            adapterUtils.assertHasUnderlyingControlAdapters( selector, 0 );
        } );

        it( 'value holds option label, option value and option item', function () {
            cy.get( selector )
                    .type( content )
                    .then( ( element ) => {
                        adapterUtils.assertAdapterValueSelected( element, 0, content, content, element[0] );
                    } );
        } );

        it( 'modifying value', function () {
            cy.get( selector )
                    .then( ( element ) => adapterUtils.assertAdapterValueSelected( element, 0, '' ) )
                    .type( content )
                    .then( ( element ) => adapterUtils.assertAdapterValueSelected( element, 0, content ) );
        } );

        it( 'reset elements initial value', function () {
            cy.get( selector )
                    .type( content )
                    .then( ( element ) => {
                        adapterUtils.assertAdapterValueSelected( element, 0, content );
                        adapterUtils.getAdapterForElement( element ).reset();
                        adapterUtils.assertAdapterValueSelected( element, 0, '' );
                    } )
        } );

        it( 'bootstrapui.change is fired when a change event is emitted on the element', function () {
            cy.get( selector )
                    .then( ( element ) => {
                        adapterUtils.assertThatBootstrapUiChangeIsTriggeredOn( element, 'change' );
                    } )
        } );
    } );

    describe( 'textarea', function () {
        const selector = '#ca-textarea';
        const content = "I'll tell you how I become the prince of a town called Bel Air\n\n\n\n\n!!";

        afterEach( 'reset adapter', function () {
            cy.get( selector )
                    .then( ( element ) => {
                        adapterUtils.getAdapterForElement( element ).reset();
                    } )
        } );

        it( 'adapter exists', function () {
            adapterUtils.assertThatAdapterExists( selector );
        } );

        it( 'does not have underlying control adapters', function () {
            adapterUtils.assertHasUnderlyingControlAdapters( selector, 0 );
        } );

        it( 'value holds option label, option value and option item', function () {
            cy.get( selector )
                    .type( content )
                    .then( ( element ) => {
                        adapterUtils.assertAdapterValueSelected( element, 0, content, content, element[0] );
                    } );
        } );

        it( 'modifying value', function () {
            cy.get( selector )
                    .then( ( element ) => adapterUtils.assertAdapterValueSelected( element, 0, '' ) )
                    .type( content )
                    .then( ( element ) => adapterUtils.assertAdapterValueSelected( element, 0, content ) );
        } );

        it( 'reset elements initial value', function () {
            cy.get( selector )
                    .type( content )
                    .then( ( element ) => {
                        adapterUtils.assertAdapterValueSelected( element, 0, content );
                        adapterUtils.getAdapterForElement( element ).reset();
                        adapterUtils.assertAdapterValueSelected( element, 0, '' );
                    } )
        } );

        it( 'bootstrapui.change is fired when a change event is emitted on the element', function () {
            cy.get( selector )
                    .then( ( element ) => {
                        adapterUtils.assertThatBootstrapUiChangeIsTriggeredOn( element, 'change' );
                    } )
        } );
    } );
} );