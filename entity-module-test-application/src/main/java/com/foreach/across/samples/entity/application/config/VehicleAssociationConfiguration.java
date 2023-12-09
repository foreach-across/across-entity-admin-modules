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

package com.foreach.across.samples.entity.application.config;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.samples.entity.application.business.HiddenAssociations;
import com.foreach.across.samples.entity.application.business.Vehicle;
import com.foreach.across.samples.entity.application.business.VehicleOwner;
import com.foreach.across.samples.entity.application.business.VehiclePlate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VehicleAssociationConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.all().association( asb -> asb.assignableTo( HiddenAssociations.class ).hide() );
		entities.withType( Vehicle.class ).association( asb -> asb.withType( VehiclePlate.class ).show() );
		entities.withType( VehicleOwner.class ).association( asb -> asb.withType( Vehicle.class ).hide() );
	}
}
