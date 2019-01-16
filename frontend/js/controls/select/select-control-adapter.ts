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
import BootstrapUiControlEvent from '../support/bootstrap-ui-control-event';

/**
 * {@link BootstrapUiControlAdapter} for <a href="https://silviomoreto.github.io/bootstrap-select/">bootstrap-select</a> elements.
 */
export default class SelectControlAdapter extends BootstrapUiControlAdapter
{
    private initialValue: any;

    constructor( target: any ) {
        super( target );
        this.initialValue = $( this.getTarget() ).val();

        $( target ).on( 'changed.bs.select', event => this.triggerChange() );
        $( target ).on( BootstrapUiControlEvent.CHANGE, ( event, adapter ) => {
            console.log( `${BootstrapUiControlEvent.CHANGE} was triggered, received:` );
            console.log( {event, adapter} );
            console.log( adapter.getValue() );
        } );

        // enter event to open
        $( target ).closest( '.bootstrap-select' )
            .find( 'button' )
            .keypress( this, ( event ) => {
                event.preventDefault();
                this.triggerSubmit();
            } );
        $( target ).on( BootstrapUiControlEvent.SUBMIT, ( event, adapter ) => {
            console.log( `${BootstrapUiControlEvent.SUBMIT} was triggered, received:` );
            console.log( {event, adapter} );
            console.log( adapter.getValue() );
        } );
    }

    getValue(): BootstrapUiControlValueHolder {
        return createControlValueHolder( '', $( this.getTarget() ).val(), '' );
    }

    reset(): void {
        this.selectValue( this.initialValue );
    }

    selectValue( obj: any ): void {
        $( this.getTarget() ).val( obj );
    }

}
