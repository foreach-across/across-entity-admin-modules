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

import BootstrapUiDatePickerControlAdapter from "./controls/datepicker/bootstrap-ui-date-picker-control-adapter";
import BootstrapUiSelectControlAdapter from "./controls/select/bootstrap-ui-select-control-adapter";

(function ( $ ) {
            const ADAPTER_ATTR = "bootstrapui-adapter";

            BootstrapUiModule.Controls["AutoSuggest"] = {
                /**
                 * Create a Typeahead autosuggest instance from a node with a configuration object.
                 */
                create: function ( node, configuration ) {
                    var typeahead = node.find( '.js-typeahead' );
                    var selectedValue = node.find( '.js-typeahead-value' );

                    var translateUrl = function ( url ) {
                        return url.replace( '{{controlName}}', encodeURIComponent( selectedValue.attr( 'name' ) ) );
                    };

                    var createBloodhoundEngine = function ( configuration ) {
                        var base = {
                            datumTokenizer: Bloodhound.tokenizers.whitespace,
                            queryTokenizer: Bloodhound.tokenizers.whitespace,
                            identify: "id",
                            remote: {
                                wildcard: '{{query}}'
                            }
                        };

                        var options = $.extend( true, base, configuration );
                        if ( options.remote && options.remote.url ) {
                            options.remote.url = translateUrl( options.remote.url );
                        }
                        if ( options.prefetch && options.prefetch.url ) {
                            options.prefetch.url = translateUrl( options.prefetch.url );
                        }

                        var engine = new Bloodhound( options );
                        engine.initialize();
                        return engine;
                    };

                    // Build datasets - bloodhound engine + typeahead config
                    var datasets = configuration._datasets;
                    delete configuration._datasets;

                    var ttDataSets = [];

                    $.each( datasets, function ( ix, value ) {
                        var engine = createBloodhoundEngine( value.bloodhound );
                        delete value.bloodhound;

                        var options = $.extend( {display: 'label'}, value );
                        if ( engine ) {
                            options.source = engine.ttAdapter();
                        }

                        ttDataSets.push( options );
                    } );

                    // Initialize the typeahead
                    typeahead.typeahead( configuration, ttDataSets );

                    var selected;
                    typeahead.on( 'typeahead:select', function ( e, suggestion ) {
                        //console.log(e);
                        //console.log( 'selected: ' + suggestion["id"] );
                        selected = suggestion;
                        node.find( '.js-typeahead-value' ).val( suggestion["id"] );
                    } );
                    typeahead.on( 'typeahead:change', function ( e, val ) {
                        //console.log( 'changed: ' + val );
                        if ( !selected || val !== selected["label"] ) {
                            typeahead.typeahead( 'val', '' );
                            node.find( '.js-typeahead-value' ).val( '' );
                        }
                    } );
                }
            };
            /**
             * Main initialization of BoostrapUiModule form elements.
             */
            BootstrapUiModule.registerInitializer( function ( node ) {
                /**
                 * Find and activate all date time pickers.
                 */
                $( '[data-bootstrapui-datetimepicker]', node ).each( function () {
                    var configuration = $( this ).data( 'bootstrapui-datetimepicker' );
                    var exportFormat = configuration.exportFormat;

                    delete configuration.exportFormat;

                    $( this ).datetimepicker( configuration )
                            .on( 'dp.change', function ( e ) {
                                var exchangeValue = e.date ? moment( e.date ).format( exportFormat ) : '';
                                $( 'input[type=hidden]', $( this ) ).attr( 'value', exchangeValue );
                            } );

                    const controlAdapter = new BootstrapUiDatePickerControlAdapter( $( this ), exportFormat );
                    $( this ).data( ADAPTER_ATTR, controlAdapter );
                } );

                /**
                 * Find an activate all autoNumeric form elements.
                 */
                $( '[data-bootstrapui-numeric]', node ).each( function () {
                    var configuration = $( this ).data( 'bootstrapui-numeric' );
                    var name = $( this ).attr( 'name' );

                    var multiplier = configuration.multiplier ? configuration.multiplier : 1;

                    var multiplied;

                    if ( multiplier !== 1 ) {
                        var currentValue = $( this ).val();
                        if ( currentValue && !isNaN( currentValue ) ) {
                            multiplied = parseFloat( currentValue ) * multiplier;
                        }
                    }

                    $( this )
                            .autoNumeric( 'init', configuration )
                            .bind( 'blur focusout keypress keyup', function () {
                                if ( name.length > 1 && name[0] === '_' ) {
                                    var val = $( this ).autoNumeric( 'get' );

                                    if ( multiplier !== 1 ) {
                                        val = val / multiplier;
                                    }

                                    $( 'input[type=hidden][name="' + name.substring( 1 ) + '"]' ).val( val );
                                }
                            } );

                    if ( multiplied ) {
                        $( this ).autoNumeric( 'set', multiplied );
                    }
                } );

                /**
                 * Find and activate all autogrow textarea elements.
                 */
                autosize( $( '.js-autosize', node ) );

                /**
                 * Disable enter on all controls that specify it.
                 */
                $( '.js-disable-line-breaks', node ).on( 'keyup keypress', function ( e ) {
                    if ( e.which === 13 || e.which === 10 ) {
                        e.preventDefault();
                        return false;
                    }
                } );

                /**
                 * Disable resizing on textareas that do not allow line breaks.
                 */
                $( '.js-disable-line-breaks.js-autosize' ).css( 'resize', 'none' );

                /**
                 * Find and activate all bootstrap-select elements.
                 */
                $( '[data-bootstrapui-select]', node ).each( function () {
                    var configuration = $( this ).data( 'bootstrapui-select' );
                    $( this ).selectpicker( configuration );

                    const controlAdapter = new BootstrapUiSelectControlAdapter( $( this ) );
                    $( this ).data( ADAPTER_ATTR, controlAdapter );
                } );

                /**
                 * Find and activate all auto-suggest instances with Typeahead.
                 */
                $( '[data-bootstrapui-autosuggest]', node ).each( function () {
                    var configuration = $( this ).data( 'bootstrapui-autosuggest' );
                    BootstrapUiModule.Controls.AutoSuggest.create( $( this ), configuration );
                } );

                /**
                 * Initialize tooltips.
                 */
                $( '[data-toggle="tooltip"]', node ).tooltip();
            } );
        }( jQuery )
);



