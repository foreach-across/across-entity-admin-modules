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
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
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

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

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
 * {@link #TEMPLATE_SECTION_H3}, {@link #TEMPLATE_PANEL_DEFAULT}, {@link #TEMPLATE_PANEL_INFO}, {@link #TEMPLATE_PANEL_PRIMARY},
 * {@link #TEMPLATE_PANEL_SUCCESS}, {@link #TEMPLATE_PANEL_WARNING} and {@link #TEMPLATE_PANEL_DANGER}.
 * </p>
 * <p>
 * See also the utility functions for manually generating simple templates:
 * <ul>
 * <li>{@link #structureTemplate(String, String)}</li>
 * <li>{@link #panelTemplate(String, Style)}</li>
 * </ul>
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
	 * Template that renders the fieldset as an actual HTML {@code fieldset} element with only a {@code legend}.
	 * An additional class {@code element-fieldset-fieldset} will be put on the outer wrapper.
	 * The body of the fieldset will not be wrapped to stay compatible with the equivalent {@link FieldsetFormElement} output.
	 * See the {@link #structureTemplate(String, String)} documentation for information on the other CSS classes added.
	 * <p/>
	 * NOTE: this is the default rendering template.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_FIELDSET = structureTemplate( "element-fieldset-fieldset",
	                                                                                                                "fieldset/legend" );

	/**
	 * Template that renders the fieldset with an outer {@code section} element, using a {@code h1} for the title and a {@code div} for the content wrapper.
	 * An additional class {@code element-fieldset-section-h1} will be put on the outer wrapper.
	 * See the {@link #structureTemplate(String, String)} documentation for information on the other CSS classes added.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_SECTION_H1 = structureTemplate( "element-fieldset-section-h1",
	                                                                                                                  "section/h1/div" );

	/**
	 * Template that renders the fieldset with an outer {@code section} element, using a {@code h2} for the title and a {@code div} for the content wrapper.
	 * An additional class {@code element-fieldset-section-h2} will be put on the outer wrapper.
	 * See the {@link #structureTemplate(String, String)} documentation for information on the other CSS classes added.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_SECTION_H2 = structureTemplate( "element-fieldset-section-h2",
	                                                                                                                  "section/h2/div" );

	/**
	 * Template that renders the fieldset with an outer {@code section} element, using a {@code h3} for the title and a {@code div} for the content wrapper.
	 * An additional class {@code element-fieldset-section-h3} will be put on the outer wrapper.
	 * See the {@link #structureTemplate(String, String)} documentation for information on the other CSS classes added.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_SECTION_H3 = structureTemplate( "element-fieldset-section-h3",
	                                                                                                                  "section/h3/div" );

	/**
	 * Template that renders the fieldset as a Bootstrap {@code panel-default} with additional css class {@code element-fieldset-panel-default}.
	 * See {@link #panelTemplate(String, Style)} for information on the structure generated.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_PANEL_DEFAULT
			= panelTemplate( "element-fieldset-panel-default", Style.DEFAULT );

	/**
	 * Template that renders the fieldset as a Bootstrap {@code panel-primary} with additional css class {@code element-fieldset-panel-primary}.
	 * See {@link #panelTemplate(String, Style)} for information on the structure generated.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_PANEL_PRIMARY
			= panelTemplate( "element-fieldset-panel-primary", Style.PRIMARY );

	/**
	 * Template that renders the fieldset as a Bootstrap {@code panel-info} with additional css class {@code element-fieldset-panel-info}.
	 * See {@link #panelTemplate(String, Style)} for information on the structure generated.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_PANEL_INFO
			= panelTemplate( "element-fieldset-panel-info", Style.INFO );

	/**
	 * Template that renders the fieldset as a Bootstrap {@code panel-success} with additional css class {@code element-fieldset-panel-success}.
	 * See {@link #panelTemplate(String, Style)} for information on the structure generated.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_PANEL_SUCCESS
			= panelTemplate( "element-fieldset-panel-success", Style.SUCCESS );

	/**
	 * Template that renders the fieldset as a Bootstrap {@code panel-warning} with additional css class {@code element-fieldset-panel-warning}.
	 * See {@link #panelTemplate(String, Style)} for information on the structure generated.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_PANEL_WARNING
			= panelTemplate( "element-fieldset-panel-warning", Style.WARNING );

	/**
	 * Template that renders the fieldset as a Bootstrap {@code panel-danger} with additional css class {@code element-fieldset-panel-danger}.
	 * See {@link #panelTemplate(String, Style)} for information on the structure generated.
	 */
	public static final Function<ViewElementFieldset, ? extends ViewElement> TEMPLATE_PANEL_DANGER
			= panelTemplate( "element-fieldset-panel-danger", Style.DANGER );

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
	private Function<ViewElementFieldset, ? extends ViewElement> template = TEMPLATE_FIELDSET;

	@Override
	public Stream<ViewElement> elementStream() {
		return Stream.of( title, header, body, footer );
	}

	@Override
	public ViewElementFieldset addChild( ViewElement element ) {
		getBody().addChild( element );
		return this;
	}

	@Override
	public ViewElementFieldset addChildren( Collection<? extends ViewElement> elements ) {
		getBody().addChildren( elements );
		return this;
	}

	@Override
	public ViewElementFieldset addFirstChild( ViewElement element ) {
		getBody().addFirstChild( element );
		return this;
	}

	@Override
	public boolean removeChild( ViewElement element ) {
		return getBody().removeChild( element );
	}

	@Override
	public ViewElementFieldset clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public List<ViewElement> getChildren() {
		return Collections.singletonList( template.apply( this ) );
	}

	/**
	 * Helper for type inference when declaring a template, simply returns the input function.
	 * Can be used with {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor} attributes, for example:
	 * {@code descriptor.attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.template( fields -> new ContainerViewElement() ) )}.
	 *
	 * @param template function (not null)
	 * @param <U>      output view element type
	 * @return template function
	 */
	public static <U extends ViewElement> Function<ViewElementFieldset, U> template( @NonNull Function<ViewElementFieldset, U> template ) {
		return template;
	}

	/**
	 * Generate a template function that outputs a simple HTML structure based on a structure string.
	 * The structure can be any of the following:
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
	 * @param structure    string
	 * @return template function
	 */
	@SuppressWarnings("unchecked")
	public static Function<ViewElementFieldset, NodeViewElement> structureTemplate( @NonNull String cssClassName, @NonNull String structure ) {
		String[] nodes = structure.split( "/" );

		if ( nodes.length == 4 || nodes.length > 7 ) {
			throw new IllegalArgumentException( "Valid fieldset template structure is 'wrapper', 'wrapper/title', 'wrapper/title/content', " +
					                                    "'wrapper/title/header/body/footer' or 'wrapper/title/content/header/body/footer'" );
		}

		return fieldset -> {
			NodeViewElement wrapper = new NodeViewElement( nodes[0] );
			wrapper.addCssClass( "element-fieldset", cssClassName );

			if ( fieldset.getTitle().hasChildren() ) {
				if ( nodes.length > 1 ) {
					NodeViewElement title = new NodeViewElement( nodes[1] );
					title.addCssClass( "element-fieldset-title" );
					title.addChild( fieldset.getTitle() );
					wrapper.addChild( title );
				}
				else {
					wrapper.addChild( fieldset.getTitle() );
				}
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

				if ( fieldset.getHeader().hasChildren() ) {
					content.addChild( header );
				}
				content.addChild( body );
				if ( fieldset.getFooter().hasChildren() ) {
					content.addChild( footer );
				}
			}
			else {
				content.addChild( fieldset.getHeader() );
				content.addChild( fieldset.getBody() );
				content.addChild( fieldset.getFooter() );
			}

			return wrapper;
		};
	}

	/**
	 * Generate a template function that builds a Bootstrap panel layout for the fieldset.
	 * Heading and footer panel elements will only be added if their content is not empty.
	 * <p>
	 * This template will also add the following CSS classes to the output:
	 * <ul>
	 * <li>{@code element-fieldset}: to the main panel div</li>
	 * <li>{@code element-fieldset-title}: to the panel heading</li>
	 * <li>{@code element-fieldset-body}: to the panel body</li>
	 * <li>{@code element-fieldset-header}: to an additional div wrapping the header element (inside panel body)</li>
	 * <li>{@code element-fieldset-body}: to an additional div wrapping the body element (inside panel body)</li>
	 * <li>{@code element-fieldset-footer}: to the panel footer</li>
	 * </ul>
	 * For example a format of {@code Style.DANGER} with a {@code cssClassName='my-custom-css'} will output the following:
	 * <pre>{@code
	 * <div class="element-fieldset my-custom-css panel panel-danger">
	 *  <div class="element-fieldset-title panel-heading">[title elements]</div>
	 *  <div class="element-fieldset-content panel-body">
	 *      <div class="element-fieldset-header">[header elements]</div>
	 *      <div class="element-fieldset-body">[body elements]</div>
	 *  </div>
	 *  <div class="element-fieldset-footer panel-footer">[footer elements]</div>
	 * </div>
	 * }</pre>
	 *
	 * @param cssClassName additional css class that should be added to the main panel div
	 * @param style        panel style
	 * @return template function
	 */
	public static Function<ViewElementFieldset, NodeViewElement> panelTemplate( @NonNull String cssClassName, @NonNull Style style ) {
		return fieldset -> {
			NodeViewElement wrapper = new NodeViewElement( "div" );
			// todo what to do with Style objects? In current case this is also applied for "default"
			wrapper.addCssClass( "element-fieldset", cssClassName, style.forPrefix( "border" ) )
			       .set( css.card );

			if ( fieldset.getTitle().hasChildren() ) {
				NodeViewElement panelHeading = new NodeViewElement( "div" );
				panelHeading.addCssClass( "element-fieldset-title", style.forPrefix( "bg" ) ).set( css.card.header );
				panelHeading.addChild( fieldset.getTitle() );
				wrapper.addChild( panelHeading );
			}

			NodeViewElement panelBody = new NodeViewElement( "div" );
			panelBody.addCssClass( "element-fieldset-content" ).set( css.card.body );

			if ( fieldset.getHeader().hasChildren() ) {
				NodeViewElement header = new NodeViewElement( "div" );
				header.addCssClass( "element-fieldset-header" );
				header.addChild( fieldset.getHeader() );
				panelBody.addChild( header );
			}

			NodeViewElement b = new NodeViewElement( "div" );
			b.addCssClass( "element-fieldset-body" );
			b.addChild( fieldset.getBody() );
			panelBody.addChild( b );

			NodeViewElement panelFooter = new NodeViewElement( "div" );
			panelFooter.addCssClass( "element-fieldset-footer", style.forPrefix( "bg" ) ).set( css.card.footer );
			panelFooter.addChild( fieldset.getFooter() );

			wrapper.addChild( panelBody );

			if ( fieldset.getFooter().hasChildren() ) {
				wrapper.addChild( panelFooter );
			}

			return wrapper;
		};
	}
}
