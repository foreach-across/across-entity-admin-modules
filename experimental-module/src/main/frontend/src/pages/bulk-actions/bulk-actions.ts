import "./bulk-actions.scss";
import BulkActionsControlAdapter from './bulk-actions-control-adapter';

BootstrapUiModule.ControlAdapterFactory.register( "bulk-actions-container", ( node: any ) => {
    BootstrapUiModule.ControlAdapterFactory.initializeControlAdapters( node );
    return new BulkActionsControlAdapter( node );
} );
