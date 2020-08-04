import { Action } from "./action-types";

EntityModule.registerInitializer(function (node) {
  $("[data-action-load]", node).each((idx: number, element: Node) => {
    initializeRequestExecutor(element);
  });
});

function initializeRequestExecutor(element: Node) {
  const $node = $(element);
  const config: Action = $.extend(true, {}, $node.data("action-load"));

  $node.on(config.event, (e: any) => {
    e.preventDefault();

    const config: Action = $.extend(true, {}, $node.data("action-load"));
    ExperimentalModule.actionFactory.handle(config, {});
  });
}
