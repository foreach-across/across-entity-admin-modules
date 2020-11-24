import { ax } from "../utils/utils";
import { EditableValueHandler } from "./editable-value-handler";
import { EditableValue } from "./editable-value";

EntityModule.registerInitializer(function (node) {
  $("[data-em-editable-value]", node).each(function () {
    const $element = $(this);
    new EditableValue($element).activate();

    $element.on("experimental.editable-value.switch-to-control", function (event, ctx) {
      if (ctx.editableValueController.controlHolder) {
        const $control = $(ctx.editableValueController.controlHolder).find("[data-editable-value-control]");
        const $elements = $("input[type=text]:not([disabled]), textarea:not([disabled])", $control);
        if ($elements.length === 1) {
          // @ts-ignore
          $elements.focusTextToEnd();
        }
      }
    });
  });
});

EntityModule.registerInitializer(function (node) {
  const $node = $(node);
  if ($node.length > 0 && $node.data("editable-value-control") === true) {
    const htmlNode = $node[0];
    ExperimentalModule.editableValueHandlerFactory.getHandledEventTypes().forEach((eventType) => {
      htmlNode.addEventListener(
        eventType,
        function (event: any) {
          if (event || !event.editableValueHolder) {
            try {
              event["editableValueHolder"] = $(node).data("editableValue");
            } catch (error) {
              ax.log.error("Unable to attach editable-value-controller to event", [event, error]);
            }
          }
        },
        true
      );

      htmlNode.addEventListener(eventType, function (event: any) {
        if (event && event.editableValueHolder) {
          ExperimentalModule.editableValueHandlerFactory.handle(event.target, event.editableValueHolder, event);
        }
      });
    });
  }
});

(function ($) {
  // @ts-ignore
  $.fn.focusTextToEnd = function () {
    this.focus();
    var $thisVal = this.val();
    this.val("").val($thisVal);
    return this;
  };
})(jQuery);

class TextInputEditableValueHandler implements EditableValueHandler {
  canHandle(control: any, editableValueHolder: EditableValue, event: any): boolean {
    if (event.type === "keypress" && event.key === "Enter" && !editableValueHolder.supportsMultiValueSelection()) {
      const $control = $(control);
      return $control.is("input[type='text']") || $control.is("input[type='search']");
    }
    return false;
  }

  getHandledEventTypes(): string[] {
    return ["keypress"];
  }

  getName(): string {
    return "TextInputEditableValueHandler";
  }

  getOrder(): number {
    return 2147483647;
  }

  handle(control: any, editableValueHolder: EditableValue, event: any): void {
    const $control = $(control);
    $control.trigger("blur");
    editableValueHolder._updateValueWithSpinner(event);
  }
}

class CheckboxRadioSelectEditableValueHandler implements EditableValueHandler {
  canHandle(control: any, editableValueHolder: EditableValue, event: any): boolean {
    if (event.type === "change" && !editableValueHolder.supportsMultiValueSelection()) {
      const $control = $(control);
      return $control.is("input[type='checkbox']") || $control.is("inupt[type='radio']") || $control.is("select");
    }
    return false;
  }

  getHandledEventTypes(): string[] {
    return ["change"];
  }

  getName(): string {
    return "CheckboxRadioSelectEditableValueHandler";
  }

  getOrder(): number {
    return 2147483647;
  }

  handle(control: any, editableValueHolder: EditableValue, event: any): void {
    $(control).trigger("blur");
    editableValueHolder._updateValueWithSpinner(event);
  }
}

ExperimentalModule.editableValueHandlerFactory.registerHandler(new TextInputEditableValueHandler());
ExperimentalModule.editableValueHandlerFactory.registerHandler(new CheckboxRadioSelectEditableValueHandler());
