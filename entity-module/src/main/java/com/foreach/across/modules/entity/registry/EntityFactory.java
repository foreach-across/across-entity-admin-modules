package com.foreach.across.modules.entity.registry;

import lombok.NonNull;
import org.springframework.beans.BeanUtils;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Interface that provides factory methods for creating a new entity instance,
 * or a DTO (detached) instance of an existing entity.
 *
 * @param <T> type of the entity
 */
public interface EntityFactory<T>
{
	/**
	 * Create a new entity instance. In case of an {@code EntityFactory} for an association,
	 * the first (and often only) argument will be the parent entity.
	 *
	 * @param args constructor arguments
	 * @return new instance
	 */
	T createNew( Object... args );

	/**
	 * Create a DTO of an existing entity.
	 *
	 * @param entity to create a DTO for
	 * @return dto
	 */
	T createDto( T entity );

	/**
	 * Create a default {@link EntityFactory} which uses the {@code supplier} for creating
	 * a new instance. For creating a DTO that same supplier will be used to first create a
	 * new instance and then copy all public properties to the DTO.
	 *
	 * @param supplier for creating a new instance
	 * @param <T>      entity type
	 * @return factory
	 */
	static <T> EntityFactory<T> of( @NonNull Supplier<T> supplier ) {
		return of( args -> supplier.get(), null );
	}

	/**
	 * Create a default {@link EntityFactory} which uses the {@code supplier} for creating
	 * a new instance, and the {@code dtoFunction} for creating a DTO.
	 *
	 * @param supplier    for creating a new instance
	 * @param dtoFunction for creating a DTO
	 * @param <T>         entity type
	 * @return factory
	 */
	static <T> EntityFactory<T> of( @NonNull Supplier<T> supplier, Function<T, T> dtoFunction ) {
		return of( args -> supplier.get(), dtoFunction );
	}

	/**
	 * Create a default {@link EntityFactory} which uses the {@code constructorFunction} for creating
	 * a new instance, and the {@code dtoFunction} for creating a DTO.
	 *
	 * @param constructorFunction for creating a new instance
	 * @param dtoFunction         for creating a dto
	 * @param <T>                 entity type
	 * @return factory
	 */
	static <T> EntityFactory<T> of( @NonNull Function<Object[], T> constructorFunction, Function<T, T> dtoFunction ) {
		return new EntityFactory<T>()
		{
			@Override
			public T createNew( Object... args ) {
				return constructorFunction.apply( args );
			}

			@Override
			public T createDto( T entity ) {
				if ( dtoFunction != null ) {
					return dtoFunction.apply( entity );
				}

				// Fallback: attempt to create a new instance and copy all public properties
				T dto = createNew();
				BeanUtils.copyProperties( entity, dto );
				return dto;
			}
		};
	}
}
