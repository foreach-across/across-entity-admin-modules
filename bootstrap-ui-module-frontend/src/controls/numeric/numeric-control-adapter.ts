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
 * @author Steven Gentens
 * @since 2.2.0
 */
import $ from 'jquery';

import BaseControlAdapter from '../support/base-control-adapter';
import BootstrapUiControlValueHolder, {
  createControlValueHolder,
} from '../support/bootstrap-ui-control-value-holder';
import BootstrapUiControlAdapter from '../support/bootstrap-ui-control-adapter';

/**
 * {@link BootstrapUiControlAdapter} for autonumeric elements.
 * The target of the control adapter is the node on which it is registered.
 *
 * @see numericInitializer
 */
export default class NumericControlAdapter extends BaseControlAdapter {
  private readonly initialValue: any;

  constructor(target: any) {
    super(target);
    this.initialValue = $(this.getTarget()).autoNumeric('get');

    $(target).on('change', _ => this.triggerChange());

    // TODO configure 'bootstrapui.submit' event
    // prevent opening the element on enter, but see it as 'submitting' the value instead.
    // $( target ).on( 'keypress', ( event ) => {
    //     if ( event.key === 'Enter' ) {
    //         this.triggerSubmit();
    //     }
    // } );
  }

  getValue(): BootstrapUiControlValueHolder[] {
    //@ts-ignore
    const label: string = $(this.getTarget())
      .val()
      .toString();
    const value: any = $(this.getTarget()).autoNumeric('get');
    return [createControlValueHolder(label, value, this.getTarget())];
  }

  reset(): void {
    this.selectValue(this.initialValue);
  }

  selectValue(newValue: any): void {
    $(this.getTarget()).autoNumeric('set', newValue);
  }
}

/**
 * Initializes a {@link NumericControlAdapter} for a given node.
 *
 * @param node to initialize
 */
export function createNumericControlAdapter(
  node: any
): BootstrapUiControlAdapter {
  return new NumericControlAdapter(node);
}
