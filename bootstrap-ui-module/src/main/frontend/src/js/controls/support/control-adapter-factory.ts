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
import BootstrapUiAttributes from './bootstrap-ui-attributes';
import BootstrapUiControlAdapter from './bootstrap-ui-control-adapter';

interface ControlAdapterInitializer
{
    ( node: any ): BootstrapUiControlAdapter;
}

/**
 * Holds a registry of {@link ControlAdapterInitializer}s to initialize {@link BootstrapUiControlAdapter}s for types.
 */
export class ControlAdapterFactoryObject
{
    private controlAdapterRegistrars: Map<string, ControlAdapterInitializer>;

    constructor() {
        this.controlAdapterRegistrars = new Map();
    }

    /**
     * Registers an initializer for a given {@code type} of control.
     * The type of the control should be specified as a {@link BootstrapUiAttributes.CONTROL_ADAPTER_TYPE} data attribute on the element.
     *
     * @param type for which the initializer applies.
     * @param initializer the initializer of the control adapter.
     */
    register( type: string, initializer: ControlAdapterInitializer ): void {
        this.controlAdapterRegistrars.set( type, initializer );
    }

    /**
     * Initializes all child nodes that have a {@code bootstrapui-adapter-type} data attribute with a {@link BaseControlAdapter}
     * if one is available for that type.
     *
     * @param node for which to initialize the child nodes
     */
    initializeControlAdapters( node: any ): void {
        $( '[data-bootstrapui-adapter-type]', node )
            .each( ( index, element ) => {
                const adapterType: string = $( element ).data( BootstrapUiAttributes.CONTROL_ADAPTER_TYPE );
                this.initializeControlAdapter( adapterType, element );
            } );
    }

    /**
     * Initializes a node with a {@link BaseControlAdapter} if it has not yet been initialized and an initializer for the type is available.
     *
     * @param type of {@link ControlAdapterInitializer} that should be used
     * @param node to create a {@link BootstrapUiControlAdapter} for
     * @param force whether the {@link BootstrapUiControlAdapter} must be overridden
     */
    initializeControlAdapter( type: string, node: any, force?: boolean ): void {
        if ( this.controlAdapterRegistrars.has( type ) ) {
            if ( !$( node ).data( BootstrapUiAttributes.CONTROL_ADAPTER ) || force ) {
                const adapter: BootstrapUiControlAdapter = this.controlAdapterRegistrars.get( type )( node );
                $( node ).data( BootstrapUiAttributes.CONTROL_ADAPTER, adapter );
            }
        }
    }
}

// tslint:disable-next-line:variable-name
export const ControlAdapterFactory = new ControlAdapterFactoryObject();
