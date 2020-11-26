package com.foreach.across.modules.experimental.webutility.viewprocessor;

import com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElementBuilders;
import com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SingleEntityFormViewProcessor;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.experimental.webutility.support.SectionedFormLayoutHelper;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

/**
 * Used to render properties in separate sections and columns.
 * Used in conjunction with the {@link SectionedFormLayoutHelper}
 */
@RequiredArgsConstructor
public class SectionedFormLayoutViewProcessor extends EntityViewProcessorAdapter
{

	private static final BootstrapViewElementBuilders builders = BootstrapViewElements.bootstrap.builders;
	private final SectionedFormLayoutHelper helper;
	private boolean letSingleElementRowOverFullRow;
	private boolean adjustToMaxRowSizeInSection;

	public SectionedFormLayoutViewProcessor singleElementRowOverFullRow( boolean singleElementRowOverFullRow ) {
		this.letSingleElementRowOverFullRow = singleElementRowOverFullRow;
		return this;
	}

	public SectionedFormLayoutViewProcessor adjustToMaxRowSizeInSection( boolean adjustToMaxRowSizeInSection ) {
		this.adjustToMaxRowSizeInSection = adjustToMaxRowSizeInSection;
		return this;
	}

	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		Optional<ContainerViewElement> containerViewElement = ContainerViewElementUtils.find( container, SingleEntityFormViewProcessor.FORM,
		                                                                                      ContainerViewElement.class );
		if ( containerViewElement.isPresent() ) {
			addRows( containerViewElement.get(), builderContext );
			fixButtons( containerViewElement.get() );
		}
		else {
			                         addRows( container, builderContext );
			                         fixButtons( container );
		}
	}

	private void addRows( ContainerViewElement container, ViewElementBuilderContext builderContext ) {
		String firstSection = helper.getFirstSection();
		helper.asIterator().forEachRemaining( entry -> {
			String section = entry.getKey();

			AtomicInteger biggestRowSizeInSection = new AtomicInteger( 0 );
			if ( adjustToMaxRowSizeInSection ) {
				entry.getValue().stream().max( Comparator.comparingInt( List::size ) )
				     .ifPresent( list -> biggestRowSizeInSection.set( list.size() ) );
			}

			List<List<String>> sectionRows = entry.getValue();
			if ( !StringUtils.equalsIgnoreCase( firstSection, section ) ) {
				container.addChild( new NodeViewElement( "hr" ) );
			}

			container.addChild( builders.row().name( "row-" + section ).add( new TextViewElement( builderContext.getMessage( section ) ) ).css( "h3" )
			                            .build() );
			Integer finalBiggestRowSizeInSection = biggestRowSizeInSection.get();
			IntStream.range( 0, sectionRows.size() )
			         .mapToObj( i -> createRowAndMoveFormElement( container, section, sectionRows.get( i ), i, finalBiggestRowSizeInSection ) )
			         .forEachOrdered( container::addChild );
		} );
	}

	private NodeViewElement createRowAndMoveFormElement( ContainerViewElement container,
	                                                     String section,
	                                                     List<String> sectionRowNames,
	                                                     int i,
	                                                     Integer biggestRowSizeInSection ) {
		int colWidth = calculateColWidth( sectionRowNames, biggestRowSizeInSection );
		Map<String, NodeViewElement> cols = IntStream.range( 0, sectionRowNames.size() )
		                                             .filter( j -> StringUtils.isNotBlank( sectionRowNames.get( j ) ) )
		                                             .mapToObj( j -> createSectionEntry( sectionRowNames, j, colWidth ) )
		                                             .collect( Collectors.toMap( AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue,
		                                                                         ( a, b ) -> a, TreeMap::new ) );
		List<NodeViewElement> sortedViewElements = cols.entrySet().stream().sorted( Comparator.comparingInt( e -> sectionRowNames.indexOf( e.getKey() ) ) ).map(
				Map.Entry::getValue ).collect( Collectors.toList() );
		NodeViewElement row = builders.row()
		                              .name( "row-" + section + "-" + i )
		                              .addAll( sortedViewElements )
		                              .build();
		cols.forEach( ( key, value ) -> ContainerViewElementUtils.move( container, "formGroup-" + key, value ) );
		return row;
	}

	private int calculateColWidth( List<String> sectionRowNames, Integer biggestRowSizeInSection ) {
		int colWidth;
		if ( biggestRowSizeInSection == null || biggestRowSizeInSection == 0 ) {
			colWidth = !letSingleElementRowOverFullRow ? Math.min( 6, 12 / sectionRowNames.size() ) : Math.max( 6, 12 / sectionRowNames.size() );
		}
		else {
			colWidth = Math.max( 4, 12 / biggestRowSizeInSection );
		}
		return colWidth;
	}

	private static AbstractMap.SimpleEntry<String, NodeViewElement> createSectionEntry( List<String> sectionRowNames, int i, int colWidth ) {
		colWidth = expandColWidthIfEmptyColumnsFollow( sectionRowNames, i, colWidth );
		return new AbstractMap.SimpleEntry<>(
				sectionRowNames.get( i ),
				new NodeViewElementBuilder( "div" ).css( "col-md-" + colWidth ).build() );
	}

	private static int expandColWidthIfEmptyColumnsFollow( List<String> sectionRowNames, int i, int colWidth ) {
		int j = i + 1;
		while ( j < sectionRowNames.size() && StringUtils.isBlank( sectionRowNames.get( j ) ) ) {
			colWidth += colWidth;
			j++;
		}
		return colWidth;
	}

	private void fixButtons( ContainerViewElement form ) {
		NodeViewElement buttonRow = builders.row()
		                                    .name( "buttons" )
		                                    .css( css.margin.top.s5.toCssClasses() )
		                                    .css( css.margin.left.s1.toCssClasses() )
		                                    .build();
		ContainerViewElementUtils.move( form, "btn-save", buttonRow );
		ContainerViewElementUtils.move( form, "btn-cancel", buttonRow );
		ContainerViewElementUtils.move( form, "btn-back", buttonRow );
		form.removeFromTree( "buttons" );
		form.addChild( buttonRow );
	}
}
