import { convertResponseToText, getCookie } from "../utils/utils";

class SortableTableAjax {
  init = (node: any) => {
    const self = this;

    $("[data-tbl-type=paged]", node).each((idx, element) => {
      if ($(element).data("ajax-pagination") === true) {
        $(element).on("emSortableTable:loadData", function (event, params) {
          event.preventDefault();
          event.stopPropagation();

          self.refreshTableForSort($(element).closest("form"), params);
        });
      }
    });
  };

  refreshTableForSort = (form: any, params: any) => {
    form.prop("readonly", true);
    // register the page attributes as hidden inputs
    $.each(params, (paramName: string, paramValue: any) => {
      this.registerHiddenInput(form, paramName, paramValue);
    });

    const tableBody = $(".pcs-body-section");
    tableBody.addClass("partial-loading");
    tableBody.append('<div class="partial-spinner"></div>');
    let url = this.buildPartialRefreshUrl(form);

    fetch(url, {
      method: "GET",
      headers: { "X-XSRF-Token": getCookie("XSRF-TOKEN") as string },
    })
      .then(convertResponseToText)
      .then((data) => {
        tableBody.removeClass("partial-loading");
        tableBody.replaceWith(data);

        EntityModule.initializeFormElements($(".pcs-body-section"));
      });
    // .catch(handleError);
  };

  private buildPartialRefreshUrl(form: any) {
    let url = window.location.href.split("?")[0];
    const lastChar = url.slice(-1);
    if (lastChar == "#") {
      url = url.slice(0, -1);
    }
    url += "?_partial=::body&" + form.serialize();
    return url;
  }

  registerHiddenInput = (form: any, name: string, value: any) => {
    if (value) {
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
