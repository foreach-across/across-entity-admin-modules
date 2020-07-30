import { ActionHandler, ActionHandlerResolver } from "../handler-types";

export class ActionHandlerFactory {
  private actionHandlerResolvers: Map<string, ActionHandlerResolver>;

  constructor() {
    this.actionHandlerResolvers = new Map();
  }

  register(type: string, handler: ActionHandlerResolver): void {
    this.actionHandlerResolvers.set(type, handler);
  }

  handle(action: ActionHandler, context: any): void {
    const handler = this.actionHandlerResolvers.get(action.type);
    if (!handler) {
      console.error("Could not find a matching handler for action", action);
      return;
    }
    handler.handle(action, context);
  }
}

export const handlerFactory = new ActionHandlerFactory();
