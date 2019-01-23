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

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
declare const Bloodhound: any;

function registerAutosuggestControl() {
    BootstrapUiModule.Controls['AutoSuggest'] = {
        /**
         * Create a Typeahead autosuggest instance from a node with a configuration object.
         */
        create( node: any, configuration: any ) {
            const typeahead: any = node.find( '.js-typeahead' );
            const selectedValue: any = node.find( '.js-typeahead-value' );
            const translateUrl = function ( url: any ) {
                return url.replace( '{{controlName}}', encodeURIComponent( selectedValue.attr( 'name' ) ) );
            };

            const createBloodhoundEngine = function ( configuration: any ) {
                const base = {
                    datumTokenizer: Bloodhound.tokenizers.whitespace, queryTokenizer: Bloodhound.tokenizers.whitespace, identify: 'id', remote: {
                        wildcard: '{{query}}',
                    },
                };

                const options = $.extend( true, base, configuration );
                if ( options.remote && options.remote.url ) {
                    options.remote.url = translateUrl( options.remote.url );
                }
                if ( options.prefetch && options.prefetch.url ) {
                    options.prefetch.url = translateUrl( options.prefetch.url );
                }

                const engine = new Bloodhound( options );
                engine.initialize();
                return engine;
            };

            // Build datasets - bloodhound engine + typeahead config
            const datasets = configuration._datasets;
            delete configuration._datasets;

            const ttDataSets: any[] = [];

            $.each( datasets, ( ix, value ) => {
                const engine = createBloodhoundEngine( value.bloodhound );
                delete value.bloodhound;

                const options = $.extend( {display: 'label'}, value );
                if ( engine ) {
                    options.source = engine.ttAdapter();
                }

                ttDataSets.push( options );
            } );

            // Initialize the typeahead
            typeahead.typeahead( configuration, ttDataSets );

            let selected: any;
            typeahead.on( 'typeahead:select', ( e: any, suggestion: any ) => {
                selected = suggestion;
                node.find( '.js-typeahead-value' ).val( suggestion['id'] );
            } );
            typeahead.on( 'typeahead:change', ( e: any, val: any ) => {
                if ( !selected || val !== selected['label'] ) {
                    typeahead.typeahead( 'val', '' );
                    node.find( '.js-typeahead-value' ).val( '' );
                }
            } );
        },
    };
}

/**
 * Find and activate all auto-suggest instances with Typeahead and bloodhound.
 */
function autosuggestInitializer( node: any ): void {
    $( '[data-bootstrapui-autosuggest]', node ).each( function () {
        const configuration = $( this ).data( 'bootstrapui-autosuggest' );
        BootstrapUiModule.Controls.AutoSuggest.create( $( this ), configuration );
    } );
}

export {registerAutosuggestControl};
export default autosuggestInitializer;
