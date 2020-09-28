import { ActionHandler } from "./handler-types";
import { Action } from "./action-types";

class BaseError {
  message: string;
  context: any;

  constructor(message: string, context: any) {
    this.message = message;
    this.context = context;
  }
}

export class ActionError extends BaseError {
  constructor(message: string, action: Action, context: any) {
    super(message, { ...context, action });
  }
}

export class ActionHandlerError extends BaseError {
  constructor(message: string, actionHandler: ActionHandler, context: any) {
    super(message, { ...context, actionHandler });
  }
}
