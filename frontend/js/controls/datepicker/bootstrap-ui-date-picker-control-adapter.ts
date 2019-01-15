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

import BootstrapUiControlAdapter from '../support/bootstrap-ui-control-adapter';
import BootstrapUiControlValueHolder, {createControlValueHolder} from '../support/bootstrap-ui-control-value-holder';
import * as $ from 'jquery';
import BootstrapUiControlEvent from '../support/bootstrap-ui-control-event';

export default class BootstrapUiDatePickerControlAdapter extends BootstrapUiControlAdapter
{
    private readonly exportFormat: string;
    private readonly initialValue: any;

    constructor( target: any, exportFormat: string ) {
        super( target );
        this.exportFormat = exportFormat;
        this.initialValue = this.getDateTimePicker().date();
        $( target ).on( 'dp.change', event => this.triggerChange() );
        $( target ).on( BootstrapUiControlEvent.CHANGE, ( event: JQueryEventObject, adapter: BootstrapUiControlAdapter ) => {
            console.log( `${BootstrapUiControlEvent.CHANGE} has triggered, received:` );
            console.log( {event, adapter} );
            console.log( adapter.getValue() );
        } );
    }

    getValue(): BootstrapUiControlValueHolder {
        const date = this.getDateTimePicker().date();
        const formattedValue = date.format( this.exportFormat );
        return createControlValueHolder( formattedValue, [date], this.getTarget() );
    }

    reset(): void {
        this.selectValue( this.initialValue );
    }

    selectValue( obj: any ): void {
        this.getDateTimePicker().date( obj );
    }

    private getDateTimePicker(): any {
        return this.getTarget().data( 'DateTimePicker' );
    }
}
