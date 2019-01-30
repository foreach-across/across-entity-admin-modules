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

describe( 'ControlAdapter - Checkbox', function () {

    const defaultElementFetcher = function ( selector ) {
        return cy.get( selector );
    };
    const wrappedElementFetcher = function ( selector ) {
        return cy.get( selector )
                .closest( '[data-bootstrapui-adapter-type="checkbox"]' )
    };

    const checkboxTests = function ( selector, elementFetcher, label ) {
        afterEach( 'reset adapter', function () {
            elementFetcher( selector )
                    .then( ( element ) => {
                        console.log( element );
                        console.log( element.data() );
                        adapterUtils.getAdapterForElement( element ).reset();
                    } )
        } );

        it( "adapter exists", function () {
            elementFetcher( selector )
                    .then( element => {
                        expect( element.data( 'bootstrapui-adapter' ) ).to.not.be.undefined;
                    } );
        } );

        it( "getValue holds label, value, and checkbox if selected", function () {
            elementFetcher( selector )
                    .then( ( wrapper ) => {
                        const contextElement = wrapper.is( 'input[type=checkbox]' ) ? wrapper : wrapper.find( 'input[type=checkbox]' );
                        const adapter = adapterUtils.getAdapterForElement( wrapper );
                        adapter.selectValue( true );
                        adapterUtils.assertAdapterValueSelected( wrapper, 0, label, 'Yes', contextElement[0] );
                    } );
        } );

        it( 'value is empty array if checkbox is not selected', function () {
            elementFetcher( selector )
                    .then( ( wrapper ) => {
                        adapterUtils.assertAdapterNoValueSelected( wrapper );
                    } );
        } );

        it( "modifying value", function () {
            elementFetcher( selector )
                    .then( ( wrapper ) => {
                        const adapter = adapterUtils.getAdapterForElement( wrapper );
                        adapterUtils.assertAdapterNoValueSelected( wrapper );

                        adapter.selectValue( true );
                        adapterUtils.assertAdapterValueSelected( wrapper, 0, label, 'Yes' );

                        adapter.selectValue( false );
                        adapterUtils.assertAdapterNoValueSelected( wrapper );

                        adapter.reset();
                        adapterUtils.assertAdapterNoValueSelected( wrapper );
                    } );
        } );

        it( "has no underlying control adapters", function () {
            elementFetcher( selector )
                    .then( ( wrapper ) => {
                        expect( wrapper.find( "[data-bootstrapui-adapter-type]" ).length ).to.eq( 0 );
                    } );
        } );

        it( "bootstrapui.change event is fired on change", function () {
            elementFetcher( selector )
                    .then( ( wrapper ) => {
                        const adapter = adapterUtils.getAdapterForElement( wrapper );
                        adapter.selectValue( true );
                        adapterUtils.assertAdapterValueSelected( wrapper, 0, label, 'Yes' );
                    } );
        } );
    };

    before( function () {
        cy.visit( "/control-adapters" );
    } );

    describe( 'checkbox', function () {
        checkboxTests( '#ca-checkbox', wrappedElementFetcher, 'Alive' );
    } );

    describe( 'unwrapped checkbox', function () {
        checkboxTests( '#ca-checkbox-unwrapped', wrappedElementFetcher, 'Alive' );
    } );

    describe( 'unwrapped checkbox without label', function () {
        checkboxTests( '#ca-checkbox-unwrapped-no-label', defaultElementFetcher );
    } );

    describe( 'wrapped checkbox outside label', function () {
        checkboxTests( '#ca-checkbox-out-label', wrappedElementFetcher, 'Alive' )
    } );

} );