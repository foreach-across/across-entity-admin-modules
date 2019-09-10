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

package com.foreach.across.modules.bootstrapui.ui.factories.support;

import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElement;

/**
 * @author Stijn Vanhoof
 * @since 3.0.0
 */
public class BootstrapTableElementFactoryHelper
{
	public TableViewElement.Body body() {
		return new TableViewElement.Body();
	}

	public TableViewElement.Body body( ViewElement.WitherSetter... setters ) {
		return body().set( setters );
	}

	public TableViewElement.Caption caption() {
		return new TableViewElement.Caption();
	}

	public TableViewElement.Caption caption( ViewElement.WitherSetter... setters ) {
		return caption().set( setters );
	}

	public TableViewElement.Cell cell() {
		return new TableViewElement.Cell();
	}

	public TableViewElement.Cell cell( ViewElement.WitherSetter... setters ) {
		return cell().set( setters );
	}

	public TableViewElement.Cell headerCell() {
		return cell().setHeading( true );
	}

	public TableViewElement.Cell headerCell( ViewElement.WitherSetter... setters ) {
		return cell( setters ).setHeading( true );
	}

	public TableViewElement.ColumnGroup columnGroup() {
		return new TableViewElement.ColumnGroup();
	}

	public TableViewElement.ColumnGroup columnGroup( ViewElement.WitherSetter... setters ) {
		return columnGroup().set( setters );
	}

	public TableViewElement.Footer footer() {
		return new TableViewElement.Footer();
	}

	public TableViewElement.Footer footer( ViewElement.WitherSetter... setters ) {
		return footer().set( setters );
	}

	public TableViewElement.Header header() {
		return new TableViewElement.Header();
	}

	public TableViewElement.Header header( ViewElement.WitherSetter... setters ) {
		return header().set( setters );
	}

	public TableViewElement.Row row() {
		return new TableViewElement.Row();
	}

	public TableViewElement.Row row( ViewElement.WitherSetter... setters ) {
		return row().set( setters );
	}
}
