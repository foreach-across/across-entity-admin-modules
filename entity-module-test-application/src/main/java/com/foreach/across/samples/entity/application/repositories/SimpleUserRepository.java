package com.foreach.across.samples.entity.application.repositories;

import com.blazebit.persistence.spring.data.repository.EntityViewRepository;
import com.foreach.across.samples.entity.application.view.UserSimpleView;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface SimpleUserRepository extends EntityViewRepository<UserSimpleView, Long>, JpaRepositoryImplementation<UserSimpleView, Long>
{
}