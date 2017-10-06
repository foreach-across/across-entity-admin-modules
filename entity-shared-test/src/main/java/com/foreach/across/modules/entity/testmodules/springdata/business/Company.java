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

package com.foreach.across.modules.entity.testmodules.springdata.business;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Arne Vandamme
 */
@Entity
public class Company implements Persistable<String>
{
	@Transient
	private boolean isNew;

	@Id
	@NotNull
	@Length(max = 20)
	@Column(name = "company_id")
	private String id;

	@Column
	private CompanyStatus status;

	@Column(name = "company_number")
	private int number;

	@Column
	private Date created;

	@ManyToMany
	private Set<Representative> representatives = new HashSet<>();

	@ManyToOne
	private Group group;

	@Embedded
	private Address address = new Address();

	public void setId( String id ) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	public void setNew( boolean isNew ) {
		this.isNew = isNew;
	}

	public Company() {
	}

	public Company( String id, int number ) {
		this( id, number, new Date() );
	}

	public Company( String id, int number, Date created ) {
		this.id = id;
		this.number = number;
		this.created = created;
		setNew( true );
	}

	public CompanyStatus getStatus() {
		return status;
	}

	public void setStatus( CompanyStatus status ) {
		this.status = status;
	}

	public Set<Representative> getRepresentatives() {
		return representatives;
	}

	public void setRepresentatives( Set<Representative> representatives ) {
		this.representatives = representatives;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup( Group group ) {
		this.group = group;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress( Address address ) {
		this.address = address;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber( int number ) {
		this.number = number;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated( Date created ) {
		this.created = created;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		Company company = (Company) o;

		if ( id != null ? !id.equals( company.id ) : company.id != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
