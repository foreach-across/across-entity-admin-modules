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

import {EntityModule} from '../modules/EntityModule';

export class EmbeddedCollection
{
    private bootstrapUiModule: any;
    private entityModule: any;

    constructor( element: JQuery )
    {
        this.bootstrapUiModule = (<any>window).BootstrapUiModule;
        this.entityModule = (<any>window).EntityModule;
        const wrapper = element;

        const filter = ( results: any ) => {
            return results.filter( ( ix: number ) => {
                return $( this ).closest( '[data-item-format]' ).is( wrapper );
            } );
        };

        const items = filter( wrapper.find( '[data-role=items]' ) ).first();
        const editItemTemplate = filter( wrapper.find( '[data-role=edit-item-template]' ) ).first();

        const templatePrefix = editItemTemplate.attr( 'data-template-prefix' );
        let nextItemIndex = editItemTemplate.data( 'next-item-index' );
        let targetPrefix = wrapper.attr( 'data-item-format' );

        const parentItem = wrapper.closest( '[data-item-prefix]' );

        if ( parentItem.length ) {
            const parentPrefix = parentItem.attr( 'data-item-prefix' );
            const parentTemplate = parentItem.closest( '[data-item-format]' ).attr( 'data-item-format' );
            targetPrefix = targetPrefix.replace( parentTemplate, parentPrefix );
        }

        filter( items.find( '[data-action=remove-item]' ) )
            .click( ( e: JQueryEventObject ) => {
                e.preventDefault();
                $( this ).closest( '[data-role=item]' ).remove();
            } );

        filter( wrapper.find( '[data-action=add-item]' ) )
            .click( () => {
                const sortIndex = nextItemIndex++;
                const id = 'item-' + sortIndex;
                const target = targetPrefix.replace( '{{key}}', id );

                const template = $( this.bootstrapUiModule.refTarget( editItemTemplate ).html() );
                template.attr( 'data-item-key', id );
                template.attr( 'data-item-prefix', target );

                template.find( '[name^="' + templatePrefix + '"]' ).each( ( node: any ) => {
                    $( this ).attr( 'name', $( this ).attr( 'name' ).replace( templatePrefix, target ) );
                } );
                template.find( '[name^="_' + templatePrefix + '"]' ).each( ( node: any ) => {
                    $( this ).attr( 'name', $( this ).attr( 'name' ).replace( '_' + templatePrefix, '_' + target ) );
                } );
                template.find( '[for^="' + templatePrefix + '"]' ).each( ( node: any ) => {
                    $( this ).attr( 'for', $( this ).attr( 'for' ).replace( templatePrefix, target ) );
                } );
                template.find( '[id^="' + templatePrefix + '"]' ).each( ( node: any ) => {
                    $( this ).attr( 'id', $( this ).attr( 'id' ).replace( templatePrefix, target ) );
                } );

                template.find( '[data-action=remove-item]' ).click( ( e: JQueryEventObject ) => {
                    e.preventDefault();
                    $( this ).closest( '[data-role=item]' ).remove();
                } );

                template.find( '[name="' + target + '.sortIndex"]' )
                    .attr( 'value', sortIndex );

                items.append( template );

                this.entityModule.initializeFormElements( template );
            } );
    }

}
