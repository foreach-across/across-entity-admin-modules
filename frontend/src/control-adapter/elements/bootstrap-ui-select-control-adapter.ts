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

import BootstrapUiControlAdapter from "../bootstrap-ui-control-adapter";
import BootstrapUiControlValueHolder, {createControlValueHolder} from "../bootstrap-ui-control-value-holder";
import BootstrapUiControlEvent from "../bootstrap-ui-control-event";

export default class BootstrapUiSelectControlAdapter extends BootstrapUiControlAdapter
{
    private initialValue: any;

    constructor( target: any )
    {
        super( target );
        console.log( target );
        this.initialValue = $( this.getTarget() ).val();
        $( this ).on( 'changed.bs.select', event => this.triggerChange() );
        $( this ).on( BootstrapUiControlEvent.CHANGE, ( event, adapter ) => {
            console.log( `${BootstrapUiControlEvent.CHANGE} was triggered, received:` );
            console.log( {event, adapter} );
            console.log( adapter.getValue() );
        } )
    }

    getValue(): BootstrapUiControlValueHolder
    {
        return createControlValueHolder( '', $( this.getTarget() ).val(), '' );
    }

    reset(): void
    {
        this.selectValue( this.initialValue );
    }

    selectValue( obj: any ): void
    {
        $( this.getTarget() ).val( obj );
    }

}