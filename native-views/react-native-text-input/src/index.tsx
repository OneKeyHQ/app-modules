import type { ComponentType } from "react";
// @ts-expect-error
import TextInputView from "./Input";
import type { ITextInputProps } from "./type";

// export * from './TextInputViewNativeComponent';

export type {
  IPasteEventPayloadItem,
  IPasteEventPayload,
  IPasteEventParams,
  ITextInputProps,
} from "./type";
export * from "./enum";

const TextInput = TextInputView as ComponentType<ITextInputProps>;
export default TextInput;
