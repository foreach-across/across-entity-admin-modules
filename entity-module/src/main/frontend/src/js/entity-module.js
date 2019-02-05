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
import {AxiosInstance as axios} from 'axios';

var EmbeddedCollection = function( element ) {
    var wrapper = element;

    var filter = function( results ) {
        return results.filter( function( ix ) {
            return $( this ).closest( '[data-item-format]' ).is( wrapper );
        } );
    };

    var items = filter( wrapper.find( '[data-role=items]' ) ).first();
    var editItemTemplate = filter( wrapper.find( '[data-role=edit-item-template]' ) ).first();

    var templatePrefix = editItemTemplate.attr( 'data-template-prefix' );
    var nextItemIndex = editItemTemplate.data( 'next-item-index' );
    var targetPrefix = wrapper.attr( 'data-item-format' );

    var parentItem = wrapper.closest( '[data-item-prefix]' );

    if ( parentItem.length ) {
        var parentPrefix = parentItem.attr( 'data-item-prefix' );
        var parentTemplate = parentItem.closest( '[data-item-format]' ).attr( 'data-item-format' );
        targetPrefix = targetPrefix.replace( parentTemplate, parentPrefix );
    }

    filter( items.find( '[data-action=remove-item]' ) )
        .click( function( e ) {
            e.preventDefault();
            $( this ).closest( '[data-role=item]' ).remove();
        } );

    filter( wrapper.find( '[data-action=add-item]' ) )
        .click( function() {
            var sortIndex = nextItemIndex++;
            var id = 'item-' + sortIndex;
            var target = targetPrefix.replace( '{{key}}', id );

            var template = $( BootstrapUiModule.refTarget( editItemTemplate ).html() );
            template.attr( 'data-item-key', id );
            template.attr( 'data-item-prefix', target );

            template.find( '[name^="' + templatePrefix + '"]' ).each( function( node ) {
                $( this ).attr( 'name', $( this ).attr( 'name' ).replace( templatePrefix, target ) );
            } );
            template.find( '[name^="_' + templatePrefix + '"]' ).each( function( node ) {
                $( this ).attr( 'name', $( this ).attr( 'name' ).replace( '_' + templatePrefix, '_' + target ) );
            } );
            template.find( '[for^="' + templatePrefix + '"]' ).each( function( node ) {
                $( this ).attr( 'for', $( this ).attr( 'for' ).replace( templatePrefix, target ) );
            } );
            template.find( '[id^="' + templatePrefix + '"]' ).each( function( node ) {
                $( this ).attr( 'id', $( this ).attr( 'id' ).replace( templatePrefix, target ) );
            } );

            template.find( '[data-action=remove-item]' ).click( function( e ) {
                e.preventDefault();
                $( this ).closest( '[data-role=item]' ).remove();
            } );

            template.find( '[name="' + target + '.sortIndex"]' )
                .attr( 'value', sortIndex );

            items.append( template );

            EntityModule.initializeFormElements( template );
        } );
};

// expose global var
var EntityModule = (function( $ ) {

    /**
     * Initializes each container marked by 'data-entity-query-filter' as an EntityQueryFilterControl
     * Registers base property control resolvers for control types.
     * @see initializeEntityQueryForm
     */
    EntityModule.registerInitializer( function ( node ) {
        EntityQueryPropertyControlFactory.register( createSelectControl, 1000 );
        EntityQueryPropertyControlFactory.register( createDateControl, 1033 );
        EntityQueryPropertyControlFactory.register( createCheckboxRadioControl, 1066 );
        EntityQueryPropertyControlFactory.register( createTextControl, 1100 );

        $( '[data-entity-query-filter-form]' ).each( function () {
            initializeEntityQueryForm( $( this ) );
        } );
    } );

    var entityModule = {
        documentInitialized: false,
        initializers: [],

        /**
         * Register an additional initializer that should execute when running initializeFormElements.
         * An initializer is a callback function that will optionally receive the container node as argument.
         *
         * @param callback function to execute
         * @param callIfAlreadyInitialized should the initializer execute immediately if document has been initialized already - defaults to true
         */
        registerInitializer: function( callback, callIfAlreadyInitialized ) {
            this.initializers.push( callback );

            var shouldExecute = (callIfAlreadyInitialized === undefined || true == callIfAlreadyInitialized) && this.documentInitialized;

            if ( shouldExecute ) {
                callback();
            }
        },

        /**
         * Scan for and initialize all known form element types.
         * This will in turn call BootstrapUiModule.initializeFormElements() if called at any time past the initial document initialization.
         *
         * @param node optional parent to limit the scan
         */
        initializeFormElements: function( node ) {
            // Dispatch to BootstrapUiModule directly if initial document initialization has happened
            if ( (node !== undefined || this.documentInitialized) && BootstrapUiModule && BootstrapUiModule.initializeFormElements ) {
                BootstrapUiModule.initializeFormElements( node );
            }

            if ( node === undefined && !this.documentInitialized ) {
                this.documentInitialized = true;
            }

            /**
             * Initialize sortable tables.
             */
            $( '[data-tbl-type="paged"]', node ).emSortableTable();

            /**
             * Initialize depends-on conditions between controls.
             */
            $( '[data-dependson]', node ).each( function() {
                var dependsonConfig = $( this ).data( 'dependson' );
                var options = dependsonConfig['options'] != null ? dependsonConfig['options'] : {hide: false};
                delete dependsonConfig['options'];

                $( this ).dependsOn( dependsonConfig, options );
            } );

            /**
             * Initialize multi value controls.
             */
            $( '.js-multi-value-control', node ).each( function() {
                var container = $( this );

                container.find( '.js-multi-value-input' ).on( 'keypress', function( e ) {
                    var keyCode = (e.keyCode ? e.keyCode : e.which);
                    if ( keyCode == 13 ) {
                        e.preventDefault();
                        var value = $( this ).val();
                        if ( value ) {
                            var template = container.find( '.js-multi-value-template' ).clone( false );
                            template.removeClass( 'hidden js-multi-value-template' );
                            template.addClass( 'js-multi-value-item' );

                            template.find( '.js-multi-value-value' ).each( function( i, node ) {
                                node.innerText = value;
                            } );

                            template.find( '[type=hidden]' ).val( value ).removeAttr( 'disabled' );
                            container.find( 'table' ).append( template );

                            template.find( 'a' ).on( 'click', function() {
                                $( this ).closest( 'tr' ).remove();
                            } );

                            $( this ).val( '' );
                        }
                    }
                } );

                container.find( '.js-multi-value-item a' ).on( 'click', function() {
                    $( this ).closest( 'tr' ).remove();
                } )
            } );

            /**
             * Experimental: initialize embedded collections.
             */
            $( '.js-embedded-collection-form-group', node ).each( function() {
                new EmbeddedCollection( $( this ) );
            } );

            /**
             * Initialize tooltips.
             */
            $( '[data-toggle="tooltip"]', node ).tooltip();

            // Dispatch to additional initializers
            for ( var i = 0; i < this.initializers.length; i++ ) {
                this.initializers[i]( node );
            }
        }
    };

    $( document ).ready( function() {
        entityModule.initializeFormElements();
    } );

    return entityModule;
}( jQuery ));
