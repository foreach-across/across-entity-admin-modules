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
import autosizeInitializer from "./controls/support/initializers/autosize-initializer";
import lineBreaksInitializer from "./controls/support/initializers/line-breaks-initializer";
import tooltipInitializer from "./controls/support/initializers/tooltip-initializer";
import {ControlAdapterFactory} from "./controls/support/control-adapter-factory";
import {createAutosuggestControlAdapter} from "./controls/autosuggest/autosuggest-control-adapter";
import {createDatePickerControlAdapter} from "./controls/datepicker/date-picker-control-adapter";
import {createBasicControlAdapter} from "./controls/input/basic-control-adapter";
import {createNumericControlAdapter} from "./controls/numeric/numeric-control-adapter";
import {createBootstrapSelectControlAdapter} from "./controls/select/bootstrap-select-control-adapter";
import {createSelectControlAdapter} from "./controls/select/select-control-adapter";
import {createCheckboxControlAdapter} from "./controls/checkbox/checkbox-control-adapter";
import {createContainerControlAdapter} from "./controls/container/container-control-adapter";

(function ( $ ) {
            registerAutosuggestControl();

            BootstrapUiModule.ControlAdapterFactory = ControlAdapterFactory;

            /**
             * Main initialization of BoostrapUiModule form elements.
             */
            BootstrapUiModule.registerInitializer( function ( node ) {
                ControlAdapterFactory.register( 'autosuggest', createAutosuggestControlAdapter );
                ControlAdapterFactory.register( 'datetime', createDatePickerControlAdapter );
                ControlAdapterFactory.register( 'textbox', createBasicControlAdapter );
                ControlAdapterFactory.register( 'numeric', createNumericControlAdapter );
                ControlAdapterFactory.register( 'bootstrap-select', createBootstrapSelectControlAdapter );
                ControlAdapterFactory.register( 'select', createSelectControlAdapter );
                ControlAdapterFactory.register( 'checkbox', createCheckboxControlAdapter );
                ControlAdapterFactory.register( 'container', createContainerControlAdapter );

                datePickerInitializer( node );
                numericInitializer( node );
                autosizeInitializer( node );
                lineBreaksInitializer( node );
                selectInitializer( node );
                autosuggestInitializer( node );
                tooltipInitializer( node );

                ControlAdapterFactory.initializeNode( node );
            } );
        }( jQuery )
);



