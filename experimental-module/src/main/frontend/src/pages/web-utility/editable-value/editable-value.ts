/* tslint:disable */
import { ax } from "../utils/utils";
import "./editable-value.scss";
import { executeFetchRequest, translateResponse } from "../utils/request-utils";
import { JsonResponse, TextResponse } from "../utils/response-types";
import { EditableValueUpdateHandler } from "./editable-value-update-handler";

/**
 * EditableValue is a readonly value which can be converted into an actual
 * control when clicked. The control is expected to be rendered in the
 * markup as a <script> snippet.
 *
 * If an attribute 'em-property-value-changed' is
 * set on the same node that contains the 'data-em-property-id', the control
 * will be fetched again from the server before showing it. This will cause a small delay
 * during with the class 'editable-value-reload' will be set on the wrapper element.
 */

function entityPrefixOf(propertyId: any): string {
  return propertyId.substring(0, propertyId.lastIndexOf("/") + 1);
}

function propertyNameOf(propertyId: any): string {
  return propertyId.substring(propertyId.lastIndexOf("/") + 1);
}

function resolveViewElementMode(node: any) {
  var viewElementMode = $(node).data("em-ve-mode");
  if (viewElementMode == null) {
    return "VALUE";
  }
  return viewElementMode;
}

export function updatePropertyData(propertyId: any, propertyData: any) {
  $('[data-em-property-id="' + propertyId + '"]').each(function () {
    $(this).data("em-property-value-changed", true);
    var label = propertyData.labels[resolveViewElementMode($(this))];
    $(this).html(label);
    EntityModule.initializeFormElements($(this));
  });
}

var EDITABLE_CONTROL_WRAPPER =
  '<form class="editable-value-form"><div class="editable-value-control"><span data-editable-value-control="true"></span>' +
  '<span class="editable-value-actions">' +
  '<a class="btn btn-sm" data-action="save"><i class="fas fa-check fa-fw"/></a>' +
  '<a class="btn btn-sm" data-action="cancel"><i class="fas fa-times fa-fw"/></a></span></div></form>';

var EDITABLE_CONTROL_WRAPPER_WITHOUT_ACTIONS =
  '<form class="editable-value-form"><div class="editable-value-control"><span data-editable-value-control="true"></span>' +
  "</div></form>";

export class EditableValue {
  private showActions: string;
  private wrapper: any;
  private settings: any;
  private label: any;
  private controlContainer: any;
  private propertyId: any;
  private entityPrefix: string;
  private propertyNameOfControl: string;
  private refreshBusy: boolean = false;
  private controlSwitchRequested = false;
  private controlHolder: any;

  constructor(wrapper: any) {
    this.showActions = wrapper.attr("show-actions");
    this.wrapper = wrapper;
    this.settings = wrapper.data("em-editable-value");
    this.label = $("[data-em-editable-value-role=value]", wrapper).first();
    this.controlContainer = $("[data-em-editable-value-role=control-container]", wrapper).first();
    this.propertyId = this.settings.propertyId;
    this.entityPrefix = entityPrefixOf(this.settings.propertyId);
    this.propertyNameOfControl = propertyNameOf(this.settings.propertyId);
  }

  activate() {
    ax.log.groupCollapsed("Activating editable value for " + this.propertyId);
    ax.log.info("Wrapper element", this.wrapper.get(0));
    ax.log.debug("Entity prefix:", this.entityPrefix as any);
    ax.log.debug("Property name:", this.propertyNameOfControl as any);

    // Activate styling when javascript hooked up
    this.wrapper.addClass("editable-value-wrapper");

    this._configureLabelHandler();

    ax.log.groupEnd();
  }

  _configureLabelHandler() {
    var label = this.label;

    // Configure refresh from other value updates
    label.addClass("editable-value-value").attr("data-em-property-id", this.propertyId);

    // Trigger the control reloading on mousedown, so it would fetch slightly faster if necessary
    label.on("mousedown", this._refreshControlScript.bind(this));
    label.on("click", this.switchToControl.bind(this));
  }

  _refreshControlScript() {
    if (this.isValuePossibleChanged()) {
      this.refreshBusy = true;
      const editableControl = this;

      ax.log.debug("Refreshing editable value control for", this.propertyId);

      let url = editableControl.settings.targetUrl;
      url =
        url.indexOf("?") === -1
          ? `${url}?_partial=::editableValue-${editableControl.propertyNameOfControl}-control`
          : `${url}&_partial=::editableValue-${editableControl.propertyNameOfControl}-control`;
      executeFetchRequest(url, "get", {
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
      })
        .then(translateResponse)
        .then((resp) => {
          const textResponse: TextResponse = resp as TextResponse;
          const textContent: any = textResponse.textContent;

          ax.log.debug("Updating control script for property", editableControl.propertyId);
          editableControl.controlContainer.html(textContent);
          editableControl.resetChangeTracking();

          editableControl.refreshBusy = false;

          if (editableControl.controlSwitchRequested) {
            editableControl._renderControl();
          }
        });
    }
  }

  switchToControl(event: any) {
    this.controlSwitchRequested = true;

    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }

    if (!this.refreshBusy) {
      this._renderControl();
    } else {
      this.wrapper.addClass("editable-value-reload");
    }
  }

  _renderControl() {
    this.controlSwitchRequested = false;

    ax.log.debug("Switching to editable value control for", this.propertyId);
    this.label.addClass("d-none");
    this.wrapper
      .removeClass("editable-value-reload")
      .append(this.showActions === "true" ? EDITABLE_CONTROL_WRAPPER : EDITABLE_CONTROL_WRAPPER_WITHOUT_ACTIONS);

    // ensure form cannot be submitted
    var controlHolder = $("form", this.wrapper);
    controlHolder.on("submit", function (e) {
      e.preventDefault();
      e.stopPropagation();
      return false;
    });

    // build the control DOM structure
    var controlScript = $("[data-em-editable-value-role=control]", this.wrapper).first();
    let $control = $("[data-editable-value-control]", controlHolder);
    $control.html(BootstrapUiModule.refTarget(controlScript).html());
    $control.data("editableValue", this);

    EntityModule.initializeFormElements($control);

    this.controlHolder = controlHolder;

    $("[data-action=cancel]", controlHolder).on("click", this._cancelControl.bind(this));
    $("[data-action=save]", controlHolder).on("click", this._updateValueWithSpinner.bind(this));

    $(document).on("mousedown", (event) => {
      var target = $(event.target);

      if (this.controlHolder && !target.closest(".editable-value-control").length) {
        this._cancelControl(event);
      }
    });
  }

  // hide the control - switch back to label
  _cancelControl(e: any) {
    e.preventDefault();
    e.stopPropagation();

    ax.log.debug("Closing editable value control for", this.propertyId);
    this.label.removeClass("d-none");
    this.controlHolder.remove();
    this.controlHolder = null;
  }

  _updateValueWithSpinner(e: any) {
    e.preventDefault();
    e.stopPropagation();

    ax.log.group("Posting editable value update for", this.propertyId);

    var properties = this._retrieveRefreshableValuesToUpdate();
    ax.log.debug("Requesting refresh for properties", properties);

    var requestedPropertiesData = properties.join("&");

    var wrapper = this.wrapper;
    var entityPrefix = this.entityPrefix;
    var propertyId = this.propertyId;
    var label = this.label;
    var propertyNameOfControl = this.propertyNameOfControl;
    var controlHolder = this.controlHolder;

    controlHolder.addClass("spinner");
    let input = controlHolder.find("input");
    let select = controlHolder.find("select");
    const serializedForm = $("form", wrapper).serialize();
    input.attr("disabled", true);
    select.attr("disabled", true);
    executeFetchRequest(this.settings.targetUrl, "post", {
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: serializedForm + "&" + requestedPropertiesData,
    })
      .then(translateResponse)
      .then((resp) => {
        const jsonResponse: JsonResponse = resp as JsonResponse;
        const jsonContent: any = jsonResponse.jsonContent;
        if (resp.ok) {
          controlHolder.removeClass("is-invalid");

          updatePropertyData(propertyId, jsonContent.properties[propertyNameOfControl]);

          label.removeClass("d-none");
          controlHolder.remove();

          //update absolute proerties
          $.each(jsonContent.absoluteProperties, function (propertyName, propertyData) {
            if (propertyName !== propertyNameOfControl) {
              updatePropertyData(propertyName, propertyData);
            }
          });

          // update other relative properties
          $.each(jsonContent.properties, function (propertyName: any, propertyData) {
            if (propertyName !== propertyNameOfControl) {
              updatePropertyData(entityPrefix + propertyName, propertyData);
            }
          });
        } else {
          controlHolder.removeClass("spinner");
          controlHolder.addClass("is-invalid");
          input.removeAttr("disabled");
          select.removeAttr("disabled");
          $(".invalid-feedback", controlHolder).remove();
          $(".form-control", controlHolder).addClass("is-invalid");

          var messages = $.map(jsonContent.errors[propertyNameOfControl], function (error, ix) {
            return '<span class="validation-message">' + error.message + "</span>";
          });

          controlHolder.append('<div class="invalid-feedback">' + messages.join() + "</div>");
        }
      })
      .catch((err) => {
        ax.log.error("Updating value failed for " + propertyId, err);
        controlHolder.addClass("is-invalid");
        $(".invalid-feedback", controlHolder).remove();
        $(".form-control", controlHolder).addClass("is-invalid");
      });

    ax.log.groupEnd();
  }

  _updateValueWithoutSpinner(e: any, successFunction: Function, failFunction: Function) {
    e.preventDefault();
    e.stopPropagation();

    ax.log.group("Posting editable value update for", this.propertyId);

    var properties = this._retrieveRefreshableValuesToUpdate();
    ax.log.debug("Requesting refresh for properties", properties);

    var requestedPropertiesData = properties.join("&");

    var wrapper = this.wrapper;
    var entityPrefix = this.entityPrefix;
    var propertyId = this.propertyId;
    var propertyNameOfControl = this.propertyNameOfControl;
    var controlHolder = this.controlHolder;

    executeFetchRequest(this.settings.targetUrl, "post", {
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: $("form", wrapper).serialize() + "&" + requestedPropertiesData,
    })
      .then(translateResponse)
      .then((resp) => {
        const jsonResponse: JsonResponse = resp as JsonResponse;
        const jsonContent: any = jsonResponse.jsonContent;
        if (resp.ok) {
          successFunction();
          controlHolder.removeClass("is-invalid");
          $(".invalid-feedback", controlHolder).remove();
          updatePropertyData(propertyId, jsonContent.properties[propertyNameOfControl]);

          // update other properties
          $.each(jsonContent.properties, function (propertyName: string, propertyData) {
            if (propertyName !== propertyNameOfControl) {
              if (propertyName.startsWith(propertyId.substring(0, propertyId.indexOf("/")))) {
                updatePropertyData(propertyName, propertyData);
              } else {
                updatePropertyData(entityPrefix + propertyName, propertyData);
              }
            }
          });
        } else {
          failFunction();
          controlHolder.addClass("is-invalid");
          $(".invalid-feedback", controlHolder).remove();
          $(".form-control", controlHolder).addClass("is-invalid");

          var messages = $.map(jsonContent.errors[propertyNameOfControl], function (error, ix) {
            return '<span class="validation-message">' + error.message + "</span>";
          });

          controlHolder.append('<div class="invalid-feedback">' + messages.join() + "</div>");
        }
      })
      .catch((err) => {
        failFunction();
        ax.log.error("Updating value failed for " + propertyId, err);
        controlHolder.addClass("is-invalid");
        $(".invalid-feedback", controlHolder).remove();
        $(".form-control", controlHolder).addClass("is-invalid");
      });

    ax.log.groupEnd();
  }

  _retrieveRefreshableValuesToUpdate() {
    var entityPrefix = this.entityPrefix;
    var properties: { [key: string]: any } = {};

    $("[data-em-property-id]").each(function () {
      var propertyId = $(this).data("em-property-id");
      if (propertyId.startsWith(entityPrefix)) {
        var viewElementMode = resolveViewElementMode($(this));
        // todo this will probably break in the case of embedded entitiy/colleciton
        properties[
          "extensions[editableValues].properties[" + propertyNameOf(propertyId) + "]=" + viewElementMode
        ] = true;
      }
    });

    return $.map(properties, function (value, key) {
      return key;
    });
  }

  resetChangeTracking() {
    this.label.removeData("em-property-value-changed");
  }

  isValuePossibleChanged() {
    return this.label.data("em-property-value-changed") != null;
  }

  supportsMultiValueSelection() {
    return this.settings.multiValueProperty;
  }
}

EntityModule.registerInitializer(function (node) {
  $("[data-em-editable-value]", node).each(function () {
    new EditableValue($(this)).activate();
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

  if (node && $(node).data("editable-value-control") === true) {
    // todo focusTextToEnd messes up the value in case of an embedded element / embedded collection
    // it selects all text controls, which will result in `.val()` returning the first value
    const $elements = $("input[type=text]:not([disabled]), textarea:not([disabled])", $(node));
    if ($elements.length === 1) {
      // @ts-ignore
      $elements.focusTextToEnd();
    }
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

class TextInputEditableValueHandler implements EditableValueUpdateHandler {
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

class CheckboxRadioSelectEditableValueHandler implements EditableValueUpdateHandler {
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
