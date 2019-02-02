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
 * Fetches the arguments for the control elements and transforms them to the specified {@code eqType}.
 *
 * @param eqType of the property
 * @param controlElements holding the values for the property
 * @param valueMap that holds the mapping of the value of the control element to its prettified version.
 * @returns {*} the query arguments.
 */
function getConditionValue( eqType, selectedValues ) {
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

function setCondition( controls, filterControl, reset = true ) {
  const property = $( controls ).data( "entity-query-property" );
  const adapter = $( controls ).data( "bootstrapui-adapter" );

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
    const conditionArg = getConditionValue( $( controls ).data( "entity-query-type" ), values );
    console.log( "args" );
    console.log( conditionArg );

    if ( !isNullOrUndefined( conditionArg ) ) {
      const operand = EntityQueryOps[$( controls ).data( "entity-query-operand" )];
      condition = new EntityQueryCondition( property, operand, conditionArg );
    }
  }

  filterControl.setPropertyCondition( property, condition, reset );
}

/**
 * Creates an EntityQueryControl for a given node.
 *
 * @param node to create a control for
 * @param control containing the value
 * @param filterControl to receive the condition from the control
 * @returns {boolean} true if a control has been made.
 */
export function createDefaultControl( node, control, filterControl ) {
  if ( !isEmptyArray( control ) ) {
    const eventName = $( node ).data( "entity-query-event" );
    setCondition( control, filterControl, false );
    const controlAdapter = $( node ).data( 'bootstrapui-adapter' );
    if ( controlAdapter ) {
      $( controlAdapter.getTarget() ).on( eventName, {"item": $( control ), "adapter": controlAdapter, "filter": filterControl}
        , event => setCondition( event.data.item, event.data.filter ) );
      return true;
    }
  }
  return false;
}
