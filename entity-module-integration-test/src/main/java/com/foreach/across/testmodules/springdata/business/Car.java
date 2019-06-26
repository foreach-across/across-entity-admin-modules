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

package com.foreach.across.testmodules.springdata.business;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * @author Arne Vandamme
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Car implements Persistable<String>
{
	@Id
	private String id;

	@ManyToOne
	private Company company;

	@Transient
	private boolean isNew;

	@Override
	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany( Company company ) {
		this.company = company;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	public void setNew( boolean isNew ) {
		this.isNew = isNew;
	}
}
