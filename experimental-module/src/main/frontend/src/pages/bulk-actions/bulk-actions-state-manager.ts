import { getCookie, setCookie } from "../web-utility/utils/utils";

declare interface BulkActionsStateManagerType {
  init: Function;
}

const COOKIE_VALUE_SEPERATOR = "-";

class BulkActionsStateManager {
  init = (node: any) => {
    $(".js-exm-bulk-select-item").on("change", function () {
      onBulkActionChanged.call(this);
    });

    this.setInitialState($(node));
  };

  setInitialState = (bulkActionForm: JQuery<HTMLElement>) => {
    let cookieValue = getCookie(getCookieNameFromBulkActionItem(bulkActionForm)) as string;

    if (typeof cookieValue !== "undefined") {
      cookieValue.split(COOKIE_VALUE_SEPERATOR).forEach((selectedValue) => {
        const checkBox = bulkActionForm.find(':input[value="' + selectedValue + '"]:not([type=hidden])');

        if (checkBox) {
          checkBox.prop("checked", true);
        }
      });
    }
  };
}

function getCookieNameFromBulkActionItem(bulkActionForm: any) {
  const entityName = bulkActionForm.find("table").data("tbl-entity-type");
  const bulkActionName = bulkActionForm.attr("name");

  return "bulkActions-" + bulkActionName + COOKIE_VALUE_SEPERATOR + entityName;
}

function onBulkActionChanged() {
  const form = $(this).closest("form");
  const cookieName = getCookieNameFromBulkActionItem(form);
  const isChecked = $(this).prop("checked") == true;
  const selectedValue = $(this).find(":input:not([type=hidden])").val() as string;

  let currentCookieValue = getCookie(cookieName) as any;
  if (typeof currentCookieValue === "undefined") {
    currentCookieValue = [];
  } else {
    currentCookieValue = currentCookieValue.split(COOKIE_VALUE_SEPERATOR);
  }

  if (isChecked) {
    delete currentCookieValue[selectedValue];
  } else {
    currentCookieValue.push(selectedValue);
  }

  setCookie(cookieName, currentCookieValue.join(COOKIE_VALUE_SEPERATOR), { path: window.location.pathname });
}

export default BulkActionsStateManager;
