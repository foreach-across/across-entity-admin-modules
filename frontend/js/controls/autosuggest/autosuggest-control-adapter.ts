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
import * as $ from 'jquery';
import BootstrapUiControlAdapter from '../support/bootstrap-ui-control-adapter';

interface AutosuggestValue
{
    label: any;
    value: any;
}

/**
 * {@link BootstrapUiControlAdapter} for <a href="https://github.com/twitter/typeahead.js">typeahead</a> autosuggest elements.
 *
 * @see autosuggestInitializer
 */
export default class AutosuggestControlAdapter extends BaseControlAdapter
{
    private readonly initialValue: AutosuggestValue;
    private readonly valueHolder: any;

    constructor( wrapper: any ) {
        const typeahead = $( wrapper ).find( '.js-typeahead.tt-input' );
        super( typeahead[0] );

        // triggering typeahead:change when input is cleared? currently only triggered on blur
        $( typeahead ).on( 'typeahead:change', event => this.triggerChange() );
        $( typeahead ).on( 'typeahead:select', event => this.triggerChange() );
        $( typeahead ).keypress( this, ( event ) => {
            if ( event.key === 'Enter' ) {
                // event.preventDefault();
                this.triggerSubmit();
            }
        } );

        const typeaheadValue = $( wrapper ).find( '.js-typeahead-value' );
        this.valueHolder = typeaheadValue[0];
        this.initialValue = {label: typeahead.typeahead( 'val' ), value: typeaheadValue.val()};
    }

    getValue(): BootstrapUiControlValueHolder[] {
        const label = $( this.getTarget() ).typeahead( 'val' );
        const value = $( this.valueHolder ).val();
        return [createControlValueHolder( label, value, {typeahead: this.getTarget(), typeaheadValue: this.valueHolder} )];
    }

    reset(): void {
        this.selectValue( this.initialValue );
    }

    selectValue( newValue: AutosuggestValue ): void {
        $( this.getTarget() ).typeahead( 'val', newValue.label );
        $( this.valueHolder ).val( newValue.value );
    }
}

/**
 * Initializes a {@link AutosuggestControlAdapter} for a given node.
 *
 * @param node to initialize
 */
export function createAutosuggestControlAdapter( node: any ): BootstrapUiControlAdapter {
    return new AutosuggestControlAdapter( node );
}
