declare var jQuery: JQueryStatic;
declare var $: JQueryStatic;
declare var BootstrapUiModule: any;
declare var EntityModule: {
  initializeFormElements: (el?: any) => unknown;
  registerInitializer: (callback: (node: HTMLElement) => void) => void;
};
