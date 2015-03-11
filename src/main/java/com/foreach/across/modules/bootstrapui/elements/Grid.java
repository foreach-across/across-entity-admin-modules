package com.foreach.across.modules.bootstrapui.elements;

import liquibase.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	public interface DeviceGridAction
	{
	}

	public static class SingleCssClassDeviceGridAction implements DeviceGridAction
	{
		private final String generatedClass;

		public SingleCssClassDeviceGridAction( String generatedClass ) {
			this.generatedClass = generatedClass;
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
		private DeviceGridAction hidden;
		private Visibility visible;

		public Device( String token ) {
			this.token = token;
			hidden = new SingleCssClassDeviceGridAction( "hidden-" + token );
			visible = new Visibility( token );
		}

		public Column width( int columns ) {
			return new Column( token, columns );
		}

		public DeviceGridAction hidden() {
			return hidden;
		}

		public Visibility visible() {
			return visible;
		}
	}

	public static class Visibility implements DeviceGridAction
	{
		private final String token;

		public Visibility( String token ) {
			this.token = token;
		}

		public DeviceGridAction block() {
			return new SingleCssClassDeviceGridAction( "visible-" + token + "-block" );
		}

		public DeviceGridAction inline() {
			return new SingleCssClassDeviceGridAction( "visible-" + token + "-inline" );
		}

		public DeviceGridAction inlineBlock() {
			return new SingleCssClassDeviceGridAction( "visible-" + token + "-inline-block" );
		}

		public String toString() {
			return block().toString();
		}
	}

	public static class Column implements DeviceGridAction
	{
		private final String token;
		private final int width;

		public Column( String token, int width ) {
			this.token = token;
			this.width = width;
		}

		public DeviceGridAction asWidth() {
			return new SingleCssClassDeviceGridAction( "col-" + token + "-" + width );
		}

		public DeviceGridAction asOffset() {
			return new SingleCssClassDeviceGridAction( "col-" + token + "-offset-" + width );
		}

		public DeviceGridAction asPull() {
			return new SingleCssClassDeviceGridAction( "col-" + token + "-pull-" + width );
		}

		public DeviceGridAction asPush() {
			return new SingleCssClassDeviceGridAction( "col-" + token + "-push-" + width );
		}

		@Override
		public String toString() {
			return asWidth().toString();
		}
	}

	public static class Position extends ArrayList<DeviceGridAction>
	{
		public Position() {
			super( 4 );
		}

		@Override
		public String toString() {
			List<String> strings = new ArrayList<>( size() );
			for ( DeviceGridAction action : this ) {
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

	public static Position position( DeviceGridAction... actions ) {
		Position position = new Position();
		Collections.addAll( position, actions );

		return position;
	}
}
