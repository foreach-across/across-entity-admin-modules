package com.across.samples.bootstrap.application.config;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityFactory;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.filemanager.business.reference.FileReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.core.EntityInformation;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.foreach.across.modules.entity.support.EntityConfigurationCustomizers.registerEntityQueryExecutor;

@Configuration
public class CarConfiguration implements EntityConfigurer
{
	private final Map<Serializable, Car> cars = new HashMap<>();

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		Car initialCar = createExampleCar();
		cars.put( initialCar.getId(), initialCar );

		entities.create()
		        .entityType( Car.class, true )
		        .displayName( "Car" )
		        .entityModel(
				        model -> model.entityFactory( CAR_FACTORY )
				                      .entityInformation( CAR_INFORMATION )
				                      .findOneMethod( cars::get )
				                      .labelPrinter( ( library, locale ) -> library.getName() )
				                      .saveMethod( library -> {
					                      if ( library.getId() == null ) {
						                      library.setId( UUID.randomUUID().toString() );
					                      }
					                      cars.put( library.getId(), library );
					                      return library;
				                      } )
				                      .deleteByIdMethod( cars::remove )
		        )
		        .properties(
				        props -> props.property( "id" )
				                      .hidden( true )
				        .and()
				        .property( "manufacturer" )
				        .valueFetcher( new ValueFetcher<Car>()
				        {
					        @Override
					        public Object getValue( Car entity ) {
						        return entity.getManufacturer();
					        }
				        } )
		        )
		        .and( registerEntityQueryExecutor( cars::values ) )
		        .detailView()
		        .listView()
		        .createFormView()
		        .updateFormView()
		        .deleteFormView()
		        .show();
	}

	private Car createExampleCar() {
		Car car = new Car();
		car.setId( UUID.randomUUID().toString() );

		Model m = new Model();
		m.setId( UUID.randomUUID().toString() );
		m.setOptions( Arrays.asList(
				new Option( "Winter tires", 2375L ),
				new Option( "Autopilot", 5400L )
		) );
		car.setModel( m );

		car.setName( "S" );
		car.setPrice( 99399L );

		return car;
	}

	private static final EntityFactory<Car> CAR_FACTORY = new EntityFactory<Car>()
	{
		@Override
		public Car createNew( Object... args ) {
			return new Car();
		}

		@Override
		public Car createDto( Car entity ) {
			return entity.copy();
		}
	};

	private static final EntityInformation<Car, String> CAR_INFORMATION = new EntityInformation<Car, String>()
	{
		@Override
		public boolean isNew( Car library ) {
			return library.id != null;
		}

		@Override
		public String getId( Car library ) {
			return library.id;
		}

		@Override
		public Class<String> getIdType() {
			return String.class;
		}

		@Override
		public Class<Car> getJavaType() {
			return Car.class;
		}
	};

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Option
	{
		@Length(max = 200)
		private String name;
		private Long price;

		public Option copy() {
			Option b = new Option();
			b.name = name;
			b.price = price;
			return b;
		}
	}

	@Data
	public static class Model
	{
		private String id;
		private List<Option> options;

		public Model copy() {
			Model b = new Model();
			b.id = id;
			b.options = options.stream().map( Option::copy ).collect( Collectors.toList() );
			return b;
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Car
	{
		@Id
		private String id;
		@NotBlank
		@Length(max = 30)
		private String name;
		private Model model;
		@NonNull
		private ManufacturerConfiguration.Manufacturer manufacturer;
		private Long price;
		private FileReference manual;
		private String remarks;

		public Car copy() {
			Car b = new Car();
			b.id = id;
			b.name = name;

			b.model = model == null ? null : model.copy();
			b.manufacturer = manufacturer == null ? null : manufacturer.copy();
			b.price = price;
			b.manual = manual == null ? null :  manual.toDto();
			b.remarks = remarks;
			return b;
		}
	}
}
