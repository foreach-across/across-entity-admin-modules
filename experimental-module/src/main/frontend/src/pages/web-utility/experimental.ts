import { handlerFactory } from "./actions/handler/action-handler-factory";
import { ActionFactory } from "./actions/action/action-factory";
import { RequestActionResolver, requestActionResolver } from "./actions/action/request-action";
import {
  RequestActionHandlerResolver,
  ResponseContentActionHandlerResolver,
  ResponseUrlIdResolver,
} from "./actions/handler/request-handlers";
import {
  ClearActionHandlerResolver,
  CloseModalHandlerResolver,
  InitializeFormElementsHandlerResolver,
  MoveActionHandlerResolver,
  RemoveActionHandlerResolver,
} from "./actions/handler/simple-handlers";
import { simpleActionResolver, SimpleActionResolver } from "./actions/action/simple-action";
import { editableValueHandlerFactory } from "./editable-value/editable-value-handler-factory";
import { sortableTableAjax } from "./ajax/sortable-table-ajax";

EntityModule.registerInitializer(function (node) {
  sortableTableAjax.init(node);
});

$.fn.findSelf = function (selector: any) {
  let result = this.find(selector);
  this.each(function () {
    let htmlElementJQuery = $(this);

    if (htmlElementJQuery.is(selector)) {
      result.push(htmlElementJQuery);
    }
  });

  return result;
};

const actionFactory = new ActionFactory(handlerFactory);

window.ExperimentalModule = (function () {
  var experimentalModule = {
    actionFactory: actionFactory,
    actionHandlerFactory: handlerFactory,
    editableValueHandlerFactory: editableValueHandlerFactory,
  };

  experimentalModule.actionFactory.register(RequestActionResolver.TYPE, requestActionResolver);
  experimentalModule.actionFactory.register(SimpleActionResolver.TYPE, simpleActionResolver);

  experimentalModule.actionHandlerFactory.register(
    ResponseContentActionHandlerResolver.TYPE,
    new ResponseContentActionHandlerResolver()
  );
  experimentalModule.actionHandlerFactory.register(MoveActionHandlerResolver.TYPE, new MoveActionHandlerResolver());
  experimentalModule.actionHandlerFactory.register(RemoveActionHandlerResolver.TYPE, new RemoveActionHandlerResolver());
  experimentalModule.actionHandlerFactory.register(ClearActionHandlerResolver.TYPE, new ClearActionHandlerResolver());
  experimentalModule.actionHandlerFactory.register(CloseModalHandlerResolver.TYPE, new CloseModalHandlerResolver());
  experimentalModule.actionHandlerFactory.register(
    RequestActionHandlerResolver.TYPE,
    new RequestActionHandlerResolver()
  );
  experimentalModule.actionHandlerFactory.register(
    InitializeFormElementsHandlerResolver.TYPE,
    new InitializeFormElementsHandlerResolver()
  );
  experimentalModule.actionHandlerFactory.register(ResponseUrlIdResolver.TYPE, new ResponseUrlIdResolver());

  return experimentalModule;
})();
