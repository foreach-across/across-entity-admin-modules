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
package com.foreach.across.modules.entity.newviews.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public class EntityQueryOptionGenerator implements ViewElementBuilder<ContainerViewElement>
{
	protected final EntityConfiguration entityConfiguration;
	protected final EntityQueryExecutor entityQueryExecutor;

	public EntityQueryOptionGenerator( EntityConfiguration entityConfiguration,
	                                   EntityQueryExecutor entityQueryExecutor ) {
		this.entityConfiguration = entityConfiguration;
		this.entityQueryExecutor = entityQueryExecutor;
	}

	@Override
	public ContainerViewElement build( ViewElementBuilderContext builderContext ) {
		EntityMessageCodeResolver codeResolver = builderContext.getAttribute( EntityMessageCodeResolver.class );

		List<OptionsFormElementBuilder.Option> options = new ArrayList<>();

		for ( Object entity : entityQueryExecutor.findAll( new EntityQuery(), null ).getContent() ) {
			OptionsFormElementBuilder.Option option = new OptionsFormElementBuilder.Option();

			option.label( entityConfiguration.getLabel( entity ) );
			option.value( entityConfiguration.getId( entity ).toString() );

			//option.selected(  )

			options.add( option );
		}

//		if ( shouldBeSorted ) {
//			//Collections.sort( options );
//		}

		ContainerViewElement container = new ContainerViewElement();

		for ( OptionsFormElementBuilder.Option option : options ) {
			container.add( option.build( builderContext ) );
		}

		return container;
	}
}
