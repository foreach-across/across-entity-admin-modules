import { Action } from "../action-types";
import { ActionHandler, ActionHandlerResolver, RequestActionHandler } from "../handler-types";
import { JsonResponse, TextResponse } from "../../utils/response-types";
import { executeRequest, translateResponse } from "../../utils/request-utils";
import { ActionHandlerError } from "../error-types";

interface Context {
  action: Action;
  response: JsonResponse | TextResponse;
  resolvedQueryParameters: { [key: string]: string };
}

interface ResponseContentActionHandler extends ActionHandler {
  replace: boolean;
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

      let contentToSet: string = "";
      /*
            todo:
                support providing your own html within this action handler?
                doesn't make much sense when considering response content, would make more sense if it's custom action handler (that perhaps checks the response attributes)
                if it's really fixed html content, why not configure the modal with the fixed content instead of replacing it?
            */
      if (action.sourceElement) {
        contentToSet = action.sourceElement;
      } else {
        const $responseElement = $("<div></div>");
        $responseElement.append(context.response.textContent);

        if (action.source) {
          let tempContent = $responseElement.find(action.source);
          $responseElement.empty();
          $responseElement.append(tempContent);
        }

        contentToSet = $responseElement.html();
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

export class ResponseUrlIdResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:response-url-id-resolver";

  handle(action: ActionHandler, context: any): Promise<any> {
    if (!context.response || !context.response.url) {
      return Promise.reject(
        new ActionHandlerError(
          `Handler ${ResponseUrlIdResolver.TYPE} requires a response object with an url in the available context.`,
          action,
          context
        )
      );
    }
    if (!action.target) {
      return Promise.reject(
        new ActionHandlerError(
          `Handler ${ResponseUrlIdResolver.TYPE} requires a target that identifies the parameter name`,
          action,
          context
        )
      );
    }
    const resolvedQueryParameters = context.resolvedQueryParameters || {};
    resolvedQueryParameters[action.target] = this.resolveIdFromUrl(context.response.url);

    context.resolvedQueryParameters = resolvedQueryParameters;
    console.log("ResponseUrlIdResolver", context);
    return Promise.resolve();
  }

  /**
   * Resolves the id for an instance based on a given url. The id part of the request is expected to be after an {@code /entities/{type}/} part.
   * An id should not contain non-encoded slashes.
   * @param url to retrieve the id for an instance from.
   */
  resolveIdFromUrl(url: string): string {
    const currentPath = new URL(url).pathname;
    let searchString = "entities/";
    let result = currentPath.substring(currentPath.indexOf(searchString) + searchString.length);
    result = result.substring(result.indexOf("/") + 1);
    let remainingSlash = result.indexOf("/");
    if (remainingSlash > 0) {
      result = result.substring(0, remainingSlash);
    }
    return result;
  }
}

export class RequestActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:request";

  handle(action: RequestActionHandler, context: Context): Promise<any> {
    const additionalQueryParameters = {
      ...action.additionalQueryParameters,
      ...context.resolvedQueryParameters,
    };
    console.log("RequestActionHandlerResolver", context, additionalQueryParameters);
    return executeRequest({ ...action, ...{ additionalQueryParameters } })
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
