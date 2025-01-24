import Base64Utils from "../web-utility/utils/base64-utils";
import { ax } from "../web-utility/utils/utils";

class BulkActionsStateManager {
  private readonly bulkActionFormNode: any;

  constructor(node: any) {
    this.bulkActionFormNode = $(node);
  }

  init = () => {
    const self = this;
    $(".js-exm-bulk-select-item", this.bulkActionFormNode).on("change", function () {
      const checkboxElement = $(this).find(":input:not([type=hidden])");

      self.onBulkActionChanged(checkboxElement);
    });

    $(".js-exm-bulk-select-all", this.bulkActionFormNode).on("change", function () {
      self.bulkActionFormNode
        .find(".js-exm-bulk-select-item :input:not([type=hidden])")
        .each((index: number, checkboxElement: any) => {
          self.onBulkActionChanged($(checkboxElement));
        });
    });

    $("[data-bulk-action-total-selected-clear]", this.bulkActionFormNode).on("click", function () {
      self.bulkActionFormNode
        .find(".js-exm-bulk-select-item :input:not([type=hidden])")
        .each((index: number, checkboxElement: any) => {
          $(checkboxElement).prop("checked", false);
        });

      self.bulkActionFormNode
        .find(".js-exm-bulk-select-all :input:not([type=hidden])")
        .each((index: number, checkboxElement: any) => {
          $(checkboxElement).prop("checked", false);
        });

      self.setState([]);
    });

    this.setInitialState();
  };

  setInitialState = () => {
    const currentState = this.getState();

    if (currentState) {
      currentState.forEach((selectedValue: any) => {
        const checkBox = this.bulkActionFormNode.find(':input[value="' + selectedValue + '"]:not([type=hidden])');

        if (checkBox) {
          checkBox.prop("checked", true);
        }
      });

      this.updateTotalSelected();
    }
  };

  updateTotalSelected = () => {
    const totalSelectedItems = this.getState().length;
    const totalItemsSelectedField = this.bulkActionFormNode.find("[data-bulk-action-total-selected-text]");

    if (!totalItemsSelectedField.data("bulk-action-total-selected-text")) {
      ax.log.error("Cannot find total items selected field for bulk actions.");
      return;
    }

    const text = totalItemsSelectedField.data("bulk-action-total-selected-text").replace("%", totalSelectedItems);

    if (totalSelectedItems < 1) {
      $("[data-bulk-action-total-selected-clear]", this.bulkActionFormNode).addClass("d-none");
    } else {
      $("[data-bulk-action-total-selected-clear]", this.bulkActionFormNode).removeClass("d-none");
    }

    totalItemsSelectedField.text(text);
  };

  onBulkActionChanged = (checkboxElement: any) => {
    const selectedValue = checkboxElement.val() as string;
    const isChecked = checkboxElement.prop("checked") == true;

    let currentState = this.getState();

    if (isChecked) {
      if (!currentState.includes(selectedValue)) {
        currentState.push(selectedValue);
      }
    } else {
      currentState = currentState.filter((state: string) => state !== selectedValue);
    }

    this.setState(currentState);
  };

  getState = () => {
    let hiddenField = this.bulkActionFormNode.find(".js-bulk-action-paging-state");
    if (!hiddenField.length) {
      return [];
    }

    const currentState = hiddenField.val() as string;

    return currentState !== "" ? JSON.parse(Base64Utils.decode(currentState)) : [];
  };

  setState = (stateToSet: string[]) => {
    let hiddenField = this.bulkActionFormNode.find(".js-bulk-action-paging-state");
    if (hiddenField.length) {
      let valueToSet = Base64Utils.encode(JSON.stringify(stateToSet));
      hiddenField.val(valueToSet);
    }

    this.updateTotalSelected();
  };
}

export default BulkActionsStateManager;

EntityModule.registerInitializer(function (node) {
  let $node = typeof node === "undefined" ? $(document) : $(node);

  $node.findSelf(".exm-table-refresh-target").each((index, element) => {
    const bulkActionStateManagement = new BulkActionsStateManager(element);
    bulkActionStateManagement.init();
  });
});
