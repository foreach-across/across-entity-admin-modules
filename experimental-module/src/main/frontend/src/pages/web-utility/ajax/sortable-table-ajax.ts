import { ax } from "../utils/utils";
import { executeRequest, translateResponse } from "../utils/request-utils";
import { JsonResponse, TextResponse } from "../utils/response-types";

class SortableTableAjax {
  init = (node: any) => {
    const self = this;

    $("[data-tbl-type=paged]", node).each((idx, table) => {
      if ($(table).data("ajax-pagination") === true) {
        $(table).on("emSortableTable:loadData", function (event, params) {
          event.preventDefault();
          event.stopPropagation();

          self.refreshTableForSort($(table), params);
        });
      }
    });
  };

  refreshTableForSort = (table: any, params: any) => {
    const form = $(table).closest("form") as any;
    const baseUrl = $(table).data("ajax-url");

    form.prop("readonly", true);
    // register the page attributes as hidden inputs
    $.each(params, (paramName: string, paramValue: any) => {
      this.registerHiddenInput(form, paramName, paramValue);
    });

    table.addClass("partial-loading");
    table.append('<div class="partial-spinner"></div>');

    executeRequest({
      partial: "::itemsTable",
      form: form,
      method: "GET",
      url: baseUrl,
    })
      .then(translateResponse)
      .then((text: TextResponse | JsonResponse) => {
        table.removeClass("partial-loading");

        if ("textContent" in text) {
          const newData = $(text.textContent);
          let refreshableTable = table.closest(".exm-table-refresh-target");
          refreshableTable.replaceWith(newData);

          if (newData.length > 0) {
            newData.each(function () {
              if (this.nodeType !== Node.COMMENT_NODE) {
                EntityModule.initializeFormElements($(this));
              }
            });
          } else {
            EntityModule.initializeFormElements(newData);
          }
        }
      })
      .catch((error) => {
        ax.log.error("Unable to fetch page", [error]);
      });
  };

  registerHiddenInput = (form: any, name: string, value: any) => {
    if (typeof value !== "undefined") {
      $("input[name=" + name + "][type=hidden]").remove();

      const control = $("input[name=" + name + "]", form);

      if (control.length) {
        control.val(value);
      } else {
        if ($.isArray(value)) {
          for (let i = 0; i < value.length; i++) {
            form.append('<input type="hidden" name="' + name + '" value="' + value[i] + '" />');
          }
        } else {
          form.append('<input type="hidden" name="' + name + '" value="' + value + '" />');
        }
      }
    }
  };
}

export const sortableTableAjax = new SortableTableAjax();
