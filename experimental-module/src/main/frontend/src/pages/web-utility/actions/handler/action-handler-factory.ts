import { ActionHandler, ActionHandlerResolver } from "../handler-types";
import { ActionHandlerError } from "../error-types";

export class ActionHandlerFactory {
  private actionHandlerResolvers: Map<string, ActionHandlerResolver>;

  constructor() {
    this.actionHandlerResolvers = new Map();
  }

  register(type: string, handler: ActionHandlerResolver): void {
    this.actionHandlerResolvers.set(type, handler);
  }

  handle(action: ActionHandler, context: any): Promise<any> {
    const handler = this.actionHandlerResolvers.get(action.type);
    if (!handler) {
      return Promise.reject(
        new ActionHandlerError(`Missing action handler resolver for type ${action.type}`, action, context)
      );
    }
    return handler.handle(action, context);
  }
}

export const handlerFactory = new ActionHandlerFactory();
