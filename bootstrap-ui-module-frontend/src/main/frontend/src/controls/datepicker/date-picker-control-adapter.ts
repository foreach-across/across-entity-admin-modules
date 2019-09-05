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
import BootstrapUiControlValueHolder, {createControlValueHolder,} from '../support/bootstrap-ui-control-value-holder';
import BootstrapUiControlAdapter from '../support/bootstrap-ui-control-adapter';

/**
 * {@link BootstrapUiControlAdapter} for <a href="https://github.com/Eonasdan/bootstrap-datetimepicker">Eonasdan Bootstrap datepicker</a> elements.
 * The target of the control adapter is the node on which it is registered.
 *
 * @see initializeDateTimePickers
 */
export default class DatePickerControlAdapter extends BaseControlAdapter {
  private readonly exportFormat: string;
  private readonly initialValue: any;
  private readonly valueHolder: any;

  constructor(target: any, exportFormat: string) {
    super(target);
    this.exportFormat = exportFormat;
    this.initialValue = this.getDateTimePicker().date();
    this.valueHolder = $('input[type=hidden]', target)[0];
    $(target).on('dp.change', _ => this.triggerChange());

    // TODO configure 'bootstrapui.submit' event
    // $( target ).find( 'input[type="text"]' ).keyup( this, ( event ) => {
    //     if ( event.key === 'Enter' ) {
    //         // event.preventDefault();
    //         this.triggerSubmit();
    //     }
    // } );
  }

  getValue(): BootstrapUiControlValueHolder[] {
    const date = this.getDateTimePicker().date();
    const formattedValue = date ? date.format(this.exportFormat) : null;
    const value = $(this.valueHolder).val();
    return [createControlValueHolder(formattedValue, value, this.getTarget())];
  }

  reset(): void {
    this.selectValue(this.initialValue);
  }

  selectValue(newValue: any): void {
    this.getDateTimePicker().date(newValue);
  }

  private getDateTimePicker(): any {
    return $(this.getTarget()).data('DateTimePicker');
  }
}

/**
 * Initializes a {@link DatePickerControlAdapter} for a given node.
 *
 * @param node to initialize
 */
export function createDatePickerControlAdapter(
  node: any
): BootstrapUiControlAdapter {
  const element = $(node);
  const configuration = $(element).data('bootstrapui-datetimepicker');
  let exportFormat;
  if (configuration) {
    exportFormat = configuration.exportFormat;
  }
  return new DatePickerControlAdapter(node, exportFormat);
}
