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

// Exposes infrastructure for form initialization logic
window.BootstrapUiModule = (function ( $ ) {
    var bootstrapUiModule = {
        Controls: {},
        documentInitialized: false,
        initializers: [],

        /**
         * Register an additional initializer that should execute when running initializeFormElements.
         * An initializer is a callback function that will optionally receive the container node as argument.
         *
         * @param callback function to execute
         * @param callIfAlreadyInitialized should the initializer execute immediately if document has been initialized already - defaults to true
         */
        registerInitializer: function ( callback, callIfAlreadyInitialized ) {
            this.initializers.push( callback );

            var shouldExecute = (callIfAlreadyInitialized === undefined || true === callIfAlreadyInitialized) && this.documentInitialized;

            if ( shouldExecute ) {
                callback();
            }
        },

        /**
         * Run form element initializers.
         *
         * @param node optional parent to limit the scan
         */
        initializeFormElements: function ( node ) {
            if ( node === undefined && !this.documentInitialized ) {
                this.documentInitialized = true;
            }

            // Dispatch to initializers
            for ( var i = 0; i < this.initializers.length; i++ ) {
                this.initializers[i]( node );
            }
        },

        /**
         * Retrieve a the target node that the current node represents.
         * If the node passed in has a 'data-bum-ref-id' attribute,
         * it will be replaced by the element having the same id as the attribute value.
         *
         * @param node
         * @param recurse should the target in turn be checked for reference id
         */
        refTarget: function ( node, recurse ) {
            if ( node ) {
                var ref = this;
                return $( node ).map( function ( ix, n ) {
                    var candidate = $( n );
                    var targetId = candidate.attr( 'data-bum-ref-id' );
                    if ( targetId ) {
                        var target = $( '#' + targetId );
                        return recurse ? ref.refTarget( target, recurse ).get() : target.get();
                    }
                    return n;
                } )
            }
            return node;
        }
    };

    $( document ).ready( function () {
        bootstrapUiModule.initializeFormElements();
    } );

    return bootstrapUiModule;
}( jQuery ));
