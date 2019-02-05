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

import {AxiosInstance as axios} from "axios";
import {SortableTableEvent} from "../events/SortableTableEvent";

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

  constructor( element: JQuery )
  {
    this.init( element );
  }

  init( element: JQuery )
  {
    this.table = $( element );
    let id = $( element ).attr( 'data-tbl' );
    this.page = parseInt( table.attr( 'data-tbl-current-page' ) );
    this.formName = $( element ).attr( 'data-tbl-form' );

    this.size = parseInt( table.attr( 'data-tbl-size' ) );
    this.totalPages = parseInt( table.attr( 'data-tbl-total-pages' ) );

    var currentSort = table.data( 'tbl-sort' );
    this.sort = currentSort != null ? currentSort : [];

    for ( let i = 0; i < this.sort.length; i++ ) {
      let order = this.sort[i];

      $( "[data-tbl='" + id + "'][data-tbl-sort-property='" + order.prop + "']", table )
        .each( () => {
          if ( i == 0 ) {
            $( this ).addClass( order.dir == 'ASC' ? 'asc' : 'desc' );
          }
          order.prop = $( this ).data( 'tbl-sort-property' );
        } );
    }

    this.doesDataLoadWithAjax = table.data( 'tbl-ajax-load' );

    table.on( SortableTableEvent.EVENT_MOVE_TO_PAGE, ( event: JQueryEventObject, pageNumber: number ) => {
      this.moveToPage( pageNumber );
    } );

    table.on( SortableTableEvent.EVENT_SORT, ( event: JQueryEventObject, propertyToSortOn: string ) => {
      this.sortOnProperty( propertyToSortOn );
    } );

    $( "[data-tbl='" + id + "'][data-tbl-page]" ).click( ( e: any ) => {
      e.preventDefault();
      e.stopPropagation();

      table.trigger( SortableTableEvent.EVENT_MOVE_TO_PAGE, parseInt( $( this ).attr( 'data-tbl-page' ) ) );
    } );

    $( "input[type='text'][data-tbl='" + id + "'][data-tbl-page-selector]" )
      .click( ( event: JQueryEventObject ) => {
        event.preventDefault();
        $( this ).select();
      } )
      .keypress( ( event: JQueryEventObject ) => {
        let keyCode = (event.keyCode ? event.keyCode : event.which);
        if ( keyCode == 13 ) {
          event.preventDefault();
          let pageNumber = parseInt( $( this ).val() );

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
            table.trigger( SortableTableEvent.EVENT_MOVE_TO_PAGE, pageNumber - 1 );
          }
        }
      } );

    this.sortables = $( "[data-tbl='" + id + "'][data-tbl-sort-property]", table );
    this.sortables.removeClass( 'asc', 'desc' );

    this.sortables.click( function ( e ) {
      e.preventDefault();
      e.stopPropagation();
      this.table.trigger( SortableTableEvent.EVENT_SORT, $( this ).data( 'tbl-sort-property' ) );
    } );

    jQuery.event.special[SortableTableEvent.EVENT_LOAD_DATA] = {
      _default: function ( event: JQueryEventObject, params: any ) {
        // fallback to default loading of paged data
        this.loadData( params );
      }
    };

    jQuery.event.special[SortableTableEvent.EVENT_PREPARE_DATA] = {
      _default: function ( event: JQueryEventObject, params: any ) {
        // fallback to default to prepare the paged data loading
        this.prepareData( params );
      }
    };
  }

  moveToPage( pageNumber: number )
  {
    let params = {
      'page': pageNumber, 'size': this.size
    };

    if ( this.sort != null && this.sort.length > 0 ) {
      let sortProperties = [];

      for ( let i = 0; i < this.sort.length; i++ ) {
        sortProperties.push( this.sort[i].prop + ',' + this.sort[i].dir );
      }

      params['sort'] = sortProperties;
    }

    this.table.trigger( SortableTableEvent.EVENT_PREPARE_DATA, params );

    this.table.trigger( SortableTableEvent.EVENT_LOAD_DATA, params );
  };

  loadDataWithAjax( params: any, form: any )
  {
    let allParams = {};
    let itemTable = $( ".pcs-body-section" ).find( '.panel-default' );

    form.serializeArray().map( ( x:any ) => {
      allParams[x.name] = x.value;
    } );

    allParams = $.extend( allParams, params );

    // $.get( '#', $.param( allParams, true ), function( data ) {
    //     itemTable.replaceWith( data );
    //     EntityModule.initializeFormElements( itemTable );
    // } );

    axios.get( '#', allParams ).then( function ( data ) {
      itemTable.replaceWith( data );
      //EntityModule.initializeFormElements( itemTable );
    } );
  }

.
  prepareData ( params:any ) {
    if ( this.doesDataLoadWithAjax ) {
      params['_partial'] = '::itemsTable';
    }
  }

  loadData( params:any ) {
    if ( this.formName ) {
      let form = $( 'form[name=' + this.formName + ']' );

      let requireHiddenElement = ( name:string, value:any ) => {
        if ( value ) {
          $( 'input[name=' + name + '][type=hidden]' ).remove();

          let control = $( 'input[name=' + name + ']', form );
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

      $.each( params, ( paramName:string, paramValue:any ) => {
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
      let pathUrl = window.location.href.split( '?' )[0];
      window.location.href = pathUrl + '?' + $.param( params, true );
    }
  };

  sortOnProperty = ( propertyName:string ) => {
    var currentIndex = -1;

    for ( let i = 0; i < this.sort.length && currentIndex < 0; i++ ) {
      if ( this.sort[i].prop == propertyName ) {
        currentIndex = i;
      }
    }

    let order = {
      'prop': propertyName, 'dir': 'ASC'
    };

    if ( currentIndex > -1 ) {
      if ( currentIndex == 0 ) {
        order.dir = this.sort[currentIndex].dir == 'ASC' ? 'DESC' : 'ASC';
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
  };

}

/**
 * Expose JQuery plugin emSortableTable, creates a SortableTable when called.
 */
$.fn.emSortableTable = function () {
  return this.each( function () {
    if ( !this._emSortableTable ) {
      this._emSortableTable = new SortableTable( this );
    }
  } );
};
