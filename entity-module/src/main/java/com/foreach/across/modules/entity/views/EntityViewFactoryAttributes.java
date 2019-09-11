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

package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import lombok.experimental.UtilityClass;

import java.util.function.BiPredicate;

/**
 * Contains common {@link EntityViewFactory} configuration attributes.
 * During view processing these can usually be retrieved using {@link EntityViewRequest#getConfigurationAttributes()}.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@UtilityClass
public class EntityViewFactoryAttributes
{
	private static final BiPredicate<EntityViewFactory, EntityViewContext> DEFAULT_ACCESS_VALIDATOR = ( viewFactory, viewContext ) -> {
		AllowableAction requiredAction = viewFactory.getAttribute( AllowableAction.class );
		return requiredAction == null || ( viewContext != null && viewContext.getAllowableActions().contains( requiredAction ) );
	};

	/**
	 * Contains the name of the view.
	 */
	public static final String VIEW_NAME = EntityViewFactory.class.getName() + ".VIEW_NAME";

	/**
	 * Contains the template name of the view.
	 */
	public static final String VIEW_TEMPLATE_NAME = EntityViewFactory.class.getName() + ".VIEW_TEMPLATE_NAME";

	/**
	 * Contains a {@link java.util.function.Consumer} that takes an {@link EntityAdminMenuEvent} as parameter.
	 * Any view with this attribute will usually generate a menu item when the entity menu is being built.
	 */
	public static final String ADMIN_MENU = EntityViewFactory.class.getName() + ".ADMIN_MENU";

	/**
	 * Contains a {@code java.util.function.BiPredicate<EntityViewFactory,EntityViewContext>} that is to be used
	 * for checking if access to the {@link EntityViewFactory} is allowed. Usually the value of {@link #defaultAccessValidator()}
	 * instance will be set, which checks on the presence of an {@link AllowableAction}.
	 * <p/>
	 * Note that it is not strictly required to have a {@link EntityViewContext} for using the validator,
	 * meaning that implementations should take into account that the second argument can be {@code null}.
	 */
	public static final String ACCESS_VALIDATOR = EntityViewFactory.class.getName() + ".ACCESS_VALIDATOR";

	/**
	 * Returns a default access validator implementation that checks if there is an {@link AllowableAction}
	 * attribute on the {@link EntityViewFactory} and expects the action to be present in the {@link EntityViewContext#getAllowableActions()}.
	 * <p/>
	 * If there is no {@link AllowableAction} registered, the predicate will always allow access.
	 * If there is an {@link AllowableAction} but no {@link EntityViewContext} available, access will always be denied.
	 * <p/>
	 * A predicate like this is usually registered as the {@link #ACCESS_VALIDATOR} attribute.
	 *
	 * @return predicate
	 */
	public static BiPredicate<EntityViewFactory, EntityViewContext> defaultAccessValidator() {
		return DEFAULT_ACCESS_VALIDATOR;
	}
}
