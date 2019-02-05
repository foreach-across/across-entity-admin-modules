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

import {EntityModule} from "./modules/EntityModule";
import {SortableTable} from "./components/SortableTable";

const entityModule = new EntityModule();

$( document ).ready( function () {
  entityModule.initializeFormElements( null );
} );

window.EntityModule = entityModule;

/**
 * Expose JQuery plugin emSortableTable, creates a SortableTable when called.
 */
$.fn.emSortableTable = function () {
  return this.each( function () {
    if ( !this._emSortableTable ) {
      this._emSortableTable = new SortableTable( this );
    }
  } );
};
