import React from 'react';
import { RgbaColorPicker } from "react-colorful";
import { ReactAdapterElement, RenderHooks } from 'Frontend/generated/flow/ReactAdapter';

type RgbaColor = {
  r: number;
  g: number;
  b: number;
  a: number;
};

class RgbaColorPickerElement extends ReactAdapterElement {
  protected override render(hooks: RenderHooks): React.ReactElement | null {
    const [color, setColor] = hooks.useState<RgbaColor>('color');

    return <RgbaColorPicker color={color} onChange={setColor} />;
  }
}

customElements.define('rgba-color-picker', RgbaColorPickerElement);
