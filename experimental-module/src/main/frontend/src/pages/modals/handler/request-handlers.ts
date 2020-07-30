import { Action } from "../action-types";
import { ActionHandler, ActionHandlerResolver } from "../handler-types";
import { JsonResponse, TextResponse } from "../../experimental/utils/response-types";
import { executePartialRequest, translateResponse } from "../../experimental/utils/request-utils";

interface Context {
  action: Action;
  response: JsonResponse | TextResponse;
}

export class RequestContentActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:request-content";

  handle(action: ActionHandler, context: Context): void {
    if ("jsonContent" in context.response) {
      console.log(`Received json response in ${RequestContentActionHandlerResolver.TYPE} handler`, context.response);
    } else {
      const response: TextResponse = context.response;
      let $content = $(response.textContent);
      if (action.source) {
        $content = $content.find(action.source);
      }
      const $wrapper = $("<div></div>");
      $wrapper.append($content);
      let contentToSet: string = $wrapper.html();
      if ($content.find("form").length > 0) {
        contentToSet = "</form>" + contentToSet;
      }
      $(action.target).html(contentToSet);
    }
  }
}

export class MoveActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:move";

  handle(action: ActionHandler, context: Context): void {
    if (action.source) {
      $(action.source).detach().appendTo(action.target);
    }
  }
}

export class RemoveActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:remove";

  handle(action: ActionHandler, context: Context): void {
    $(action.target).remove();
  }
}

export class ClearActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:clear";

  handle(action: ActionHandler, context: Context): void {
    $(action.target).empty();
  }
}

export class CloseModalHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:close";

  handle(action: ActionHandler, context: Context): void {
    $(action.target).modal("hide");
  }
}

export class PartialHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:partial";

  handle(action: ActionHandler, context: Context): void {
    executePartialRequest(action)
      .then(translateResponse)
      .then((response) => {
        const asTextResponse: TextResponse = response as TextResponse;
        $(action.target).replaceWith(asTextResponse.textContent);
        EntityModule.initializeFormElements($(action.target));
      });
  }
}
