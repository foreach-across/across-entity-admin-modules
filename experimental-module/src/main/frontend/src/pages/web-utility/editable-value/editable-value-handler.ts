import { EditableValue } from "./editable-value";

export interface EditableValueHandler {
  getName(): string;

  getOrder(): number;

  getHandledEventTypes(): string[];

  canHandle(control: any, editableValueHolder: EditableValue, event: any): boolean;

  handle(control: any, editableValueHolder: EditableValue, event: any): void;
}
