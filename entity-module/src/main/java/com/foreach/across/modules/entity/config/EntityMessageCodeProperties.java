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

package com.foreach.across.modules.entity.config;

import com.foreach.across.core.context.info.AcrossModuleInfo;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConfigurationProperties("entity-module")
public class EntityMessageCodeProperties
{
	/**
	 * Map of module name and list of message code prefixes.
	 */
	@Getter
	private final Map<String, String[]> messageCodes = new ConcurrentHashMap<>();

	/**
	 * Get all the message code prefixes that should be attached to the {@link com.foreach.across.modules.entity.support.EntityMessageCodeResolver}.
	 */
	public String[] getEntityMessageCodePrefixes( AcrossModuleInfo moduleInfo ) {
		return messageCodes.computeIfAbsent( moduleInfo.getName(), name -> new String[] { moduleInfo.getName() + ".entities" } );
	}
}
