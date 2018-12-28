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
import {isEmptyArray} from "../../utilities";

function setCondition( controlItem, control, filterControl, reset = true ) {
  const property = $( controlItem ).data( "entity-query-property" );
  const value = $( control ).val();
  let condition = null;

  if ( value.trim() !== "" ) {
    const operand = EntityQueryOps[$( controlItem ).data( "entity-query-operand" )];
    const queryValue = $( controlItem ).data( "entity-query-text-type" ) === true ? new EQString( `${value}` ) : new EQValue( `${value}` );
    condition = new EntityQueryCondition( property, operand, queryValue );
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
export function createControl( node, control, filterControl ) {
  if ( !isEmptyArray( control ) ) {
    const eventName = $( node ).data( "entity-query-control-event" );
    setCondition( node, control, filterControl, false );
    $( node ).on( eventName, {"formGroup": $( node ), "item": $( control ), "filter": filterControl},
                  event => setCondition( event.data.formGroup, event.data.item, event.data.filter ) );
    return true;
  }
  return false;
}
