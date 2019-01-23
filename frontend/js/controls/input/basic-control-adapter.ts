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
import BaseControlAdapter from '../support/base-control-adapter';
import BootstrapUiControlValueHolder, {createControlValueHolder} from '../support/bootstrap-ui-control-value-holder';
import BootstrapUiControlAdapter from '../support/bootstrap-ui-control-adapter';

/**
 * {@link BootstrapUiControlAdapter} for basic html elements.
 * This adapter sets and retrieves the value using jquery's val method.
 *
 * @see datePickerInitializer
 */
export default class BasicControlAdapter extends BaseControlAdapter
{
    private readonly initialValue: any;

    constructor( target: any ) {
        super( target );
        this.initialValue = $( this.getTarget() ).val();

        $( target ).on( 'change', event => this.triggerChange() );

        // prevent opening the element on enter, but see it as 'submitting' the value instead.
        $( target ).keypress( this, ( event ) => {
            if ( event.key === 'Enter' ) {
                this.triggerSubmit();
            }
        } );
    }

    getValue(): BootstrapUiControlValueHolder[] {
        const value: any = $( this.getTarget() ).val();
        return [createControlValueHolder( value, value, this.getTarget() )];
    }

    reset(): void {
        this.selectValue( this.initialValue );
    }

    selectValue( newValue: any ): void {
        $( this.getTarget() ).val( newValue );
    }
}

/**
 * Initializes a {@link BasicControlAdapter} for a given node.
 *
 * @param node to initialize
 */
export function createBasicControlAdapter( node: any ): BootstrapUiControlAdapter {
    return new BasicControlAdapter( node );
}
