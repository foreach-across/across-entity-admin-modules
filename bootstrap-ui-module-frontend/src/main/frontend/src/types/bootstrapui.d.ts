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

declare interface BootstrapUiModuleObject {
  Controls: any;
  ControlAdapterFactory: any;
  documentInitialized: boolean;
  initializers: any[];

  registerInitializer(callback: any, callIfAlreadyInitialized?: boolean): void;

  initializeFormElements(node?: any): void;

  refTarget(node: any, recurse: any): any;
}

declare const BootstrapUiModule: BootstrapUiModuleObject;

interface JQuery {
  /**
   * Calls the <a href="https://github.com/twitter/typeahead.js">typeahead</a> object on a node to execute a method or retrieve/set a property.
   * Optionally an object can be provided, for example to set the value.
   *
   * @param variable to retrieve or execute
   * @param object to set
   */
  typeahead(variable: string, object?: any): any;

  /**
   * Initializes an <a href="https://github.com/Eonasdan/bootstrap-datetimepicker">Eonasdan Bootstrap datepicker</a> on a node.
   *
   * @param config that should be applied to the datetimepicker.
   */
  datetimepicker(config: any): any;

  /**
   * Calls the autonumeric object on a node to execute a method or retrieve/set a property.
   * Optionally an object can be provided, for example to set the value.
   *
   * @param variable to retrieve or execute
   * @param object to set
   */
  autoNumeric(variable: string, object?: any): any;

  /**
   * Calls the <a href="https://silviomoreto.github.io/bootstrap-select/">bootstrap-select</a> object on a node to execute a method or retrieve/set a property.
   * Optionally an object can be provided, for example to set the value.
   *
   * @param variable to retrieve or execute
   * @param object to set
   */
  selectpicker(variable: string, object?: any): any;

  /**
   * Initializes a jquery ui tooltip on the node.
   */
  tooltip(): void;
}
