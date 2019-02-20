import {SortableTableEvent} from '../events/SortableTableEvent';
import {EntityModule} from '../modules/EntityModule';

export class SortableTable
{
    private page: number;
    private size: number;
    private sort: any;
    private doesDataLoadWithAjax: boolean;
    private totalPages: number;
    private formName: string;
    private sortables: any;
    private form: JQuery;
    private table: JQuery;
    private entityModule: EntityModule;
    private pageNumber: number;
    private dataIsLoading: boolean = false;

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
        this.form = $( 'form[name=' + this.formName + ']' );
        this.doesDataLoadWithAjax = this.table.data( 'tbl-ajax-load' );

        this.size = parseInt( this.table.attr( 'data-tbl-size' ), 10 );
        this.totalPages = parseInt( this.table.attr( 'data-tbl-total-pages' ), 10 );

        const currentSort = this.table.data( 'tbl-sort' );
        this.sort = currentSort != null ? currentSort : [];

        for ( let i = 0; i < this.sort.length; i++ ) {
            const order = this.sort[i];

            $( "[data-tbl='" + id + "'][data-tbl-sort-property='" + order.prop + "']", this.table )
                .each( ( index: number, item: any ) => {
                    if ( i === 0 ) {
                        $( item ).addClass( order.dir === 'ASC' ? 'asc' : 'desc' );
                    }
                    order.prop = $( item ).data( 'tbl-sort-property' );
                } );
        }

        this.table.on( SortableTableEvent.MOVE_TO_PAGE, ( event: JQueryEventObject, pageNumber: number ) => {
            this.moveToPage( pageNumber );
        } );

        this.table.on( SortableTableEvent.SORT, ( event: JQueryEventObject, propertyToSortOn: string ) => {
            this.sortOnProperty( propertyToSortOn );
        } );

        this.form.on( 'submit', ( e: JQueryEventObject ) => {
            if ( this.doesDataLoadWithAjax && !this.dataIsLoading) {
                e.preventDefault();
                this.dataIsLoading = true;
                this.table.trigger( SortableTableEvent.MOVE_TO_PAGE, this.page );
            }
        } );

        $( "[data-tbl='" + id + "'][data-tbl-page]" ).click( ( e: JQueryEventObject ) => {
            e.preventDefault();
            e.stopPropagation();

            this.table.trigger( SortableTableEvent.MOVE_TO_PAGE, parseInt( $( e.currentTarget ).attr( 'data-tbl-page' ), 10 ) );
        } );

        $( "input[type='text'][data-tbl='" + id + "'][data-tbl-page-selector]" )
            .click( ( event: JQueryEventObject ) => {
                event.preventDefault();
                $( event.currentTarget ).select();
            } )
            .keypress( ( event: JQueryEventObject ) => {
                const keyCode = (event.keyCode ? event.keyCode : event.which);
                if ( keyCode === 13 ) {
                    event.preventDefault();
                    let pageNumber = parseInt( $( event.currentTarget ).val(), 10 );

                    if ( isNaN( pageNumber ) ) {
                        $(  event.currentTarget ).addClass( 'has-error' );
                    }
                    else {
                        $(  event.currentTarget ).removeClass( 'has-error' );
                        if ( pageNumber < 1 ) {
                            pageNumber = 1;
                        }
                        else if ( pageNumber > this.totalPages ) {
                            pageNumber = this.totalPages;
                        }
                        this.table.trigger( SortableTableEvent.MOVE_TO_PAGE, pageNumber - 1 );
                    }
                }
            } );

        this.sortables = $( "[data-tbl='" + id + "'][data-tbl-sort-property]", this.table );
        this.sortables.removeClass( 'asc' );
        this.sortables.removeClass( 'desc' );

        this.sortables.click( ( e: JQueryEventObject ) => {
            e.preventDefault();
            e.stopPropagation();
            this.table.trigger( SortableTableEvent.SORT, $( e.target ).data( 'tbl-sort-property' ) );
        } );
        const self = this;

        jQuery.event.special[SortableTableEvent.LOAD_DATA] = {
            _default: function ( event: JQueryEventObject, params: any ) {
                // fallback to default loading of paged data
                self.loadData( params );
            },
        };

        jQuery.event.special[SortableTableEvent.PREPARE_DATA] = {
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

        this.table.trigger( SortableTableEvent.PREPARE_DATA, params );

        this.table.trigger( SortableTableEvent.LOAD_DATA, params );
    }

    // Todo: the find of the table is not ideal
    loadDataWithAjax( baseParams: any, form: any )
    {
        let params: any = {};
        const itemTable = $( '.pcs-body-section' ).find( '.panel-default' );

        form.serializeArray().map( ( x: any ) => {
            params[x.name] = x.value;
        } );

        params = $.extend( params, baseParams );

        $.get( '#', $.param( params, true ), ( data ) => {
            itemTable.replaceWith( data );
            const loadedTable =  $('[data-tbl-type="paged"]');
            this.init(loadedTable);
        } ).done(() => {
            this.dataIsLoading = false;
            this.table.trigger( SortableTableEvent.NEW_DATA_LOADED );
        }).fail(() => {
            // Retry on fail
            this.loadDataWithAjax(baseParams, form);
        });
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
                this.dataIsLoading = true;
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
