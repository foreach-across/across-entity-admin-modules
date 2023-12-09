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

package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.DefaultEntityModel;
import com.foreach.across.modules.entity.registry.EntityFactory;
import com.foreach.across.modules.entity.registry.EntityModel;
import lombok.NonNull;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.format.Printer;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Builder to create a new {@link DefaultEntityModel} or modify an existing one.
 *
 * @author Arne Vandamme
 * @see EntityModel
 * @since 2.0.0
 */
public class EntityModelBuilder<T>
{
	private EntityFactory<T> entityFactory;
	private EntityInformation<?, ?> entityInformation;
	private Printer<T> labelPrinter;

	private Function<Serializable, T> findOneMethod;
	private UnaryOperator<T> saveMethod;
	private Consumer<T> deleteMethod;
	private Consumer<Serializable> deleteByIdMethod;

	/**
	 * Set the {@link EntityFactory} for this model.
	 *
	 * @param entityFactory to set
	 * @return current builder
	 */
	public EntityModelBuilder<T> entityFactory( EntityFactory<T> entityFactory ) {
		this.entityFactory = entityFactory;
		return this;
	}

	/**
	 * Set the {@link EntityInformation} for this model.
	 *
	 * @param entityInformation to set
	 * @return current builder
	 */
	public EntityModelBuilder<T> entityInformation( EntityInformation<?, ?> entityInformation ) {
		this.entityInformation = entityInformation;
		return this;
	}

	/**
	 * Set the label printer for this model as a function.
	 *
	 * @param labelFunction to use as
	 * @return current builder
	 */
	public EntityModelBuilder<T> labelPrinter( Function<T, String> labelFunction ) {
		return labelPrinter( ( e, l ) -> labelFunction.apply( e ) );
	}

	/**
	 * Set the label printer for this model.
	 *
	 * @param labelPrinter to set
	 * @return current builder
	 */
	public EntityModelBuilder<T> labelPrinter( Printer<T> labelPrinter ) {
		this.labelPrinter = labelPrinter;
		return this;
	}

	/**
	 * Set the method callback for finding a single entity by id.
	 *
	 * @param findOneMethod callback method
	 * @return current builder
	 */
	public EntityModelBuilder<T> findOneMethod( Function<Serializable, T> findOneMethod ) {
		this.findOneMethod = findOneMethod;
		return this;
	}

	/**
	 * Set the method callback for saving an entity.
	 *
	 * @param saveMethod callback method
	 * @return current builder
	 */
	public EntityModelBuilder<T> saveMethod( UnaryOperator<T> saveMethod ) {
		this.saveMethod = saveMethod;
		return this;
	}

	/**
	 * Set the method callback for deleting an entity.
	 *
	 * @param deleteMethod callback method
	 * @return current builder
	 */
	public EntityModelBuilder<T> deleteMethod( Consumer<T> deleteMethod ) {
		this.deleteMethod = deleteMethod;
		return this;
	}

	/**
	 * Set the method callback based on the unique id of the entity.
	 * Will automatically convert into an entity based delete method that uses the {@link EntityModel} itself
	 * to lookup the id of the entity.
	 * <p/>
	 * Will only be used if the builder was not called with {@link #deleteMethod(Consumer)}.
	 *
	 * @param deleteMethodById callback method
	 * @return current builder
	 */
	public EntityModelBuilder<T> deleteByIdMethod( Consumer<Serializable> deleteMethodById ) {
		this.deleteByIdMethod = deleteMethodById;
		return this;
	}

	/**
	 * Apply an additional consumer to this builder.
	 *
	 * @param consumer to apply
	 * @return current builder
	 */
	public EntityModelBuilder<T> and( @NonNull Consumer<EntityModelBuilder<T>> consumer ) {
		consumer.accept( this );
		return this;
	}

	/**
	 * Create a new model.
	 *
	 * @return newly created model
	 */
	public EntityModel<T, Serializable> build() {
		DefaultEntityModel<T, Serializable> model = new DefaultEntityModel<>();
		apply( model );
		return model;
	}

	/**
	 * Modify an existing {@link DefaultEntityModel}.
	 *
	 * @param model to be customized with the builder settings
	 */
	public void apply( DefaultEntityModel<T, Serializable> model ) {
		if ( entityFactory != null ) {
			model.setEntityFactory( entityFactory );
		}
		if ( entityInformation != null ) {
			model.setEntityInformation( (EntityInformation<T, Serializable>) entityInformation );
		}
		if ( labelPrinter != null ) {
			model.setLabelPrinter( labelPrinter );
		}
		if ( findOneMethod != null ) {
			model.setFindOneMethod( findOneMethod );
		}
		if ( saveMethod != null ) {
			model.setSaveMethod( saveMethod );
		}
		if ( deleteMethod != null ) {
			model.setDeleteMethod( deleteMethod );
		}
		else if ( deleteByIdMethod != null ) {
			// wrap convert by id method in a regular delete method
			model.setDeleteMethod( entity -> deleteByIdMethod.accept( model.getId( entity ) ) );
		}
	}
}
