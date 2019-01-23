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
import SelectControlAdapter from './select-control-adapter';
import BootstrapUiControlAdapter from '../support/bootstrap-ui-control-adapter';

/**
 * {@link BootstrapUiControlAdapter} for <a href="https://silviomoreto.github.io/bootstrap-select/">bootstrap-select</a> elements.
 */
export default class BootstrapSelectControlAdapter extends SelectControlAdapter
{
    constructor( target: any ) {
        super( target );
    }

    public initializeEventTriggers(): void {
        $( this.getTarget() ).on( 'changed.bs.select', event => this.triggerChange() );

        // prevent opening the element on enter, but see it as 'submitting' the value instead.
        $( this.getTarget() ).closest( '.bootstrap-select' )
            .find( 'button' )
            .keypress( this, ( event ) => {
                if ( event.key === 'Enter' ) {
                    // event.preventDefault();
                    this.triggerSubmit();
                }
            } );
    }

    public selectValue( newValue: any ): void {
        $( this.getTarget() ).selectpicker( 'val', newValue );
    }
}

/**
 * Initializes a {@link BootstrapSelectControlAdapter} for a given node.
 *
 * @param node to initialize
 */
export function createBootstrapSelectControlAdapter( node: any ): BootstrapUiControlAdapter {
    return new BootstrapSelectControlAdapter( node );
}
