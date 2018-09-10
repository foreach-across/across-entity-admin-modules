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

package com.foreach.across.modules.entity.views.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Represents a fieldset of other {@link ViewElement} objects. A fieldset has for {@link ContainerViewElement} properties:
 * <ul>
 * <li>{@link #getTitle()}: contains elements that make up the title of the fieldset</li>
 * <li>{@link #getHeader()}: contains elements that appear before the body, usually additional information about the fieldset as a whole</li>
 * <li>{@link #getBody()}: contains the actual fields that make up the fieldset</li>
 * <li>{@link #getFooter()}: contains elements that appear after the body, usually additional information about the fieldset as a whole</li>
 * </ul>
 * <p>
 * How a {@code ViewElementFieldset} is rendered depends on the {@link #setTemplate(Function)} that has been set.
 * By default all content will be rendered in an actual HTML fieldset element.
 * A number of default templates are also available: {@link #TEMPLATE_BODY_ONLY}, {@link #TEMPLATE_SECTION_H1}, {@link #TEMPLATE_SECTION_H2},
 * {@link #TEMPLATE_SECTION_H3}, {@link #TEMPLATE_SECTION_H4}, {@link #TEMPLATE_SECTION_H5} and {@link #TEMPLATE_SECTION_H6}.
 * </p>
 * <p>
 * See also the {@link #template(String, String)} utility function for manually generating simple templates.
 * </p>
 * <p>
 * Any child element operations (adding, removing) performed directly on the {@code ViewElementFieldset},
 * will actually be performed on the {@code body} container instead.
 * <p>
 * <strong>NOTE</strong>: This class does not represent a {@link FieldsetFormElement} which always renders a HTML fieldset.
 * An HTML fieldset will also be rendered if no specific template is set, but the {@code ViewElementFieldset} is a general abstraction.
 * </p>
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.views.bootstrapui.FieldsetElementBuilderFactory
 * @since 3.2.0
 */
public class ViewElementFieldset extends ContainerViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.FIELDSET;

	/**
	 * Key for the {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor} attribute that holds a
	 * {@code Function<ViewElementFieldset,? extends ViewElement>} that should be used as template when a
	 * {@link com.foreach.across.modules.entity.views.bootstrapui.FieldsetElementBuilderFactory} builds a fieldset.
	 */
	public static final String TEMPLATE = ViewElementFieldset.class.getName() + ".TEMPLATE";

	/**
	 * Template that only renders the {@link #getBody()} content without any wrapping element.
	 * In essence makes the fieldset behave as if it is not a fieldset at all.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_BODY_ONLY = ViewElementFieldset::getBody;

	/**
	 * Template that renders the fieldset with an outer {@code section} element, using a {@code h1} for the title and a {@code div} for the content wrapper.
	 * An additional class {@code element-fieldset-section-h1} will be put on the outer wrapper.
	 * See the {@link #template(String, String)} documentation for information on the other CSS classes added.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_SECTION_H1 = template( "element-fieldset-section-h1", "section/h1/div" );

	/**
	 * Template that renders the fieldset with an outer {@code section} element, using a {@code h2} for the title and a {@code div} for the content wrapper.
	 * An additional class {@code element-fieldset-section-h2} will be put on the outer wrapper.
	 * See the {@link #template(String, String)} documentation for information on the other CSS classes added.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_SECTION_H2 = template( "element-fieldset-section-h2", "section/h2/div" );

	/**
	 * Template that renders the fieldset with an outer {@code section} element, using a {@code h3} for the title and a {@code div} for the content wrapper.
	 * An additional class {@code element-fieldset-section-h3} will be put on the outer wrapper.
	 * See the {@link #template(String, String)} documentation for information on the other CSS classes added.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_SECTION_H3 = template( "element-fieldset-section-h3", "section/h3/div" );

	/**
	 * Template that renders the fieldset with an outer {@code section} element, using a {@code h4} for the title and a {@code div} for the content wrapper.
	 * An additional class {@code element-fieldset-section-h4} will be put on the outer wrapper.
	 * See the {@link #template(String, String)} documentation for information on the other CSS classes added.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_SECTION_H4 = template( "element-fieldset-section-h4", "section/h4/div" );

	/**
	 * Template that renders the fieldset with an outer {@code section} element, using a {@code h5} for the title and a {@code div} for the content wrapper.
	 * An additional class {@code element-fieldset-section-h5} will be put on the outer wrapper.
	 * See the {@link #template(String, String)} documentation for information on the other CSS classes added.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_SECTION_H5 = template( "element-fieldset-section-h5", "section/h5/div" );

	/**
	 * Template that renders the fieldset with an outer {@code section} element, using a {@code h6} for the title and a {@code div} for the content wrapper.
	 * An additional class {@code element-fieldset-section-h6} will be put on the outer wrapper.
	 * See the {@link #template(String, String)} documentation for information on the other CSS classes added.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_SECTION_H6 = template( "element-fieldset-section-h6", "section/h6/div" );

	/**
	 * Contains elements that make up the title of the fieldset.
	 */
	@Getter
	private final ContainerViewElement title = new ContainerViewElement();

	/**
	 * Contains elements that appear before the body, usually additional information about the fieldset as a whole.
	 */
	@Getter
	private final ContainerViewElement header = new ContainerViewElement();

	/**
	 * Contains the actual fields that this fieldset contains.
	 */
	@Getter
	private final ContainerViewElement body = new ContainerViewElement();

	/**
	 * Contains elements that appear after the body, usually additional information about the fieldset as a whole.
	 */
	@Getter
	private final ContainerViewElement footer = new ContainerViewElement();

	/**
	 * The actual template that should be used for rendering the fieldset.
	 * Takes the {@code ViewElementFieldset} as single input parameter and should return
	 * the resulting {@link ViewElement} that should be rendered.
	 */
	@Setter
	@Getter
	@NonNull
	private Function<ViewElementFieldset, ? extends ViewElement> template = fields -> {
		FieldsetFormElement fieldset = new FieldsetFormElement();
		fieldset.getLegend().addChild( title );
		fieldset.addChild( header );
		fieldset.addChild( body );
		fieldset.addChild( footer );
		return fieldset;
	};

	@Override
	public Stream<ViewElement> elementStream() {
		return Stream.of( title, header, body, footer );
	}

	@Override
	public void addChild( ViewElement element ) {
		getBody().addChild( element );
	}

	@Override
	public void addChildren( Collection<ViewElement> elements ) {
		getBody().addChildren( elements );
	}

	@Override
	public void addFirstChild( ViewElement element ) {
		getBody().addFirstChild( element );
	}

	@Override
	public boolean removeChild( ViewElement element ) {
		return getBody().removeChild( element );
	}

	@Override
	public void clearChildren() {
		super.clearChildren();
	}

	@Override
	public List<ViewElement> getChildren() {
		return Collections.singletonList( template.apply( this ) );
	}

	/**
	 * Generate a template function that outputs a simple HTML structure based on a format string.
	 * The format can be any of the following:
	 * <ul>
	 * <li>{@code wrapper}: a single outer element {@code wrapper} will be added</li>
	 * <li>{@code wrapper/title}: in addition to the wrapper, the title will be wrapped with a {@code title} element</li>
	 * <li>{@code wrapper/title/content}: additionally the content (header+body+footer) will be wrapped with a {@code content} element</li>
	 * <li>{@code wrapper/title/header/body/footer}: the content will <strong>not</strong> be wrapped as a whole, but every component will be wrapped separately</li>
	 * <li>{@code wrapper/title/content/header/body/footer}: all components will be wrapped separately, and header/body/footer will additionally be wrapped with a {@code content} element</li>
	 * </ul>
	 * This template will also add the following CSS classes to the output:
	 * <ul>
	 * <li>{@code element-fieldset}: to the outer fieldset wrapper element</li>
	 * <li>{@code element-fieldset-title}: to the title wrapper element</li>
	 * <li>{@code element-fieldset-content}: to the content wrapper element</li>
	 * <li>{@code element-fieldset-header}: to the header wrapper element</li>
	 * <li>{@code element-fieldset-body}: to the body wrapper</li>
	 * <li>{@code element-fieldset-footer}: to the footer wrapper element</li>
	 * </ul>
	 * For example a format of {@code section/h1/div} with a {@code cssClassName='my-custom-css'} will output the following:
	 * <pre>{@code
	 * <section class="element-fieldset my-custom-css">
	 *     <h1 class="element-fieldset-title">[title elements]</h1>
	 *     <div class="element-fieldset-content">
	 *         [header elements]
	 *         [body elements]
	 *         [footer elements]
	 *     </div>
	 * </section>
	 * }</pre>
	 *
	 * @param cssClassName additional css class that should be added to the outer wrapper
	 * @param format       string
	 * @return template function
	 */
	@SuppressWarnings("unchecked")
	public static Function<ViewElementFieldset, NodeViewElement> template( @NonNull String cssClassName, @NonNull String format ) {
		String[] nodes = format.split( "/" );

		if ( nodes.length == 4 || nodes.length > 7 ) {
			throw new IllegalArgumentException( "Valid fieldset template format is 'wrapper', 'wrapper/title', 'wrapper/title/content', " +
					                                    "'wrapper/title/header/body/footer' or 'wrapper/title/content/header/body/footer'" );
		}

		return fieldset -> {
			NodeViewElement wrapper = new NodeViewElement( nodes[0] );
			wrapper.addCssClass( "element-fieldset", cssClassName );

			if ( nodes.length > 1 ) {
				NodeViewElement title = new NodeViewElement( nodes[1] );
				title.addCssClass( "element-fieldset-title" );
				title.addChild( fieldset.getTitle() );
				wrapper.addChild( title );
			}
			else {
				wrapper.addChild( fieldset.getTitle() );
			}

			NodeViewElement content = wrapper;

			if ( nodes.length == 3 || nodes.length == 6 ) {
				content = new NodeViewElement( nodes[2] );
				content.addCssClass( "element-fieldset-content" );
				wrapper.addChild( content );
			}

			int offset = nodes.length == 5 ? 2 : ( nodes.length == 6 ? 3 : 0 );

			if ( offset > 0 ) {
				NodeViewElement header = new NodeViewElement( nodes[offset] );
				header.addCssClass( "element-fieldset-header" );
				header.addChild( fieldset.getHeader() );

				NodeViewElement body = new NodeViewElement( nodes[offset + 1] );
				body.addCssClass( "element-fieldset-body" );
				body.addChild( fieldset.getBody() );

				NodeViewElement footer = new NodeViewElement( nodes[offset + 2] );
				footer.addCssClass( "element-fieldset-footer" );
				footer.addChild( fieldset.getFooter() );

				content.addChild( header );
				content.addChild( body );
				content.addChild( footer );
			}
			else {
				content.addChild( fieldset.getHeader() );
				content.addChild( fieldset.getBody() );
				content.addChild( fieldset.getFooter() );
			}

			return wrapper;
		};
	}
}
