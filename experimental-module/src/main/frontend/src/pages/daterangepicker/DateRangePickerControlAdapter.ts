import moment from "moment";

export class DateRangePickerControlAdapter implements BootstrapUiControlAdapter {
  private readonly adapters: any[];
  private readonly target: any;
  // private initialValue: any;
  // private dateRangeOptionValue: string = '';

  constructor(target: any) {
    this.target = target;
    const controlElements: any[] = [];

    $(target)
        .find("[data-bootstrapui-adapter-type]")
        .not('[data-bootstrapui-adapter-type="date-range-picker"]')
        .each((_: number, element: any) => {
          // @ts-ignore
          const adapter = $(element).data("bootstrapui-adapter");
          controlElements.push(adapter);

          $(adapter.getTarget()).on("bootstrapui.change", (event: any) => {
            event.stopPropagation();
            this.triggerChange();
          });
        });

    // @ts-ignore
    $(target)
        .find(".js-custom-datepicker-options")
        .on("change", (event: any) => {
          // @ts-ignore
          if (event.currentTarget.value === "custom") {
            // @ts-ignore
            $(event.currentTarget).parent().find(".js-custom-date-picker-dates").removeClass("d-none");
          } else {
            // @ts-ignore
            $(event.currentTarget).parent().find(".js-custom-date-picker-dates").addClass("d-none");
          }
        });

    this.adapters = controlElements;
  }

  findControlAdapterById(id: any) {
    for (const adapter of this.adapters) {
      // @ts-ignore
      if ($(adapter.getTarget()).attr("data-daterange") === id) {
        // @ts-ignore
        return $(adapter.getTarget()).data("bootstrapui-adapter");
      }
    }

    return null;
  }

  getValue(): BootstrapUiControlValueHolder[] {
    const datepickerOption = this.getDatepickerOption();

    if (datepickerOption === "custom") {
      const dateFrom = this.findControlAdapterById("from");
      const dateTo = this.findControlAdapterById("to");

      let dateFromValue = dateFrom == null ? "" : dateFrom.getValue()[0].value;
      let dateToValue = dateTo == null ? "" : dateTo.getValue()[0].value;

      if (dateFromValue === "") {
        dateFromValue = "";
        dateToValue = "";
      } else {
        if (dateToValue === "") {
          dateToValue = moment(new Date()).format("YYYY-MM-DD");
        }
      }

      return [
        {
          label: "",
          value: `dateRange('${dateFromValue}', '${dateToValue}')`,
          context: "",
        },
      ];
    }
    return [
      {
        label: "",
        value: this.getDatepickerOption(),
        context: "",
      },
    ];
  }

  getDatepickerOption(): string {
    // @ts-ignore
    return $(this.target).find(".js-custom-datepicker-options").val();
  }

  triggerChange() {
    // @ts-ignore
    $(this.getTarget()).trigger("bootstrapui.change", [this]);
  }

  triggerSubmit() {
    // @ts-ignore
    $(this.getTarget()).trigger("bootstrapui.submit", [this]);
  }

  getTarget() {
    return this.target;
  }

  reset(): void {
  }

  selectValue(_: any): void {
  }
}
