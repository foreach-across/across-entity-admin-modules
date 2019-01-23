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

    it( "element exists", function () {
        cy.visit( "/control-adapters" );
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
    } )
} );