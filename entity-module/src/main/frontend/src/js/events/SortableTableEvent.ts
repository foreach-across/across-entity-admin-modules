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

export enum SortableTableEvent
{
    /**
     * Trigger this event with the page number as parameter.
     */
    MOVE_TO_PAGE = 'emSortableTable:moveToPage',

    /**
     * Trigger this event with the new field name to sort on as parameter.
     */
    SORT = 'emSortableTable:sort',

    /**
     * Subscribe to this event if you want to modify the parameters for loading the data.
     */
    PREPARE_DATA = 'emSortableTable:prepareData',

    /**
     * Subscribe to this event if you want to modify the parameters for loading the data,
     * or if you want to implement custom data loading and rendering.
     * Prevent the default event handling in the latter case.
     */
    LOAD_DATA = 'emSortableTable:loadData',

    /**
     * Subscribe to this event if you want to be notified if data new has been loaded
     */
    NEW_DATA_LOADED = 'emSortableTable:newDataLoaded',
}
