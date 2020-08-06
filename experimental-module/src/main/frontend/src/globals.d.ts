declare var jQuery: JQueryStatic;
declare var $: JQueryStatic;

declare interface ActionFactory {
  register(type: string, handler: any): void;

  handle(action: any, context: any);
}

declare interface ActionHandlerFactory {
  register(type: string, handler: any): void;

  handle(action: any, context: any): void;
}

declare global {
  interface Window {
    ExperimentalModule: ExperimentalModuleObject;
  }
}

declare interface ExperimentalModuleObject {
  actionFactory: ActionFactory;
  actionHandlerFactory: ActionHandlerFactory;
}

declare var ExperimentalModule: ExperimentalModuleObject;

declare var BootstrapUiModule: {
  Controls: any;
  ControlAdapterFactory: any;
  documentInitialized: boolean;
  initializers: any[];

  registerInitializer(callback: any, callIfAlreadyInitialized?: boolean): void;

  initializeFormElements(node: any): void;

  refTarget(node: any, recurse: any): void;
};

declare var EntityModule: {
  initializeFormElements: (el?: any) => unknown;

  registerInitializer: (callback: (node: HTMLElement) => void) => void;
};

/**
 * Represents the value of a {@link BaseControlAdapter}.
 */
declare interface BootstrapUiControlValueHolder {
  /**
   * Displayed representation of the value.
   * E.g. 'John Doe' for an option element of which the actual value is 1.
   */
  readonly label: string;

  /**
   * Actual value of the displayed element
   * E.g. 1 as the value for a selected option element.
   */
  readonly value: any;

  /**
   * Context of the value, e.g. which html element defined this value.
   */
  readonly context: any;
}

declare interface BootstrapUiControlAdapter {
  /**
   * Returns the current value of the {@link BootstrapUiControlAdapter#getTarget} element.
   *
   * {BootstrapUiControlValueHolder}
   */
  getValue(): BootstrapUiControlValueHolder[];

  /**
   * Sets the current value of the {@link BootstrapUiControlAdapter#getTarget} element.
   *
   * @param newValue
   */
  selectValue(newValue: any): void;

  /**
   * Triggers a {@link BootstrapUiControlEvent#CHANGE} event for the current {@link BootstrapUiControlAdapter#getTarget}.
   * This event should be triggered when the value of the {@link BootstrapUiControlAdapter#getTarget} element is actually changed.
   */
  triggerChange(): void;

  /**
   * Triggers a {@link BootstrapUiControlEvent#SUBMIT} event for the current {@link BootstrapUiControlAdapter#getTarget}.
   * This event should be triggered when the value of the {@link BootstrapUiControlAdapter#getTarget} element should be submitted.
   * (e.g. by pressing enter)
   */
  triggerSubmit(): void;

  /**
   * Resets the value of the {@link BootstrapUiControlAdapter#getTarget} element to its initial value.
   */
  reset(): void;

  /**
   * Returns the target element of the adapter.
   * Which element is used specifically depends on the implementation.
   */
  getTarget(): any;
}

declare const BootstrapUiControlValueHolder: BootstrapUiControlValueHolder;
declare const BootstrapUiControlAdapter: BootstrapUiControlAdapter;
