export interface ActionHandler {
  type: string;
  source?: string;
  target: string;
}

export interface ActionHandlerResolver {
  handle(action: ActionHandler, context: any): void;
}
