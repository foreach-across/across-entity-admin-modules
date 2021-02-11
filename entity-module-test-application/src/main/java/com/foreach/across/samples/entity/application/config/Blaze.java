package com.foreach.across.samples.entity.application.config;

import com.blazebit.persistence.spring.data.impl.repository.BlazePersistenceRepositoryFactory;
import com.blazebit.persistence.spring.data.impl.repository.BlazePersistenceRepositoryFactoryBean;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.hibernate.jpa.repositories.EntityInterceptingJpaRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.util.TxUtils;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class Blaze<T extends Repository<S, ID>, S, ID extends Serializable> extends BlazePersistenceRepositoryFactoryBean<T, S, ID>
{
    private String transactionManagerName = TxUtils.DEFAULT_TRANSACTION_MANAGER;
    private AcrossContextBeanRegistry acrossContextBeanRegistry;

    public Blaze( Class<? extends T> repositoryInterface ) {
        super( repositoryInterface );
    }

    @Autowired
    public void setAcrossContextBeanRegistry( AcrossContextBeanRegistry acrossContextBeanRegistry ) {
        this.acrossContextBeanRegistry = acrossContextBeanRegistry;
    }

    @Override
    public void setTransactionManager( String transactionManager ) {
        this.transactionManagerName
                = transactionManager == null ? TxUtils.DEFAULT_TRANSACTION_MANAGER : transactionManager;
        super.setTransactionManager( transactionManager );
    }

    @Override
    protected BlazePersistenceRepositoryFactory createRepositoryFactory( EntityManager entityManager ) {
        BlazePersistenceRepositoryFactory repositoryFactory = super.createRepositoryFactory( entityManager );
        repositoryFactory.addRepositoryProxyPostProcessor(
                new EntityInterceptingJpaRepositoryFactoryBean.EntityInterceptorProxyPostProcessor( acrossContextBeanRegistry, transactionManagerName )
        );
        return repositoryFactory;
    }
}
