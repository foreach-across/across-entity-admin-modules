import { Action } from "../action-types";
import { ActionHandler, ActionHandlerResolver, RequestActionHandler } from "../handler-types";
import { JsonResponse, TextResponse } from "../../utils/response-types";
import { executeRequest, translateResponse } from "../../utils/request-utils";
import { ActionHandlerError } from "../error-types";

interface Context {
  action: Action;
  response: JsonResponse | TextResponse;
}

interface ResponseContentActionHandler extends ActionHandler {
  replace: boolean;
}

function originalFlow(action: any, response: any) {
  console.log("==== original flow");
  let $content = $(response.textContent);
  console.log("response is", $(response.textContent), "content to set is", $content);
  if (action.source) {
    console.log("action.source is present", action.source, $content, "new content", $content.find(action.source));
    console.log("action.source is present", action.source, $content);
    $content = $content.find(action.source);
  }

  const $wrapper = $("<div></div>");
  $wrapper.append($content);
  console.log("content to set result:", $wrapper, $wrapper.html());

  let contentToSet: string = $wrapper.html();
  if ($(action.target).parent("form").length > 0) {
    contentToSet = "</form>" + contentToSet;
  }
}

export class ResponseContentActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:response-content";

  handle(action: ResponseContentActionHandler, context: Context): Promise<any> {
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

      originalFlow(action, context.response);

      console.log("==== new flow");
      const $wrapper = $("<div></div>");
      $wrapper.append(context.response.textContent);
      let contentToSet = $wrapper.html();
      console.log("response is", $(context.response.textContent), "content to set is", $(contentToSet));
      if (action.source) {
        console.log(
          "action.source is present",
          action.source,
          $(contentToSet),
          "new content",
          $wrapper.find(action.source)
        );
        contentToSet = $wrapper.find(action.source).html();
      }

      console.log("content to set result:", $(contentToSet), contentToSet);

      if (action.sourceElement) {
        contentToSet = action.sourceElement;
      }

      if ($(action.target).closest("form").length > 0) {
        contentToSet = "</form>" + contentToSet;
      }

      if (action.replace) {
        $(action.target).replaceWith(contentToSet);
      } else {
        $(action.target).html(contentToSet);
      }

      return Promise.resolve();
    }
  }
}

export class RequestActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:request";

  handle(action: RequestActionHandler, context: Context): Promise<any> {
    if (context.response) {
      action.responseUrl = context.response.url;
    }

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
