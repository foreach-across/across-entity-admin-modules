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

declare const AutoNumeric: any;

/**
 * Find an activate all autoNumeric form elements.
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
function numericInitializer( node: any ): void {
    $( '[data-bootstrapui-numeric]', node ).each( function () {
        const configuration = $( this ).data( 'bootstrapui-numeric' );
        const name = $( this ).attr( 'name' );

        const multiplier = configuration.multiplier ? configuration.multiplier : 1;

        let multiplied;

        if ( multiplier !== 1 ) {
            const currentValue = $( this ).val();
            if ( currentValue && !isNaN( currentValue ) ) {
                multiplied = parseFloat( currentValue ) * multiplier;
            }
        }

        const autoNumeric = new AutoNumeric( this, configuration );
        $( this )
            .bind( 'blur focusout keypress keyup', () => {
                if ( name && name.length > 1 && name[0] === '_' ) {
                    let val = autoNumeric.get();

                    if ( multiplier !== 1 ) {
                        val = val / multiplier;
                    }

                    $( `input[type=hidden][name="${name.substring( 1 )}"]` ).val( val );
                }
            } );

        $( this ).data( 'autoNumeric', autoNumeric );

        if ( multiplied ) {
            autoNumeric.set( multiplied );
        }
    } );
}

export default numericInitializer;
