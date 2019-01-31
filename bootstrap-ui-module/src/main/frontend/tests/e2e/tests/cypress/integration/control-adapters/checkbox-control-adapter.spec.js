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

    const unwrappedElementFetcher = function ( selector ) {
        return cy.get( selector );
    };
    const wrappedElementFetcher = function ( selector ) {
        return cy.get( selector )
                .closest( '[data-bootstrapui-adapter-type="checkbox"]' )
    };

    const unwrappedCheckboxTests = function ( selector, label ) {
        it( "checking modifies the value", function () {
            cy.get( selector )
                    .should( 'not.be.checked' )
                    .then( ( wrapper ) => adapterUtils.assertAdapterNoValueSelected( wrapper ) )
                    .check().should( 'be.checked' )
                    .then( ( wrapper ) => {
                        adapterUtils.assertAdapterValueSelected( wrapper, 0, label, 'Yes' );

                        const adapter = adapterUtils.getAdapterForElement( wrapper );
                        adapter.selectValue( false );
                        adapterUtils.assertAdapterNoValueSelected( wrapper );

                        adapter.reset();
                        adapterUtils.assertAdapterNoValueSelected( wrapper );
                    } );
        } );
    };

    const wrappedCheckboxTests = function ( selector, label ) {
        it( "checking modifies the value", function () {
            cy.get( selector )
                    .should( 'not.be.checked' )
                    .closest( '[data-bootstrapui-adapter-type="checkbox"]' )
                    .then( ( wrapper ) => adapterUtils.assertAdapterNoValueSelected( wrapper ) )
                    .find( selector )
                    .check( {force: true} ).should( 'be.checked' )
                    .closest( '[data-bootstrapui-adapter-type="checkbox"]' )
                    .then( ( wrapper ) => {
                        adapterUtils.assertAdapterValueSelected( wrapper, 0, label, 'Yes' );

                        const adapter = adapterUtils.getAdapterForElement( wrapper );
                        adapter.selectValue( false );
                        adapterUtils.assertAdapterNoValueSelected( wrapper );

                        adapter.reset();
                        adapterUtils.assertAdapterNoValueSelected( wrapper );
                    } );
        } );
    };

    const checkboxTests = function ( selector, elementFetcher, label ) {
        afterEach( 'reset adapter', function () {
            console.log( elementFetcher );
            elementFetcher( selector )
                    .then( ( element ) => {
                        adapterUtils.getAdapterForElement( element ).reset();
                    } )
        } );

        // it( "adapter exists", function () {
        //     elementFetcher( selector )
        //             .then( element => {
        //                 expect( element.data( 'bootstrapui-adapter' ) ).to.not.be.undefined;
        //             } );
        // } );
        //
        // it( "has no underlying control adapters", function () {
        //     elementFetcher( selector )
        //             .then( ( wrapper ) => {
        //                 expect( wrapper.find( "[data-bootstrapui-adapter-type]" ).length ).to.eq( 0 );
        //             } );
        // } );
        //
        // it( 'value is empty array if checkbox is not selected', function () {
        //     elementFetcher( selector )
        //             .then( ( wrapper ) => {
        //                 adapterUtils.assertAdapterNoValueSelected( wrapper );
        //             } );
        // } );

        it( "getValue holds label, value, and checkbox if selected", function () {
            elementFetcher( selector )
                    .then( ( wrapper ) => {
                        const contextElement = wrapper.is( 'input[type=checkbox], input[type=radio]' )
                                ? wrapper : wrapper.find( 'input[type=checkbox],input[type=radio]' );
                        const adapter = adapterUtils.getAdapterForElement( wrapper );
                        adapter.selectValue( true );
                        adapterUtils.assertAdapterValueSelected( wrapper, 0, label, 'Yes', contextElement[0] );
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

    describe( 'Checkbox tests', function () {
        describe( 'checkbox', function () {
            checkboxTests( '#ca-checkbox', wrappedElementFetcher, 'Alive' );
            wrappedCheckboxTests( '#ca-checkbox', 'Alive' );
        } );

        describe( 'unwrapped checkbox', function () {
            checkboxTests( '#ca-checkbox-unwrapped', wrappedElementFetcher, 'Alive' );
            wrappedCheckboxTests( '#ca-checkbox-unwrapped', 'Alive' );
        } );

        describe( 'unwrapped checkbox without label', function () {
            checkboxTests( '#ca-checkbox-unwrapped-no-label', unwrappedElementFetcher );
            unwrappedCheckboxTests( '#ca-checkbox-unwrapped-no-label' );
        } );

        describe( 'wrapped checkbox outside label', function () {
            checkboxTests( '#ca-checkbox-out-label', wrappedElementFetcher, 'Alive' );
            wrappedCheckboxTests( '#ca-checkbox-out-label', 'Alive' );
        } );
    } );

    describe( 'Radio tests', function () {
        describe( 'radio', function () {
            checkboxTests( '#ca-radio', wrappedElementFetcher, 'Alive' );
            wrappedCheckboxTests( '#ca-radio', 'Alive' );
        } );

        describe( 'unwrapped radio', function () {
            checkboxTests( '#ca-radio-unwrapped', wrappedElementFetcher, 'Alive' );
            wrappedCheckboxTests( '#ca-radio-unwrapped', 'Alive' );
        } );

        describe( 'unwrapped radio without label', function () {
            checkboxTests( '#ca-radio-unwrapped-no-label', unwrappedElementFetcher );
            unwrappedCheckboxTests( '#ca-radio-unwrapped-no-label' );
        } );

        describe( 'wrapped radio outside label', function () {
            checkboxTests( '#ca-radio-out-label', wrappedElementFetcher, 'Alive' );
            wrappedCheckboxTests( '#ca-radio-out-label', 'Alive' );
        } );
    } );
} );