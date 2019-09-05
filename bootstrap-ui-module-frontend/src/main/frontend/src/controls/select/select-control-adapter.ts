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

import $ from 'jquery';

import BaseControlAdapter from '../support/base-control-adapter';
import BootstrapUiControlValueHolder, {createControlValueHolder,} from '../support/bootstrap-ui-control-value-holder';
import BootstrapUiControlAdapter from '../support/bootstrap-ui-control-adapter';

/**
 * {@link BootstrapUiControlAdapter} for select elements.
 * The target of the control adapter is the node on which it is registered.
 */
export default class SelectControlAdapter extends BaseControlAdapter {
  private readonly initialValue: any;

  constructor(target: any) {
    super(target);
    this.initialValue = $(this.getTarget()).val();
    this.initializeEventTriggers();
  }

  initializeEventTriggers(): void {
    $(this.getTarget()).on('change', _ => this.triggerChange());

    // TODO configure 'bootstrapui.submit' event
    // prevent opening the element on enter, but see it as 'submitting' the value instead.
    // $( this.getTarget() ).keypress( this, ( event ) => {
    //     if ( event.key === 'Enter' ) {
    //         event.preventDefault();
    //         this.triggerSubmit();
    //     }
    // } );
  }

  getValue(): BootstrapUiControlValueHolder[] {
    const selected: BootstrapUiControlValueHolder[] = [];
    const selectedOptions: any = $(this.getTarget()).find('option:checked');
    selectedOptions.each(function() {
      //@ts-ignore
      const element = $(this);
      selected.push(
        //@ts-ignore
        createControlValueHolder(element.html(), element.val(), this)
      );
    });
    return selected;
  }

  reset(): void {
    this.selectValue(this.initialValue);
  }

  selectValue(newValue: any): void {
    $(this.getTarget()).val(newValue);
  }
}

/**
 * Initializes a {@link SelectControlAdapter} for a given node.
 *
 * @param node to initialize
 */
export function createSelectControlAdapter(
  node: any
): BootstrapUiControlAdapter {
  return new SelectControlAdapter(node);
}
