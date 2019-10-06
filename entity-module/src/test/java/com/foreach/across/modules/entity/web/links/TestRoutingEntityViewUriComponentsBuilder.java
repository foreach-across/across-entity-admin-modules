/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.entity.web.links;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.web.routing.EntityViewRouter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
@ExtendWith(MockitoExtension.class)
class TestRoutingEntityViewUriComponentsBuilder
{
	//private EntityViewRouter router = new EntityViewRouter();
	private UriComponentsBuilder originalUri = UriComponentsBuilder.fromPath( "" );
	//private RoutingEntityViewUriComponentsBuilder builder = new RoutingEntityViewUriComponentsBuilder( router );

	@Test
	void forEntityConfigurationWithoutViewName( @Mock EntityConfiguration entityConfiguration ) {
		when( entityConfiguration.getName() ).thenReturn( "user" );

		assertUri( defaultRouter().forEntityConfiguration( originalUri, entityConfiguration, null ), "/user" );
		assertUri( withRouter( r -> r.entityRoot( "/users" ) ).forEntityConfiguration( originalUri, entityConfiguration, null ), "/users" );
		assertUri( withRouter( r -> r.entityRoot( "/entities/{entityName}/view?directLink" ) )
				           .forEntityConfiguration( originalUri, entityConfiguration, null ), "/entities/user/view?directLink" );
	}

	@Test
	void forEntityConfigurationWithViewName( @Mock EntityConfiguration entityConfiguration ) {
		when( entityConfiguration.getName() ).thenReturn( "user" );

		assertUri( defaultRouter().forEntityConfiguration( originalUri, entityConfiguration, "someView" ), "/user?view=someView" );
		assertUri( withRouter( r -> r.entityRoot( "/users" ) )
				           .forEntityConfiguration( originalUri, entityConfiguration, "someView" ), "/users?view=someView" );
		assertUri( withRouter( r -> r.entityRoot( "/entities/{entityName}/view?directLink" ) )
				           .forEntityConfiguration( originalUri, entityConfiguration, "someView" ), "/entities/user/view?directLink&view=someView" );

		assertUri( withRouter( r -> r.entityRoot( "/users" ).viewSegment( "-views?id={viewName}" ) )
				           .forEntityConfiguration( originalUri, entityConfiguration, "someView" ), "/users-views?id=someView" );
		assertUri( withRouter( r -> r.entityRoot( "/users" ).viewSegment( "/{viewName}" ) )
				           .forEntityConfiguration( originalUri, entityConfiguration, "someView" ), "/users/someView" );

		// manually mapped view
		assertUri( withRouter( r -> r.entityRoot( "/users" ).routeView( "someView", "/look/at/{viewName}/of-{entityName}", false ) )
				           .forEntityConfiguration( originalUri, entityConfiguration, "someView" ), "/look/at/someView/of-user" );
		RoutingEntityViewUriComponentsBuilder router = withRouter(
				r -> r.entityRoot( "/users" ).routeView( "someView", "/{viewName}", true )
		);
		assertUri( router.forEntityConfiguration( originalUri, entityConfiguration, "someView" ), "/users/someView" );
		assertUri( router.forEntityConfiguration( originalUri, entityConfiguration, "otherView" ), "/users?view=otherView" );

		// default views
		assertUri( router.forEntityConfiguration( originalUri, entityConfiguration, EntityView.CREATE_VIEW_NAME ), "/users/create" );
		assertUri( router.forEntityConfiguration( originalUri, entityConfiguration, EntityView.LIST_VIEW_NAME ), "/users" );
	}

	private RoutingEntityViewUriComponentsBuilder defaultRouter() {
		return withRouter( r -> {
		} );
	}

	private RoutingEntityViewUriComponentsBuilder withRouter( Consumer<EntityViewRouter.EntityViewRouterBuilder> router ) {
		EntityViewRouter.EntityViewRouterBuilder routerBuilder = EntityViewRouter.builder();
		router.accept( routerBuilder );
		return new RoutingEntityViewUriComponentsBuilder( routerBuilder.build() );
	}

	private void assertUri( UriComponentsBuilder uriComponentsBuilder, String expectedUriString ) {
		assertThat( uriComponentsBuilder ).isNotEqualTo( originalUri );
		assertThat( uriComponentsBuilder.toUriString() ).isEqualTo( expectedUriString );
	}
}