import React from "react";
import type { BottomTabsDrawerNativeProps } from "./BottomTabsDrawerNativeComponent";

// The fixed first slot is the menu; Web only renders the foreground slot.
export default function BottomTabsDrawerWeb({
  children,
}: React.PropsWithChildren<BottomTabsDrawerNativeProps>) {
  return <>{React.Children.toArray(children)[1]}</>;
}
