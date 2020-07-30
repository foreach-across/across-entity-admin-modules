import { Action } from "./action-types";

EntityModule.registerInitializer(function (node) {
  $("[data-action-executor]", node).each((idx: number, element: Node) => {
    initializeRequestExecutor(element);
  });
});

function initializeRequestExecutor(element: Node) {
  const $node = $(element);
  $node.on("click", (e: any) => {
    e.preventDefault();

    const config: Action = $.extend(true, {}, $node.data("action-executor"));
    ExperimentalModule.actionFactory.handle(config, {});
  });
}
