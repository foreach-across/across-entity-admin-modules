import { Action, ActionResolver } from "../action-types";
import { ActionHandlerFactory } from "../handler/action-handler-factory";
import { executeRequest, translateResponse } from "../../utils/request-utils";
import { ActionHandler } from "../handler-types";

interface RequestAction extends Action {
  method: string; // request method
  url?: string; // url to call
  form?: string; // form selector
  partial?: string;
  copyOriginalRequestParameters?: boolean;
  redirect: ActionHandler[];
  success: ActionHandler[];
  failure: ActionHandler[]; // action handlers
  error: ActionHandler[]; // action handlers
}

export class RequestActionResolver implements ActionResolver {
  static readonly TYPE: string = "exm:request";

  handle(action: RequestAction, context: any, actionHandlerFactory: ActionHandlerFactory): Promise<void> {
    context.action = action;
    return executeRequest(action)
      .then((response) => translateResponse(response))
      .then((response: Response) => {
        let sequence = Promise.resolve();
        context.response = response;

        if (response.redirected) {
          action.redirect.forEach((handler) => {
            sequence = sequence.then(() => actionHandlerFactory.handle(handler, context));
          });
        } else if (response.ok) {
          action.success.forEach((handler) => {
            sequence = sequence.then(() => actionHandlerFactory.handle(handler, context));
          });
        } else {
          action.failure.forEach((handler) => {
            sequence = sequence.then(() => actionHandlerFactory.handle(handler, context));
          });
        }
        return sequence;
      })
      .catch((error) => {
        let sequence = Promise.resolve();
        context.error = error;

        if (action.error.length > 0) {
          action.error.forEach((handler) => {
            sequence = sequence.then(() => actionHandlerFactory.handle(handler, context));
          });
          return sequence;
        }

        throw error;
      });
  }
}

export const requestActionResolver = new RequestActionResolver();
