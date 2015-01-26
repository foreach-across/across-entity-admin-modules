package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.modules.entity.views.support.NestedValueFetcher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEntityPropertyRegistry extends EntityPropertyRegistrySupport
{
	private final EntityPropertyRegistries registries;
	private final Class<?> entityType;

	private EntityPropertyOrder declarationOrder = null;

	public DefaultEntityPropertyRegistry( Class<?> entityType ) {
		this( entityType, null );
	}

	public DefaultEntityPropertyRegistry( Class<?> entityType, EntityPropertyRegistries registries ) {
		this.entityType = entityType;
		this.registries = registries;

		Map<String, PropertyDescriptor> scannedDescriptors = new HashMap<>();

		for ( PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors( entityType ) ) {
			register( SimpleEntityPropertyDescriptor.forPropertyDescriptor( descriptor ) );
			scannedDescriptors.put( descriptor.getName(), descriptor );
		}

		declarationOrder = buildDeclarationOrder( scannedDescriptors );
		super.setDefaultOrder( declarationOrder );
	}

	private EntityPropertyOrder buildDeclarationOrder( Map<String, PropertyDescriptor> scannedDescriptors ) {
		final Map<String, Integer> order = new HashMap<>();

		ReflectionUtils.doWithFields( entityType, new ReflectionUtils.FieldCallback()
		{
			private Class declaringClass;
			private int declaringClassOffset = 10000;

			@Override
			public void doWith( Field field ) throws IllegalArgumentException, IllegalAccessException {
				if ( !field.getDeclaringClass().equals( declaringClass ) ) {
					declaringClass = field.getDeclaringClass();
					declaringClassOffset -= 1000;
				}

				if ( contains( field.getName() ) ) {
					order.put( field.getName(), declaringClassOffset + order.size() + 1 );
				}
			}
		} );

		// Determine method indices
		final Map<Method, Integer> methodIndex = new HashMap<>();

		ReflectionUtils.doWithMethods( entityType, new ReflectionUtils.MethodCallback()
		{
			private Class declaringClass;
			private int declaringClassOffset = 1000000;

			@Override
			public void doWith( Method method ) throws IllegalArgumentException, IllegalAccessException {
				if ( !method.getDeclaringClass().equals( declaringClass ) ) {
					declaringClass = method.getDeclaringClass();
					declaringClassOffset -= 1000;
				}

				if ( !methodIndex.containsKey( method ) ) {
					methodIndex.put( method, declaringClassOffset + methodIndex.size() + 1 );
				}
			}
		} );

		// For every property without declared order, use the read method first, write method second to determine order
		for ( EntityPropertyDescriptor entityPropertyDescriptor : getRegisteredDescriptors() ) {
			if ( !order.containsKey( entityPropertyDescriptor.getName() ) ) {
				PropertyDescriptor propertyDescriptor = scannedDescriptors.get( entityPropertyDescriptor.getName() );

				if ( propertyDescriptor != null ) {
					Method lookupMethod = propertyDescriptor.getReadMethod();

					if ( lookupMethod != null ) {
						order.put( entityPropertyDescriptor.getName(), methodIndex.get( lookupMethod ) );
					}
					else {
						lookupMethod = propertyDescriptor.getWriteMethod();

						if ( lookupMethod != null ) {
							order.put( entityPropertyDescriptor.getName(), methodIndex.get( lookupMethod ) );
						}
					}
				}
			}
		}

		return new EntityPropertyOrder( order );
	}

	@Override
	public void setDefaultOrder( Comparator<EntityPropertyDescriptor> defaultOrder ) {
		super.setDefaultOrder( EntityPropertyOrder.composite( defaultOrder, declarationOrder ) );
	}

	@Override
	public List<EntityPropertyDescriptor> getProperties() {
		return getProperties( EntityPropertyFilters.NoOp );
	}

	@Override
	public EntityPropertyDescriptor getProperty( String propertyName ) {
		EntityPropertyDescriptor descriptor = super.getProperty( propertyName );

		if ( descriptor == null && registries != null ) {
			// Find a registered shizzle
			String rootProperty = findRootProperty( propertyName );

			if ( rootProperty != null ) {
				EntityPropertyDescriptor rootDescriptor = super.getProperty( rootProperty );

				if ( rootDescriptor != null && rootDescriptor.getPropertyType() != null ) {
					EntityPropertyRegistry subRegistry = registries.getRegistry( rootDescriptor.getPropertyType() );

					if ( subRegistry != null ) {
						EntityPropertyDescriptor childDescriptor = subRegistry
								.getProperty( findChildProperty( propertyName ) );

						if ( childDescriptor != null ) {
							descriptor = buildNestedDescriptor( propertyName, rootDescriptor, childDescriptor );
						}
					}

				}
			}

		}

		return descriptor;
	}

	private EntityPropertyDescriptor buildNestedDescriptor( String name,
	                                                        EntityPropertyDescriptor parent,
	                                                        EntityPropertyDescriptor child ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor();
		descriptor.setName( name );
		descriptor.setDisplayName( name );
		descriptor.setPropertyType( child.getPropertyType() );
		descriptor.setReadable( child.isReadable() );
		descriptor.setWritable( child.isWritable() );
		descriptor.setHidden( child.isHidden() );

		if ( descriptor.isReadable() ) {
			descriptor.setValueFetcher( new NestedValueFetcher( parent.getValueFetcher(), child.getValueFetcher() ) );
		}

		return descriptor;
	}

	private String findChildProperty( String propertyName ) {
		return StringUtils.defaultIfEmpty( StringUtils.substringAfter( propertyName, "." ), null );
	}

	private String findRootProperty( String propertyName ) {
		return StringUtils.defaultIfEmpty( StringUtils.substringBefore( propertyName, "." ), null );
	}
}
