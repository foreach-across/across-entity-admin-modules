import { ActionHandler, ActionHandlerResolver } from "../handler-types";
import { Action } from "../action-types";

interface Context {
  action: Action;
}

export class MoveActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:move";

  handle(action: ActionHandler, context: Context): void {
    if (action.source) {
      $(action.source).detach().appendTo(action.target);
    }
  }
}

export class RemoveActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:remove";

  handle(action: ActionHandler, context: Context): void {
    $(action.target).remove();
  }
}

export class ClearActionHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:clear";

  handle(action: ActionHandler, context: Context): void {
    $(action.target).empty();
  }
}

export class CloseModalHandlerResolver implements ActionHandlerResolver {
  static readonly TYPE: string = "exm:modal:close";

  handle(action: ActionHandler, context: Context): void {
    $(action.target).modal("hide");
  }
}
