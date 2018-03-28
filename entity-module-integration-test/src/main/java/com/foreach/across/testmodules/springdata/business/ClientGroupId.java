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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Arne Vandamme
 */
@JsonSerialize(using = ClientGroupId.ClientGroupIdSerializer.class)
@Embeddable
public class ClientGroupId implements Serializable
{
	@NotNull
	@ManyToOne
	private Client client;

	@NotNull
	@ManyToOne
	private Group group;

	public Client getClient() {
		return client;
	}

	public void setClient( Client client ) {
		this.client = client;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup( Group group ) {
		this.group = group;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		ClientGroupId that = (ClientGroupId) o;

		if ( client != null ? !client.equals( that.client ) : that.client != null ) {
			return false;
		}
		if ( group != null ? !group.equals( that.group ) : that.group != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = client != null ? client.hashCode() : 0;
		result = 31 * result + ( group != null ? group.hashCode() : 0 );
		return result;
	}

	static class ClientGroupIdSerializer extends StdSerializer<ClientGroupId>
	{
		public ClientGroupIdSerializer() {
			super( ClientGroupId.class );
		}

		@Override
		public void serialize( ClientGroupId value, JsonGenerator gen, SerializerProvider provider ) throws IOException {
			gen.writeRawValue( value.getClient().getId() + "-" + value.getGroup().getId() );
		}
	}
}
