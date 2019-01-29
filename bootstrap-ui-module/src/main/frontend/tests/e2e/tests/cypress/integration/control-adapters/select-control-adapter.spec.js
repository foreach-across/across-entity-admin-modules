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

import adapterUtils from '../../support/utils/control-adapters';

describe( 'ControlAdapter - Select', function () {
    before( function () {
        cy.visit( "/control-adapters" );
    } );

    describe( 'single select', function () {
        it( 'adapter exists', function () {
            cy.get( '#ca-select' )
                    .then( ( select ) => {
                        expect( select.data( 'bootstrapui-adapter' ) ).to.not.be.undefined;
                    } );
        } );

        it( 'does not have underlying control adapters', function () {
            cy.get( '#ca-select' )
                    .then( ( select ) => {
                        expect( Cypress.$( '[data-bootstrapui-adapter-type]', select ) ).to.have.property( 'length', 0 );
                    } );
        } );

        it( 'value holds option label, option value and option item', function () {
            cy.get( '#ca-select' )
                    .then( ( select ) => {
                        const adapter = select.data( 'bootstrapui-adapter' );
                        const currentValues = adapter.getValue();
                        expect( currentValues ).to.have.length( 1 );
                        const currentValue = currentValues[0];
                        expect( currentValue ).to.have.property( 'label', 'One' );
                        expect( currentValue ).to.have.property( 'value', '1' );
                        expect( currentValue ).to.have.property( 'context', Cypress.$( 'option:selected', select )[0] );
                    } );
        } );

        it( 'modifying value', function () {
            cy.get( '#ca-select' )
                    .then( ( select ) => {
                        const adapter = select.data( 'bootstrapui-adapter' );
                        let currentValues = adapter.getValue();
                        expect( currentValues ).to.have.length( 1 );
                        let currentValue = currentValues[0];
                        expect( currentValue ).to.have.property( 'label', 'One' );
                        expect( currentValue ).to.have.property( 'value', '1' );

                        adapter.selectValue( 2 );
                        currentValues = adapter.getValue();
                        expect( currentValues ).to.have.length( 1 );
                        currentValue = currentValues[0];
                        expect( currentValue ).to.have.property( 'label', 'Two' );
                        expect( currentValue ).to.have.property( 'value', '2' );
                    } );
        } );

        it( 'reset selects initial value', function () {
            cy.get( '#ca-select' )
                    .select( 'Three' )
                    .then( ( select ) => {
                        const adapter = select.data( 'bootstrapui-adapter' );
                        let currentValues = adapter.getValue();
                        expect( currentValues ).to.have.length( 1 );
                        let currentValue = currentValues[0];
                        expect( currentValue ).to.have.property( 'label', '3' );
                        expect( currentValue ).to.have.property( 'value', 'Three' );

                        adapter.reset();

                        currentValues = adapter.getValue();
                        expect( currentValues ).to.have.length( 1 );
                        currentValue = currentValues[0];
                        expect( currentValue ).to.have.property( 'label', 'One' );
                        expect( currentValue ).to.have.property( 'value', '1' );

                    } )
        } );

        it( 'bootstrapui.change is fired when a change event is emitted on the select', function () {
            cy.get( '#ca-select' )
                    .then( ( select ) => {
                        const adapter = select.data( "bootstrapui-adapter" );

                        const obj = {
                            handle( controlAdapter ) {
                                return controlAdapter;
                            }
                        };
                        const spy = cy.spy( obj, 'handle' );

                        select.on( "bootstrapui.change", function ( event, controlAdapter ) {
                            obj.handle( controlAdapter );
                        } );

                        Cypress.$( '#ca-select' ).trigger( 'change' );
                        select.trigger( 'change' );

                        expect( spy ).to.have.callCount( 1 );
                        expect( spy ).to.have.returned( adapter );
                    } )
        } );
    } );

    describe( 'multi select', function () {
        it( 'adapter exists', function () {
            adapterUtils.assertThatAdapterExists( '#ca-multi-select' );

        } );

        it( 'does not have underlying control adapters', function () {
            adapterUtils.assertHasUnderlyingControlAdapters( '#ca-multi-select', 0 );
        } );

        it( 'value holds option label, option value and option item', function () {
            cy.get( '#ca-multi-select' )
                    .select( '2' )
                    .then( ( select ) => {
                        adapterUtils.assertAdapterValueSelected( select, 0, 'Two', '2', select.find( 'option:selected' )[0] );

                        const adapter = adapterUtils.getAdapterForElement( select );
                        adapter.reset();
                    } );
        } );

        it( 'modifying value', function () {
            cy.get( '#ca-multi-select' )
                    .then( ( select ) => adapterUtils.assertAdapterNoValueSelected( select ) )
                    .select( ['1', '2'] )
                    .then( ( select ) => {
                        adapterUtils.assertAdapterValueSelected( select, 0, 'One', '1' );
                        adapterUtils.assertAdapterValueSelected( select, 1, 'Two', '2' );
                    } );
        } );

        it( 'reset selects initial value', function () {
            cy.get( '#ca-multi-select' )
                    .select( ['2', 'Three'] )
                    .then( ( select ) => {
                        adapterUtils.assertAdapterValueSelected( select, 0, 'Two', '2' );
                        adapterUtils.assertAdapterValueSelected( select, 1, '3', 'Three' );

                        adapterUtils.getAdapterForElement( select ).reset();
                        adapterUtils.assertAdapterNoValueSelected( select );
                    } );
        } );

        it( 'bootstrapui.change is fired when a change event is emitted on the select', function () {
            cy.get( '#ca-select' )
                    .then( ( select ) => {
                        adapterUtils.assertThatBootstrapUiChangeIsTriggered( select, 'change' );
                    } )
        } );
    } );

    describe( 'bootstrap select', function () {

    } );

    describe( 'bootstrap multi select', function () {

    } );
} );
