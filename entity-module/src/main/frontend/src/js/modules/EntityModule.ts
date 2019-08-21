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
import {EmbeddedCollection} from '../components/EmbeddedCollection';

export class EntityModule
{
    private documentInitialized: boolean = false;
    private initializers: any = [];
    private bootstrapUiModule: any;

    constructor()
    {
        this.bootstrapUiModule = (<any>window).BootstrapUiModule;
    }

    /**
     * Register an additional initializer that should execute when running initializeFormElements.
     * An initializer is a callback function that will optionally receive the container node as argument.
     *
     * @param callback function to execute
     * @param callIfAlreadyInitialized should the initializer execute immediately if document has been initialized already - defaults to true
     */
    registerInitializer( callback: any, callIfAlreadyInitialized: any )
    {
        this.initializers.push( callback );

        const shouldExecute = (callIfAlreadyInitialized === undefined || true === callIfAlreadyInitialized) && this.documentInitialized;

        if ( shouldExecute ) {
            callback();
        }
    }

    /**
     * Scan for and initialize all known form element types.
     * This will in turn call BootstrapUiModule.initializeFormElements() if called at any time past the initial document initialization.
     *
     * @param node optional parent to limit the scan
     */
    initializeFormElements( node?: any )
    {
        // Dispatch to BootstrapUiModule directly if initial document initialization has happened
        if ( (node !== undefined || this.documentInitialized) && this.bootstrapUiModule && this.bootstrapUiModule.initializeFormElements ) {
            this.bootstrapUiModule.initializeFormElements( node );
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
        $( '[data-dependson]', node ).each( function (ix, n) {
            const dependsonConfig = $( this ).data( 'dependson' );
            const options = dependsonConfig['options'] != null ? dependsonConfig['options'] : {hide: false};
            delete dependsonConfig['options'];

            $( n ).dependsOn( dependsonConfig, options );
        } );

        /**
         * Initialize multi value controls.
         */
        $( '.js-multi-value-control', node ).each( ( ix, element ) => {
            const container = $( element );

            container.find( '.js-multi-value-input' ).on( 'keypress', ( e ) => {
                const keyCode = (e.keyCode ? e.keyCode : e.which);
                if ( keyCode === 13 ) {
                    e.preventDefault();
                    const value = $( e.currentTarget ).val();
                    if ( value ) {
                        const template = container.find( '.js-multi-value-template' ).clone( false );
                        template.removeClass( 'd-none js-multi-value-template' );
                        template.addClass( 'js-multi-value-item' );

                        template.find( '.js-multi-value-value' ).each( ( i: number, node: any ) => {
                            node.innerText = value;
                        } );

                        template.find( '[type=hidden]' ).val( value ).removeAttr( 'disabled' );
                        container.find( 'table' ).append( template );

                        template.find( 'a' ).on( 'click', ( clickEvent ) => {
                            clickEvent.preventDefault();
                            $( clickEvent.currentTarget ).closest( 'tr' ).remove();
                        } );

                        $( e.currentTarget ).val( '' );
                    }
                }
            } );

            container.find( '.js-multi-value-item a' ).on( 'click', ( clickEvent ) => {
                clickEvent.preventDefault();
                $( clickEvent.currentTarget ).closest( 'tr' ).remove();
            } );
        } );

        /**
         * Experimental: initialize embedded collections.
         */
        $( '.js-embedded-collection-form-group', node ).each( ( ix, n ) => new EmbeddedCollection( $( n ) ) );

        /**
         * Initialize tooltips.
         */
        $( '[data-toggle="tooltip"]', node ).tooltip();

        // Dispatch to additional initializers
        for ( let i = 0; i < this.initializers.length; i++ ) {
            this.initializers[i]( node );
        }
    }
}
