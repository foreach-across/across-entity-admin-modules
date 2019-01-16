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

import DatePickerControlAdapter from './date-picker-control-adapter';
import BootstrapUiAttributes from '../support/bootstrap-ui-attributes';
// @ts-ignore
import * as moment from 'moment';

/**
 * Find and activate all date time pickers as <a href="https://github.com/Eonasdan/bootstrap-datetimepicker">Eonasdan Bootstrap datepicker</a> elements.
 */
function datePickerInitializer( node: any ): void {
    $( '[data-bootstrapui-datetimepicker]', node ).each( function () {
        const configuration = $( this ).data( 'bootstrapui-datetimepicker' );
        const exportFormat = configuration.exportFormat;

        delete configuration.exportFormat;

        $( this ).datetimepicker( configuration )
            .on( 'dp.change', function ( e: any ) {
                const exchangeValue = e.date ? moment( e.date ).format( exportFormat ) : '';
                $( 'input[type=hidden]', $( this ) ).attr( 'value', exchangeValue );
            } );

        const controlAdapter = new DatePickerControlAdapter( $( this ), exportFormat );
        $( this ).data( BootstrapUiAttributes.CONTROL_ADAPTER, controlAdapter );
    } );
}

export default datePickerInitializer;
