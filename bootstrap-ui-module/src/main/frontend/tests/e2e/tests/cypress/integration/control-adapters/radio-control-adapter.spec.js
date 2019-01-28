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

describe( 'ControlAdapter - Radio', function () {

    before( function () {
        cy.visit( "/control-adapters" );
        cy.wait( 1000 );
    } );

    it( "element exists", function () {
        cy.get( "#ca-radio" ).closest( "[data-bootstrapui-adapter-type]" )
                .then( element => {
                    expect( element.data( 'bootstrapui-adapter-type' ) ).to.be.eq( "checkbox" );
                    expect( element.data( 'bootstrapui-adapter' ) ).to.not.be.undefined;
                } );
    } );

    it( "current value returns label if selected", function () {
        cy.get( "#ca-radio" ).closest( "[data-bootstrapui-adapter-type]" )
                .then( ( wrapper ) => {
                    const adapter = wrapper.data( "bootstrapui-adapter" );
                    expect( adapter.getTarget() ).to.eq( wrapper.find( '#ca-radio' )[0] );

                    expect( adapter.getValue() ).to.be.empty;

                    adapter.selectValue( true );
                    const currentValues = adapter.getValue();
                    expect( currentValues ).to.have.length( 1 );
                    const currentValue = currentValues[0];
                    expect( currentValue ).to.have.property( 'label', 'Once you go ... You can\'t go back!' );
                    expect( currentValue ).to.have.property( 'value', 'Male' );

                    adapter.reset();
                } );
    } );

    it( "modifying value", function () {
        cy.get( "#ca-radio" ).closest( "[data-bootstrapui-adapter-type]" )
                .then( ( wrapper ) => {
                    const adapter = wrapper.data( "bootstrapui-adapter" );
                    expect( adapter.getValue() ).to.be.empty;

                    adapter.selectValue( true );
                    expect( adapter.getValue()[0] ).to.have.property( 'value', 'Male' );

                    adapter.selectValue( false );
                    expect( adapter.getValue() ).to.be.empty;

                    adapter.reset();
                    expect( adapter.getValue()[0] ).to.be.empty;
                } );
    } );

    it( "has no underlying control adapters", function () {
        cy.get( "#ca-radio" ).closest( "[data-bootstrapui-adapter-type]" )
                .then( ( wrapper ) => {
                    expect( wrapper.find( "[data-bootstrapui-adapter-type]" ).length ).to.eq( 0 );
                } );
    } );

    it( "bootstrapui.change event is fired on change", function () {
        cy.get( "#ca-radio" ).closest( "[data-bootstrapui-adapter-type]" )
                .then( ( wrapper ) => {
                    const adapter = wrapper.data( "bootstrapui-adapter" );

                    const obj = {
                        handle( controlAdapter ) {
                            return controlAdapter;
                        }
                    };
                    const spy = cy.spy( obj, 'handle' );

                    wrapper.on( "bootstrapui.change", function ( event, controlAdapter ) {
                        obj.handle( controlAdapter );
                    } );

                    wrapper.find( "#ca-radio" ).trigger( 'change' );

                    expect( spy ).to.have.callCount( 1 );
                    expect( spy ).to.have.returned( adapter );
                } );
    } );

} );