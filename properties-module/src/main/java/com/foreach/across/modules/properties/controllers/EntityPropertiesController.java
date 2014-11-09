package com.foreach.across.modules.properties.controllers;

import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@AdminWebController
@RequestMapping("/entities/{entityConfig}")
public class EntityPropertiesController
{
	@RefreshableCollection(includeModuleInternals = true)
	private Collection<EntityPropertiesDescriptor> propertiesDescriptors;

//	@Autowired
//	private AdminWeb adminWeb;
//
//	@Autowired
//	private EntityRegistry entityRegistry;
//
//	@Autowired
//	private EntityFormFactory formFactory;
//
//	@Autowired
//	private MenuFactory menuFactory;
//
//	@Autowired
//	private Validator entityValidatorFactory;
//
//	@InitBinder
//	protected void initBinder( WebDataBinder binder ) {
//		binder.setValidator( entityValidatorFactory );
//	}

	@Event
	protected void registerCustomPropertiesTab( EntityAdminMenuEvent<IdBasedEntity> menu ) {
		if ( menu.isForUpdate() && hasProperties( menu.getEntityClass() ) ) {
			menu.builder().item( "properties",
			                     "Properties",
			                     "/entities/" + ( menu.getEntityClass().getSimpleName().toLowerCase() ) + "/"
					                     + menu.getEntity().getId()
					                     + "/properties" )
			    .order( Ordered.HIGHEST_PRECEDENCE + 1 );
		}
	}

	private boolean hasProperties( Class<?> entityClass ) {
		for ( EntityPropertiesDescriptor descriptor : propertiesDescriptors ) {
			if ( descriptor.entityClass() != null && descriptor.entityClass().isAssignableFrom( entityClass ) ) {
				return true;
			}
		}

		return false;
	}

//	@ModelAttribute("entity")
//	public Object entity( @PathVariable("entityConfig") String entityType,
//	                      @RequestParam(value = "id") long entityId,
//	                      ModelMap model
//	) throws Exception {
//		EntityConfiguration entityConfiguration = entityRegistry.getEntityByPath( entityType );
//
//		Object entity = entityConfiguration.getEntityClass().newInstance();
//
//		if ( entityId != null && entityId != 0 ) {
//			Object original = entityConfiguration.getRepository().getById( entityId );
//			model.addAttribute( "original", entityConfiguration.wrap( original ) );
//			BeanUtils.copyProperties( original, entity );
//		}
//
//		return entity;
//	}

}
