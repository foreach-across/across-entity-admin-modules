/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.modules.bootstrapui.styles;

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.NonNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@FunctionalInterface
public interface BootstrapStyleRule extends ViewElement.WitherSetter<HtmlViewElement>, ViewElement.WitherRemover<HtmlViewElement>, Predicate<HtmlViewElement>
{
	static BootstrapStyleRule empty() {
		return of();
	}

	static BootstrapStyleRule of( String... css ) {
		return () -> css;
	}

	static BootstrapStyleRule appendOnSet( BootstrapStyleRule original, String... css ) {
		return new AppendingBootstrapStyleRule( original, css );
	}

	static BootstrapStyleRule combine( BootstrapStyleRule... rules ) {
		return () ->
				Stream.of( rules )
				      .map( BootstrapStyleRule::toCssClasses )
				      .flatMap( Stream::of )
				      .toArray( String[]::new );
	}

	String[] toCssClasses();

	@Override
	default void removeFrom( HtmlViewElement target ) {
		target.removeCssClass( toCssClasses() );
	}

	@Override
	default void applyTo( HtmlViewElement target ) {
		target.addCssClass( toCssClasses() );
	}

	/**
	 * Create a new rule with applies a suffix to all css classes from the current rule.
	 *
	 * @param suffix to apply
	 * @return new rule instance
	 */
	default BootstrapStyleRule suffix( @NonNull String suffix ) {
		String[] cssClasses = Stream.of( toCssClasses() ).map( s -> s + "-" + suffix )
		                            .toArray( String[]::new );
		return () -> cssClasses;
	}

	/**
	 * Create a new rule with applies a prefix to all css classes from the current rule.
	 *
	 * @param prefix to apply
	 * @return new rule instance
	 */
	default BootstrapStyleRule prefix( @NonNull String prefix ) {
		String[] cssClasses = Stream.of( toCssClasses() ).map( s -> prefix + "-" + s )
		                            .toArray( String[]::new );
		return () -> cssClasses;
	}

	@Override
	default boolean test( HtmlViewElement target ) {
		for ( String cssClassName : toCssClasses() ) {
			if ( !target.hasCssClass( cssClassName ) ) {
				return false;
			}
		}
		return true;
	}
}
