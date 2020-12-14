package com.foreach.across.modules.experimental.entitycontrols.domain;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.support.EntityViewMessageSource;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.helpers.EntityViewElementBatch;
import com.foreach.across.modules.experimental.entitycontrols.support.EntityConfigurationResolver;
import com.foreach.across.modules.web.support.LocalizedTextResolver;
import com.foreach.across.modules.web.support.MessageCodeSupportingLocalizedTextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * The entity control factory can be used to create controls for a given dto or entity.
 * When a dto is given that is not yet registered as an entity in entity module, the dto will be registered as an entity
 * and the entityConfiguration for this class will be created on the fly.
 * <p>
 * The main method {@link #createControlsForClass(Class)} will return a {@link EntityControls} instance that is able to
 * render the controls.
 *
 * @author Stijn Vanhoof
 * @see EntityControls for more information
 */
@Exposed
@Component
@RequiredArgsConstructor
public class EntityControlFactory
{
	private final EntityConfigurationResolver entityConfigurationResolver;
	private final EntityViewElementBuilderService builderService;

	/**
	 * Generate the {@link EntityControls} for a given entityType
	 *
	 * @param entityType can be a registred entity or a dto
	 */
	@SuppressWarnings("unchecked")
	public <V> EntityControls<V> createControlsForClass( Class<V> entityType ) {
		EntityViewElementBatch<V> batchForEntity = new EntityViewElementBatch<>( builderService );
		EntityConfiguration entityConfiguration = entityConfigurationResolver.resolve( entityType );

		batchForEntity.setPropertyRegistry( entityConfiguration.getPropertyRegistry() );
		batchForEntity.setPropertySelector( new EntityPropertySelector( EntityPropertySelector.ALL ) );

		batchForEntity.setAttribute( EntityMessageCodeResolver.class, entityConfiguration.getEntityMessageCodeResolver() );

		EntityViewMessageSource viewMessageSource = new EntityViewMessageSource( entityConfiguration.getEntityMessageCodeResolver() );
		batchForEntity.setAttribute( MessageSource.class, viewMessageSource );
		batchForEntity.setAttribute( LocalizedTextResolver.class, new MessageCodeSupportingLocalizedTextResolver( viewMessageSource ) );

		return new EntityControls<>( batchForEntity, entityConfiguration );
	}
}
