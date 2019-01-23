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
/**
 * Disable enter on all controls that should disable line breaks.
 */
function lineBreaksInitializer( node: any ): void {
    $( '.js-disable-line-breaks', node ).on( 'keyup keypress', ( e ) => {
        if ( e.which === 13 || e.which === 10 ) {
            e.preventDefault();
            return false;
        }
    } );
}

export default lineBreaksInitializer;
