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

package com.foreach.across.modules.entity.views.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryParser;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates {@link OptionFormElementBuilder}s for a particular entity type, where the list of options
 * is fetched through an {@link EntityQueryExecutor} and custom {@link EntityQuery}.
 * By default an {@link EntityQuery} without parameters will be used, resulting in all entities being returned.
 * <p/>
 * A query can be specified using either an {@link EntityQuery} instance or a EQL statement.
 * <p/>
 * Use {@link #forEntityConfiguration(EntityConfiguration)} to easily create an  {@link EntityQueryOptionIterableBuilder}
 * configured with the entity query support for a particular {@link EntityConfiguration}.
 *
 * @author Arne Vandamme
 */
public class EntityQueryOptionIterableBuilder implements OptionIterableBuilder
{
	private static final ConversionService DEFAULT_CONVERSION_SERVICE = DefaultConversionService.getSharedInstance();
	private ConversionService conversionService;
	private EntityModel<Object, Serializable> entityModel;
	private EntityQueryExecutor<Object> entityQueryExecutor;
	private EntityQueryParser entityQueryParser;
	private EntityQuery entityQuery = EntityQuery.all();

	public EntityModel getEntityModel() {
		return entityModel;
	}

	@SuppressWarnings("unchecked")
	public void setEntityModel( EntityModel entityModel ) {
		Assert.notNull( entityModel, "entityModel is required" );
		this.entityModel = entityModel;
	}

	/*
	Sets an alternative {@link ConversionService} which is used to convert the Id of an entity.
	If none is set, the {@link DefaultConversionService} will be used.

	@author Marc Vanbrabant
	@since 3.2.0
	 */
	public void setConversionService( ConversionService conversionService ) {
		this.conversionService = conversionService;
	}

	/**
	 * Set an entity query via an EQL statement.
	 *
	 * @param eql statement that represents the query to execute
	 */
	public void setEntityQuery( String eql ) {
		Assert.notNull( eql, "EQL statement is required" );
		this.entityQuery = EntityQuery.parse( eql );
	}

	/**
	 * Set a typed entity query object.
	 *
	 * @param entityQuery to use
	 */
	public void setEntityQuery( EntityQuery entityQuery ) {
		Assert.notNull( entityQuery, "entityQuery is required" );
		this.entityQuery = entityQuery;
	}

	@SuppressWarnings("unchecked")
	public void setEntityQueryExecutor( EntityQueryExecutor entityQueryExecutor ) {
		Assert.notNull( entityQueryExecutor, "entityQueryExecutor is required" );
		this.entityQueryExecutor = entityQueryExecutor;
	}

	public void setEntityQueryParser( EntityQueryParser entityQueryParser ) {
		this.entityQueryParser = entityQueryParser;
	}

	@Override
	public boolean isSorted() {
		return entityQuery.hasSort();
	}

	@Override
	public Iterable<OptionFormElementBuilder> buildOptions( ViewElementBuilderContext builderContext ) {
		EntityQuery query = prepareEntityQuery();

		Assert.notNull( entityModel, "no EntityModel set" );
		Assert.notNull( entityQuery, "no EntityQuery set" );
		Assert.notNull( entityQueryExecutor, "no EntityQueryExecutor set" );

		List<OptionFormElementBuilder> options = new ArrayList<>();

		for ( Object entityOption : entityQueryExecutor.findAll( query ) ) {
			OptionFormElementBuilder option = new OptionFormElementBuilder();

			option.rawValue( entityOption );
			option.label( entityModel.getLabel( entityOption ) );
			option.value( convertId( entityModel.getId( entityOption ) ) );

			options.add( option );
		}

		return options;
	}

	String convertId( Object value ) {
		return conversionService != null ? conversionService.convert( value, String.class ) : DEFAULT_CONVERSION_SERVICE.convert( value, String.class );
	}

	private EntityQuery prepareEntityQuery() {
		Assert.notNull( entityQueryParser, "No EntityQueryParser is available to prepare the EntityQuery" );
		return entityQueryParser.prepare( entityQuery );
	}

	/**
	 * Creates an {@link EntityQueryOptionIterableBuilder} for all entities of a particular {@link EntityConfiguration}.
	 *
	 * @param entityConfiguration whose options to select
	 * @return option builder
	 */
	public static EntityQueryOptionIterableBuilder forEntityConfiguration( EntityConfiguration entityConfiguration ) {
		EntityQueryOptionIterableBuilder iterableBuilder = new EntityQueryOptionIterableBuilder();
		iterableBuilder.setEntityModel( entityConfiguration.getEntityModel() );
		iterableBuilder.setEntityQueryExecutor( entityConfiguration.getAttribute( EntityQueryExecutor.class ) );
		iterableBuilder.setEntityQueryParser( entityConfiguration.getAttribute( EntityQueryParser.class ) );

		return iterableBuilder;
	}
}
