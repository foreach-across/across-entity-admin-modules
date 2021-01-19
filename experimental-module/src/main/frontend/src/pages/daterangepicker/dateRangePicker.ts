import {DateRangePickerControlAdapter} from "./DateRangePickerControlAdapter";

import "./dateRangePicker.scss";

window.jQuery = $;

/**
 * Register the dateRangePicker control in bootstrapUiModule
 */

BootstrapUiModule.ControlAdapterFactory.register("date-range-picker", (node: any) => {
  BootstrapUiModule.ControlAdapterFactory.initializeControlAdapters(node);

  return new DateRangePickerControlAdapter(node);
});

function runWhenPageIsFullyParsed() {
  if (window.EntityModule && document.getElementsByClassName("js-initialize-command").length) {
    window.EntityModule.initializeFormElements();
  }


  const searchInput = window.document.getElementById("extensions[eqFilterProperties][basicSearch]");
  searchInput && searchInput.focus();
}

if (document.readyState === "complete") {
  // already fired, so run logic right away
  runWhenPageIsFullyParsed();
} else {
  // not fired yet, so let's listen for the event
  window.addEventListener("DOMContentLoaded", runWhenPageIsFullyParsed);
}
