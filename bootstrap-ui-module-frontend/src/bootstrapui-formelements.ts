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

import initializeDateTimePickerControls from './controls/datepicker/date-picker-initializer';
import initializeNumericControls from './controls/numeric/numeric-initializer';
import initializeSelectControls from './controls/select/select-initializer';
import initializeAutoSuggestControls, {
  registerAutosuggestControl,
} from './controls/autosuggest/autosuggest-initializer';
import initializeAutoSizeControls from './controls/support/initializers/autosize-initializer';
import disableLineBreakSupport from './controls/support/initializers/line-breaks-initializer';
import initializeTooltips from './controls/support/initializers/tooltip-initializer';

(function() {
  registerAutosuggestControl();

  /**
   * Main initialization of BoostrapUiModule form elements.
   */
  BootstrapUiModule.registerInitializer(function(node: any) {
    initializeDateTimePickerControls(node);
    initializeNumericControls(node);
    initializeAutoSizeControls(node);
    disableLineBreakSupport(node);
    initializeSelectControls(node);
    initializeAutoSuggestControls(node);
    initializeTooltips(node);

    BootstrapUiModule.ControlAdapterFactory.initializeControlAdapters(node);
  });
})();
