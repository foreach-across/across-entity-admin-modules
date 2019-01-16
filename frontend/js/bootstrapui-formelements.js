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

import datePickerInitializer from "./controls/datepicker/date-picker-initializer";
import numericInitializer from "./controls/numeric/numeric-initializer";
import selectInitializer from "./controls/select/select-initializer";
import autosuggestInitializer, {registerAutosuggestControl} from "./controls/autosuggest/autosuggest-initializer";
import autosizeInitializer from "./controls/autosize/autosize-initializer";
import lineBreaksInitializer from "./controls/line-breaks/line-breaks-initializer";
import tooltipInitializer from "./controls/tooltip/tooltip-initializer";

(function ( $ ) {
            registerAutosuggestControl();
            /**
             * Main initialization of BoostrapUiModule form elements.
             */
            BootstrapUiModule.registerInitializer( function ( node ) {
                datePickerInitializer( node );
                numericInitializer( node );
                autosizeInitializer( node );
                lineBreaksInitializer( node );
                selectInitializer( node );
                autosuggestInitializer( node );
                tooltipInitializer( node );
            } );
        }( jQuery )
);



