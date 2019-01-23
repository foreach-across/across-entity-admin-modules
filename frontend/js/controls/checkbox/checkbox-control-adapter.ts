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
 * {@link BootstrapUiControlAdapter} for checkbox elements.
 */
export default class CheckboxControlAdapter extends BaseControlAdapter
{
    private readonly initialValue: boolean;

    constructor( target: any ) {
        const element: any = $( target ).find( 'input[type=checkbox], input[type=radio]' );
        super( element );
        this.initialValue = this.isSelected();
        $( element ).change( event => this.triggerChange() );

        // prevent opening the element on enter, but see it as 'submitting' the value instead.
        $( element ).keypress( this, ( event ) => {
            if ( event.key === 'Enter' ) {
                this.triggerSubmit();
            }
        } );
    }

    getValue(): BootstrapUiControlValueHolder[] {
        const target = $( this.getTarget() );
        if ( this.isSelected() ) {
            return [createControlValueHolder( target.parent().text(), target.attr( 'value' ), this.getTarget() )];
        }
        return [];
    }

    reset(): void {
        this.selectValue( this.initialValue );
    }

    selectValue( select: boolean ): void {
        $( this.getTarget() ).prop( 'checked', select );
    }

    isSelected(): boolean {
        return $( this.getTarget() ).is( ':checked' );
    }
}

/**
 * Initializes a {@link CheckboxControlAdapter} for a given node.
 *
 * @param node to initialize
 */
export function createCheckboxControlAdapter( node: any ): BootstrapUiControlAdapter {
    return new CheckboxControlAdapter( node );
}
