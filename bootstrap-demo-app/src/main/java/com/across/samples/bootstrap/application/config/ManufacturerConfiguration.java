package com.across.samples.bootstrap.application.config;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.core.EntityInformation;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.foreach.across.modules.entity.support.EntityConfigurationCustomizers.registerEntityQueryExecutor;

@Configuration
public class ManufacturerConfiguration implements EntityConfigurer
{
	public final static Map<Serializable, Manufacturer> manufacturers = new ConcurrentHashMap<>();

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		Manufacturer m = createManufacturer( "Tesla", new Address( "Boomsesteenweg 8", "Aartselaar", 2630 ), "123415234" );
		manufacturers.put( m.getId(), m );
		m = createManufacturer( "BMW", new Address( "Willem van Halmalelaan 1", "Wijnegem", 2110 ), "63422342" );
		manufacturers.put( m.getId(), m );
		m = createManufacturer( "Mercedes", new Address( "Dascottelei 147", "Deurne", 2100 ), "934282" );
		manufacturers.put( m.getId(), m );

		entities.create()
		        .entityType( Manufacturer.class, true )
		        .displayName( "Manufacturer" )
		        .entityModel(
				        model -> model.entityFactory( MANUFACTURER_FACTORY )
				                      .entityInformation( MANUFACTURER_INFORMATION )
				                      .findOneMethod( manufacturers::get )
				                      .labelPrinter( ( library, locale ) -> library.getName() )
				                      .saveMethod( library -> {
					                      if ( library.getId() == null ) {
						                      library.setId( UUID.randomUUID().toString() );
					                      }
					                      manufacturers.put( library.getId(), library );
					                      return library;
				                      } )
				                      .deleteByIdMethod( manufacturers::remove )
		        )
		        .properties( props -> props.property( "id" ).hidden( true ) )
		        .and( registerEntityQueryExecutor( manufacturers::values ) )
		        .detailView()
		        .listView()
		        .createFormView()
		        .updateFormView()
		        .deleteFormView()
		        .show();
	}

	private Manufacturer createManufacturer( String name, Address address, String vatNumber ) {
		Manufacturer m = new Manufacturer();
		m.setId( UUID.randomUUID().toString() );
		m.setName( name );
		m.setAddress( address );
		m.setVATNumber( vatNumber );
		return m;
	}

	private static final EntityFactory<Manufacturer> MANUFACTURER_FACTORY = new EntityFactory<Manufacturer>()
	{
		@Override
		public Manufacturer createNew( Object... args ) {
			return new Manufacturer();
		}

		@Override
		public Manufacturer createDto( Manufacturer entity ) {
			return entity.copy();
		}
	};

	private static final EntityInformation<Manufacturer, String> MANUFACTURER_INFORMATION = new EntityInformation<Manufacturer, String>()
	{
		@Override
		public boolean isNew( Manufacturer library ) {
			return library.id != null;
		}

		@Override
		public String getId( Manufacturer library ) {
			return library.id;
		}

		@Override
		public Class<String> getIdType() {
			return String.class;
		}

		@Override
		public Class<Manufacturer> getJavaType() {
			return Manufacturer.class;
		}
	};

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Address
	{
		@Length(max = 200)
		private String streetAndHouseNumber;
		@Length(max = 200)
		private String city;
		private int zipCode;

		public Address copy() {
			Address b = new Address();
			b.city = city;
			b.zipCode = zipCode;
			b.streetAndHouseNumber = streetAndHouseNumber;
			return b;
		}
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Manufacturer
	{
		private String id;
		@Length(max = 200)
		private String name;
		@NonNull
		private Address address;
		@Length(min = 5)
		private String VATNumber;

		public Manufacturer copy() {
			Manufacturer b = new Manufacturer();
			b.id = id;
			b.name = name;
			b.address = address == null ? null : address.copy();
			b.VATNumber = VATNumber;
			return b;
		}
	}
}
