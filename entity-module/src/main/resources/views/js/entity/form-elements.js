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
$( document ).ready( function() {
    //$('.js-form-element-datepicker').each(function(){
    //    var datepickerConfig = $( this ).data( 'datepicker-config' );
    //    if ( !datepickerConfig ) {
    //        datepickerConfig = {};
    //    }
    //    datepickerConfig['constrainInput'] = false;
    //    $( this ).datepicker( datepickerConfig );
    //
    //    var datepickerLocale = $( this ).data( 'datepicker-locale' );
    //    if ( datepickerLocale ) {
    //        var datepickerRegional = $.datepicker.regional[datepickerLocale];
    //        $( this ).datepicker( "option", datepickerRegional );
    //    }
    //});

    $( '[data-dependson]' ).each( function() {
        var dependsonConfig = $( this ).data( 'dependson' );
        $( this ).dependsOn( dependsonConfig, {hide: false} );
    } );

    /**
     * Initialize multi value controls.
     */
    $( '.js-multi-value-control' ).each( function() {
        var container = $( this );

        container.find( '.js-multi-value-input' ).on( 'keypress', function( e ) {
            var keyCode = (e.keyCode ? e.keyCode : e.which );
            if ( keyCode == 13 ) {
                e.preventDefault();
                var value = $( this ).val();
                if ( value ) {
                    var template = container.find( '.js-multi-value-template' ).clone( false );
                    template.removeClass( 'hidden js-multi-value-template' );
                    template.addClass( 'js-multi-value-item' );

                    template.find( '.js-multi-value-value' ).each( function( i, node ) {
                        node.innerText = value;
                    } );

                    template.find( '[type=hidden]' ).val( value ).removeAttr( 'disabled' );
                    container.find( 'table' ).append( template );

                    template.find( 'a' ).on( 'click', function() {
                        $( this ).closest( 'tr' ).remove();
                    } );

                    $( this ).val( '' );
                }
            }
        } );

        container.find( '.js-multi-value-item a' ).on( 'click', function() {
            $( this ).closest( 'tr' ).remove();
        } )
    } )
} );