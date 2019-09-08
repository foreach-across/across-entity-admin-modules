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

describe( 'ControlAdapter - Datepicker', function () {
    const selector = "[data-bootstrapui-adapter-type='datetime']";
    const initialFormattedDate = '2019-01-23 00:00';

    before( function () {
        cy.visit( "/utilities/control-adapters" );
    } );

    it( "adapter exists", function () {
        adapterUtils.assertThatAdapterExists( selector );
    } );

    it( "value holds the formatted date (label), hidden input date (value) and datetimepicker (context)", function () {
        cy.get( selector )
                .then( ( datepicker ) => {
                    const value = datepicker.find( 'input[type=hidden]' ).val();
                    adapterUtils.assertAdapterValueSelected( datepicker, 0, initialFormattedDate, value, datepicker[0] );
                } );
    } );

    it( "modifying the value", function () {
        cy.get( selector )
                .then( ( datepicker ) => {
                    const adapter = datepicker.data( "bootstrapui-adapter" );
                    adapterUtils.assertAdapterValueSelected( datepicker, 0, initialFormattedDate );

                    adapter.selectValue( '2019-01-25 13:00' );
                    adapterUtils.assertAdapterHoldsAmountOfValues( datepicker, 1 );
                    adapterUtils.assertAdapterValueSelected( datepicker, 0, "2019-01-25 13:00", "2019-01-25 13:00" );

                    adapter.reset();
                    adapterUtils.assertAdapterHoldsAmountOfValues( datepicker, 1 );
                    adapterUtils.assertAdapterValueSelected( datepicker, 0, initialFormattedDate, initialFormattedDate );
                } );
    } );

    it( "has no underlying control adapters", function () {
        adapterUtils.assertHasUnderlyingControlAdapters( selector, 0 );
    } );

    it( "bootstrapui.change event is fired on dp.change", function () {
        cy.get( selector )
                .then( ( datepicker ) => {
                    adapterUtils.assertThatBootstrapUiChangeIsTriggeredOn( datepicker, 'dp.change' );
                } );
    } );
} );
