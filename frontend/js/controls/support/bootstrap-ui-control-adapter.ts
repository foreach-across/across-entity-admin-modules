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

import BootstrapUiControlValueHolder from './bootstrap-ui-control-value-holder';
import * as $ from 'jquery';
import BootstrapUiControlEvent from './bootstrap-ui-control-event';

/**
 *
 */
export default abstract class BootstrapUiControlAdapter
{
    private target: any;

    constructor( target: any ) {
        this.target = target;
    }

    /**
     * Returns the current value of the {@link BootstrapUiControlAdapter#getTarget} element.
     *
     * {BootstrapUiControlValueHolder}
     */
    public abstract getValue(): BootstrapUiControlValueHolder;

    /**
     * Sets the current value of the {@link BootstrapUiControlAdapter#getTarget} element.
     * @param obj
     */
    public abstract selectValue( obj: any ): void;

    /**
     * Triggers a {@link BootstrapUiControlEvent#CHANGE} event for the current {@link BootstrapUiControlAdapter#getTarget}.
     * This event should be triggered when the value of the {@link BootstrapUiControlAdapter#getTarget} element is actually changed.
     */
    public triggerChange(): void {
        $( this.getTarget() ).trigger( BootstrapUiControlEvent.CHANGE, [this] );
    }

    /**
     * Triggers a {@link BootstrapUiControlEvent#SUBMIT} event for the current {@link BootstrapUiControlAdapter#getTarget}.
     * This event should be triggered when the value of the {@link BootstrapUiControlAdapter#getTarget} element should be submitted.
     * (e.g. by pressing enter)
     */
    public triggerSubmit(): void {
        $( this.getTarget() ).trigger( BootstrapUiControlEvent.SUBMIT, [this] );
    }

    /**
     * Resets the value of the {@link BootstrapUiControlAdapter#getTarget} element to its initial value.
     */
    public abstract reset(): void;

    /**
     * Returns the html element that this control handles.
     */
    public getTarget(): any {
        return this.target;
    }
}
