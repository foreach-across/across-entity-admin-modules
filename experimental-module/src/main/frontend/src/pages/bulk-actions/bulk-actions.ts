import "./bulk-actions.scss";
import BulkActionsControlAdapter from "./bulk-actions-control-adapter";
import BulkActionsStateManager from "./bulk-actions-state-manager";

BootstrapUiModule.ControlAdapterFactory.register("bulk-actions-container", (node: any) => {
  BootstrapUiModule.ControlAdapterFactory.initializeControlAdapters(node);
  return new BulkActionsControlAdapter(node);
});

$('form[name="bulkActionForm"]').each((index, element) => {
  const bulkActionStateManagement = new BulkActionsStateManager();
  bulkActionStateManagement.init(element);
});
