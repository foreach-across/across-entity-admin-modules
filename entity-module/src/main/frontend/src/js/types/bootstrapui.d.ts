/*
 * Copyright 2014 the original author or authors
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

declare interface BootstrapUiModuleObject
{
    Controls: any;
    ControlAdapterFactory: any;
    documentInitialized: boolean;
    initializers: any[];

    registerInitializer( callback: any, callIfAlreadyInitialized?: boolean ): void;

    initializeFormElements( node: any ): void;

    refTarget( node: any, recurse: any ): void;
}

/**
 * Represents the value of a {@link BaseControlAdapter}.
 */
declare interface BootstrapUiControlValueHolder
{
    /**
     * Displayed representation of the value.
     * E.g. 'John Doe' for an option element of which the actual value is 1.
     */
    readonly label: string;

    /**
     * Actual value of the displayed element
     * E.g. 1 as the value for a selected option element.
     */
    readonly value: any;

    /**
     * Context of the value, e.g. which html element defined this value.
     */
    readonly context: any;
}

declare interface BootstrapUiControlAdapter
{
    /**
     * Returns the current value of the {@link BootstrapUiControlAdapter#getTarget} element.
     *
     * {BootstrapUiControlValueHolder}
     */
    getValue(): BootstrapUiControlValueHolder[];

    /**
     * Sets the current value of the {@link BootstrapUiControlAdapter#getTarget} element.
     *
     * @param newValue
     */
    selectValue( newValue: any ): void;

    /**
     * Triggers a {@link BootstrapUiControlEvent#CHANGE} event for the current {@link BootstrapUiControlAdapter#getTarget}.
     * This event should be triggered when the value of the {@link BootstrapUiControlAdapter#getTarget} element is actually changed.
     */
    triggerChange(): void;

    /**
     * Triggers a {@link BootstrapUiControlEvent#SUBMIT} event for the current {@link BootstrapUiControlAdapter#getTarget}.
     * This event should be triggered when the value of the {@link BootstrapUiControlAdapter#getTarget} element should be submitted.
     * (e.g. by pressing enter)
     */
    triggerSubmit(): void;

    /**
     * Resets the value of the {@link BootstrapUiControlAdapter#getTarget} element to its initial value.
     */
    reset(): void;

    /**
     * Returns the target element of the adapter.
     * Which element is used specifically depends on the implementation.
     */
    getTarget(): any;
}

declare const BootstrapUiControlValueHolder: BootstrapUiControlValueHolder;
declare const BootstrapUiControlAdapter: BootstrapUiControlAdapter;
declare const BootstrapUiModule: BootstrapUiModuleObject;
declare const moment: any;

interface JQuery
{
    /**
     * Calls the <a href="https://github.com/twitter/typeahead.js">typeahead</a> object on a node to execute a method or retrieve/set a property.
     * Optionally an object can be provided, for example to set the value.
     *
     * @param variable to retrieve or execute
     * @param object to set
     */
    typeahead( variable: string, object?: any ): any;

    /**
     * Initializes an <a href="https://github.com/Eonasdan/bootstrap-datetimepicker">Eonasdan Bootstrap datepicker</a> on a node.
     *
     * @param config that should be applied to the datetimepicker.
     */
    datetimepicker( config: any ): any;

    /**
     * Calls the autonumeric object on a node to execute a method or retrieve/set a property.
     * Optionally an object can be provided, for example to set the value.
     *
     * @param variable to retrieve or execute
     * @param object to set
     */
    autoNumeric( variable: string, object?: any ): any;

    /**
     * Calls the <a href="https://silviomoreto.github.io/bootstrap-select/">bootstrap-select</a> object on a node to execute a method or retrieve/set a property.
     * Optionally an object can be provided, for example to set the value.
     *
     * @param variable to retrieve or execute
     * @param object to set
     */
    selectpicker( variable: string, object?: any ): any;

    /**
     * Initializes a jquery ui tooltip on the node.
     */
    tooltip(): void;
}
