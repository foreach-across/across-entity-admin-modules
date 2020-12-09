package com.foreach.across.testapplication.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.testapplication.application.domain.company.Company;
import com.foreach.across.testapplication.application.domain.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.List;

@Installer(name = "Company installer", description = "Ensures some default companies are available", phase = InstallerPhase.AfterModuleBootstrap)
@RequiredArgsConstructor
@Order(10)
public class CompanyInstaller
{
	private final CompanyRepository companyRepository;

	@InstallerMethod
	public void install() {
		List<Company> allCompanies = companyRepository.findAll();
		List<Company> companies = Arrays.asList(
				getOrCreateCompany( "BMW", allCompanies ),
				getOrCreateCompany( "foreach", allCompanies ),
				getOrCreateCompany( "AGFA", allCompanies ),
				getOrCreateCompany( "Kodak", allCompanies ),
				getOrCreateCompany( "Steinway & Sons", allCompanies )
		);
		companyRepository.saveAll( companies );
	}

	private Company getOrCreateCompany( String name, List<Company> companies ) {
		return companies.stream()
		                .filter( c -> name.equals( c.getName() ) )
		                .findFirst()
		                .orElseGet( () -> Company.builder()
		                                         .name( name )
		                                         .build() );

	}
}
