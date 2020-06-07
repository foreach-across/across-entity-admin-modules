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

/**
 * Registers utility methods that can be used as Cypress commands.
 *
 * @type {{getAdapterForElement: (function(*): *), getAdaptersOfType: (function(*): Cypress.Chainable<JQuery<HTMLElement>>), assertAdapterValueSelected: Cypress.Commands.ControlAdapter.assertAdapterValueSelected, assertAdapterNoValueSelected: Cypress.Commands.ControlAdapter.assertAdapterNoValueSelected}}
 */
export default {
    /**
     * Returns the adapter for a given jquery node that was selected via {@code cy}.
     *
     * @param jqueryNode the node for which to fetch the adapter.
     * @returns {*}
     */
    getAdapterForElement: function ( jqueryNode ) {
        return jqueryNode.data( 'bootstrapui-adapter' );
    },
    /**
     * Selects all elements of a given adapter type.
     *
     * @see cy#get
     * @param type
     * @returns {Cypress.Chainable<JQuery<HTMLElement>>}
     */
    getAdaptersOfType: function ( type ) {
        return cy.get( `[data-bootstrapui-adapter-type='${type}']` );
    },
    /**
     * Checks whether an adapter exists on a specific element.
     *
     * @param selector for the element to check
     */
    assertThatAdapterExists: function ( selector ) {
        cy.get( selector )
                .then( ( element ) => {
                    expect( element.data( 'bootstrapui-adapter' ) ).to.not.be.undefined;
                } );
    },
    /**
     * Checks whether an element has underlying control adapter elements.
     *
     * @param selector of the element to check
     * @param amountOfControlAdapters that should be present
     */
    assertHasUnderlyingControlAdapters: function ( selector, amountOfControlAdapters ) {
        cy.get( selector )
                .then( ( item ) => {
                    expect( item.find( '[data-bootstrapui-adapter-type]' ) ).to.have.property( 'length', amountOfControlAdapters || 0 );
                } );
    },
    /**
     * Validates whether an adapter holds a specific value.
     *
     * @param element jquery node on which the adapter is registered
     * @param index  of the value to check (optional - default 0)
     * @param label value that should be present as label (optional)
     * @param value that should be present as value (optional)
     * @param context node that defines the value (optional)
     */
    assertAdapterValueSelected: function ( element, index, label, value, context ) {
        const adapter = this.getAdapterForElement( element );
        const valueToCheck = adapter.getValue()[index || 0];
        if ( label !== undefined ) {
            expect( valueToCheck ).to.have.property( 'label', label );
        }
        if ( value !== undefined ) {
            expect( valueToCheck ).to.have.property( 'value', value );
        }
        if ( context !== undefined ) {
            expect( valueToCheck ).to.have.property( 'context', context );
        }
    },
    /**
     * Checks whether an element currently holds a certain amount of values.
     *
     * @param element on which the adapter is registered
     * @param amountOfValues to expect when calling {@link BootstrapUiControlAdapter#getValue}
     */
    assertAdapterHoldsAmountOfValues: function ( element, amountOfValues ) {
        const adapter = this.getAdapterForElement( element );
        expect( adapter.getValue() ).to.have.length( amountOfValues );
    },
    /**
     * Validates that the adapter holds no values.
     *
     * @param element jquery node on which the adapter is registered
     */
    assertAdapterNoValueSelected: function ( element ) {
        const adapter = this.getAdapterForElement( element );
        expect( adapter.getValue() ).to.have.length( 0 );
    },
    /**
     * Checks whether the {@code bootstrapui.change} event is triggered when the specified event is executed
     *
     * @param element that holds the adapter
     * @param event name of the event that should trigger {@code bootstrapui.change}
     * @returns {*}
     */
    assertThatBootstrapUiChangeIsTriggeredOn: function ( element, event ) {
        const adapter = this.getAdapterForElement( element );

        let targetNode;
        if ( element.is( adapter.getTarget() ) ) {
            targetNode = element;
        }
        else {
            targetNode = element.find( adapter.getTarget() );
        }

        const obj = {
            handle( controlAdapter ) {
                return controlAdapter;
            }
        };
        const spy = cy.spy( obj, 'handle' );

        element.on( "bootstrapui.change", function ( event, controlAdapter ) {
            obj.handle( controlAdapter );
        } );

        targetNode.trigger( event );

        expect( spy ).to.have.callCount( 1 );
        expect( spy ).to.have.returned( adapter );
    }

};
