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
package com.foreach.across.modules.metrics.config;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class BaseMetricModuleConfiguration
{
	@Autowired
	private AcrossMetricRegistry acrossMetricRegistry;

	private final MetricRegistry metricRegistry;

	protected BaseMetricModuleConfiguration( MetricRegistry metricRegistry ) {
		this.metricRegistry = metricRegistry;
	}

	@PostConstruct
	public void validate() {
		validateDependency();
		register();
	}

	public void register() {
		acrossMetricRegistry.register( this );
	}

	public AcrossMetricRegistry acrossMetricRegistry() {
		return acrossMetricRegistry;
	}

	public void validateDependency() {
		String dependencyClass = getDependencyClass();
		if( dependencyClass != null ) {
			try {
				Class.forName( dependencyClass );
			}
			catch ( ClassNotFoundException e ) {
				String implementationVersion = Metric.class.getPackage().getImplementationVersion();
				throw new IllegalArgumentException( this.getClass() + " Metrics depends on " + getDependencyInfo() + ", please add the dependency or disable the metric in MetricModuleSettings version: " + implementationVersion );
			}
		}
	}

	public abstract String getDependencyClass();

	public String getDependencyInfo() {
		return StringUtils.EMPTY;
	}

	public abstract String getName();

	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}
}
