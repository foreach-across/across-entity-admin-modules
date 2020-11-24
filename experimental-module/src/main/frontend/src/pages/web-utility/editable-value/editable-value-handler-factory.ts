import { EditableValueHandler } from "./editable-value-handler";
import { EditableValue } from "./editable-value";

export class EditableValueHandlerFactory {
  private handlers: EditableValueHandler[] = [];
  private eventTypes!: Set<string>;

  registerHandler(handler: EditableValueHandler) {
    this.removeHandler(handler.getName());
    this.handlers.push(handler);
    this.handlers.sort((a, b) => a.getOrder() - b.getOrder());
  }

  removeHandler(name: string) {
    const existing = this.handlers.findIndex((handler) => handler.getName() === name);
    if (existing !== -1) {
      this.handlers.splice(existing, 1);
    }
  }

  getHandledEventTypes(): Set<string> {
    if (!this.eventTypes) {
      this.eventTypes = new Set<string>();
      this.handlers.flatMap((h) => h.getHandledEventTypes()).forEach((het) => this.eventTypes.add(het));
    }
    return this.eventTypes;
  }

  handle(control: any, editableValueHolder: EditableValue, event: any): void {
    const matchingHandler = this.handlers.find((h) => h.canHandle(control, editableValueHolder, event));
    if (matchingHandler) {
      matchingHandler.handle(control, editableValueHolder, event);
    }
  }
}

export const editableValueHandlerFactory = new EditableValueHandlerFactory();
