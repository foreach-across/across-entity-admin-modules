import * as React from 'react';

interface Props {
  children?: React.ReactNode;
  test?: string;
}

export const MyComponent: React.SFC<Props> = ({ children = 'Test' }) => (
  <div>{children}</div>
);

// MyComponent.defaultProps = {
//   test: 'test'
// }
