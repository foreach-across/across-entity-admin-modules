export interface ActionHandler {
  type: string;
  source?: string;
  target: string;
}

export interface RequestActionHandler extends ActionHandler {
  method: string;
  url?: string;
  form?: string;
  partial?: string;
}

export interface ActionHandlerResolver {
  handle(action: ActionHandler, context: any): void;
}
