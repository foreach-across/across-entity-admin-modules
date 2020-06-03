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

package it.com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterFormControlBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
// todo: find a more reliable way of testing the base markup without requiring that much detail
@Ignore
@ContextConfiguration(classes = TestEntityQueryFilterFormControlBuilder.Config.class)
public class TestEntityQueryFilterFormControlBuilder extends AbstractViewElementTemplateTest
{
	private static final String FILTER_ADVANCED = "<div data-entity-query-filter-form=\"default\" class=\"entity-query-filter-form\">" +
			"<div class=\"entity-query-filter-form-advanced\"><div class=\"form-group\"><input name=\"extensions[eqFilter]\" id=\"extensions[eqFilter]\" placeholder=\"entityQueryFilter.eqlPlaceholder\" type=\"text\" class=\"form-control\" value=\"name = &#39;john&#39;\" aria-describedby=\"extensions[eqFilter].help\"></input><span class=\"help-block\" id=\"extensions[eqFilter].help\">entityQueryFilter.eqlDescription</span></div>" +
			"<div class='list-header-actions'><button type=\"submit\" class=\"btn btn-link\"><span aria-hidden=\"true\" class=\"fas fa-search\"></span>entityQueryFilter.searchButton</button></div></div>" +
			"</div>";
	private static final String FILTER_BASIC = "<div data-entity-query-filter-form=\"default\" class=\"entity-query-filter-form\">" +
			"<div class=\"entity-query-filter-form-basic\">" +
			"<input data-entity-query-control=\"marker\" name=\"extensions[myName]\" data-entity-query-operand=\"LIKE\" id=\"extensions[myName]\" type=\"text\" class=\"form-control\" data-entity-query-property=\"myName\"></input>" +
			"<div data-entity-query-control=\"marker\" data-entity-query-operand=\"IN\" id=\"extensions[myOption]\" data-entity-query-property=\"myOption\"><div class=\"checkbox\"><input name=\"extensions[myOption]\" id=\"extensions[myOption]1\" type=\"checkbox\" value=\"myOption\"></input><input name=\"_extensions[myOption]\" type=\"hidden\" value=\"on\"></input></div></div>" +
			"<button type=\"submit\" class=\"btn btn-link\"><span aria-hidden=\"true\" class=\"fas fa-search\"></span>entityQueryFilter.searchButton</button></div>" +
			"<input name=\"extensions[eqFilter]\" type=\"hidden\"  value=\"\" />" +
			"</div>";
	private static final String FILTER_BASIC_ADVANCED_WITH_BASIC = "<div data-entity-query-filter-form=\"default\" class=\"entity-query-filter-form\">" +
			"<div class=\"entity-query-filter-form-basic\">" +
			"<input data-entity-query-control=\"marker\" name=\"extensions[myName]\" data-entity-query-operand=\"LIKE\" id=\"extensions[myName]\" type=\"text\" class=\"form-control\" data-entity-query-property=\"myName\"></input>" +
			"<div data-entity-query-control=\"marker\" data-entity-query-operand=\"IN\" id=\"extensions[myOption]\" data-entity-query-property=\"myOption\"><div class=\"checkbox\"><input name=\"extensions[myOption]\" id=\"extensions[myOption]1\" type=\"checkbox\" value=\"myOption\"></input><input name=\"_extensions[myOption]\" type=\"hidden\" value=\"on\"></input></div></div>" +
			"<a data-entity-query-filter-form-link=\"advanced\" role=\"button\" href=\"#\" class=\"btn btn-link\">entityQueryFilter.linkToAdvancedMode</a><button type=\"submit\" class=\"btn btn-link\"><span aria-hidden=\"true\" class=\"fas fa-search\"></span>entityQueryFilter.searchButton</button></div>" +
			"<div class=\"entity-query-filter-form-advanced hidden\"><div class=\"form-group\"><input name=\"extensions[eqFilter]\" id=\"extensions[eqFilter]\" placeholder=\"entityQueryFilter.eqlPlaceholder\" type=\"text\" class=\"form-control\" value=\"\" aria-describedby=\"extensions[eqFilter].help\"></input><span class=\"help-block\" id=\"extensions[eqFilter].help\">entityQueryFilter.eqlDescription</span></div>" +
			"<div class='list-header-actions'><a data-entity-query-filter-form-link=\"basic\" role=\"button\" href=\"#\" class=\"btn btn-link\">entityQueryFilter.linkToBasicMode</a><button type=\"submit\" class=\"btn btn-link\"><span aria-hidden=\"true\" class=\"fas fa-search\"></span>entityQueryFilter.searchButton</button></div></div>" +
			"<input class='js-entity-query-filter-form-show-basic-filter' name=\"extensions[entityQueryRequest].showBasicFilter\" type=\"hidden\" value=\"true\" /></div>";
	private static final String FILTER_BASIC_ADVANCED_WITH_ADVANCED = "<div data-entity-query-filter-form=\"default\" class=\"entity-query-filter-form\">" +
			"<div class=\"entity-query-filter-form-basic hidden\">" +
			"<input data-entity-query-control=\"marker\" name=\"extensions[myName]\" data-entity-query-operand=\"LIKE\" id=\"extensions[myName]\" type=\"text\" class=\"form-control\" data-entity-query-property=\"myName\"></input>" +
			"<div data-entity-query-control=\"marker\" data-entity-query-operand=\"IN\" id=\"extensions[myOption]\" data-entity-query-property=\"myOption\"><div class=\"checkbox\"><input name=\"extensions[myOption]\" id=\"extensions[myOption]1\" type=\"checkbox\" value=\"myOption\"></input><input name=\"_extensions[myOption]\" type=\"hidden\" value=\"on\"></input></div></div>" +
			"<a data-entity-query-filter-form-link=\"advanced\" role=\"button\" href=\"#\" class=\"btn btn-link\">entityQueryFilter.linkToAdvancedMode</a><button type=\"submit\" class=\"btn btn-link\"><span aria-hidden=\"true\" class=\"fas fa-search\"></span>entityQueryFilter.searchButton</button></div>" +
			"<div class=\"entity-query-filter-form-advanced\"><div class=\"form-group\"><input name=\"extensions[eqFilter]\" id=\"extensions[eqFilter]\" placeholder=\"entityQueryFilter.eqlPlaceholder\" type=\"text\" class=\"form-control\" value=\"\" aria-describedby=\"extensions[eqFilter].help\"></input><span class=\"help-block\" id=\"extensions[eqFilter].help\">entityQueryFilter.eqlDescription</span></div>" +
			"<div class='list-header-actions'><a data-entity-query-filter-form-link=\"basic\" role=\"button\" href=\"#\" class=\"btn btn-link\">entityQueryFilter.linkToBasicMode</a><button type=\"submit\" class=\"btn btn-link\"><span aria-hidden=\"true\" class=\"fas fa-search\"></span>entityQueryFilter.searchButton</button></div></div>" +
			"<input class='js-entity-query-filter-form-show-basic-filter' name=\"extensions[entityQueryRequest].showBasicFilter\" type=\"hidden\" value=\"false\" /></div>";
	private static final String FILTER_NOTHING = "<div data-entity-query-filter-form=\"default\" class=\"entity-query-filter-form\"></div>";
	private static final String FILTER_BASIC_ADVANCED_NOT_CONVERTIBLE_TO_BASIC =
			"<div data-entity-query-filter-form=\"default\" class=\"entity-query-filter-form\">" +
					"<div class=\"entity-query-filter-form-basic d-none\">" +
					"<input data-entity-query-control=\"marker\" name=\"extensions[myName]\" data-entity-query-operand=\"LIKE\" id=\"extensions[myName]\" type=\"text\" class=\"form-control\" data-entity-query-property=\"myName\"></input>" +
					"<div data-entity-query-control=\"marker\" data-entity-query-operand=\"IN\" id=\"extensions[myOption]\" data-entity-query-property=\"myOption\"><div class=\"checkbox\"><input name=\"extensions[myOption]\" id=\"extensions[myOption]1\" type=\"checkbox\" value=\"myOption\"></input><input name=\"_extensions[myOption]\" type=\"hidden\" value=\"on\"></input></div></div>" +
					"<a data-entity-query-filter-form-link=\"advanced\" role=\"button\" href=\"#\" class=\"btn btn-link\">entityQueryFilter.linkToAdvancedMode</a><button type=\"submit\" class=\"btn btn-link\"><span aria-hidden=\"true\" class=\"fas fa-search\"></span>entityQueryFilter.searchButton</button></div>" +
					"<div class=\"entity-query-filter-form-advanced\"><div class=\"form-group\"><input name=\"extensions[eqFilter]\" id=\"extensions[eqFilter]\" placeholder=\"entityQueryFilter.eqlPlaceholder\" type=\"text\" class=\"form-control\" value=\"\" aria-describedby=\"extensions[eqFilter].help\"></input><span class=\"help-block\" id=\"extensions[eqFilter].help\">entityQueryFilter.eqlDescription</span></div>" +
					"<div class='list-header-actions'><div title=\"entityQueryFilter.linkToBasicMode[impossibleTooltip]\" class=\"disabled-button-wrapper\"><a data-entity-query-filter-form-link=\"basic\" role=\"button\" href=\"#\" class=\"btn btn-link disabled\">entityQueryFilter.linkToBasicMode</a></div><button type=\"submit\" class=\"btn btn-link\"><span aria-hidden=\"true\" class=\"fas fa-search\"></span>entityQueryFilter.searchButton</button></div></div>" +
					"<input class='js-entity-query-filter-form-show-basic-filter' name=\"extensions[entityQueryRequest].showBasicFilter\" type=\"hidden\" value=\"false\" /></div>";

	private EntityQueryFilterFormControlBuilder queryFilterFormControlBuilder;

	private List<ViewElement> viewElementControlItems;

	private DefaultViewElementBuilderContext builderContext;

	@Before
	public void setUp() {
		builderContext = new DefaultViewElementBuilderContext();
		queryFilterFormControlBuilder = new EntityQueryFilterFormControlBuilder().eqlControlName( "extensions[eqFilter]" );

		viewElementControlItems = new ArrayList<>();
		viewElementControlItems.add( bootstrap.builders.textbox()
		                                               .controlName( "extensions[myName]" )
		                                               .htmlId( "extensions[myName]" )
		                                               .attribute( "data-entity-query-property", "myName" )
		                                               .attribute( "data-entity-query-operand", "LIKE" )
		                                               .attribute( "data-entity-query-control", "marker" )
		                                               .build( builderContext ) );

		viewElementControlItems.add( bootstrap.builders.checkboxList()
		                                               .controlName( "extensions[myOption]" )
		                                               .htmlId( "extensions[myOption]" )
		                                               .attribute( "data-entity-query-property", "myOption" )
		                                               .attribute( "data-entity-query-operand", "IN" )
		                                               .attribute( "data-entity-query-control", "marker" )
		                                               .add( bootstrap.builders.checkbox().value( "myOption" ) )
		                                               .build( builderContext ) );
	}

	@Test
	public void advancedFilterShouldBeRendered() {
		queryFilterFormControlBuilder.advancedFilter( true )
		                             .basicFilter( false )
		                             .query( "name = 'john'" );
		expect( FILTER_ADVANCED );
	}

	@Test
	public void basicFilterShouldBeRendered() {
		queryFilterFormControlBuilder.basicFilter( true )
		                             .advancedFilter( false )
		                             .basicControls( viewElementControlItems );
		expect( FILTER_BASIC );
	}

	@Test
	public void basicFilterShouldBeRenderedAnywayIfAdvancedDisabled() {
		queryFilterFormControlBuilder.basicFilter( true )
		                             .basicControls( viewElementControlItems )
		                             .advancedFilter( false )
		                             .showAdvancedFilter();

		expect( FILTER_BASIC );
	}

	@Test
	public void advancedFilterShouldBeRenderedAnywayIfBasicDisabled() {
		queryFilterFormControlBuilder.basicFilter( false )
		                             .basicControls( viewElementControlItems )
		                             .advancedFilter( true )
		                             .showBasicFilter()
		                             .query( EntityQuery.parse( "name = 'john'" ) );

		expect( FILTER_ADVANCED );
	}

	@Test
	public void basicAndAdvancedFilterShouldBeRenderedByDefault() {
		queryFilterFormControlBuilder.basicControls( viewElementControlItems );
		expect( FILTER_BASIC_ADVANCED_WITH_BASIC );
	}

	@Test
	public void advancedFilterShouldBeShownIfSelected() {
		queryFilterFormControlBuilder.basicControls( viewElementControlItems )
		                             .showAdvancedFilter();
		expect( FILTER_BASIC_ADVANCED_WITH_ADVANCED );
	}

	@Test
	public void advancedFilterShouldBeRenderedIfBasicHasNoControls() {
		queryFilterFormControlBuilder.basicFilter( true )
		                             .advancedFilter( true )
		                             .query( "name = 'john'" );

		expect( FILTER_ADVANCED );
	}

	@Test
	public void onlyContainerIsRenderedIfNothingToFilter() {
		queryFilterFormControlBuilder.basicFilter( false )
		                             .advancedFilter( false );

		expect( FILTER_NOTHING );
	}

	@Test
	public void notConvertibleToBasicMode() {
		queryFilterFormControlBuilder.basicControls( viewElementControlItems )
		                             .showAdvancedFilter()
		                             .convertibleToBasicMode( false );
		expect( FILTER_BASIC_ADVANCED_NOT_CONVERTIBLE_TO_BASIC );
	}

	private void expect( String output ) {
		renderAndExpect( queryFilterFormControlBuilder.build( builderContext ), output );
	}

	@Configuration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new BootstrapUiModule() );
		}

	}
}
