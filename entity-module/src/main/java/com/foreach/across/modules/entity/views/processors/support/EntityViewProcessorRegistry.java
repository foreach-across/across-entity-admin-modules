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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.modules.entity.views.EntityViewProcessor;
import lombok.*;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a collection of {@link com.foreach.across.modules.entity.views.EntityViewProcessor}s, where
 * every processor has a unique name and optionally an order specified.  When dispatching a consumer to
 * the processors, all matching processors will be called in order.
 * <p/>
 * The default order for a processor will be 1000, the default name is the fully qualified class name.
 * Every processor must have a unique name.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityViewProcessorRegistry
{
	public static final int DEFAULT_ORDER = 1000;

	private final List<EntityViewProcessorRegistration> registrations = new ArrayList<>();

	/**
	 * Add a processor to the registry.  The name will be the fully qualified class name and the order will be the {@link #DEFAULT_ORDER}.
	 * An exception will be thrown if another processor with that name is already present.
	 *
	 * @param entityViewProcessor processor to add
	 */
	public void addProcessor( @NonNull EntityViewProcessor entityViewProcessor ) {
		addProcessor( ClassUtils.getUserClass( entityViewProcessor ).getName(), entityViewProcessor );
	}

	/**
	 * Add a processor with a specific order to the registry.  The name will be the fully qualified class name.
	 * An exception will be thrown if another processor with that name is already present.
	 *
	 * @param entityViewProcessor processor to add
	 * @param order               for the processor
	 */
	public void addProcessor( @NonNull EntityViewProcessor entityViewProcessor, int order ) {
		addProcessor( ClassUtils.getUserClass( entityViewProcessor ).getName(), entityViewProcessor, order );
	}

	/**
	 * Add a processor with a specific name to the registry.
	 * An exception will be thrown if another processor with that name is already present.
	 * The order will be the {@link #DEFAULT_ORDER}.
	 *
	 * @param processorName       unique name for the processor
	 * @param entityViewProcessor processor to add
	 */
	public void addProcessor( String processorName, EntityViewProcessor entityViewProcessor ) {
		addProcessor( processorName, entityViewProcessor, DEFAULT_ORDER );
	}

	/**
	 * Add a processor with a specific name and order to the registry.
	 * An exception will be thrown if another processor with that name is already present.
	 *
	 * @param processorName       unique name for the processor
	 * @param entityViewProcessor processor to add
	 * @param order               for the processor
	 */
	public void addProcessor( String processorName, EntityViewProcessor entityViewProcessor, int order ) {
		getProcessorRegistration( processorName ).ifPresent( p -> {
			throw new IllegalArgumentException( "There is already a processor registered with name " + processorName );
		} );

		registrations.add( new EntityViewProcessorRegistration( processorName, entityViewProcessor, order ) );
		sortRegistrations();
	}

	/**
	 * @return the list of {@link EntityViewProcessor} instances in order
	 */
	public Collection<EntityViewProcessor> getProcessors() {
		return Collections.unmodifiableList(
				registrations.stream()
				             .map( EntityViewProcessorRegistration::getProcessor )
				             .collect( Collectors.toList() )
		);
	}

	/**
	 * @return the list of processor names in order
	 */
	public Collection<String> getProcessorNames() {
		return Collections.unmodifiableList(
				registrations.stream()
				             .map( EntityViewProcessorRegistration::getProcessorName )
				             .collect( Collectors.toList() )
		);
	}

	/**
	 * @param processorName name of the processor
	 * @return true if a processor with that name is registered
	 */
	public boolean contains( String processorName ) {
		return getProcessorRegistration( processorName ).isPresent();
	}

	/**
	 * @param processorName name of the processor
	 * @return true if a processor with that name was present and has been removed, false if it was not present to begin with
	 */
	public boolean remove( String processorName ) {
		Optional<EntityViewProcessorRegistration> processorRegistration = getProcessorRegistration( processorName );
		processorRegistration.ifPresent( EntityViewProcessorRegistration::remove );
		return processorRegistration.isPresent();
	}

	/**
	 * @return the registration record of the processor with that name, allows instant modification of processor value or order
	 */
	public Optional<EntityViewProcessorRegistration> getProcessorRegistration( String processorName ) {
		return registrations.stream().filter( r -> r.getProcessorName().equals( processorName ) ).findFirst();
	}

	/**
	 * Get the processor value, expecting it to be of a specific type.
	 * If no processor with that name is present, the optional will be empty.  If there is a processor but the type
	 * is different, a {@link ClassCastException} will be thrown instead.
	 *
	 * @param expectedType the processor should have
	 * @param <T>          type
	 * @return processor or {@link ClassCastException} if not of the expected type
	 */
	public <T extends EntityViewProcessor> Optional<T> getProcessor( String processorName, Class<T> expectedType ) {
		Optional<EntityViewProcessorRegistration> registration = getProcessorRegistration( processorName );

		if ( registration.isPresent() ) {
			return Optional.ofNullable( registration.get().getProcessor( expectedType ) );
		}

		return Optional.empty();
	}

	/**
	 * Dispatch a consumer to all processors.  This will apply the consumer to all processors in their
	 * registration order.
	 *
	 * @param consumer to apply
	 */
	public void dispatch( Consumer<EntityViewProcessor> consumer ) {
		dispatch( consumer, EntityViewProcessor.class );
	}

	/**
	 * Dispatch a consumer to all processors that are of the specific type.
	 * This will apply to consumer to all processors that match the required type, in registration order.
	 *
	 * @param consumer      to apply
	 * @param processorType the processors should have
	 * @param <U>           processor type
	 */
	public <U> void dispatch( Consumer<U> consumer, Class<U> processorType ) {
		registrations
				.stream()
				.map( EntityViewProcessorRegistration::getProcessor )
				.filter( processorType::isInstance )
				.forEach( p -> consumer.accept( processorType.cast( p ) ) );
	}

	private void sortRegistrations() {
		OrderComparator.sort( registrations );
	}

	/**
	 * Represents a processor entry in the factory.  A processor is identifiable by name and can (optionally) have an order.
	 * If no explicit order is given, they will be kept in order they are registered in.
	 */
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@Setter
	public final class EntityViewProcessorRegistration implements Ordered
	{
		@NonNull
		@Getter
		private final String processorName;

		@NonNull
		private EntityViewProcessor processor;

		private int order;

		/**
		 * Update the order of this registration.
		 * Will immediately sort all registrations after setting the value.
		 *
		 * @param order new order to use
		 */
		public void setOrder( int order ) {
			this.order = order;
			sortRegistrations();
		}

		/**
		 * Get the processor value, expecting it to be of a specific type.
		 *
		 * @param expectedType the processor should have
		 * @param <U>          type
		 * @return processor or {@link ClassCastException} if not of the expected type
		 */
		public <U extends EntityViewProcessor> U getProcessor( Class<U> expectedType ) {
			return expectedType.cast( this.getProcessor() );
		}

		/**
		 * Remove this registration immediately.
		 */
		public boolean remove() {
			return registrations.remove( this );
		}
	}
}
