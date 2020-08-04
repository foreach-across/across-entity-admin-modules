import { ActionHandler, ActionHandlerResolver } from "../handler-types";
import { Action } from "../action-types";

interface Context {
  action: Action;
}

export class MoveActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:move";

  handle(action: ActionHandler, context: Context): Promise<any> {
    if (action.source) {
      $(action.source).detach().appendTo(action.target);
      return Promise.resolve();
    }
    return Promise.reject(`Handler ${MoveActionHandlerResolver.TYPE}: missing source element`);
  }
}

export class RemoveActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:remove";

  handle(action: ActionHandler, context: Context): Promise<any> {
    $(action.target).remove();
    return Promise.resolve();
  }
}

export class ClearActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:clear";

  handle(action: ActionHandler, context: Context): Promise<any> {
    $(action.target).empty();
    return Promise.resolve();
  }
}

export class CloseModalHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:modal:close";

  handle(action: ActionHandler, context: Context): Promise<any> {
    $(action.target).modal("hide");
    return Promise.resolve();
  }
}

export class InitializeFormElementsHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:initialize-elements";

  handle(action: ActionHandler, context: any): Promise<any> {
    if (!!EntityModule) {
      EntityModule.initializeFormElements($(action.target));
      return Promise.resolve();
    }
    return Promise.reject(
      `Handler ${InitializeFormElementsHandlerResolver.TYPE}: EntityModule is not present to initialize elements`
    );
  }
}
