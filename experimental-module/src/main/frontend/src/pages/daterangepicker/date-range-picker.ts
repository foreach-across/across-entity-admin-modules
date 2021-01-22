import { DateRangePickerControlAdapter } from "./date-range-picker-control-adapter";

import "./date-range-picker.scss";

/**
 * Register the dateRangePicker control in bootstrapUiModule
 */

BootstrapUiModule.ControlAdapterFactory.register("date-range-picker", (node: any) => {
  BootstrapUiModule.ControlAdapterFactory.initializeControlAdapters(node);

  return new DateRangePickerControlAdapter(node);
});
