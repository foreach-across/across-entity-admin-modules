/*
 * Copyright 2014 the original author or authors
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
 * @author Steven Gentens
 * @since 3.3.0
 */
import $ from "jquery";
import {EntityQueryOps} from "../../entity-query/entity-query-ops";
import EntityQueryCondition from "../../entity-query/entity-query-condition";
import EQValue from "../../entity-query/eq-value";
import EQString from "../../entity-query/eq-string";
import {convertToTypedValue, isEmptyArray, isNullOrUndefined} from "../../utilities";
import EQGroup from "../../entity-query/eq-group";

/**
 * Fetches the arguments for the control elements and transforms them for the specified {@code eqType}.
 *
 * @param eqType of the property
 * @param selectedValues for the property
 * @returns {*} the query arguments.
 */
function convertToEQType( eqType, selectedValues ) {
    const filteredValues = selectedValues.filter( val => val !== '' );
    if ( eqType === 'EQGroup' ) {
        return new EQGroup( filteredValues.map( convertToTypedValue ) );
    }

    if ( filteredValues.length !== 0 ) {
        const converted = convertToTypedValue( filteredValues.toString() );
        if ( eqType === 'EQString' && converted instanceof EQValue ) {
            return new EQString( converted.getValue() );
        }
        return converted;
    }
    return null;
}

/**
 * Sets the entity query condition for a specific property.
 *
 * @param controlElement that holds the entity query attributes as well as the control adapter
 * @param filterControl that the condition should be applied on
 * @param reset whether the filter control should be reset.
 */
function setCondition( controlElement, filterControl, reset = true ) {
    const property = $( controlElement ).data( "entity-query-property" );
    const adapter = $( controlElement ).data( "bootstrapui-adapter" );

    const values = adapter.getValue()
            .map( ( selectedVal ) => {
                const context = $( selectedVal.context );
                if ( context.is( '[data-entity-query-pretty-value]' ) ) {
                    return context.data( 'entity-query-pretty-value' );
                }
                return selectedVal.value;
            } );

    let condition = null;
    if ( !isEmptyArray( values ) ) {
        const args = convertToEQType( $( controlElement ).data( "entity-query-type" ), values );

        if ( !isNullOrUndefined( args ) ) {
            const operand = EntityQueryOps[$( controlElement ).data( "entity-query-operand" )];
            condition = new EntityQueryCondition( property, operand, args );
        }
    }
    filterControl.setPropertyCondition( property, condition, reset );
}

/**
 * Creates an EntityQueryControl for a given node.
 *
 * @param control containing the value
 * @param filterControl to receive the condition from the control
 * @returns {boolean} true if a control has been made.
 */
export function createDefaultControl( control, filterControl ) {
    if ( !isEmptyArray( control ) ) {
        setCondition( control, filterControl, false );
        const controlAdapter = $( control ).data( 'bootstrapui-adapter' );
        if ( controlAdapter ) {
            $( controlAdapter.getTarget() ).on(
                    'bootstrapui.change',
                    {"item": $( control ), "filter": filterControl},
                    event => setCondition( event.data.item, event.data.filter )
            );
            return true;
        }
    }
    return false;
}
