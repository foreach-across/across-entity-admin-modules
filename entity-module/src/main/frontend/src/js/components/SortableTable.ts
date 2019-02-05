import {SortableTableEvent} from '../events/SortableTableEvent';
import {EntityModule} from '../modules/EntityModule';
import axios from 'axios';

export class SortableTable
{
    private page: number;
    private size: number;
    private sort: any;
    private doesDataLoadWithAjax: boolean;
    private totalPages: number;
    private formName: string;
    private sortables: any;
    private table: JQuery;
    private entityModule: EntityModule;

    constructor( element: JQuery )
    {
        this.init( element );
        this.entityModule = (<any>window).EntityModule;
    }

    init( element: JQuery )
    {
        this.table = $( element );
        const id = $( element ).attr( 'data-tbl' );
        this.page = parseInt( this.table.attr( 'data-tbl-current-page' ), 10 );
        this.formName = $( element ).attr( 'data-tbl-form' );

        this.size = parseInt( this.table.attr( 'data-tbl-size' ), 10 );
        this.totalPages = parseInt( this.table.attr( 'data-tbl-total-pages' ), 10 );

        const currentSort = this.table.data( 'tbl-sort' );
        this.sort = currentSort != null ? currentSort : [];

        for ( let i = 0; i < this.sort.length; i++ ) {
            const order = this.sort[i];

            $( "[data-tbl='" + id + "'][data-tbl-sort-property='" + order.prop + "']", this.table )
                .each( () => {
                    if ( i === 0 ) {
                        $( this ).addClass( order.dir === 'ASC' ? 'asc' : 'desc' );
                    }
                    order.prop = $( this ).data( 'tbl-sort-property' );
                } );
        }

        this.doesDataLoadWithAjax = this.table.data( 'tbl-ajax-load' );

        this.table.on( SortableTableEvent.EVENT_MOVE_TO_PAGE, ( event: JQueryEventObject, pageNumber: number ) => {
            this.moveToPage( pageNumber );
        } );

        this.table.on( SortableTableEvent.EVENT_SORT, ( event: JQueryEventObject, propertyToSortOn: string ) => {
            this.sortOnProperty( propertyToSortOn );
        } );

        $( "[data-tbl='" + id + "'][data-tbl-page]" ).click( ( e: any ) => {
            e.preventDefault();
            e.stopPropagation();

            this.table.trigger( SortableTableEvent.EVENT_MOVE_TO_PAGE, parseInt( $( this ).attr( 'data-tbl-page' ), 10 ) );
        } );

        $( "input[type='text'][data-tbl='" + id + "'][data-tbl-page-selector]" )
            .click( ( event: JQueryEventObject ) => {
                event.preventDefault();
                $( this ).select();
            } )
            .keypress( ( event: JQueryEventObject ) => {
                const keyCode = (event.keyCode ? event.keyCode : event.which);
                if ( keyCode === 13 ) {
                    event.preventDefault();
                    let pageNumber = parseInt( $( this ).val(), 10 );

                    if ( isNaN( pageNumber ) ) {
                        $( this ).addClass( 'has-error' );
                    }
                    else {
                        $( this ).removeClass( 'has-error' );
                        if ( pageNumber < 1 ) {
                            pageNumber = 1;
                        }
                        else if ( pageNumber > this.totalPages ) {
                            pageNumber = this.totalPages;
                        }
                        this.table.trigger( SortableTableEvent.EVENT_MOVE_TO_PAGE, pageNumber - 1 );
                    }
                }
            } );

        this.sortables = $( "[data-tbl='" + id + "'][data-tbl-sort-property]", this.table );
        this.sortables.removeClass( 'asc' );
        this.sortables.removeClass( 'desc' );

        this.sortables.click( ( e: JQueryEventObject ) => {
            e.preventDefault();
            e.stopPropagation();
            this.table.trigger( SortableTableEvent.EVENT_SORT, $( this ).data( 'tbl-sort-property' ) );
        } );
        const self = this;

        jQuery.event.special[SortableTableEvent.EVENT_LOAD_DATA] = {
            _default: function ( event: JQueryEventObject, params: any ) {
                // fallback to default loading of paged data
                self.loadData( params );
            },
        };

        jQuery.event.special[SortableTableEvent.EVENT_PREPARE_DATA] = {
            _default: function ( event: JQueryEventObject, params: any ) {
                // fallback to default to prepare the paged data loading
                self.prepareData( params );
            },
        };
    }

    moveToPage( pageNumber: number )
    {
        const params: any = {
            page: pageNumber, size: this.size,
        };

        if ( this.sort != null && this.sort.length > 0 ) {
            const sortProperties = [];

            for ( let i = 0; i < this.sort.length; i++ ) {
                sortProperties.push( this.sort[i].prop + ',' + this.sort[i].dir );
            }

            params['sort'] = sortProperties;
        }

        this.table.trigger( SortableTableEvent.EVENT_PREPARE_DATA, params ).bind(this);

        this.table.trigger( SortableTableEvent.EVENT_LOAD_DATA, params ).bind(this);
    }

    loadDataWithAjax( params: any, form: any )
    {
        let allParams: any = {};
        const itemTable = $( '.pcs-body-section' ).find( '.panel-default' );

        form.serializeArray().map( ( x: any ) => {
            allParams[x.name] = x.value;
        } );

        allParams = $.extend( allParams, params );

        // $.get( '#', $.param( allParams, true ), function( data ) {
        //     itemTable.replaceWith( data );
        //     EntityModule.initializeFormElements( itemTable );
        // } );

        axios.get( '#', allParams ).then( ( data: any ) => {
            itemTable.replaceWith( data );
            this.entityModule.initializeFormElements( itemTable );
        } );
    }

    prepareData( params: any )
    {
        if ( this.doesDataLoadWithAjax ) {
            params['_partial'] = '::itemsTable';
        }
    }

    loadData( params: any )
    {
        if ( this.formName ) {
            const form = $( 'form[name=' + this.formName + ']' );

            const requireHiddenElement = ( name: string, value: any ) => {
                if ( value ) {
                    $( 'input[name=' + name + '][type=hidden]' ).remove();

                    const control = $( 'input[name=' + name + ']', form );
                    if ( control.length ) {
                        control.val( value );
                    }
                    else {
                        if ( $.isArray( value ) ) {
                            for ( let i = 0; i < value.length; i++ ) {
                                form.append( '<input type="hidden" name="' + name + '" value="' + value[i] + '" />' );
                            }
                        }
                        else {
                            form.append( '<input type="hidden" name="' + name + '" value="' + value + '" />' );
                        }
                    }
                }
            };

            $.each( params, ( paramName: string, paramValue: any ) => {
                requireHiddenElement( paramName, paramValue );
            } );

            if ( this.doesDataLoadWithAjax ) {
                this.loadDataWithAjax( params, form );
            }
            else {
                form.submit();
            }
        }
        else {
            const pathUrl = window.location.href.split( '?' )[0];
            window.location.href = pathUrl + '?' + $.param( params, true );
        }
    }

    sortOnProperty = ( propertyName: string ) => {
        let currentIndex = -1;

        for ( let i = 0; i < this.sort.length && currentIndex < 0; i++ ) {
            if ( this.sort[i].prop === propertyName ) {
                currentIndex = i;
            }
        }

        const order = {
            prop: propertyName, dir: 'ASC',
        };

        if ( currentIndex > -1 ) {
            if ( currentIndex === 0 ) {
                order.dir = this.sort[currentIndex].dir === 'ASC' ? 'DESC' : 'ASC';
            }

            if ( this.sort.length > 1 ) {
                this.sort.splice( currentIndex, 1 );
            }
            else {
                this.sort = [];
            }
        }

        this.sort = [order].concat( this.sort );

        this.moveToPage( this.page );
    }

}
