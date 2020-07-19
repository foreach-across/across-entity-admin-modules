import BootstrapUiControlEvent from './bootstrap-ui-control-event';
import BootstrapUiAttributes from './bootstrap-ui-attributes';

export default class BulkActionsControlAdapter implements BootstrapUiControlAdapter
{
    private target: any;
    /**
     * Adapter for the control to select all elements
     */
    private readonly bulkSelectorAdapter: any;
    private readonly itemAdapters: any[];

    constructor( target: any )
    {
        this.target = target;
        const controlElements: BootstrapUiControlAdapter[] = [];

        this.bulkSelectorAdapter = $( target ).find( `[data-${BootstrapUiAttributes.CONTROL_ADAPTER_TYPE}].js-exm-bulk-select-all` )
                                              .first()
                                              .data( BootstrapUiAttributes.CONTROL_ADAPTER );

        $( target ).find( `[data-${BootstrapUiAttributes.CONTROL_ADAPTER_TYPE}]:not(.js-exm-bulk-select-all)` )
                   .not( `[data-${BootstrapUiAttributes.CONTROL_ADAPTER_TYPE}="container"]` )
                   .each( ( index, element ) => {
                       const adapter = $( element ).data( BootstrapUiAttributes.CONTROL_ADAPTER );
                       controlElements.push( adapter );
                   } );
        this.itemAdapters = controlElements;

        $( this.bulkSelectorAdapter.getTarget() ).on( BootstrapUiControlEvent.CHANGE, ( event, adapter ) => {
            event.stopPropagation();

            let selectAll = adapter.getValue().length > 0;
            this.applyValueToAllItems( selectAll );
        } );

        this.itemAdapters.forEach( adapter => {
            $( adapter.getTarget() ).on( BootstrapUiControlEvent.CHANGE, ( event, adapter ) => {
                event.stopPropagation();

                const bulkSelectorIsSelected = this.getAllSelectorValue().length > 0;
                const currentSelectedElements = this.getValue();
                if ( currentSelectedElements.length === this.itemAdapters.length && !bulkSelectorIsSelected ) {
                    this.applyValueToBulkSelector( true );
                }
                else if ( currentSelectedElements.length !== this.itemAdapters.length && bulkSelectorIsSelected ) {
                    this.applyValueToBulkSelector( false );
                }

                this.triggerChange();
            } );
        } );
    }

    applyValueToBulkSelector( shouldSelect: boolean ): void
    {
        this.bulkSelectorAdapter.selectValue( shouldSelect );
    }

    applyValueToAllItems( shouldSelect: boolean ): void
    {
        this.itemAdapters.forEach( adapter => adapter.selectValue( shouldSelect ) );
        this.triggerChange();
    }

    getAllSelectorValue(): BootstrapUiControlValueHolder[]
    {
        return this.bulkSelectorAdapter.getValue();
    }

    getValue(): BootstrapUiControlValueHolder[]
    {
        return [].concat( [].concat( ...this.itemAdapters.map( adapter => adapter.getValue() ) ) );
    }

    reset(): void
    {
        this.itemAdapters.forEach( adapter => adapter.reset() );
    }

    selectValue( select: boolean ): void
    {
        throw new Error( 'Selecting values is currently not support on BulkActionsControlAdapter.' );
    }

    triggerChange(): void
    {
        $( this.getTarget() ).trigger( BootstrapUiControlEvent.CHANGE, [this] );
    }

    triggerSubmit(): void
    {
        $( this.getTarget() ).trigger( BootstrapUiControlEvent.SUBMIT, [this] );
    }

    getTarget(): any
    {
        return this.target;
    }
}
