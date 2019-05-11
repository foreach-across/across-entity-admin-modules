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

package com.foreach.across.modules.bootstrapui.styles.components;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/components/forms/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class FormStyleRule
{
	public final BootstrapStyleRule group = of( "form-group" );
	public final BootstrapStyleRule text = of( "form-text" );
	public final BootstrapStyleRule row = of( "form-row" );
	public final BootstrapStyleRule inline = of( "form-inline" );
	public final Control control = new Control();
	public final Check check = new Check();
	public final Horizontal horizontal = new Horizontal();
	public final Custom custom = new Custom();

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Control implements BootstrapStyleRule
	{
		public final BootstrapStyleRule file = of( "form-control-file" );
		public final BootstrapStyleRule large = of( "form-control-lg" );
		public final BootstrapStyleRule small = of( "form-control-sm" );
		public final BootstrapStyleRule plainText = of( "form-control-plaintext" );
		public final BootstrapStyleRule range = of( "form-control-range" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "form-control" };
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Check implements BootstrapStyleRule
	{
		public final BootstrapStyleRule input = of( "form-check-input" );
		public final BootstrapStyleRule label = of( "form-check-label" );
		public final BootstrapStyleRule inline = of( "form-check-inline" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "form-check" };
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Horizontal
	{
		public final Label label = new Label();

		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public static class Label implements BootstrapStyleRule
		{
			public final BootstrapStyleRule large = of( "col-form-label", "col-form-label-lg" );
			public final BootstrapStyleRule small = of( "col-form-label", "col-form-label-sm" );

			@Override
			public String[] toCssClasses() {
				return new String[] { "col-form-label" };
			}
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Custom
	{
		public final Control control = new Control();
		public final BootstrapStyleRule checkbox = of( "custom-control", "custom-checkbox" );
		public final BootstrapStyleRule radio = of( "custom-control", "custom-radio" );
		public final BootstrapStyleRule switchControl = of( "custom-control", "custom-switch" );
		public final BootstrapStyleRule range = of( "custom-range" );
		public final Select select = new Select();
		public final File file = new File();

		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public static class Control implements BootstrapStyleRule
		{
			public final BootstrapStyleRule input = of( "custom-control-input" );
			public final BootstrapStyleRule label = of( "custom-control-label" );
			public final BootstrapStyleRule inline = of( "custom-control-inline" );

			@Override
			public String[] toCssClasses() {
				return new String[] { "custom-control" };
			}
		}

		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public static class Select implements BootstrapStyleRule
		{
			public final BootstrapStyleRule small = of( "custom-select", "custom-select-sm" );
			public final BootstrapStyleRule large = of( "custom-select", "custom-select-lg" );

			@Override
			public String[] toCssClasses() {
				return new String[] { "custom-select" };
			}
		}

		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public static class File implements BootstrapStyleRule
		{
			public final BootstrapStyleRule input = of( "custom-file-input" );

			@Override
			public String[] toCssClasses() {
				return new String[] { "custom-file" };
			}
		}
	}
}
