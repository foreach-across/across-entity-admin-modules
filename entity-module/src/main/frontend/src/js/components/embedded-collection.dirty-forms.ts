/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
interface DirtyFormsHelper
{
    init( $node: any ): void;

    isDirty( $node: any ): boolean;

    rescan( $node: any ): void;

    setClean( $node: any ): void;
}

const COLLECTION_SELECTORS = {
    COLLECTION: '[data-item-format]',
    ITEMS: '[data-role=items]',
    ITEM: '[data-role=item]',
    ADD_ITEM: '[data-action=add-item]',
    REMOVE_ITEM: '[data-action=remove-item]',
    ORIGINAL_VALUE_ATTR: 'dirty',
};

const DIRTY_FORMS = {
    DIRTY_CLASS: 'dirty',
    DIRTY_CHECKING_SELECTOR: '[data-em-dirty-form-check-active]',
};

function retrieveItemsForCollection( $collection: any )
{
    const $itemsWrapper = $collection.find( COLLECTION_SELECTORS.ITEMS )
                                     .filter( ( ix: number, element: any ) => $( element ).closest( COLLECTION_SELECTORS.COLLECTION ).is( $collection ) );
    return $itemsWrapper.children( COLLECTION_SELECTORS.ITEM );
}

class EmbeddedCollectionDirtyForms implements DirtyFormsHelper
{
    init( $node: any ): void
    {
        const $collections = $node.find( COLLECTION_SELECTORS.COLLECTION );
        $collections.toArray().some( ( wrapper: any ) => EmbeddedCollectionDirtyForms.isDirtyCollection( $( wrapper ) ) );

        $collections.each( ( idx: number, element: any ) => {
            this.checkDirtyOnAddOrRemoveItem( $( element ) );
        } );
    }

    checkDirtyOnAddOrRemoveItem( $collection: any )
    {
        $collection.on( 'click', ( event: Event ) => {
            const $target = $( event.target );
            const forAdd = $target.closest( COLLECTION_SELECTORS.ADD_ITEM );
            const forRemove = $target.closest( COLLECTION_SELECTORS.REMOVE_ITEM ).length !== 0;

            if ( forRemove || forAdd ) {
                const isDirty = EmbeddedCollectionDirtyForms.isDirtyCollection( $collection );
                const collectionMarkedDirty = $collection.hasClass( DIRTY_FORMS.DIRTY_CLASS );
                if ( isDirty && !collectionMarkedDirty ) {
                    $collection.addClass( DIRTY_FORMS.DIRTY_CLASS );
                }
                else if ( !isDirty && collectionMarkedDirty ) {
                    $collection.removeClass( DIRTY_FORMS.DIRTY_CLASS );
                }
                EmbeddedCollectionDirtyForms.setDirtyStatus( $collection, isDirty );
            }
        } );
    }

    isDirty( $node: any ): boolean
    {
        const $collections = $node.find( COLLECTION_SELECTORS.COLLECTION );
        if ( $collections.length !== 0 ) {
            return $collections.toArray().some( ( collection: any ) => EmbeddedCollectionDirtyForms.isDirtyCollection( $( collection ) ) );
        }
        return false;
    }

    static isDirtyCollection( $collection: any )
    {
        const $collectionItems = retrieveItemsForCollection( $collection );

        const existingValue = EmbeddedCollectionDirtyForms.getOriginalValue( $collection );
        if ( existingValue !== null && existingValue !== undefined ) {
            return !EmbeddedCollectionDirtyForms.checkIfStoredValueIsEqual( existingValue, $collectionItems );
        }
        EmbeddedCollectionDirtyForms.storeOriginalValue( $collection, $collectionItems );

        return false;
    }

    rescan( $node: any ): void
    {
    }

    setClean( $node: any ): void
    {
    }

    static storeOriginalValue( $collection: any, $collectionItems: any )
    {
        $collection.data( COLLECTION_SELECTORS.ORIGINAL_VALUE_ATTR, $collectionItems );
    }

    static getOriginalValue( $collection: any )
    {
        return $collection.data( COLLECTION_SELECTORS.ORIGINAL_VALUE_ATTR );
    }

    static setDirtyStatus( $collection: any, isDirty: boolean )
    {
        const $form = $collection.closest( DIRTY_FORMS.DIRTY_CHECKING_SELECTOR );
        const formMarkedAsDirty = $form.hasClass( DIRTY_FORMS.DIRTY_CLASS );
        const dirtyElementsArePresent = $form.find( ':dirty' ).length === 0;
        const changed = (isDirty !== (formMarkedAsDirty && dirtyElementsArePresent));

        if ( changed ) {
            $form.toggleClass( DIRTY_FORMS.DIRTY_CLASS, isDirty );

            if ( isDirty ) {
                $form.trigger( 'dirty.dirtyforms' );
            }
            if ( !isDirty ) {
                $form.trigger( 'clean.dirtyforms' );
            }
        }
    }

    static checkIfStoredValueIsEqual( original: any[], current: any[] )
    {
        if ( original.length !== current.length ) {
            return false;
        }
        for ( let i = 0; i < original.length; i++ ) {
            if ( original[i] !== current[i] ) {
                return false;
            }
        }
        return true;
    }
}

export const embeddedCollectionHelper = new EmbeddedCollectionDirtyForms();
