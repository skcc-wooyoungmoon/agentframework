import { createBrowserRouter } from 'react-router-dom';

import { RootLayout } from './RootLayout';
import { routeConfig } from './route.config';

export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: routeConfig,
  },
]);
