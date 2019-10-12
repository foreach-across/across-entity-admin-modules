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
package com.foreach.across.modules.entity.registrars;

import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.core.events.AcrossContextBootstrappedEvent;
import com.foreach.across.core.events.AcrossModuleBootstrappedEvent;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntityRegistryConfigurer;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Takes care of running all registered {@link com.foreach.across.modules.entity.registrars.EntityRegistrar}
 * instances when a module has bootstrapped, and afterwards applying the {@link com.foreach.across.modules.entity.config.EntityConfigurer}
 * instances from the module.
 * <p/>
 * A registrar automatically creates an EntityConfiguration, whereas an
 * {@link com.foreach.across.modules.entity.config.EntityConfigurer} provides a builders that can be used to modify
 * existing configurations.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.config.EntityConfigurer
 * @see com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder
 * @see com.foreach.across.modules.entity.registrars.EntityRegistrar
 */
@Component
@RequiredArgsConstructor
public class ModuleEntityRegistration
{
	private final AcrossContextInfo contextInfo;
	private final MutableEntityRegistry entityRegistry;
	private final AutowireCapableBeanFactory beanFactory;
	private final AcrossModuleInfo currentModule;

	private boolean previousModulesRegistered = false;

	@SuppressWarnings("all")
	@RefreshableCollection(includeModuleInternals = true, incremental = true)
	private Collection<EntityRegistrar> registrars;

	@EventListener
	public void moduleBootstrapped( AcrossModuleBootstrappedEvent moduleBootstrappedEvent ) {
		if ( !currentModule.equals( moduleBootstrappedEvent.getModule() ) ) {
			if ( !previousModulesRegistered ) {
				// todo: workaround for AX-251 (beans from creating module are only added once the module is bootstrapped)
				previousModulesRegistered = true;
				registerAlreadyBootstrappedModules();
			}

			applyModule( moduleBootstrappedEvent.getModule() );
		}
	}

	private void registerAlreadyBootstrappedModules() {
		for ( AcrossModuleInfo moduleInfo : contextInfo.getModules() ) {
			switch ( moduleInfo.getBootstrapStatus() ) {
				case BootstrapBusy:
				case Bootstrapped:
					applyModule( moduleInfo );
					break;
				default:
					break;
			}
		}
	}

	@EventListener
	public void contextBootstrapped( AcrossContextBootstrappedEvent contextBootstrappedEvent ) {
		AcrossContextBeanRegistry beanRegistry = AcrossContextUtils.getBeanRegistry( contextBootstrappedEvent.getContext() );

		// Configure the configuration builder
		EntityRegistryConfigurer registryBuilder = new EntityRegistryConfigurer( beanFactory );
		for ( EntityConfigurer configurer : beanRegistry.getBeansOfType( EntityConfigurer.class, true ) ) {
			registryBuilder.add( configurer );
		}

		registryBuilder.applyTo( entityRegistry );
	}

	private void applyModule( AcrossModuleInfo moduleInfo ) {
		AcrossContextBeanRegistry beanRegistry = AcrossContextUtils.getBeanRegistry( moduleInfo );

		for ( EntityRegistrar registrar : registrars ) {
			registrar.registerEntities( entityRegistry, moduleInfo, beanRegistry );
		}
	}
}
