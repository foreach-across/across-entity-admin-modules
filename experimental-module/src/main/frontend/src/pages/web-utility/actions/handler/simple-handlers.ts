import { ActionHandler, ActionHandlerResolver } from "../handler-types";
import { Action } from "../action-types";
import { ActionHandlerError } from "../error-types";

interface Context {
  action: Action;
}

export class MoveActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:move";

  handle(action: ActionHandler, context: Context): Promise<any> {
    if (action.source && action.target) {
      $(action.source).detach().appendTo(action.target);
      return Promise.resolve();
    }

    return Promise.reject(
      new ActionHandlerError(
        `Handler ${MoveActionHandlerResolver.TYPE}: both source and target are required`,
        action,
        context
      )
    );
  }
}

export class RemoveActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:remove";

  handle(action: ActionHandler, context: Context): Promise<any> {
    if (action.target) {
      $(action.target).remove();
      return Promise.resolve();
    }
    return Promise.reject(
      new ActionHandlerError(`Handler ${RemoveActionHandlerResolver.TYPE}: target is required`, action, context)
    );
  }
}

export class ClearActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:clear";

  handle(action: ActionHandler, context: Context): Promise<any> {
    if (action.target) {
      $(action.target).empty();
      return Promise.resolve();
    }
    return Promise.reject(
      new ActionHandlerError(`Handler ${ClearActionHandlerResolver.TYPE}: target is required`, action, context)
    );
  }
}

export class CloseModalHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:modal:close";

  handle(action: ActionHandler, context: Context): Promise<any> {
    if (action.target) {
      $(action.target).modal("hide");
      return Promise.resolve();
    }
    return Promise.reject(
      new ActionHandlerError(`Handler ${CloseModalHandlerResolver.TYPE}: target is required`, action, context)
    );
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
      new ActionHandlerError(
        `Handler ${InitializeFormElementsHandlerResolver.TYPE}: EntityModule is required to initialize elements`,
        action,
        context
      )
    );
  }
}
