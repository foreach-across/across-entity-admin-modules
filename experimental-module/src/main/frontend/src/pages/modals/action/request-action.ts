import { Action, ActionResolver } from "../action-types";
import { ActionHandlerFactory } from "../handler/action-handler-factory";
import { executeRequest, translateResponse } from "../../experimental/utils/request-utils";
import { ActionHandler } from "../handler-types";

interface RequestAction extends Action {
  method: string; // request method
  url?: string; // url to call
  form?: string; // form selector
  partial?: string;
  redirect: ActionHandler[];
  success: ActionHandler[];
  // error: ActionHandler[]; // action handlers
}

export class RequestActionResolver implements ActionResolver {
  static readonly TYPE: string = "exm:request";

  handle(action: RequestAction, context: any, actionHandlerFactory: ActionHandlerFactory): Promise<void> {
    return executeRequest(action)
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
