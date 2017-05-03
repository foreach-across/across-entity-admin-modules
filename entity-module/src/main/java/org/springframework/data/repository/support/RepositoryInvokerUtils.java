package org.springframework.data.repository.support;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;

import java.io.Serializable;

/**
 * Helper to provide access to package protected classes from Spring data commons.
 */
public class RepositoryInvokerUtils
{
	@SuppressWarnings("unchecked")
	public static <T, ID extends Serializable> RepositoryInvoker createRepositoryInvoker(
			RepositoryInformation repositoryInformation,
			Repository<T, ID> repository,
			ConversionService conversionService
	) {
		if ( repository instanceof CrudRepository ) {
			return crudRepositoryInvoker( (CrudRepository<Object, Serializable>) repository, repositoryInformation,
			                              conversionService );
		}
		else {
			return reflectionRepositoryInvoker( (Repository<T, Serializable>) repository,
			                                    repositoryInformation,
			                                    conversionService );
		}
	}

	private static <T> RepositoryInvoker crudRepositoryInvoker( CrudRepository<Object, Serializable> repository,
	                                                            RepositoryInformation repositoryInformation,
	                                                            ConversionService conversionService ) {
		return new CrudRepositoryInvoker( repository, repositoryInformation, conversionService );
	}

	private static <T> RepositoryInvoker reflectionRepositoryInvoker( Repository<T, Serializable> repository,
	                                                                  RepositoryInformation repositoryInformation,
	                                                                  ConversionService conversionService ) {
		return new ReflectionRepositoryInvoker( repository, repositoryInformation, conversionService );
	}
}
