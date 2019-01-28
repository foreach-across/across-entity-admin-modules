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

describe( 'ControlAdapter - Datepicker', function () {

    before( function () {
        cy.visit( "/control-adapters" );
        cy.wait( 1000 );
    } );

    it( "element exists", function () {
        cy.get( "[data-bootstrapui-adapter-type='datetime']" ).should( "have.length", 1 );
    } );

    it( "check current value", function () {
        cy.get( "[data-bootstrapui-adapter-type='datetime']" )
                .then( ( datepicker ) => {
                    const adapter = datepicker.data( "bootstrapui-adapter" );
                    const exportFormat = datepicker.data( "bootstrapui-datetimepicker" ).exportFormat;
                    expect( adapter.getTarget() ).to.eq( datepicker[0] );
                    expect( adapter.getValue() ).to.have.length( 1 );
                    const currentValue = adapter.getValue()[0];
                    expect( currentValue ).to.have.property( 'label', "2019-01-23 00:00" );
                    expect( Cypress.moment( currentValue.value._d ).format( exportFormat ) ).to.eq( currentValue.label );
                } );
    } );

    it( "Modifying the value", function () {
        cy.get( "[data-bootstrapui-adapter-type='datetime']" )
                .then( ( datepicker ) => {
                    const adapter = datepicker.data( "bootstrapui-adapter" );
                    expect( adapter.getValue()[0] ).to.have.property( 'label', "2019-01-23 00:00" );
                    adapter.selectValue( '2019-01-25 13:00' );
                    expect( adapter.getValue()[0] ).to.have.property( 'label', "2019-01-25 13:00" );
                    adapter.reset();
                    expect( adapter.getValue()[0] ).to.have.property( 'label', "2019-01-23 00:00" );
                } );
    } );

    it( "has no underlying control adapters", function () {
        cy.get( "[data-bootstrapui-adapter-type='datetime']" )
                .then( ( datepicker ) => {
                    expect( datepicker.find( "[data-bootstrapui-adapter-type]" ).length ).to.eq( 0 );
                } );
    } );

    describe( "Event handling", function () {
        it( "Change event is fired on dp.change", function () {
            cy.get( "[data-bootstrapui-adapter-type='datetime']" )
                    .then( ( datepicker ) => {
                        const adapter = datepicker.data( "bootstrapui-adapter" );

                        const obj = {
                            handle( event, controlAdapter ) {
                                return controlAdapter;
                            }
                        };
                        const spy = cy.spy( obj, 'handle' );

                        datepicker.on( "bootstrapui.change", function ( event, controlAdapter ) {
                            obj.handle( event, controlAdapter );
                        } );

                        datepicker.trigger( 'dp.change' );

                        expect( spy ).to.be.calledOnce;
                        expect( spy ).to.have.returned( adapter );
                    } );
        } );
    } );
} );