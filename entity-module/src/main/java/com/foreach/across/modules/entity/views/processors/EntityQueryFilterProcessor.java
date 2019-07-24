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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.query.*;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.util.EntityTypeDescriptor;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterConfiguration;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterFormControlBuilder;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryRequest;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryRequestValueFetcher;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.ScopedAttributesViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.alert;
import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.text;
import static com.foreach.across.modules.entity.views.DefaultEntityViewFactory.ATTRIBUTE_CONTAINER_ELEMENT;
import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.find;

/**
 * Processor and page executor for {@link com.foreach.across.modules.entity.query.EntityQuery} based approach.
 * For this filter to work, the class must have a valid {@link com.foreach.across.modules.entity.query.EntityQueryExecutor}
 * and {@link com.foreach.across.modules.entity.query.EntityQueryParser}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Exposed
@Scope("prototype")
@Slf4j
public class EntityQueryFilterProcessor extends AbstractEntityFetchingViewProcessor
{
	/**
	 * Can hold an optional EQL statement that should be applied to the query being executed.
	 */
	public static final String EQL_PREDICATE_ATTRIBUTE_NAME = "_entityQueryPredicate";

	public static final String ENTITY_QUERY_REQUEST = "entityQueryRequest";

	/**
	 * Attribute on an {@link EntityPropertyDescriptor} that can optionally hold the {@link EntityQueryOps}
	 * operand that should be used when building a basic filter control for that property.
	 *
	 * @deprecated use {@code EntityQueryOps.class} instead
	 */
	@Deprecated
	public static final String ENTITY_QUERY_OPERAND = EntityQueryOps.class.getName();

	/**
	 * Css class that marks the control element which holds the value for the specified property.
	 * Only a single element should have the marker specified.
	 */
	public static final String ENTITY_QUERY_CONTROL_MARKER = "js-entity-query-control";

	/**
	 * Specifies whether the property value should be parsed as an {@link EQValue} or as an {@link EQString}.
	 * In the case of an {@link EQString}, the value should be {@code true}.
	 */
	public static final String ENTITY_QUERY_PROPERTY_TEXT_VALUE = "entityQueryPropertyTextValue";

	/**
	 * Attribute on an {@link EntityPropertyDescriptor} that holds the event name that should
	 * result in an update of the entity query. Defaults to {@code "change"}
	 */
	public static final String ENTITY_QUERY_CONTROL_EVENT = "entityQueryControlEvent";

	private static final String PARAM = "eqFilter";
	private static final String PARAM_PROPERTIES = "eqFilterProperties";
	public static final List<? extends Class<? extends Serializable>> TEXT_VALUE_TYPES = Arrays.asList( String.class, Date.class, LocalDate.class,
	                                                                                                    LocalTime.class, LocalDateTime.class );

	private EntityPropertyRegistryProvider propertyRegistryProvider;
	private EntityViewElementBuilderService viewElementBuilderService;
	private EntityQueryFacadeResolver entityQueryFacadeResolver;
	private EntityRegistry entityRegistry;  // todo check if different way to resolve the EntityTypeDescriptor ?
	private EQTypeConverter eqTypeConverter;

	/**
	 * Holds the configuration for this query filter processor.
	 */
	@Getter
	@Setter
	private EntityQueryFilterConfiguration filterConfiguration = EntityQueryFilterConfiguration.builder().build();

	/**
	 * Custom property registry.
	 */
	private MutableEntityPropertyRegistry propertyRegistry;

	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		Map<String, Object> properties = new HashMap<>();
		entityViewRequest.getEntityViewContext().getPropertyRegistry().getRegisteredDescriptors()
		                 .forEach( property -> properties.put( property.getName(), null ) );
		command.addExtension( PARAM, filterConfiguration.getDefaultQuery() != null ? filterConfiguration.getDefaultQuery().toString() : "" );
		command.addExtension( PARAM_PROPERTIES, properties );
		EntityQueryRequest entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setShowBasicFilter( filterConfiguration.isBasicMode() && filterConfiguration.isAdvancedMode() );
		command.addExtension( ENTITY_QUERY_REQUEST, entityQueryRequest );
	}

	@Override
	protected Iterable fetchItems( EntityViewRequest entityViewRequest, EntityView entityView, Sort sort ) {
		return fetchItems( entityViewRequest, entityView, null, sort );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Iterable fetchItems( EntityViewRequest entityViewRequest,
	                               EntityView entityView,
	                               Pageable pageable ) {
		return fetchItems( entityViewRequest, entityView, pageable, null );
	}

	private Iterable fetchItems( EntityViewRequest entityViewRequest,
	                             EntityView entityView,
	                             Pageable pageable,
	                             Sort sort ) {
		EntityViewContext viewContext = entityViewRequest.getEntityViewContext();
		EntityViewCommand command = entityViewRequest.getCommand();
		String filter = command.getExtension( PARAM, String.class );

		EntityQueryRequest entityQueryRequest = command.getExtension( ENTITY_QUERY_REQUEST );
		boolean shouldOnlySort = pageable == null;

		try {
			EntityQueryFacade queryFacade = resolveEntityQueryFacade( entityViewRequest );
			Assert.notNull( queryFacade, "No EntityQueryExecutor or EntityQueryFacade is available" );

			EntityQuery query = EntityQueryParser.parseRawQuery( filter );
			entityQueryRequest.setRawQuery( query );
			entityQueryRequest.setTranslatedRawQuery( queryFacade.convertToExecutableQuery( query ) );

			EntityQuery combinedPredicate = EntityQuery.all();
			combinedPredicate = EntityQueryUtils.and( combinedPredicate, filterConfiguration.getBasePredicate() );
			combinedPredicate = EntityQueryUtils.and( combinedPredicate, entityView.getAttribute( EQL_PREDICATE_ATTRIBUTE_NAME ) );

			query = EntityQueryUtils.and( entityQueryRequest.getTranslatedRawQuery(), queryFacade.convertToExecutableQuery( combinedPredicate ) );

			entityQueryRequest.setExecutableQuery( query );

			if ( viewContext.isForAssociation() ) {
				EntityAssociation association = viewContext.getEntityAssociation();
				val property = association.getTargetProperty();
				EntityQueryCondition propertyCondition = new EntityQueryCondition(
						property.getName(),
						property.getPropertyTypeDescriptor().isCollection() ? EntityQueryOps.CONTAINS : EntityQueryOps.EQ,
						viewContext.getParentContext().getEntity()
				);

				EntityQuery propertyPredicate = queryFacade.convertToExecutableQuery( EntityQuery.and( propertyCondition ) );
				query = EntityQuery.and( query, propertyPredicate );
			}
			if ( shouldOnlySort ) {
				return queryFacade.findAll( EntityQuery.and( query ), sort );
			}
			return queryFacade.findAll( EntityQuery.and( query ), pageable );
		}
		catch ( EntityQueryParsingException pe ) {
			String message = pe.getMessage();

			if ( pe.hasErrorExpressionPosition() ) {
				message += " ; position " + pe.getErrorExpressionPosition();
			}

			entityView.addAttribute( "filterError", message );

		}
		catch ( Exception e ) {
			entityView.addAttribute( "filterError", ExceptionUtils.getRootCauseMessage( e ) );
		}

		// Explicitly return null to avoid "0 users found" along with an exception
		return null;
	}

	@Override
	public void postRender( EntityViewRequest entityViewRequest, EntityView entityView ) {
		EntityViewCommand command = entityViewRequest.getCommand();
		EntityQueryRequest queryRequest = command.getExtension( ENTITY_QUERY_REQUEST, EntityQueryRequest.class );
		ContainerViewElement container = entityView.getAttribute( ATTRIBUTE_CONTAINER_ELEMENT, ContainerViewElement.class );
		ViewElementBuilderContext builderContext = EntityViewProcessorAdapter.retrieveBuilderContext();

		try (ScopedAttributesViewElementBuilderContext ignore = builderContext
				.withAttributeOverride( EntityViewModel.ENTITY, null )
				.withAttributeOverride( EntityPropertyBindingContext.class, EntityPropertyBindingContext.forReading( queryRequest ) )) {
			List<ViewElement> propertyFilters = buildFilterControls( entityViewRequest, builderContext );
			EntityQueryFilterFormControlBuilder filterForm = new EntityQueryFilterFormControlBuilder()
					.basicFilter( filterConfiguration.isBasicMode() )
					.advancedFilter( filterConfiguration.isAdvancedMode() )
					.basicControls( propertyFilters )
					.eqlControlName( "extensions[" + PARAM + "]" )
					.convertibleToBasicMode( queryRequest.isConvertibleToBasicMode() );

			if ( queryRequest.getRawQuery() != null ) {
				filterForm.query( queryRequest.getRawQuery() );
			}
			else {
				filterForm.query( command.getExtension( PARAM, String.class ) )
				          .convertibleToBasicMode( false );
			}

			if ( queryRequest.isShowBasicFilter() ) {
				filterForm.showBasicFilter();
			}
			else {
				filterForm.showAdvancedFilter();
			}

			// move the original actions
			Optional<ContainerViewElement> header = find( container, ListFormViewProcessor.DEFAULT_FORM_NAME + "-header", ContainerViewElement.class );
			header.ifPresent( h -> {
				h.addFirstChild( filterForm.build( builderContext ) );

				String errorMessage = entityView.getAttribute( "filterError", String.class );

				if ( !StringUtils.isBlank( errorMessage ) ) {
					container.addChild(
							alert().danger()
							       .add( text( errorMessage ) )
							       .build( builderContext )
					);
				}
			} );
		}
	}

	private List<ViewElement> buildFilterControls( EntityViewRequest entityViewRequest, ViewElementBuilderContext builderContext ) {
		EntityViewElementUtils.setCurrentEntity( builderContext, entityViewRequest.getCommand().getExtension( ENTITY_QUERY_REQUEST ) );

		if ( propertyRegistry == null ) {
			initializePropertyRegistry( entityViewRequest.getEntityViewContext().getPropertyRegistry() );
		}

		List<EntityPropertyDescriptor> properties = Collections.emptyList();
		if ( filterConfiguration.getPropertySelector() != null ) {
			properties = propertyRegistry.select( filterConfiguration.getPropertySelector() );
		}

		List<ViewElement> controls = new ArrayList<>();
		properties.stream()
		          .map( MutableEntityPropertyDescriptor.class::cast )
		          .peek( prop -> {
			          if ( prop.getPropertyTypeDescriptor() != null ) {
				          // ensure manual handling of EQL filtering properties
				          prop.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.MANUAL );
				          prop.setAttribute( EntityAttributes.CONTROL_NAME, "extensions[" + PARAM_PROPERTIES + "][" + prop.getName() + "]" );
				          EntityQueryRequestValueFetcher valueFetcher = new EntityQueryRequestValueFetcher(
						          eqTypeConverter, prop, retrieveEntityQueryOperand( prop ), filterConfiguration.isMultiValue( prop.getName() )
				          );

				          prop.setAttribute( EntityQueryOps.class, retrieveEntityQueryOperand( prop ) );
				          prop.setValueFetcher( valueFetcher );
			          }
		          } )
		          .map(
				          property -> viewElementBuilderService.getElementBuilder( property, determineViewElementMode( property ) )
				                                               .build( builderContext )
		          )
		          .forEach( controls::add );

		return controls;
	}

	private EntityQueryOps retrieveEntityQueryOperand( EntityPropertyDescriptor property ) {
		EntityQueryOps operand = property.getAttribute( EntityQueryOps.class );
		boolean isMultiValue = filterConfiguration.isMultiValue( property.getName() );

		if ( operand == null ) {
			EntityTypeDescriptor typeDescriptor = EntityUtils.resolveEntityTypeDescriptor( property.getPropertyTypeDescriptor(), entityRegistry );

			if ( String.class.equals( typeDescriptor.getSimpleTargetType() ) || ( typeDescriptor.isCollection() && typeDescriptor.isTargetTypeResolved() ) ) {
				operand = EntityQueryOps.CONTAINS;
			}
			else {
				operand = EntityQueryOps.EQ;
			}
		}

		return isMultiValue ? Optional.ofNullable( EntityQueryOps.resolveMultiValueOperand( operand ) ).orElse( operand ) : operand;
	}

	protected ViewElementMode determineViewElementMode( EntityPropertyDescriptor property ) {
		return filterConfiguration.isMultiValue( property.getName() ) ? ViewElementMode.FILTER_FORM.forMultiple() : ViewElementMode.FILTER_FORM;
	}

	protected EntityQueryFacade resolveEntityQueryFacade( EntityViewRequest viewRequest ) {
		return entityQueryFacadeResolver.forEntityViewRequest( viewRequest );
	}

	private void initializePropertyRegistry( EntityPropertyRegistry parent ) {
		propertyRegistry = propertyRegistryProvider.createForParentRegistry( parent );
		propertyRegistry.setId( parent.getId() + "_eqf" );
		filterConfiguration.getPropertyRegistryBuilder().apply( propertyRegistry );
	}

	@Autowired
	void setPropertyRegistryProvider( EntityPropertyRegistryProvider propertyRegistryProvider ) {
		this.propertyRegistryProvider = propertyRegistryProvider;
	}

	@Autowired
	void setViewElementBuilderService( EntityViewElementBuilderService viewElementBuilderService ) {
		this.viewElementBuilderService = viewElementBuilderService;
	}

	@Autowired
	void setEntityRegistry( EntityRegistry entityRegistry ) {
		this.entityRegistry = entityRegistry;
	}

	@Autowired
	void setEntityQueryFacadeResolver( EntityQueryFacadeResolver entityQueryFacadeResolver ) {
		this.entityQueryFacadeResolver = entityQueryFacadeResolver;
	}

	@Autowired
	void setEqTypeConverter( EQTypeConverter eqTypeConverter ) {
		this.eqTypeConverter = eqTypeConverter;
	}
}
