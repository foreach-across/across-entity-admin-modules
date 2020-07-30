import { Action, ActionResolver } from "../action-types";
import { ActionHandlerFactory } from "../handler/action-handler-factory";
import { executeFetchRequest, executeFormRequest, translateResponse } from "../../experimental/utils/request-utils";

export class RequestActionResolver implements ActionResolver {
  static readonly TYPE: string = "exm:request";

  handle(action: Action, context: any, actionHandlerFactory: ActionHandlerFactory): Promise<void> {
    const form = action.form ? $(action.form) : null;
    const requestUrl = action.url ? action.url : window.location.href.split("?")[0];

    let requestToExecute: Promise<Response>;
    if (form) {
      requestToExecute = executeFormRequest(requestUrl, action.method, form);
    } else {
      requestToExecute = executeFetchRequest(requestUrl, action.method);
    }
    return requestToExecute
      .then((response) => translateResponse(response))
      .then((response: Response) => {
        if (response.redirected) {
          action.redirect.forEach((handler) => {
            actionHandlerFactory.handle(handler, { action, response });
          });
        } else if (response.ok) {
          action.success.forEach((handler) => {
            actionHandlerFactory.handle(handler, { action, response });
          });
        } else {
          console.error("response for action was neither redirected nor ok", action, response);
        }
      });
  }
}

export const requestActionResolver = new RequestActionResolver();
