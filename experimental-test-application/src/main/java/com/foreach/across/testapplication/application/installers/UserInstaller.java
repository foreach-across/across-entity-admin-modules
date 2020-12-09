package com.foreach.across.testapplication.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.testapplication.application.domain.company.Company;
import com.foreach.across.testapplication.application.domain.company.CompanyRepository;
import com.foreach.across.testapplication.application.domain.user.Address;
import com.foreach.across.testapplication.application.domain.user.Degree;
import com.foreach.across.testapplication.application.domain.user.User;
import com.foreach.across.testapplication.application.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Installer(name = "User installer", description = "Ensures some default users are available", phase = InstallerPhase.AfterModuleBootstrap)
@RequiredArgsConstructor
@Order(20)
public class UserInstaller
{
	private final UserRepository userRepository;
	private final CompanyRepository companyRepository;

	@InstallerMethod
	public void install() {
		companyRepository.findByName( "AGFA" )
		                 .ifPresent(
				                 agfa -> {
					                 List<User> allUsers = userRepository.findAll();
					                 List<User> users = Arrays.asList(
							                 getOrCreateUser( allUsers, "John", Address.builder()
							                                                           .addressType( Address.AddressType.PRIMARY )
							                                                           .city( "Antwerp" )
							                                                           .number( 21 )
							                                                           .street( "Vrederikstraat" )
							                                                           .build(), BigDecimal.valueOf( 123 ), true, null, Degree.BACHELOR ),
							                 getOrCreateUser( allUsers, "Deborah", Address.builder()
							                                                              .addressType( Address.AddressType.WORK )
							                                                              .city( "Brussels" )
							                                                              .number( 5 )
							                                                              .street( "Terhoflaan" )
							                                                              .build(), BigDecimal.valueOf( 123 ), false, agfa, Degree.BACHELOR,
							                                  Degree.MASTER ),
							                 getOrCreateUser( allUsers, "Svetty", Address.builder()
							                                                             .addressType( Address.AddressType.PRIMARY )
							                                                             .city( "Ghent" )
							                                                             .number( 21 )
							                                                             .street( "Werregarenstraat" )
							                                                             .build(), BigDecimal.valueOf( 123 ), true, null, Degree.BACHELOR ),
							                 getOrCreateUser( allUsers, "Jors", Address.builder()
							                                                           .addressType( Address.AddressType.PRIMARY )
							                                                           .city( "Amsterdam" )
							                                                           .number( 72 )
							                                                           .street( "Javastraat" )
							                                                           .build(), BigDecimal.valueOf( 123 ), true, agfa, Degree.MASTER ),
							                 getOrCreateUser( allUsers, "Lora", Address.builder()
							                                                           .addressType( Address.AddressType.WORK )
							                                                           .city( "Vienna" )
							                                                           .number( 357 )
							                                                           .street( "Kohlmarkt" )
							                                                           .build(), BigDecimal.valueOf( 123 ), false, agfa )
					                 );
					                 userRepository.saveAll( users );
				                 }
		                 );

	}

	private User getOrCreateUser( List<User> users, String name, Address address, BigDecimal netValue, boolean active, Company company, Degree... degrees ) {
		return users.stream()
		            .filter( c -> name.equals( c.getName() ) )
		            .findFirst()
		            .orElseGet( () -> User.builder()
		                                  .name( name )
		                                  .email( name + "@local" )
		                                  .company( company )
		                                  .dateOfBirth( getRandomLocalDate() )
		                                  .degrees( new HashSet<>( Arrays.asList( degrees ) ) )
		                                  .active( active )
		                                  .netValue( netValue )
		                                  .address( Collections.singletonList( address ) )
		                                  .build() );

	}

	private LocalDate getRandomLocalDate() {
		LocalDate start = LocalDate.of( 1975, Month.JANUARY, 1 );
		long days = start.lengthOfYear() * 25;
		return start.plusDays( new Random().nextInt( (int) days + 1 ) );
	}

}
