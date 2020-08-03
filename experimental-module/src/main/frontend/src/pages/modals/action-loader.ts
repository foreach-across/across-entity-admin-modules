import { Action } from "./action-types";

EntityModule.registerInitializer(function (node) {
  $("[data-action-loader]", node).each((idx: number, element: Node) => {
    initializeRequestExecutor(element);
  });
});

function initializeRequestExecutor(element: Node) {
  const $node = $(element);
  const config: Action = $.extend(true, {}, $node.data("action-loader"));

  $node.on(config.event, (e: any) => {
    e.preventDefault();

    const config: Action = $.extend(true, {}, $node.data("action-loader"));
    ExperimentalModule.actionFactory.handle(config, {});
  });
}
