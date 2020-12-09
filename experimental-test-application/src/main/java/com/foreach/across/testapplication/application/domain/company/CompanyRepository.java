package com.foreach.across.testapplication.application.domain.company;

import com.foreach.across.modules.hibernate.jpa.repositories.IdBasedEntityJpaRepository;

import java.util.Optional;

public interface CompanyRepository extends IdBasedEntityJpaRepository<Company>
{
	Optional<Company> findByName( String name );
}
