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

describe( 'ControlAdapter - Container', function () {
    const selector = '#options-ca-multi-checkbox';

    before( function () {
        cy.visit( "/control-adapters" );
    } );

    it( "adapter exists", function () {
        adapterUtils.assertThatAdapterExists( selector );
    } );

    it( "has underlying control adapters", function () {
        adapterUtils.assertHasUnderlyingControlAdapters( selector, 3 );
    } );

    it( "modifying value throws an error", function () {
        cy.get( selector )
                .then( ( wrapper ) => {
                    const adapter = wrapper.data( "bootstrapui-adapter" );
                    expect( () => adapter.selectValue( "anything" ) ).to.throw( 'Selecting values is currently not support on ContainerControlAdapters.' );
                } );
    } );

    it( "bootstrapui.change event is fired if a child element is fired", function () {
        cy.get( selector )
                .then( ( wrapper ) => {
                    const adapter = wrapper.data( "bootstrapui-adapter" );

                    const obj = {
                        handle( controlAdapter ) {
                            return controlAdapter;
                        }
                    };
                    const spy = cy.spy( obj, 'handle' );

                    wrapper.on( "bootstrapui.change", function ( event, controlAdapter ) {
                        if ( event.target === adapter.getTarget() ) {
                            obj.handle( controlAdapter );
                        }
                    } );

                    wrapper.find( "[type=checkbox]" ).first().trigger( 'change' );

                    expect( spy ).to.have.callCount( 1 );
                    expect( spy ).to.have.returned( adapter );
                } );
    } );

    it( "reset applies reset on underlying control adapters", function () {
        cy.get( selector )
                .find( '[type=checkbox]' )
                .each( ( cb ) => {
                    cb.prop( 'checked', true );
                    expect( cb.is( ':checked' ) ).to.be.true;
                } )
                .closest( '[data-bootstrapui-adapter-type="container"]' )
                .then( ( container ) => {
                    const adapter = container.data( 'bootstrapui-adapter' );
                    expect( adapter.getValue() ).to.have.length( 3 );

                    adapter.reset();
                    expect( adapter.getValue() ).to.have.length( 0 );
                    expect( Cypress.$( ':checked', container ) ).to.have.property( 'length', 0 );
                } );
    } );

    it( "getValue holds values of underlying control adapters", function () {
        cy.get( selector )
                .find( '[type=checkbox]' )
                .each( ( cb, idx ) => {
                    if ( idx % 2 === 0 ) {
                        cb.prop( 'checked', true );
                        expect( cb.is( ':checked' ) ).to.be.true;
                    }
                } )
                .closest( '[data-bootstrapui-adapter-type="container"]' )
                .then( ( container ) => {
                    adapterUtils.assertAdapterValueSelected( container, 0, 'One', '1' );
                    adapterUtils.assertAdapterValueSelected( container, 1, '3', 'Three' );
                } );
    } )

} );