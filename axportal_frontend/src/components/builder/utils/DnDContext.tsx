// noinspection JSUnusedGlobalSymbols

import { createContext, useContext, useState } from 'react';

// eslint-disable-next-line no-unused-vars,@typescript-eslint/no-unused-vars
const DnDContext = createContext([null, (_: any) => {}]);

// @ts-ignore
export const DnDProvider = ({ children }) => {
  const [type, setType] = useState(null);

  return <DnDContext.Provider value={[type, setType]}>{children}</DnDContext.Provider>;
};

export default DnDContext;

// eslint-disable-next-line react-refresh/only-export-components
export const useDnD = () => {
  return useContext(DnDContext);
};
