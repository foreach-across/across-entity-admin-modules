package com.foreach.across.modules.experimental.webutility.viewelements;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Steven Gentens
 * @since 0.0.1
 */
@Component
@Exposed
@RequiredArgsConstructor
public class EditableValuesUtils
{
	private final ConversionService mvcConversionService;
	private final EntityRegistry entityRegistry;

	/**
	 * Attempts to resolve the property id of the current property being rendered.
	 */
	public Optional<String> resolveEntityPropertyId( ViewElementBuilderContext builderContext ) {
		EntityPropertyDescriptor property = EntityViewElementUtils.currentPropertyDescriptor( builderContext );
		EntityViewContext entityViewContext = builderContext.getAttribute( EntityViewModel.VIEW_CONTEXT, EntityViewContext.class );
		Object entity = EntityViewElementUtils.currentEntity( builderContext );

		if ( entity != null ) {
			if ( !( builderContext instanceof IteratorViewElementBuilderContext ) && !entityViewContext.holdsEntity() && entityViewContext
					.isForAssociation() ) {
				entityViewContext = entityViewContext.getParentContext();
			}

			if ( entityViewContext != null ) {
				return resolveEntityPropertyId( entityViewContext.getEntityConfiguration(), property, entity );
			}
		}

		return Optional.empty();
	}

	/**
	 * Attempts to resolve the property id of the given property of the entity view context.
	 */
	public Optional<String> resolveEntityPropertyId( @NonNull EntityViewContext entityViewContext, @NonNull EntityPropertyDescriptor propertyDescriptor ) {
		return resolveEntityPropertyId( entityViewContext.getEntityConfiguration(), propertyDescriptor, entityViewContext.getEntity() );
	}

	/**
	 * Attempts to resolve the property id of the given property for the entity
	 */
	@SuppressWarnings("unchecked")
	public Optional<String> resolveEntityPropertyId( @NonNull EntityConfiguration entityConfiguration, @NonNull EntityPropertyDescriptor propertyDescriptor,
	                                                 Object entity ) {
		EntityModel<Object, ?> entityModel = entityConfiguration.getEntityModel();

		if ( entity != null && entityModel != null && !entityModel.isNew( entity ) ) {
			String entityId = mvcConversionService.convert( entityModel.getId( entity ), String.class );
			return Optional.of( entityConfiguration.getName() + "/" + entityId + "/" + propertyDescriptor.getName() );
		}

		return Optional.empty();
	}

	public String resolvePropertyReferenceId( ViewElementBuilderContext builderContext ) {
		EntityPropertyDescriptor property = EntityViewElementUtils.currentPropertyDescriptor( builderContext );
		Object entity = EntityViewElementUtils.currentEntity( builderContext );

		if ( entity != null && property.getName().contains( "." ) && property.isNestedProperty() ) {
			EntityPropertyDescriptor parentDescriptor = property.getParentDescriptor();
			EntityConfiguration parentEntityConfiguration = entityRegistry.getEntityConfiguration( parentDescriptor.getPropertyType() );
			if ( parentEntityConfiguration != null && parentEntityConfiguration.hasEntityModel() ) {
				EntityPropertyDescriptor referencedPropertyDescriptor =
						parentEntityConfiguration.getPropertyRegistry().getProperty( property.getName().substring( property.getName().indexOf( "." ) + 1 ) );
				Object referencedEntity = parentDescriptor.getPropertyValue( entity );
				if ( referencedPropertyDescriptor != null && referencedEntity != null ) {
					return resolveEntityPropertyId( parentEntityConfiguration, referencedPropertyDescriptor, referencedEntity ).orElse( "" );
				}
			}
		}
		return "";
	}
}
