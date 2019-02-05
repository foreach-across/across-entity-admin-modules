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

import * as EntityModule from './modules/EntityModule';

function makeExpandable( node: JQuery )
{
    let lastTarget;
    const fadeDuration = 200;
    const subscrTbl = $( 'table[data-tbl-entity-type]', node );
    const entityType = subscrTbl.data( 'tbl-entity-type' );
    const rows = subscrTbl.find( 'tbody tr[data-entity-id],tbody tr[data-summary-url]' );
    rows.click( ( evt: JQueryEventObject ) => {
        if ( $( evt.target ).closest( 'a' ).length > 0 ) {
            // bubble edit button
            return true;
        }
        const target = $( this );
        if ( target.next().attr( 'data-summary' ) !== undefined ) {
            target.toggleClass( 'summary-expanded' );
            target.next().fadeToggle( {
                duration: fadeDuration, complete: () => {
                    lastTarget = target;
                },
            } );
        }
        else {
            const newTr = $( '<tr data-summary style="display:none"></tr>' );
            const newTd = $( '<td colspan=' + $( this ).find( 'td' ).length + '"></td>' );
            const entityId = $( this ).data( 'entity-id' );
            const summaryUrl = $( this ).data( 'summary-url' );
            newTd.appendTo( newTr );
            target.after( newTr );
            if ( summaryUrl ) {
                newTd.load( summaryUrl, null, () => {
                    target.toggleClass( 'summary-expanded' );
                    newTr.fadeIn( {duration: fadeDuration} );
                    lastTarget = target;
                } );
            }
            else if ( entityId ) {
                newTd.load( entityType + '/' + $( this ).attr( 'data-entity-id' ) + '?view=summary&_partial=content', null, () => {
                    target.toggleClass( 'summary-expanded' );
                    newTr.fadeIn( {duration: fadeDuration} );
                    lastTarget = target;
                } );
            }
        }
    } );
}

(<any>window).EntityModule.registerInitializer( makeExpandable );
