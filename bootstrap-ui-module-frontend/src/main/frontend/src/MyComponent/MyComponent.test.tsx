import * as React from 'react';
import {render} from '@testing-library/react';
import {MyComponent} from './MyComponent';

describe('it', () => {
  it('renders without crashing', () => {
    const { getByText } = render(<MyComponent />);
    expect(getByText('Test'));
  });
});
