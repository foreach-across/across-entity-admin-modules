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
var BootstrapUiModule = {
    /**
     * Scan for and initialize all known form element types.
     *
     * @param node optional parent to limit the scan
     */
    initializeFormElements: function ( node ) {
        /**
         * Find and activate all date time pickers.
         */
        $( '.js-form-datetimepicker', node ).each( function () {
            var configuration = $( this ).data( 'datetimepicker' );
            var exportFormat = configuration.exportFormat;

            delete configuration.exportFormat;

            $( this ).datetimepicker( configuration )
                    .on( 'dp.change', function ( e ) {
                             var exchangeValue = e.date ? moment( e.date ).format( exportFormat ) : '';
                             $( 'input[type=hidden]', $( this ) ).attr( 'value', exchangeValue );
                         } );
        } );
    }
};

$( document ).ready( function () {
    BootstrapUiModule.initializeFormElements();
} );