/* tslint:disable */
import { ax } from "../utils/utils";
import "./editable-value.scss";
import { executeFetchRequest, translateResponse } from "../utils/request-utils";
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

class EditableValue {
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
      var editableControl = this;

      ax.log.debug("Refreshing editable value control for", this.propertyId);

      executeFetchRequest(editableControl.settings.targetUrl, "get", {
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "&_partial=::editableValue-" + editableControl.propertyNameOfControl + "-control",
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
    $("[data-action=save]", controlHolder).on("click", this._updateValue.bind(this));

    // $( '[data-editable-value-control=true]', controlHolder ).on( 'focusout', ( e ) => {
    // this._cancelControl( e );
    // } );

    /**
     * Hide the blade when the user clicks outside of it.
     * Uses mousedown as blade might be opened with 'up-instant', and else the
     * 'click' would follow the mouse down and close instantly.
     */
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

  // submit the control value
  _updateValue(e: any) {
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
        console.log(err);
        // ax.log.error( "Updating value failed for " + propertyId, xhr.responseText as any );
        controlHolder.addClass("is-invalid");
        $(".invalid-feedback", controlHolder).remove();
        $(".form-control", controlHolder).addClass("is-invalid");
      });

    ax.log.groupEnd();
  }

  // submit the control value
  _submitValue(e: any, successFunction: Function, failFunction: Function) {
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
        console.log(err);
        // ax.log.error( "Updating value failed for " + propertyId, xhr.responseText as any );
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
}

EntityModule.registerInitializer(function (node) {
  $("[data-em-editable-value]", node).each(function () {
    new EditableValue($(this)).activate();
  });
});

EntityModule.registerInitializer(function (node) {
  if (node && $(node).data("editable-value-control") === true) {
    // todo focusTextToEnd messes up the value in case of an embedded element / embedded collection
    // it selects all text controls, which will result in `.val()` returnnig the first value
    $("input[type=text]", $(node)).each(function (e) {
      // @ts-ignore
      $(this).focusTextToEnd();
    });
    $(".bootstrap-tagsinput span[data-role=remove]", $(node))
      .off()
      .on("click", function (e) {
        let removeOnSuccess = false;

        if ($(this).parent().parent().children("span").length === 1) {
          $(this).parent().find("input").val("");
          removeOnSuccess = true;
        } else {
          $(this).parent().remove();
        }

        let editableValue = $(node).data("editableValue");
        editableValue._submitValue(
          e,
          () => {
            if (removeOnSuccess) {
              $(this).parent().remove();
            }
          },
          () => {}
        );
      });

    $("input[type=text].js-multi-value-input", $(node))
      .off()
      .on("keypress", (e) => {
        if (e.key === "Enter") {
          const target: HTMLInputElement = e.target as HTMLInputElement;
          if ($(target).attr("new-value") === "true" && !!target.value) {
            const id = $(target).attr("id");
            const newTag = `<span data-editable-value-control="true" hidden="true" class="tag label label-info"><input name=${id} type="hidden" value=${target.value}> ${target.value} <span data-role="remove"></span></span>`;
            $(newTag).insertBefore($(target));
            let editableValue = $(node).data("editableValue");
            // //LOL, remove clears the JQuery context before it removes the node from the DOM, #eendagnietgeklaagdiseendagnietgeleefd
            editableValue._submitValue(
              e,
              () => {
                let eventTarget: HTMLInputElement = e.target as HTMLInputElement;
                $(`input[value="${eventTarget.value}"]`).parent().removeAttr("hidden");
                $(eventTarget).val("");
                EntityModule.initializeFormElements($(node));
              },
              () => {
                $(`input[value="${target.value}"]`).parent().remove();
              }
            );
          } else {
            let editableValue = $(node).data("editableValue");
            // //LOL, remove clears the JQuery context before it removes the node from the DOM, #eendagnietgeklaagdiseendagnietgeleefd
            editableValue._submitValue(e);
          }
        }
      });

    // Update editable value on enter
    $("input[type=text]:not(.js-multi-value-input)", $(node)).on("keypress", (e) => {
      if (e.key === "Enter") {
        let editableValue = $(node).data("editableValue");
        //LOL, remove clears the JQuery context before it removes the node from the DOM, #eendagnietgeklaagdiseendagnietgeleefd
        $("input[type=text]", $(node)).blur();
        editableValue._updateValue(e);
      }
    });

    // Update editable value on change
    $("input[type=checkbox], input[type=radio], select", $(node)).change(function (e) {
      let editableValue = $(node).data("editableValue");
      editableValue._updateValue(e);
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
