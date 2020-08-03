import { ActionHandlerFactory } from "./handler/action-handler-factory";

export interface Action {
  action: string;
  event: string;
}

export interface ActionResolver {
  handle(action: Action, context: any, actionHandlerFactory: ActionHandlerFactory): void;
}
