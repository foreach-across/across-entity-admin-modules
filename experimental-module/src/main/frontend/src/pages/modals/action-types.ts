import { ActionHandler } from "./handler-types";
import { ActionHandlerFactory } from "./handler/action-handler-factory";

export interface Action {
  action: string; // action type
  method: string; // request method
  url?: string; // url to call
  form?: string; // form selector
  redirect: ActionHandler[];
  success: ActionHandler[];
  // error: ActionHandler[]; // action handlers
}

export interface ActionResolver {
  handle(action: Action, context: any, actionHandlerFactory: ActionHandlerFactory): void;
}
