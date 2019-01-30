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

describe( 'ControlAdapter - Autosuggest', function () {
    const selector = '[data-bootstrapui-adapter-type="autosuggest"]';
    const selectAutosuggestItem = function ( selector ) {
        return cy.get( selector )
                .find( '.js-typeahead.tt-input' )
                .type( 'AAA' )
                .wait( 400 )
                .type( '{downarrow}' )
                .type( '{enter}' )
                .closest( selector )
    };

    before( function () {
        cy.visit( "/control-adapters" );
    } );

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

    it( "value holds the label of the value and the value as well as both input elements as its context", function () {
        selectAutosuggestItem( selector )
                .then( ( element ) => {
                    adapterUtils.assertAdapterValueSelected( element, 0, 'AAAlabel', '1' );
                    const adapter = adapterUtils.getAdapterForElement( element );
                    expect( adapter.getValue()[0].context ).to.have.property( 'typeahead', Cypress.$( '.js-typeahead.tt-input', element )[0] );
                    expect( adapter.getValue()[0].context ).to.have.property( 'typeaheadValue', Cypress.$( '.js-typeahead-value', element )[0] );
                } );
    } );

    it( 'modifying value', function () {
        const label = 'Jadajada';
        const value = 123;

        cy.get( selector )
                .then( ( element ) => {
                    adapterUtils.assertAdapterValueSelected( element, 0, '', '' );
                    const adapter = adapterUtils.getAdapterForElement( element );

                    adapter.selectValue( {label, value} );
                    adapterUtils.assertAdapterValueSelected( element, 0, label, value.toString() )
                } )
                .find( '.js-typeahead.tt-input' )
                .then( ( labelInput ) => {
                    expect( labelInput ).to.have.value( label );
                } )
                .closest( selector ).find( '.js-typeahead-value' )
                .then( ( valueInput ) => {
                    expect( valueInput ).to.have.value( value.toString() );
                } );
    } );

    it( 'reset elements initial value', function () {
        selectAutosuggestItem( selector )
                .then( ( element ) => {
                    adapterUtils.assertAdapterValueSelected( element, 0, 'AAAlabel', '1' );
                    const adapter = adapterUtils.getAdapterForElement( element );
                    adapter.reset();
                    adapterUtils.assertAdapterValueSelected( element, 0, '', '' );
                } );
    } );

    it( 'bootstrapui.change is fired when a typeahead:change event is emitted', function () {
        cy.get( selector )
                .then( ( element ) => {
                    adapterUtils.assertThatBootstrapUiChangeIsTriggeredOn( element, 'typeahead:change' );
                } );
    } );

    it( 'bootstrapui.change is fired when a typeahead:open event is emitted', function () {
        cy.get( selector )
                .then( ( element ) => {
                    adapterUtils.assertThatBootstrapUiChangeIsTriggeredOn( element, 'typeahead:open' );
                } );
    } );

    it( 'bootstrapui.change is fired when a typeahead:select event is emitted', function () {
        const obj = {
            handleChange( controlAdapter ) {
                return controlAdapter;
            },
            handleSelect() {

            }
        };
        const changeEventSpy = cy.spy( obj, 'handleChange' );
        const selectEventSpy = cy.spy( obj, 'handleSelect' );

        cy.get( selector )
                .then( ( element ) => {
                    element.on( "bootstrapui.change", function ( event, controlAdapter ) {
                        obj.handleChange( controlAdapter );
                    } );

                    const adapter = adapterUtils.getAdapterForElement( element );
                    let targetNode;
                    if ( element.is( adapter.getTarget() ) ) {
                        targetNode = element;
                    }
                    else {
                        targetNode = element.find( adapter.getTarget() );
                    }

                    targetNode.on( 'typeahead:select', () => {
                        obj.handleSelect();
                    } )
                } )
                .find( '.js-typeahead.tt-input' )
                .type( 'AAA' )
                .wait( 400 )
                .type( '{downarrow}' )
                .type( '{enter}' )
                .closest( selector )
                .then( ( element ) => {
                    const adapter = adapterUtils.getAdapterForElement( element );
                    expect( changeEventSpy ).to.have.callCount( 1 );
                    expect( changeEventSpy ).to.have.returned( adapter );
                    expect( selectEventSpy ).to.have.callCount( 1 );
                } );
    } );

    it( 'value is deselected as soon as search starts', function () {
        const obj = {
            handle( label, value ) {
                return {label, value};
            },
        };
        const spy = cy.spy( obj, 'handle' );

        selectAutosuggestItem( selector )
                .then( ( element ) => {
                    adapterUtils.assertAdapterValueSelected( element, 0, 'AAAlabel', '1' );

                    element.on( "bootstrapui.change", function ( event, controlAdapter ) {
                        const currentValue = controlAdapter.getValue()[0];
                        obj.handle( currentValue.label, currentValue.value );
                    } );
                } )
                .find( '.js-typeahead.tt-input' )
                .type( '{backspace}' )
                .then( ( element ) => {
                    expect( spy ).to.have.callCount( 1 );
                    expect( spy ).to.have.returned( {label: 'AAAlabe', value: ''} );
                } )
    } )
} );