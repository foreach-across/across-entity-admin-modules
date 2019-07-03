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
import {createDefaultControl} from "./utils/controls/property-control-builders/property-control";
import {isNullOrUndefined} from "./utils/utilities";
import EntityQueryPropertyControlFactory from "./utils/controls/entity-query-property-control-factory";
import EntityQueryFilterControl from "./utils/controls/entity-query-filter-control";
import {EntityModule} from "./modules/EntityModule";
import {SortableTableEvent} from "./events/SortableTableEvent";

/**
 * Initializes an EntityQueryFilterControl for the given node.
 * @param node a container containing an eql filter and controls to filter on.
 */
function initializeEntityQueryForm( node ) {
  const nodes = $( node ).find( "[data-entity-query-property]" );
  const eqlFilter = $( node ).find( "input[name='extensions[eqFilter]']" );
  const entityQueryFilterControl = new EntityQueryFilterControl( nodes, eqlFilter );

  function findShowBasicFilterElement() {
    return $( node ).find( ".js-entity-query-filter-form-show-basic-filter" );
  }

  function toggleFilters( event ) {
    event.preventDefault();
    $( node ).find( ".entity-query-filter-form-basic" ).toggleClass( "hidden" );
    $( node ).find( ".entity-query-filter-form-advanced" ).toggleClass( "hidden" );

    let inputShowBasicFilter = findShowBasicFilterElement();
    if ( $( inputShowBasicFilter ).attr( "value" ) === "true" ) {
      $( inputShowBasicFilter ).attr( "value", false );
    }
    else {
      $( inputShowBasicFilter ).attr( "value", true );
    }
  }

  $( node ).find( "a[data-entity-query-filter-form-link]" ).click( toggleFilters );

  $( node ).find( "button[type=submit]" ).on( 'click', function( e ) {
    if ( $( findShowBasicFilterElement() ).attr( "value" ) === "true" ) {
      entityQueryFilterControl.resetEntityQuery();
    }
    $( this ).find( 'span' ).removeClass( 'glyphicon glyphicon-search' ).addClass( 'fa fa-spin fa-spinner' );
  } );

  // After submit ajax

  // $( node ).find( 'span' ).removeClass( 'fa fa-spin fa-spinner' ).addClass( 'glyphicon glyphicon-search' );
}

/**
 * Sets the EntityQueryPropertyControlFactory globally.
 */
if ( !isNullOrUndefined( window ) ) {
  window.EntityQueryPropertyControlFactory = EntityQueryPropertyControlFactory;
}

/** Initializes each container marked by "data-entity-query-filter" as an EntityQueryFilterControl
 * Registers base property control resolvers for control types.
 * @see initializeEntityQueryForm
 */
window.EntityModule.registerInitializer( function( node ) {
  EntityQueryPropertyControlFactory.register( createDefaultControl, 1000 );

  $( "[data-entity-query-filter-form]" ).each( function() {
    initializeEntityQueryForm( $( this ) );
  } );
} );
