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

function getPrettyValueMapping( control ) {
  const holder = new Map();
  if ( isEmptyArray( control ) ) {
    const _this = $( control );
    const prettyValue = _this.data( 'entity-query-pretty-value' );
    if ( prettyValue ) {
      holder.set( _this.val(), prettyValue );
    }
  }
  else {
    $( control ).each( function() {
      const _this = $( this );
      holder.set( _this.val(), _this.data( 'entity-query-pretty-value' ) );
    } );
  }
  return holder;
}

function prettify( value, valueMap ) {
  const trimmed = value.trim();
  if ( valueMap.has( trimmed ) ) {
    return valueMap.get( trimmed ).trim();
  }
  return trimmed;
}

/**
 * Fetches the values from the underlying control elements.
 * If multiple control elements are passed, the value of each element will be returned.
 * By specifying a {@code valueMap}, values can be converted to their prettified version.
 *
 * @param controlElements of which the value should be fetched
 * @param valueMap that holds the mapping for a value to it's prettified version
 * @returns {*} the actual value of the passed elements
 */
function getValuesFromControlElements( controlElements, valueMap ) {
  const values = $( controlElements ).val();
  if ( Array.isArray( values ) ) {
    return values.map( val => prettify( val, valueMap ) )
      .filter( val => val !== '' );
  }
  return prettify( values, valueMap );
}

/**
 * Fetches the arguments for the control elements and transforms them to the specified {@code eqType}.
 *
 * @param eqType of the property
 * @param controlElements holding the values for the property
 * @param valueMap that holds the mapping of the value of the control element to its prettified version.
 * @returns {*} the query arguments.
 */
function getConditionValue( eqType, controlElements, valueMap ) {
  const valuesFromControlElements = getValuesFromControlElements( controlElements, valueMap );
  if ( eqType === 'EQGroup' ) {
    return new EQGroup( valuesFromControlElements.map( convertToTypedValue ) );
  }

  if ( valuesFromControlElements.length !== 0 ) {
    const converted = convertToTypedValue( valuesFromControlElements.toString() );
    if ( eqType === 'EQString' && converted instanceof EQValue ) {
      return new EQString( converted.getValue() );
    }
    return converted;
  }
  return null;
}

function setCondition( controls, filterControl, reset = true ) {
  const property = $( controls ).data( "entity-query-property" );
  const value = $( controls ).val();
  const itemsWithPrettyValue = $( controls ).find( "[data-entity-query-pretty-value]" );
  const valueMap = getPrettyValueMapping( itemsWithPrettyValue );
  let condition = null;
  console.log( `Updating condition for property ${property}` );
  console.log( "values" );
  console.log( value );
  console.log( "valueMap" );
  console.log( valueMap );

  if ( (Array.isArray( value ) && !isEmptyArray( value )) || (value && value.trim() !== "") ) {
    const conditionArg = getConditionValue( $( controls ).data( "entity-query-type" ), controls, valueMap );
    console.log( "arguments" );
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
    $( node ).on( eventName, {"item": $( control ), "filter": filterControl},
                  event => setCondition( event.data.item, event.data.filter ) );
    console.log( "registered new property control" );
    return true;
  }
  return false;
}
