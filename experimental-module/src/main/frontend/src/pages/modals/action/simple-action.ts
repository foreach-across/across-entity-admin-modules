import { Action, ActionResolver } from "../action-types";
import { ActionHandler } from "../handler-types";
import { ActionHandlerFactory } from "../handler/action-handler-factory";

interface SimpleAction extends Action {
  handlers: ActionHandler[];
}

export class SimpleActionResolver implements ActionResolver {
  static readonly TYPE: string = "exm:simple";

  handle(action: SimpleAction, context: any, actionHandlerFactory: ActionHandlerFactory): Promise<void> {
    return Promise.all(
      action.handlers.map((handler) => {
        return actionHandlerFactory.handle(handler, { action });
      })
    ).then(() => {});
  }
}

export const simpleActionResolver = new SimpleActionResolver();
