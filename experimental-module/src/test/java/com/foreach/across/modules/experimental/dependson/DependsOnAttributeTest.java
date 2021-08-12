package com.foreach.across.modules.experimental.dependson;

import com.foreach.across.modules.experimental.webutility.support.DependsOnAttribute;
import com.foreach.across.modules.experimental.webutility.support.WebUtilityConfigurers;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DependsOnAttributeTest
{
	@Test
	void testDependsOnMap() {
		DependsOnAttribute dependsOnAttribute = WebUtilityConfigurers.dependsOn( dependsOn -> dependsOn
				.viewElementName( "parent" )
				.values( Boolean.TRUE.toString() )
				.and()
				.viewElementName( "child" )
				.values( Boolean.TRUE.toString() ) );
		NodeViewElement html = HtmlViewElements.html.html();
		dependsOnAttribute.applyTo( html );

		LinkedHashMap<String, Object> dataDependsOn = (LinkedHashMap<String, Object>) html.getAttribute( "data-dependson" );

		LinkedHashMap<String, Object> parentDependency = (LinkedHashMap<String, Object>) dataDependsOn.get( "[name=\"parent\"]" );
		assertThat( parentDependency).isNotNull();
		Object[] parentDependencyValues = (Object[]) parentDependency.get( "values" );
		assertThat( parentDependencyValues ).isNotNull();
		assertThat( parentDependencyValues[0] ).isEqualTo( "true" );

		LinkedHashMap<String, Object> childDependency = (LinkedHashMap<String, Object>) dataDependsOn.get( "[name=\"child\"]" );
		assertThat( childDependency).isNotNull();
		Object[] childDependencyValues = (Object[]) childDependency.get( "values" );
		assertThat( childDependencyValues ).isNotNull();
		assertThat( childDependencyValues[0] ).isEqualTo( "true" );
	}

	@Test
	void testDependsOnMapContains() {
		DependsOnAttribute dependsOnAttribute = WebUtilityConfigurers.dependsOn( dependsOn -> dependsOn
				.viewElementName( "parent" )
				.contains( "test" )
				.and()
				.viewElementName( "child" )
				.values( Boolean.TRUE.toString() ) );
		NodeViewElement html = HtmlViewElements.html.html();
		dependsOnAttribute.applyTo( html );

		LinkedHashMap<String, Object> dataDependsOn = (LinkedHashMap<String, Object>) html.getAttribute( "data-dependson" );

		LinkedHashMap<String, Object> parentDependency = (LinkedHashMap<String, Object>) dataDependsOn.get( "[name=\"parent\"]" );
		assertThat( parentDependency).isNotNull();
		Object[] parentDependencyValues = (Object[]) parentDependency.get( "contains" );
		assertThat( parentDependencyValues ).isNotNull();
		assertThat( parentDependencyValues[0] ).isEqualTo( "test" );

		LinkedHashMap<String, Object> childDependency = (LinkedHashMap<String, Object>) dataDependsOn.get( "[name=\"child\"]" );
		assertThat( childDependency).isNotNull();
		Object[] childDependencyValues = (Object[]) childDependency.get( "values" );
		assertThat( childDependencyValues ).isNotNull();
		assertThat( childDependencyValues[0] ).isEqualTo( "true" );
	}
}
