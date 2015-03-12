package com.foreach.across.modules.bootstrapui.elements;

public class FormLayout
{
	public static enum Type
	{
		DEFAULT,
		INLINE,
		HORIZONTAL
	}

	private Type type = Type.DEFAULT;
	private boolean showLabels = true;
	private Grid grid = null;

	public Type getType() {
		return type;
	}

	public void setType( Type type ) {
		this.type = type;
	}

	public boolean isShowLabels() {
		return showLabels;
	}

	public void setShowLabels( boolean showLabels ) {
		this.showLabels = showLabels;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setGrid( Grid grid ) {
		this.grid = grid;
	}

	public static FormLayout inline( boolean showLabels ) {
		FormLayout layout = new FormLayout();
		layout.setType( Type.INLINE );
		layout.setShowLabels( showLabels );

		return layout;
	}

	public static FormLayout horizontal( int columnsForLabel ) {
		int columnsForControl = Grid.Width.FULL - columnsForLabel;

		if ( columnsForControl <= 0 ) {
			throw new IllegalArgumentException(
					"Unable to auto-create a grid with " + columnsForLabel + " label column width" );
		}

		return horizontal( Grid.position( Grid.Device.MD.width( columnsForLabel ) ),
		                   Grid.position( Grid.Device.MD.width( columnsForControl ) ) );

	}

	public static FormLayout horizontal( Grid.Position labelColumn, Grid.Position controlColumn ) {
		return horizontal( Grid.create( labelColumn, controlColumn ) );
	}

	public static FormLayout horizontal( Grid grid ) {
		FormLayout layout = new FormLayout();
		layout.setType( Type.HORIZONTAL );
		layout.setGrid( grid );

		return layout;
	}
}
