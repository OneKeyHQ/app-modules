import type { TextInputProps } from "react-native";
import type { EPasteEventPayloadItemType } from "./enum";

export type IPasteEventPayloadItem = {
  data?: string;
  type?: EPasteEventPayloadItemType;
};
export type IPasteEventPayload = IPasteEventPayloadItem[];
export type IPasteEventParams = { nativeEvent: { items?: IPasteEventPayload } };

export interface ITextInputProps extends TextInputProps {
  onPaste?: (event: IPasteEventParams) => void;
}
