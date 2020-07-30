import { handlerFactory } from "../modals/handler/action-handler-factory";
import { ActionFactory } from "../modals/action/action-factory";
import { RequestActionResolver, requestActionResolver } from "../modals/action/request-action";
import {
  ClearActionHandlerResolver,
  CloseModalHandlerResolver,
  MoveActionHandlerResolver,
  PartialHandlerResolver,
  RemoveActionHandlerResolver,
  RequestContentActionHandlerResolver,
} from "../modals/handler/request-handlers";

const actionFactory = new ActionFactory(handlerFactory);

window.ExperimentalModule = (function () {
  var experimentalModule = {
    actionFactory: actionFactory,
    actionHandlerFactory: handlerFactory,
  };

  experimentalModule.actionFactory.register(RequestActionResolver.TYPE, requestActionResolver);
  experimentalModule.actionHandlerFactory.register(
    RequestContentActionHandlerResolver.TYPE,
    new RequestContentActionHandlerResolver()
  );
  experimentalModule.actionHandlerFactory.register(MoveActionHandlerResolver.TYPE, new MoveActionHandlerResolver());
  experimentalModule.actionHandlerFactory.register(RemoveActionHandlerResolver.TYPE, new RemoveActionHandlerResolver());
  experimentalModule.actionHandlerFactory.register(ClearActionHandlerResolver.TYPE, new ClearActionHandlerResolver());
  experimentalModule.actionHandlerFactory.register(CloseModalHandlerResolver.TYPE, new CloseModalHandlerResolver());
  experimentalModule.actionHandlerFactory.register(PartialHandlerResolver.TYPE, new PartialHandlerResolver());

  return experimentalModule;
})();
