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

package com.foreach.across.samples.entity.application.business;

import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Entity
@Table(name = "test_user")
@Getter
@Setter
public class User extends SettableIdBasedEntity<User>
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_test_user_id")
	@GenericGenerator(
			name = "seq_test_user_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_test_user_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "1")
			}
	)
	private Long id;

	@NotBlank
	@Length(max = 250)
	@Column(unique = true)
	private String name;

	@NotNull
	@ManyToOne
	private Group group;

	@Column
	private Date registrationDate;

	@Getter
	@Setter
	@ElementCollection
	@Column(name = "telephone")
	private Set<String> phoneNumbers = new LinkedHashSet<>();

	//@NotEmpty
	@ElementCollection
	@CollectionTable
	@Getter
	@Setter
	private Set<Address> address = new HashSet<>();


	@Column
	private String profilePicture;

	private boolean active;

	public User() {
	}

	public User( String name ) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup( Group group ) {
		this.group = group;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate( Date registrationDate ) {
		this.registrationDate = registrationDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive( boolean active ) {
		this.active = active;
	}
}
