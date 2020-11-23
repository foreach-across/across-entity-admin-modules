package com.foreach.across.modules.experimental.webutility.domain;

import com.foreach.across.modules.entity.actions.EntityConfigurationAllowableActionsBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import lombok.RequiredArgsConstructor;

/**
 * Possibility to have a separate AllowableActionBuilder on association views
 * <p>
 * Example of usage:
 * <p>
 * First add an attribute on the assaciation and then
 *
 * @code .attribute(EntityConfigurationAllowableActionsBuilder.class, companyUsersAllowableActions)
 *
 * <p>
 * Add a post processor on the target entity configuration
 * @code .postProcessor(target - > {
 *EntityConfigurationAllowableActionsBuilder original = userConfig.getAllowableActionsBuilder ();
 * userConfig.setAllowableActionsBuilder( new AssociationProxyAllowableActionsBuilder( entityViewContext, original ) );
 * } )
 */
@RequiredArgsConstructor
public class AssociationProxyAllowableActionsBuilder implements EntityConfigurationAllowableActionsBuilder {
    private final EntityViewContext entityViewContext;
    private final EntityConfigurationAllowableActionsBuilder original;

    @Override
    public AllowableActions getAllowableActions(EntityConfiguration<?> entityConfiguration) {
        if (entityViewContext != null && entityViewContext.isForAssociation()) {
            EntityAssociation entityAssociation = entityViewContext.getEntityAssociation();
            EntityConfigurationAllowableActionsBuilder associationAllowableActions = entityAssociation.getAttribute(EntityConfigurationAllowableActionsBuilder.class);
            if (associationAllowableActions != null) {
                return associationAllowableActions.getAllowableActions(entityConfiguration);
            }
        }
        return original.getAllowableActions(entityConfiguration);
    }

    @Override
    public <V> AllowableActions getAllowableActions(EntityConfiguration<V> entityConfiguration, V entity) {
        if (entityViewContext != null && entityViewContext.isForAssociation()) {
            EntityAssociation entityAssociation = entityViewContext.getEntityAssociation();
            if (entityAssociation.hasAttribute(EntityConfigurationAllowableActionsBuilder.class)) {
                return entityAssociation.getAttribute(EntityConfigurationAllowableActionsBuilder.class).getAllowableActions(entityConfiguration, entity);
            }
        }
        return original.getAllowableActions(entityConfiguration, entity);
    }

}