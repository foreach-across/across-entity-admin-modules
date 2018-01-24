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
package com.foreach.across.modules.entity.web;

/**
 * @author Arne Vandamme
 */
public interface EntityLinkBuilder
{
	/**
	 * @return link to the overview/list page
	 */
	String overview();

	/**
	 * @return link to create-new page
	 */
	String create();

	/**
	 * @param entity being updated
	 * @return link to update page for entity
	 */
	String update( Object entity );

	/**
	 * @param entity being deleted
	 * @return link to delete page for entity
	 */
	String delete( Object entity );

	/**
	 * @param entity being viewed
	 * @return link to view page for entity
	 */
	String view( Object entity );

	/**
	 * @param entity being viewed
	 * @return base link for associations
	 */
	String associations( Object entity );

	/**
	 * Creates a new link builder that represents the current linkbuilder as an association from a source entity,
	 * this will use {@link com.foreach.across.modules.entity.web.EntityLinkBuilder#associations(Object)} on the
	 * source link builder for prefixing the current link builder.
	 */
	EntityLinkBuilder asAssociationFor( EntityLinkBuilder sourceLinkBuilder, Object sourceEntity );
}
