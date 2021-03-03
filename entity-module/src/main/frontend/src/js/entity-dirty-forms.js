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

import {embeddedCollectionHelper} from "./components/embedded-collection.dirty-forms";

function initializeDirtyForm( $node ) {
    $node.dirtyForms();
    $node.attr( "data-em-dirty-form-check-active", true );
}

function rescanDirtyForm( $node ) {
    $node.dirtyForms( 'rescan' );
}

$.DirtyForms.helpers.push( embeddedCollectionHelper );

window.EntityModule.registerInitializer( function( node ) {
    // activate dirty form checks
    $( '[data-em-dirty-form-check=true]', node )
            .each( function() {
                let $dirtyCheckingForm = $( this );
                initializeDirtyForm( $dirtyCheckingForm );
                embeddedCollectionHelper.init( $dirtyCheckingForm )
            } );

    // rescan when a node is updated within a dirty-form-check parent
    const $node = $( node );
    const $parents = $node.closest( '[data-em-dirty-form-check-active]' );
    if ( $parents.length > 0 ) {
        $parents.each( function() {
            rescanDirtyForm( $( this ) );
        } )
    }
} );
