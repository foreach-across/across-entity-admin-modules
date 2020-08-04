import { Action } from "../action-types";
import { ActionHandler, ActionHandlerResolver, RequestActionHandler } from "../handler-types";
import { JsonResponse, TextResponse } from "../../experimental/utils/response-types";
import { executeRequest, translateResponse } from "../../experimental/utils/request-utils";

interface Context {
  action: Action;
  response: JsonResponse | TextResponse;
}

export class RequestContentActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:request-content";

  handle(action: ActionHandler, context: Context): Promise<any> {
    if ("jsonContent" in context.response) {
      console.log(`Received json response in ${RequestContentActionHandlerResolver.TYPE} handler`, context.response);
      return Promise.resolve();
    } else {
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
        $(action.target).replaceWith(asTextResponse.textContent);
      });
  }
}
