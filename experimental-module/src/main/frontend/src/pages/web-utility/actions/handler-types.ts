export interface ActionHandler {
  type: string;
  source?: string;
  sourceElement?: string;
  target: string;
}

export interface RequestActionHandler extends ActionHandler {
  method: string;
  url?: string;
  form?: string;
  partial?: string;
  responseUrl?: string;
  additionalQueryParameters?: { [key: string]: string };
}

export interface ActionHandlerResolver {
  handle(action: ActionHandler, context: any): Promise<any>;
}
