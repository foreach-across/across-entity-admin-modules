import { Action } from "../action-types";
import { ActionHandler, ActionHandlerResolver, RequestActionHandler } from "../handler-types";
import { JsonResponse, TextResponse } from "../../experimental/utils/response-types";
import { executeRequest, translateResponse } from "../../experimental/utils/request-utils";
import { ActionHandlerError } from "../error-types";

interface Context {
  action: Action;
  response: JsonResponse | TextResponse;
}

export class ResponseContentActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:response-content";

  handle(action: ActionHandler, context: Context): Promise<any> {
    if ("jsonContent" in context.response) {
      return Promise.reject(
        new ActionHandlerError(
          `Handler ${ResponseContentActionHandlerResolver.TYPE} currently does not support json responses`,
          action,
          context
        )
      );
    } else {
      if (!action.target) {
        return Promise.reject(
          new ActionHandlerError(
            `Handler ${ResponseContentActionHandlerResolver.TYPE} requires a target`,
            action,
            context
          )
        );
      }

      const response: TextResponse = context.response;

      let $content = $(response.textContent);
      if (action.source) {
        $content = $content.find(action.source);
      }

      const $wrapper = $("<div></div>");
      $wrapper.append($content);

      let contentToSet: string = $wrapper.html();
      if ($(action.target).parent("form").length > 0) {
        contentToSet = "</form>" + contentToSet;
      }
      $(action.target).html(contentToSet);
      return Promise.resolve();
    }
  }
}

export class RequestActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:request";

  handle(action: RequestActionHandler, context: Context): Promise<any> {
    return executeRequest(action)
      .then(translateResponse)
      .then((response) => {
        const asTextResponse: TextResponse = response as TextResponse;
        if (!action.target) {
          throw new ActionHandlerError(
            `Handler ${RequestActionHandlerResolver.TYPE} requires a target`,
            action,
            context
          );
        }
        $(action.target).replaceWith(asTextResponse.textContent);
      });
  }
}
