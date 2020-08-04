// @ts-ignore
EntityModule.registerInitializer(function (node) {
  $("[data-modal-load]", node).each((idx: number, element: Node) => {
    initializeModalConfiguration(element);
  });
});

function initializeModalConfiguration(element: Node) {
  const $node = $(element);
  $node.on("click", (e: any) => {
    e.preventDefault();

    const config = $.extend(true, {}, $node.data("modal-load"));
    const targetModalId = config.target;

    if (config.renderAfterContentLoaded) {
      fetchModalContent(targetModalId, config, () => {
        $(targetModalId).modal("show");
      });
    } else {
      fetchModalContent(targetModalId, config, () => {});
      $(targetModalId).modal("show");
    }
  });
}

function fetchModalContent(targetModalId: string, config: any, callback: () => void) {
  const handlerResults: any[] = [];
  config.content.forEach((action: any) => {
    handlerResults.push(ExperimentalModule.actionFactory.handle(action, {}));
  });
  Promise.all(handlerResults).then(callback);
}
