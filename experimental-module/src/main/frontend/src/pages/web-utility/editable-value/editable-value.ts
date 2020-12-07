/* tslint:disable */
import { ax } from "../utils/utils";
import "./editable-value.scss";
import { executeFetchRequest, getFormData, translateResponse } from "../utils/request-utils";
import { JsonResponse, TextResponse } from "../utils/response-types";

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
  if (viewElementMode == null || viewElementMode == "undefined") {
    return "VALUE";
  }
  return viewElementMode;
}

function refreshPropertyValue($node: any, propertyData: any): boolean {
  const label = propertyData.labels[resolveViewElementMode($(this))];
  if (label) {
    $node.data("em-property-value-changed", true);
    $node.html(label);
    if ($node.is("[data-em-editable-value-role='value']") && $(".cta-item", $node).length === 0) {
      $node.append(EDITABLE_VALUE_CTA);
    }
    EntityModule.initializeFormElements($node);
    return true;
  }
  return false;
}

interface PropertyData {
  properties: [{ propertyId: string; data: any }];
  absoluteProperties: [{ propertyId: string; data: any }];
}

export function updatePropertyData(origin: EditableValue, propertyData: PropertyData) {
  //data-em-reference-property-id
  const updatedControls: any[] = [];
  $.each(propertyData.properties, function (propertyName: any, propertyData) {
    // @ts-ignore
    const asEntityProperty = origin.entityPrefix + propertyName;
    $(`[data-em-property-id="${asEntityProperty}"],[data-em-reference-property-id="${asEntityProperty}"]`).each(
      function () {
        let $this = $(this);
        if (!updatedControls.includes($this)) {
          if (refreshPropertyValue($this, propertyData)) {
            updatedControls.push($this);
          }
        }
      }
    );
  });

  $.each(propertyData.absoluteProperties, function (propertyName, propertyData) {
    $(`[data-em-property-id="${propertyName}"],[data-em-reference-property-id="${propertyName}"]`).each(function () {
      let $this = $(this);
      if (!updatedControls.includes($this)) {
        if (refreshPropertyValue($this, propertyData)) {
          updatedControls.push($this);
        }
      }
    });
  });
}

const EDITABLE_VALUE_CTA = "<span class='cta-item cta-edit'></span>";

const EDITABLE_CONTROL_WRAPPER =
  '<form class="editable-value-form"><div class="editable-value-control"><span data-editable-value-control="true"></span>' +
  '<span class="editable-value-actions">' +
  '<a class="btn btn-sm" data-action="save"><i class="fas fa-check fa-fw"/></a>' +
  '<a class="btn btn-sm" data-action="cancel"><i class="fas fa-times fa-fw"/></a>' +
  '<span class="btn btn-sm d-none" data-action="loading"><i class="fas fa-circle-notch fa-spin fa-fw"/></span>' +
  "</span></div></form>";

const EDITABLE_CONTROL_WRAPPER_WITHOUT_ACTIONS =
  '<form class="editable-value-form"><div class="editable-value-control"><span data-editable-value-control="true"></span>' +
  "</div></form>";

export class EditableValue {
  private wrapper: any;
  private settings: any;
  private label: any;
  private controlContainer: any;
  private propertyId: any;
  private propertyReferenceId: string[];
  private entityPrefix: string;
  private propertyNameOfControl: string;
  private refreshBusy: boolean = false;
  private controlSwitchRequested = false;
  private controlHolder: any;

  constructor(wrapper: any) {
    this.wrapper = wrapper;
    this.settings = wrapper.data("em-editable-value");
    this.label = $("[data-em-editable-value-role=value]", wrapper).first();
    this.controlContainer = $("[data-em-editable-value-role=control-container]", wrapper).first();
    this.propertyId = this.settings.propertyId;
    this.entityPrefix = entityPrefixOf(this.settings.propertyId);
    this.propertyNameOfControl = propertyNameOf(this.settings.propertyId);
    this.propertyReferenceId = this.settings.propertyReferenceId;
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
    const label = this.label;

    // Configure refresh from other value updates
    label.addClass("editable-value-value").attr("data-em-property-id", this.propertyId);
    if (this.propertyReferenceId && this.propertyReferenceId.length > 1) {
      label.attr("data-em-reference-property-id", this.propertyReferenceId);
    }

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
      if (event.target && event.target.tagName && event.target.tagName.toLowerCase() === "a") {
        return;
      }
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
      .append(this.includesActions() ? EDITABLE_CONTROL_WRAPPER : EDITABLE_CONTROL_WRAPPER_WITHOUT_ACTIONS);

    // ensure form cannot be submitted
    const controlHolder = $("form", this.wrapper);
    controlHolder.on("submit", function (e) {
      e.preventDefault();
      e.stopPropagation();
      return false;
    });

    // build the control DOM structure
    const controlScript = $("[data-em-editable-value-role=control]", this.wrapper).first();
    const $control = $("[data-editable-value-control]", controlHolder);
    $control.html(BootstrapUiModule.refTarget(controlScript).html());
    $control.data("editableValue", this);

    const shouldBeMultipart = $("form", this.wrapper).find(":file").length !== 0;
    if (shouldBeMultipart) {
      $("form", this.wrapper).attr("enctype", "multipart/form-data");
    }

    EntityModule.initializeFormElements($control);
    this.controlHolder = controlHolder;

    this.sendEvent("switch-to-control");

    $("[data-action=cancel]", controlHolder).on("click", this._cancelControl.bind(this));
    $("[data-action=save]", controlHolder).on("click", this._updateValueWithSpinner.bind(this));

    $(document).on("mousedown", (event) => {
      const target = $(event.target);

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
    this.sendEvent("switch-to-value");
    // @ts-ignore
    const $focusedElement = $(document.activeElement);
    // @ts-ignore
    const focusedElementWithinControlHolder = $focusedElement.parent(this.controlHolder).length !== 0;
    if ($focusedElement && focusedElementWithinControlHolder) {
      // @ts-ignore
      $focusedElement.trigger("blur");
    }
    this.label.removeClass("d-none");
    this.controlHolder.remove();
    this.controlHolder = null;
  }

  _updateValueWithSpinner(e: any) {
    e.preventDefault();
    e.stopPropagation();

    ax.log.group("Posting editable value update for", this.propertyId);

    const properties = this._retrieveRefreshableValuesToUpdate();
    ax.log.debug("Requesting refresh for properties", properties);

    const wrapper = this.wrapper;
    const entityPrefix = this.entityPrefix;
    const propertyId = this.propertyId;
    const label = this.label;
    const propertyNameOfControl = this.propertyNameOfControl;
    const controlHolder = this.controlHolder;

    controlHolder.addClass("loading");
    const formControlElements = controlHolder.find(":input:not([disabled])");
    const actionsToToggleVisibility = controlHolder.find(".editable-value-actions > [data-action]");

    const requestConfiguration = this.getRequestConfiguration($("form", wrapper), properties);

    actionsToToggleVisibility.toggleClass("d-none");
    formControlElements.attr("disabled", true);

    executeFetchRequest(this.settings.targetUrl, "post", requestConfiguration)
      .then(translateResponse)
      .then((resp) => {
        const jsonResponse: JsonResponse = resp as JsonResponse;
        const jsonContent: any = jsonResponse.jsonContent;
        if (jsonContent.success) {
          controlHolder.removeClass("is-invalid");

          updatePropertyData(this, jsonContent);
          label.removeClass("d-none");
          controlHolder.remove();
        } else {
          controlHolder.removeClass("loading");
          controlHolder.addClass("is-invalid");
          formControlElements.removeAttr("disabled");
          actionsToToggleVisibility.toggleClass("d-none");

          $(".invalid-feedback", controlHolder).remove();
          $(".form-control", controlHolder).addClass("is-invalid");

          const messages = $.map(jsonContent.errors[propertyNameOfControl], function (error, ix) {
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
        controlHolder.removeClass("loading");
        actionsToToggleVisibility.toggleClass("d-none");
      });

    ax.log.groupEnd();
  }

  _updateValueWithoutSpinner(e: any, successFunction: Function, failFunction: Function) {
    e.preventDefault();
    e.stopPropagation();

    ax.log.group("Posting editable value update for", this.propertyId);

    const properties = this._retrieveRefreshableValuesToUpdate();
    ax.log.debug("Requesting refresh for properties", properties);

    const wrapper = this.wrapper;
    const entityPrefix = this.entityPrefix;
    const propertyId = this.propertyId;
    const propertyNameOfControl = this.propertyNameOfControl;
    const controlHolder = this.controlHolder;

    const requestConfiguration = this.getRequestConfiguration($("form", wrapper), properties);

    executeFetchRequest(this.settings.targetUrl, "post", requestConfiguration)
      .then(translateResponse)
      .then((resp) => {
        const jsonResponse: JsonResponse = resp as JsonResponse;
        const jsonContent: any = jsonResponse.jsonContent;
        if (jsonContent.success) {
          successFunction();

          updatePropertyData(this, jsonContent);

          controlHolder.removeClass("is-invalid");
          $(".invalid-feedback", controlHolder).remove();
        } else {
          failFunction();
          controlHolder.addClass("is-invalid");
          $(".invalid-feedback", controlHolder).remove();
          $(".form-control", controlHolder).addClass("is-invalid");

          const messages = $.map(jsonContent.errors[propertyNameOfControl], function (error, ix) {
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

  getRequestConfiguration($form: any, propertiesToUpdate: any[]): any {
    const formConfiguration: any = $.extend(
      true,
      {},
      {
        headers: {},
      }
    );

    const formData: FormData = getFormData($form[0], false);
    propertiesToUpdate.forEach((prop: string) => {
      const idx = prop.lastIndexOf("=");
      formData.set(prop.substring(0, idx), prop.substring(idx + 1));
    });

    if ($form.attr("enctype") === "multipart/form-data") {
      // formConfiguration.headers["Content-Type"] = undefined;
      formConfiguration.body = formData;
      formConfiguration.processData = false;
      formConfiguration.contentType = false;
      formConfiguration.cache = "no-store";
    } else {
      formConfiguration.headers["Content-Type"] = "application/x-www-form-urlencoded";
      formConfiguration.body = new URLSearchParams(formData as any);
    }
    return formConfiguration;
  }

  _retrieveRefreshableValuesToUpdate() {
    const entityPrefix = this.entityPrefix;
    const properties: { [key: string]: any } = {};

    $("[data-em-property-id]").each(function () {
      const propertyId = $(this).data("em-property-id");
      if (propertyId.startsWith(entityPrefix)) {
        const viewElementMode = resolveViewElementMode($(this));
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

  includesActions() {
    return this.settings.includeActions;
  }

  sendEvent(eventName: string): void {
    this.wrapper.trigger(`experimental.editable-value.${eventName}`, {
      editableValueController: this,
    });
  }
}
