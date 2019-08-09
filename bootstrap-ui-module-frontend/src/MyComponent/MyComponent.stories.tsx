import * as React from 'react';
import { storiesOf } from '@storybook/react';
// import { withInfo } from "@storybook/addon-info";

// import { action } from '@storybook/addon-actions';
import { MyComponent } from './MyComponent';

const stories = storiesOf('Components', module);

stories.add('MyComponent', () => <MyComponent />, {
  info: { inline: true, header: false },
});
