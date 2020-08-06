import { Action, ActionResolver } from "../action-types";
import { ActionHandlerFactory } from "../handler/action-handler-factory";
import { ActionError } from "../error-types";

export class ActionFactory {
  private actionResolvers: Map<string, ActionResolver>;
  private actionHandlerFactory: ActionHandlerFactory;

  constructor(actionHandlerFactory: ActionHandlerFactory) {
    this.actionHandlerFactory = actionHandlerFactory;
    this.actionResolvers = new Map();
  }

  register(type: string, handler: ActionResolver): void {
    this.actionResolvers.set(type, handler);
  }

  handle(action: Action, context: any): Promise<void> {
    const handler = this.actionResolvers.get(action.action);
    if (!handler) {
      return Promise.reject(new ActionError(`Missing action resolver for type ${action.action}`, action, context));
    }
    return Promise.resolve(handler.handle(action, context, this.actionHandlerFactory));
  }
}
