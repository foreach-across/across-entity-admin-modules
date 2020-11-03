package com.foreach.across.modules.experimental.webutility.support;


import java.util.*;

public class SectionedFormLayoutHelper implements Iterator<Map.Entry<String, List<List<String>>>> {

    private List<String> sections = new ArrayList<>();
    private List<List<List<String>>> rows = new ArrayList<>();
    private int index;

    public static SectionedFormLayoutHelper sectionedFormLayoutHelper() {
        return new SectionedFormLayoutHelper();
    }

    public SectionedFormLayoutHelper addSection( String sectionMessageCode ) {
        sections.add( sectionMessageCode );
        rows.add( new ArrayList<>() );
        return this;
    }

    public SectionedFormLayoutHelper addSection() {
        return addSection( "" );
    }

    public SectionedFormLayoutHelper addRow( List<String> row ) {
        rows.get( sections.size() - 1 ).add( row );
        return this;
    }

    public Iterator<Map.Entry<String, List<List<String>>>> asIterator() {
        index = 0;
        return this;
    }

    public String getFirstSection() {
        return sections.get( 0 );
    }

    public String[] getProperties() {
        return rows.stream().flatMap( Collection::stream ).flatMap( Collection::stream ).toArray( String[]::new );
    }

    @Override
    public boolean hasNext() {
        return sections.size() > index;
    }

    @Override
    public Map.Entry<String, List<List<String>>> next() {
        AbstractMap.SimpleEntry<String, List<List<String>>> next = new AbstractMap.SimpleEntry<>( sections.get( index ), rows.get( index ) );
        index++;
        return next;
    }
}
