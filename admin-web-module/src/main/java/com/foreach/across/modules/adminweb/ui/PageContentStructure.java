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

package com.foreach.across.modules.adminweb.ui;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents the content structure of a single page.
 * A standard content structure has the following elements:
 * <ul>
 * <li>header</li>
 * <li>feedback section</li>
 * <li>nav</li>
 * <li>body section</li>
 * <li>footer</li>
 * </ul>
 * All elements have their own <em>pcs</em>-prefixed css class added, and everything
 * will be wrapped in a single div with class <em>pcs</em>.
 * <p/>
 * Children added directly to the {@link PageContentStructure} will be added to the body section when rendering.
 * If rendered as a tab layout, this will be the active tab pane.
 * <p/>
 * <strong>Request-bound bean:</strong> {@link PageContentStructure} is declared as an exposed request-bound bean.
 * This means a controller can write a {@link PageContentStructure} that will be put on the request under the default
 * attribute name.  Any model attribute set directly will replace the bean structure!
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class PageContentStructure extends AbstractNodeViewElement
{
	/**
	 * This should equal the default {@link org.springframework.web.bind.annotation.ModelAttribute} name generated
	 * based on the class name.
	 */
	public static final String MODEL_ATTRIBUTE = "pageContentStructure";
	public static final String TEMPLATE = AdminWeb.PAGE_CONTENT;

	public static final String ELEMENT_PAGE_TITLE = "pageTitle";
	public static final String ELEMENT_PAGE_TITLE_TEXT = "pageTitleText";
	public static final String ELEMENT_PAGE_TITLE_SUB_TEXT = "pageTitleSubText";
	public static final String ELEMENT_BODY_SECTION = "body";

	public static final String CSS_PCS = "pcs";
	public static final String CSS_HEADER = "pcs-header";
	public static final String CSS_FOOTER = "pcs-footer";
	public static final String CSS_NAV = "pcs-nav";
	public static final String CSS_FEEDBACK_SECTION = "pcs-feedback-section";
	public static final String CSS_BODY_SECTION = "pcs-body-section";

	@Getter
	@Setter
	private boolean renderAsTabs;

	@Getter
	private final NodeViewElement header;

	@Getter
	private final NodeViewElement feedback;

	@Getter
	private final NodeViewElement footer;

	@Getter
	private final NodeViewElement nav;

	public PageContentStructure() {
		super( "div" );
		addCssClass( CSS_PCS );

		header = new NodeViewElement( "header" );
		header.addCssClass( CSS_HEADER );

		footer = new NodeViewElement( "footer" );
		footer.addCssClass( CSS_FOOTER );

		nav = new NodeViewElement( "nav" );
		nav.addCssClass( CSS_NAV );

		feedback = new NodeViewElement( "section" );
		feedback.addCssClass( CSS_FEEDBACK_SECTION );
	}

	/**
	 * Add an element directly to the header section.
	 * This will not create the default header content.  If you want to alter the default page title text
	 * and sub text structure created, you should use either {@link #addToPageTitle(ViewElement)} or
	 * {@link #addToPageTitleSubText(ViewElement)}.
	 *
	 * @param element to add
	 */
	public void addToHeader( ViewElement element ) {
		header.addChild( element );
	}

	/**
	 * Add an element directly to the page title element.
	 * The element will be added after the sub text element.  If you want to add an element before,
	 * you should use the {@link #withPageTitle(Consumer)} method.
	 * <p/>
	 * Calling this method will create the default header if the header section is still empty.
	 *
	 * @param element to add
	 */
	public void addToPageTitle( ViewElement element ) {
		withPageTitle( n -> n.addChild( element ) );
	}

	/**
	 * Add an element directly to the page title sub text element.
	 * <p/>
	 * Calling this method will create the default header if the header section is still empty.
	 *
	 * @param element to add
	 */
	public void addToPageTitleSubText( ViewElement element ) {
		withPageTitleSubText( n -> n.addChild( element ) );
	}

	/**
	 * Add an element directly to the nav section.
	 *
	 * @param element to add
	 */
	public void addToNav( ViewElement element ) {
		nav.addChild( element );
	}

	/**
	 * Add an element directly to the feedback section.
	 *
	 * @param element to add
	 */
	public void addToFeedback( ViewElement element ) {
		feedback.addChild( element );
	}

	/**
	 * Add an element directly to the footer section.
	 *
	 * @param element to add
	 */
	public void addToFooter( ViewElement element ) {
		footer.addChild( element );
	}

	/**
	 * Perform one or more actions with the header element.
	 *
	 * @param consumer to execute
	 */
	public void withHeader( Consumer<NodeViewElement> consumer ) {
		consumer.accept( header );
	}

	/**
	 * Perform one or more actions with the footer element.
	 *
	 * @param consumer to execute
	 */
	public void withFooter( Consumer<NodeViewElement> consumer ) {
		consumer.accept( footer );
	}

	/**
	 * Perform one or more actions with the nav element.
	 *
	 * @param consumer to execute
	 */
	public void withNav( Consumer<NodeViewElement> consumer ) {
		consumer.accept( nav );
	}

	/**
	 * Perform one or more actions with the feedback section element.
	 *
	 * @param consumer to execute
	 */
	public void withFeedback( Consumer<NodeViewElement> consumer ) {
		consumer.accept( feedback );
	}

	/**
	 * Set the page title text directly. Requires an element named {@link #ELEMENT_PAGE_TITLE_TEXT} to be present,
	 * else this method will do nothing.
	 * <p/>
	 * Calling this method will create the default header if the header section if still empty.
	 *
	 * @param pageTitle text to set
	 */
	public void setPageTitle( String pageTitle ) {
		createDefaultHeaderIfHeaderSectionEmpty();
		ContainerViewElementUtils
				.find( header, ELEMENT_PAGE_TITLE_TEXT, ConfigurableTextViewElement.class )
				.ifPresent( t -> t.setText( pageTitle ) );
	}

	/**
	 * Get the current page title text set.  Retrieves the value from the child element named
	 * {@link #ELEMENT_PAGE_TITLE_TEXT}, will return {@code null} if title not set or no page title element.
	 *
	 * @return title text if could be found
	 */
	public String getPageTitle() {
		return ContainerViewElementUtils
				.find( header, ELEMENT_PAGE_TITLE_TEXT, ConfigurableTextViewElement.class )
				.map( ConfigurableTextViewElement::getText )
				.orElse( null );
	}

	/**
	 * Perform one or more actions with the page title element.
	 * The page title is the element named {@link #ELEMENT_PAGE_TITLE} inside the header section,
	 * it usually contains both the page title text and sub text elements.
	 * <p/>
	 * Calling this method will create the default header if the header section is still empty.
	 *
	 * @param consumer to execute
	 */
	public void withPageTitle( Consumer<NodeViewElement> consumer ) {
		createDefaultHeaderIfHeaderSectionEmpty();
		ContainerViewElementUtils
				.find( header, ELEMENT_PAGE_TITLE, NodeViewElement.class )
				.ifPresent( consumer );
	}

	/**
	 * Perform one or more actions with the page title subtext element.
	 * The page title subtext is the element named {@link #ELEMENT_PAGE_TITLE_SUB_TEXT}, usually a &lt;small&gt;
	 * element inside of the page title element inside the header section.
	 * <p/>
	 * Calling this method will create the default header if the header section is still empty.
	 *
	 * @param consumer to execute
	 */
	public void withPageTitleSubText( Consumer<NodeViewElement> consumer ) {
		createDefaultHeaderIfHeaderSectionEmpty();
		ContainerViewElementUtils
				.find( header, ELEMENT_PAGE_TITLE_SUB_TEXT, NodeViewElement.class )
				.ifPresent( consumer );
	}

	private void createDefaultHeaderIfHeaderSectionEmpty() {
		if ( !header.hasChildren() ) {
			TextViewElement titleText = new TextViewElement( ELEMENT_PAGE_TITLE_TEXT, null );

			NodeViewElement heading = new NodeViewElement( ELEMENT_PAGE_TITLE, "h3" );
			heading.addCssClass( "page-header" );
			heading.addChild( titleText );
			heading.addChild( new TextViewElement( " " ) );

			NodeViewElement actionsElement = new NodeViewElement( ELEMENT_PAGE_TITLE_SUB_TEXT, "small" );
			heading.addChild( actionsElement );

			header.addChild( heading );
		}
	}

	/**
	 * @return list of elements that make up the actual content body
	 */
	public List<ViewElement> getContentChildren() {
		return super.getChildren();
	}

	@Override
	public List<ViewElement> getChildren() {
		List<ViewElement> children = new ArrayList<>();

		if ( header.hasChildren() ) {
			children.add( header );
		}

		if ( feedback.hasChildren() ) {
			children.add( feedback );
		}

		NodeViewElement body = new NodeViewElement( ELEMENT_BODY_SECTION, "section" );
		body.addCssClass( CSS_BODY_SECTION );

		if ( super.hasChildren() ) {
			if ( renderAsTabs ) {
				NodeViewElement tabWrapper = new NodeViewElement( "div" );
				tabWrapper.addCssClass( "tabbable", "filled" );
				if ( nav.hasChildren() ) {
					tabWrapper.addChild( nav );
				}
				tabWrapper.addChild( body );

				NodeViewElement tabContent = new NodeViewElement( "div" );
				tabContent.addCssClass( "tab-content" );
				body.addChild( tabContent );

				NodeViewElement tabPane = new NodeViewElement( "div" );
				tabPane.addCssClass( "tab-pane", "active" );
				tabPane.addChildren( getContentChildren() );

				tabContent.addChild( tabPane );

				children.add( tabWrapper );
			}
			else {
				if ( nav.hasChildren() ) {
					children.add( nav );
				}
				children.add( body );

				body.addChildren( getContentChildren() );
			}
		}

		if ( footer.hasChildren() ) {
			children.add( footer );
		}

		return children;
	}
}
