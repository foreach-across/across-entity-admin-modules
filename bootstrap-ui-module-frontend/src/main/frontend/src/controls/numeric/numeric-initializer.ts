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
 * Find an activate all autoNumeric form elements.
 *
 * @author Steven Gentens
 * @since 2.2.0
 */

import $ from 'jquery';

function numericInitializer(node: any): void {
  $('[data-bootstrapui-numeric]', node).each(function() {
    const configuration = $(this).data('bootstrapui-numeric');
    const name = $(this).attr('name');

    const multiplier = configuration.multiplier ? configuration.multiplier : 1;

    let multiplied;

    if (multiplier !== 1) {
      const currentValue = $(this).val();
      //@ts-ignore
      if (currentValue && !isNaN(currentValue)) {
        //@ts-ignore
        multiplied = parseFloat(currentValue) * multiplier;
      }
    }

    $(this)
      .autoNumeric('init', configuration)
      .bind('blur focusout keypress keyup', function() {
        //@ts-ignore
        if (name.length > 1 && name[0] === '_') {
          //@ts-ignore
          let val = $(this).autoNumeric('get');

          if (multiplier !== 1) {
            val = val / multiplier;
          }
          //@ts-ignore
          $(`input[type=hidden][name="${name.substring(1)}"]`).val(val);
        }
      });

    if (multiplied) {
      $(this).autoNumeric('set', multiplied);
    }
  });
}

export default numericInitializer;
