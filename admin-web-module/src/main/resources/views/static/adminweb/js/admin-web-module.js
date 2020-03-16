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

var AcrossWebModule = Across.AcrossWebModule;

var AdminWebModule = (function( $ ) {
    var adminWebModule = Across.AdminWebModule;
    adminWebModule.useToastrNotifications = true;

    BootstrapUiModule.registerInitializer( function( node ) {
        if ( toastr && adminWebModule.useToastrNotifications ) {
            toastr.options = {
                closeButton: true, positionClass: 'toast-top-center'
            };

            // render dismissible feedback section alerts as toastr notifications
            $( '.alert-dismissible:not(.no-toast)', node ).each( function() {
                var alert = $( this );
                alert.addClass( 'axu-d-none' );

                var type = 'success';
                if ( alert.hasClass( 'alert-danger' ) ) {
                    type = 'error';
                }
                else if ( alert.hasClass( 'alert-warning' ) ) {
                    type = 'warning';
                }
                else if ( alert.hasClass( 'alert-info' ) ) {
                    type = 'info';
                }

                toastr[type]( alert.html() );
            } );
        }
    } );

    return adminWebModule;
})( jQuery );
