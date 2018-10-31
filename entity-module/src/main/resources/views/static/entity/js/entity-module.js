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
var EmbeddedCollection = function( element ) {
    var index = 0;

    var wrapper = element;

    var filter = function( results ) {
        return results.filter( function( ix ) {
            return $( this ).closest( '[data-item-format]' ).is( wrapper );
        } );
    };

    var items = filter( wrapper.find( '[data-role=items]' ) ).first();
    var editItemTemplate = filter( wrapper.find( '[data-role=edit-item-template]' ) ).first();

    var templatePrefix = editItemTemplate.attr( 'data-template-prefix' );
    var targetPrefix = wrapper.attr( 'data-item-format' );

    var parentItem = wrapper.closest( '[data-item-prefix]' );

    if ( parentItem.length ) {
        var parentPrefix = parentItem.attr( 'data-item-prefix' );
        var parentTemplate = parentItem.closest( '[data-item-format]' ).attr( 'data-item-format' );
        targetPrefix = targetPrefix.replace( parentTemplate, parentPrefix );
    }

    index = items.find( '.form-group' ).length;

    filter( items.find( '[data-action=remove-item]' ) )
            .click( function( e ) {
                e.preventDefault();
                $( this ).closest( '[data-role=item]' ).remove();
            } );

    filter( wrapper.find( '[data-action=add-item]' ) )
            .click( function() {
                var id = 'item-' + index++;
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
                        .attr( 'value', 10 + index );

                items.append( template );

                EntityModule.initializeFormElements( template );
            } );
};

// expose global var
var EntityModule = (function( $ ) {
    /**
     * Trigger this event with the page number as parameter.
     */
    var EVENT_MOVE_TO_PAGE = 'emSortableTable:moveToPage';

    /**
     * Trigger this event with the new field name to sort on as parameter.
     */
    var EVENT_SORT = 'emSortableTable:sort';

    /**
     * Subscribe to this event if you want to modify the parameters for loading the data.
     */
    var EVENT_PREPARE_DATA = 'emSortableTable:prepareData';

    /**
     * Subscribe to this event if you want to modify the parameters for loading the data,
     * or if you want to implement custom data loading and rendering.
     * Prevent the default event handling in the latter case.
     */
    var EVENT_LOAD_DATA = 'emSortableTable:loadData';

    var SortableTable = function( element ) {
        var table = $( element );
        var id = $( element ).attr( 'data-tbl' );
        var page = parseInt( table.attr( 'data-tbl-current-page' ) );
        this.formName = $( element ).attr( 'data-tbl-form' );

        this.size = parseInt( table.attr( 'data-tbl-size' ) );
        this.totalPages = parseInt( table.attr( 'data-tbl-total-pages' ) );

        var currentSort = table.data( 'tbl-sort' );
        this.sort = currentSort != null ? currentSort : [];

        this.sortables = $( "[data-tbl='" + id + "'][data-tbl-sort-property]", table );
        this.sortables.removeClass( 'asc', 'desc' );

        for ( var i = 0; i < this.sort.length; i++ ) {
            var order = this.sort[i];

            $( "[data-tbl='" + id + "'][data-tbl-sort-property='" + order.prop + "']", table )
                    .each( function() {
                        if ( i == 0 ) {
                            $( this ).addClass( order.dir == 'ASC' ? 'asc' : 'desc' );
                        }
                        order.prop = $( this ).data( 'tbl-sort-property' );
                    } );
        }

        var pager = this;

        table.on( EVENT_MOVE_TO_PAGE, function( event, pageNumber ) {
            pager.moveToPage( pageNumber );
        } );

        table.on( EVENT_SORT, function( event, propertyToSortOn ) {
            pager.sortOnProperty( propertyToSortOn );
        } );

        $( "[data-tbl='" + id + "'][data-tbl-page]" ).click( function( e ) {
            e.preventDefault();
            e.stopPropagation();

            table.trigger( EVENT_MOVE_TO_PAGE, parseInt( $( this ).attr( 'data-tbl-page' ) ) );
        } );

        $( "input[type='text'][data-tbl='" + id + "'][data-tbl-page-selector]" )
                .click( function( event ) {
                    event.preventDefault();
                    $( this ).select();
                } )
                .keypress( function( event ) {
                    var keyCode = (event.keyCode ? event.keyCode : event.which);
                    if ( keyCode == 13 ) {
                        event.preventDefault();
                        var pageNumber = parseInt( $( this ).val() );

                        if ( isNaN( pageNumber ) ) {
                            $( this ).addClass( 'has-error' );
                        }
                        else {
                            $( this ).removeClass( 'has-error' );
                            if ( pageNumber < 1 ) {
                                pageNumber = 1;
                            }
                            else if ( pageNumber > pager.totalPages ) {
                                pageNumber = pager.totalPages;
                            }
                            table.trigger( EVENT_MOVE_TO_PAGE, pageNumber - 1 );
                        }
                    }
                } );

        this.sortables.click( function( e ) {
            e.preventDefault();
            e.stopPropagation();
            table.trigger( EVENT_SORT, $( this ).data( 'tbl-sort-property' ) );
        } );

        jQuery.event.special[EVENT_LOAD_DATA] = {
            _default: function( event, params ) {
                // fallback to default loading of paged data
                pager.loadData( params );
            }
        };

        this.moveToPage = function( pageNumber ) {
            var params = {
                'page': pageNumber, 'size': this.size
            };

            if ( this.sort != null && this.sort.length > 0 ) {
                var sortProperties = [];

                for ( var i = 0; i < this.sort.length; i++ ) {
                    sortProperties.push( this.sort[i].prop + ',' + this.sort[i].dir );
                }

                params['sort'] = sortProperties;
            }

            table.trigger( EVENT_PREPARE_DATA, params );

            table.trigger( EVENT_LOAD_DATA, params );
        };

        this.loadData = function( params ) {
            if ( this.formName ) {
                var form = $( 'form[name=' + this.formName + ']' );

                var requireHiddenElement = function( name, value ) {
                    if ( value ) {
                        $( 'input[name=' + name + '][type=hidden]' ).remove();

                        var control = $( 'input[name=' + name + ']', form );
                        if ( control.length ) {
                            control.val( value );
                        }
                        else {
                            if ( $.isArray( value ) ) {
                                for ( var i = 0; i < value.length; i++ ) {
                                    form.append( '<input type="hidden" name="' + name + '" value="' + value[i] + '" />' );
                                }
                            }
                            else {
                                form.append( '<input type="hidden" name="' + name + '" value="' + value + '" />' );
                            }
                        }
                    }
                };

                $.each( params, function( paramName, paramValue ) {
                    requireHiddenElement( paramName, paramValue );
                } );

                form.submit();
            }
            else {
                var pathUrl = window.location.href.split( '?' )[0];
                window.location.href = pathUrl + '?' + $.param( params, true );
            }
        };

        this.sortOnProperty = function( propertyName ) {
            var currentIndex = -1;

            for ( var i = 0; i < this.sort.length && currentIndex < 0; i++ ) {
                if ( this.sort[i].prop == propertyName ) {
                    currentIndex = i;
                }
            }

            var order = {
                'prop': propertyName, 'dir': 'ASC'
            };

            if ( currentIndex > -1 ) {
                if ( currentIndex == 0 ) {
                    order.dir = this.sort[currentIndex].dir == 'ASC' ? 'DESC' : 'ASC';
                }

                if ( this.sort.length > 1 ) {
                    this.sort.splice( currentIndex, 1 );
                }
                else {
                    this.sort = [];
                }
            }

            this.sort = [order].concat( this.sort );

            this.moveToPage( page );
        };
    };

    /**
     * Expose JQuery plugin emSortableTable, creates a SortableTable when called.
     */
    $.fn.emSortableTable = function() {
        return this.each( function() {
            if ( !this._emSortableTable ) {
                this._emSortableTable = new SortableTable( this );
            }
        } );
    };

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