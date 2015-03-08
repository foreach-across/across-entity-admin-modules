package com.foreach.across.modules.web.ui.elements;

import com.foreach.across.modules.web.ui.StandardViewElements;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a generic node (html element).  Supports tag, set of attributes and child elements.
 *
 * @author Arne Vandamme
 */
public class NodeViewElement extends ContainerViewElement
{
	public static final String TYPE = StandardViewElements.NODE;

	private String tagName;
	private Map<String, String> attributes = new HashMap<>();

	public NodeViewElement() {
		setElementType( TYPE );
	}

	public NodeViewElement( String name ) {
		super( name );
	}

	public NodeViewElement( String name, String tag ) {
		super( name );
		setTagName( tag );
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName( String tagName ) {
		this.tagName = tagName;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes( Map<String, String> attributes ) {
		Assert.notNull(attributes);
		this.attributes = attributes;
	}

	public void setAttribute( String attributeName, String attributeValue ) {
		attributes.put( attributeName, attributeValue );
	}

	public void addAttributes( Map<String, String> attributes ) {
		this.attributes.putAll( attributes );
	}

	public void removeAttribute( String attributeName ) {
		attributes.remove( attributeName );
	}

	public String getAttribute( String attributeName ) {
		return attributes.get( attributeName );
	}

	public boolean hasAttribute( String attributeName ) {
		return attributes.containsKey( attributeName );
	}

	public static NodeViewElement forTag( String tagName ) {
		NodeViewElement node = new NodeViewElement();
		node.setTagName( tagName );

		return node;
	}
}
