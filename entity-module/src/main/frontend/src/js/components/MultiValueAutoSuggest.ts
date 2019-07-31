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
import * as $ from 'jquery';

export class MultiValueAutoSuggest
{
    private bootstrapUiModule: any;

    constructor( element: JQuery )
    {
        this.bootstrapUiModule = (<any>window).BootstrapUiModule;

        const items = element.find( '[data-role=items]' ).first();
        const editItemTemplate = element.find( '[data-role=edit-item-template]' ).first();

        if ( items.find( 'tr' ).length > 1 ) {
            items.removeClass( 'hidden' );
        }

        const triggerChange = () => {
            const controller = element.data( 'bootstrapui-adapter' ) as BootstrapUiControlAdapter;
            if ( controller ) {
                controller.triggerChange();
            }
        };

        const removeHandler = ( e: JQueryEventObject ) => {
            e.preventDefault();
            $( e.currentTarget ).closest( '[data-role=item]' ).remove();
            if ( items.find( 'tr' ).length === 1 ) {
                items.addClass( 'hidden' );
            }
            triggerChange();
        };

        items.find( '[data-action=remove-item]' ).click( removeHandler );

        const adapter = element.find( '[data-role=control]' ).first().data( 'bootstrapui-adapter' );
        $( adapter.getTarget() ).on( 'bootstrapui.change', ( e: JQueryEventObject, adapter: any ) => {
            e.stopPropagation();
            const selectedValue = adapter.getValue()[0];
            if ( selectedValue.value !== '' ) {
                const value = MultiValueAutoSuggest.escapeHtml( '' + selectedValue.value );

                if ( items.find( '[value="' + value + '"]' ).length === 0 ) {
                    const item = $( $( this.bootstrapUiModule.refTarget( editItemTemplate ) )
                                    .html()
                                    .replace( '{{id}}', value )
                                    .replace( '{{label}}', MultiValueAutoSuggest.escapeHtml( '' + selectedValue.label ) ) );
                    item.find( '[data-action=remove-item]' ).click( removeHandler );
                    items.append( item );
                    items.removeClass( 'hidden' );
                }
                adapter.reset();
                triggerChange();
            }
        } );
    }

    private static escapeHtml( value: string ): string
    {
        return value.replace( /&/g, '&amp;' )
                    .replace( /</g, '&lt;' )
                    .replace( />/g, '&gt;' )
                    .replace( /"/g, '&quot;' );
    }
}

/**
 * Control adapter implementation for a multi-value autosuggest control.
 * With limited support as it only allows fetching the values.
 */
export class MultiValueAutoSuggestControlAdapter implements BootstrapUiControlAdapter
{
    private items: JQuery;
    private target: any;

    constructor( node: any )
    {
        this.target = node;
        this.items = $( node ).find( '[data-role=items]' ).first();
    }

    getTarget(): any
    {
        return this.target;
    }

    getValue(): BootstrapUiControlValueHolder[]
    {
        return this.items.find( 'input[type=hidden]' )
                   .map( ( ix, hidden ) => {
                       const val = $( hidden ).val();
                       return {
                           label: val,
                           value: val,
                           context: hidden,
                       };
                   } ).toArray() as BootstrapUiControlValueHolder[];
    }

    reset(): void
    {
    }

    selectValue( newValue: any ): void
    {
    }

    triggerChange(): void
    {
        $( this.getTarget() ).trigger( 'bootstrapui.change', [this] );
    }

    triggerSubmit(): void
    {
        $( this.getTarget() ).trigger( 'bootstrapui.submit', [this] );
    }
}
