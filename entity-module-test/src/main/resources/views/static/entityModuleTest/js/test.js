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

console.log( "test javascript loaded" );

EntityModule.registerInitializer( function() {
    $( '[data-tbl-type="paged"]' )
            .on( "emSortableTable:prepareData", function( e, params ) {
                console.log( "enhancing the data", params );
                params['_partial'] = 'content';
            } )
            .on( "emSortableTable:loadData", function( e, params ) {
                console.log( "performing ajax load" );
                e.preventDefault();

                var allParams = {};
                $( 'form' ).serializeArray().map( function( x ) {
                    allParams[x.name] = x.value;
                } );

                allParams = $.extend( allParams, params );
                console.log( $.param( params, true ) );

                $.get( '#', $.param( allParams, true ), function( data ) {
                           var pcs = $( '.pcs' ).replaceWith( data );
                           EntityModule.initializeFormElements( $('.pcs') );
                       }
                );
            } )
} );
