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
package com.foreach.across.modules.entity.registry;

import lombok.NonNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.format.Printer;

import java.io.Serializable;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Default implementation of {@link EntityModel} that allows customizing the different methods
 * using callbacks and delegate instances.
 * <ul>
 * <li>{@link EntityFactory} for creation of instances and transforming them into dtos</li>
 * <li>{@link EntityInformation} for accessing an entity metadata</li>
 * <li>{@link Printer} for generating the label</li>
 * <li>{@link Function} for save and find methods</li>
 * <li>{@link Consumer} for delete method</li>
 * </ul>
 *
 * @param <T> entity type
 * @param <U> id type of the entity
 * @author Arne Vandamme
 * @see EntityFactory
 * @see EntityInformation
 * @see Printer
 */
public class DefaultEntityModel<T, U extends Serializable> implements EntityModel<T, U>
{
	private EntityFactory<T> entityFactory;
	private EntityInformation<T, U> entityInformation;
	private Printer<T> labelPrinter;

	private Function<U, T> findOneMethod;
	private UnaryOperator<T> saveMethod;
	private Consumer<T> deleteMethod;

	/**
	 * Set the printer to be used for calls to {@link #getLabel(Object)} and {@link #getLabel(Object, Locale)}.
	 *
	 * @param labelPrinter instance
	 */
	public void setLabelPrinter( @NonNull Printer<T> labelPrinter ) {
		this.labelPrinter = labelPrinter;
	}

	@Override
	public String getLabel( T entity ) {
		return getLabel( entity, LocaleContextHolder.getLocale() );
	}

	@Override
	public String getLabel( T entity, Locale locale ) {
		return labelPrinter.print( entity, locale );
	}

	/**
	 * Set the {@link EntityFactory} delegate that should be used for creating a new instance or converting
	 * an existing instance to a dto.
	 *
	 * @param entityFactory instance
	 */
	public void setEntityFactory( @NonNull EntityFactory<T> entityFactory ) {
		this.entityFactory = entityFactory;
	}

	@Override
	public T createNew( Object... args ) {
		return entityFactory.createNew( args );
	}

	@Override
	public T createDto( T entity ) {
		return entityFactory.createDto( entity );
	}

	/**
	 * Set the callback method to be used for finding a single entity based on its id.
	 *
	 * @param findOneMethod callback
	 */
	public void setFindOneMethod( @NonNull Function<U, T> findOneMethod ) {
		this.findOneMethod = findOneMethod;
	}

	@Override
	public T findOne( U id ) {
		return findOneMethod.apply( id );
	}

	/**
	 * Set the callback method to be used for saving a single entity.
	 *
	 * @param saveMethod callback
	 */
	public void setSaveMethod( @NonNull UnaryOperator<T> saveMethod ) {
		this.saveMethod = saveMethod;
	}

	@Override
	public T save( T entity ) {
		return saveMethod.apply( entity );
	}

	/**
	 * Set the callback method to be used when deleting an entity.
	 *
	 * @param deleteMethod callback
	 */
	public void setDeleteMethod( @NonNull Consumer<T> deleteMethod ) {
		this.deleteMethod = deleteMethod;
	}

	@Override
	public void delete( T entity ) {
		deleteMethod.accept( entity );
	}

	/**
	 * Set the metadata provider for this entity type.
	 *
	 * @param entityInformation implementation
	 */
	public void setEntityInformation( @NonNull EntityInformation<T, U> entityInformation ) {
		this.entityInformation = entityInformation;
	}

	@Override
	public boolean isNew( T entity ) {
		return entityInformation.isNew( entity );
	}

	@Override
	public U getId( T entity ) {
		return entityInformation.getId( entity );
	}

	@Override
	public Class<U> getIdType() {
		return entityInformation.getIdType();
	}

	@Override
	public Class<T> getJavaType() {
		return entityInformation.getJavaType();
	}
}
