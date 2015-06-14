package com.foreach.across.modules.bootstrapui.elements;

import liquibase.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Grid extends ArrayList<Grid.Position>
{
	public static class Width
	{
		public static final int QUARTER = 3;
		public static final int THIRD = 4;
		public static final int HALF = 6;
		public static final int TWO_THIRDS = 8;
		public static final int THREE_QUARTERS = 9;
		public static final int FULL = 12;
	}

	public static class Columns
	{
		public static final int ONE = 1;
		public static final int TWO = 2;
		public static final int THREE = 3;
		public static final int FOUR = 4;
		public static final int FIVE = 5;
		public static final int SIX = 6;
		public static final int SEVEN = 7;
		public static final int EIGHT = 8;
		public static final int NINE = 9;
		public static final int TEN = 10;
		public static final int ELEVEN = 11;
		public static final int TWELVE = 12;
	}

	public interface DeviceGridLayout
	{
	}

	public static class SingleCssClassDeviceGridLayout implements DeviceGridLayout
	{
		private final String generatedClass;

		public SingleCssClassDeviceGridLayout( String generatedClass ) {
			this.generatedClass = generatedClass;
		}

		@Override
		public boolean equals( Object o ) {
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}
			SingleCssClassDeviceGridLayout that = (SingleCssClassDeviceGridLayout) o;
			return Objects.equals( generatedClass, that.generatedClass );
		}

		@Override
		public int hashCode() {
			return Objects.hash( generatedClass );
		}

		@Override
		public String toString() {
			return generatedClass;
		}
	}

	public static class Device
	{
		public static final Device XS = new Device( "xs" );
		public static final Device SM = new Device( "sm" );
		public static final Device MD = new Device( "md" );
		public static final Device LG = new Device( "lg" );

		public static final Device EXTRA_SMALL = XS;
		public static final Device SMALL = SM;
		public static final Device MEDIUM = MD;
		public static final Device LARGE = LG;

		private final String token;
		private DeviceGridLayout hidden;
		private Visibility visible;

		public Device( String token ) {
			this.token = token;
			hidden = new SingleCssClassDeviceGridLayout( "hidden-" + token );
			visible = new Visibility( token );
		}

		public Column width( int columns ) {
			return new Column( token, columns );
		}

		public DeviceGridLayout hidden() {
			return hidden;
		}

		public Visibility visible() {
			return visible;
		}
	}

	public static class Visibility implements DeviceGridLayout
	{
		private final String token;

		public Visibility( String token ) {
			this.token = token;
		}

		public DeviceGridLayout block() {
			return new SingleCssClassDeviceGridLayout( "visible-" + token + "-block" );
		}

		public DeviceGridLayout inline() {
			return new SingleCssClassDeviceGridLayout( "visible-" + token + "-inline" );
		}

		public DeviceGridLayout inlineBlock() {
			return new SingleCssClassDeviceGridLayout( "visible-" + token + "-inline-block" );
		}

		public String toString() {
			return block().toString();
		}
	}

	public static class Column implements DeviceGridLayout
	{
		private final String token;
		private final int width;

		public Column( String token, int width ) {
			this.token = token;
			this.width = width;
		}

		public DeviceGridLayout asWidth() {
			return new SingleCssClassDeviceGridLayout( "col-" + token + "-" + width );
		}

		public DeviceGridLayout asOffset() {
			return new SingleCssClassDeviceGridLayout( "col-" + token + "-offset-" + width );
		}

		public DeviceGridLayout asPull() {
			return new SingleCssClassDeviceGridLayout( "col-" + token + "-pull-" + width );
		}

		public DeviceGridLayout asPush() {
			return new SingleCssClassDeviceGridLayout( "col-" + token + "-push-" + width );
		}

		@Override
		public boolean equals( Object o ) {
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}
			Column column = (Column) o;
			return Objects.equals( width, column.width ) &&
					Objects.equals( token, column.token );
		}

		@Override
		public int hashCode() {
			return Objects.hash( token, width );
		}

		@Override
		public String toString() {
			return asWidth().toString();
		}
	}

	public static class Position extends ArrayList<DeviceGridLayout>
	{
		public Position() {
			super( 4 );
		}

		@Override
		public String toString() {
			List<String> strings = new ArrayList<>( size() );
			for ( DeviceGridLayout action : this ) {
				strings.add( action.toString() );
			}

			return StringUtils.join( strings, " " );
		}
	}

	public Grid() {
		super( Width.FULL );
	}

	@Override
	public String toString() {
		List<String> strings = new ArrayList<>( size() );
		for ( Position position : this ) {
			strings.add( "[" + position.toString() + "]" );
		}

		return "Grid{" + StringUtils.join( strings, "," ) + "}";
	}

	public static Grid create( Position... positions ) {
		Grid grid = new Grid();
		Collections.addAll( grid, positions );
		return grid;
	}

	public static Position position( DeviceGridLayout... layouts ) {
		Position position = new Position();
		Collections.addAll( position, layouts );

		return position;
	}
}
