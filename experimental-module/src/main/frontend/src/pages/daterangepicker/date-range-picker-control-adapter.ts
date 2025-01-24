export class DateRangePickerControlAdapter implements BootstrapUiControlAdapter {
  private readonly rangeTypeSelector: BootstrapUiControlAdapter;
  private readonly target: any;
  private readonly adapters: any[];

  constructor(target: any) {
    this.target = target;
    const controlElements: any[] = [];

    $("[data-bootstrapui-adapter-type]", target)
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

    this.rangeTypeSelector = $(".js-custom-datepicker-options[data-bootstrapui-adapter-type]", target).data(
      "bootstrapui-adapter"
    );

    // @ts-ignore
    $(this.rangeTypeSelector.getTarget()).on("bootstrapui.change", (event, adapter) => {
      const currentSelection = adapter.getValue();
      if (currentSelection.length > 0) {
        const currentValue = currentSelection[0].value;
        if (currentValue === "custom") {
          $(".js-custom-date-picker-dates", this.getTarget()).removeClass("d-none");
        } else {
          $(".js-custom-date-picker-dates", this.getTarget()).addClass("d-none");
        }
      }
    });

    this.adapters = controlElements;
  }

  findControlAdapterById(id: any) {
    for (const adapter of this.adapters) {
      const adapterTarget = $(adapter.getTarget());
      if (adapterTarget.attr("data-daterange") === id) {
        return adapterTarget.data("bootstrapui-adapter");
      }
    }

    return null;
  }

  getValue(): BootstrapUiControlValueHolder[] {
    const value = this.rangeTypeSelector.getValue();

    if (value.length == 0) {
      return [];
    }

    if (value.length > 0 && value[0].value === "custom") {
      const dateFrom = this.findControlAdapterById("from");
      const dateTo = this.findControlAdapterById("to");

      const startDateValue = dateFrom?.getValue()[0];
      const endDateValue = dateTo?.getValue()[0];
      return [
        {
          label: `dateRange('${startDateValue?.label}','${endDateValue?.label}')`,
          value: `dateRange('${startDateValue?.label}', '${endDateValue?.label}')`,
          context: {
            type: this.rangeTypeSelector.getTarget(),
            startDate: dateFrom.getTarget(),
            endDate: dateTo.getTarget(),
          },
        },
      ];
    }

    return [{ ...value[0], ...{ context: this.rangeTypeSelector.getTarget() } }];
  }

  triggerChange() {
    $(this.getTarget()).trigger("bootstrapui.change", [this]);
  }

  triggerSubmit() {
    $(this.getTarget()).trigger("bootstrapui.submit", [this]);
  }

  getTarget() {
    return this.target;
  }

  reset(): void {}

  selectValue(_: any): void {}
}
