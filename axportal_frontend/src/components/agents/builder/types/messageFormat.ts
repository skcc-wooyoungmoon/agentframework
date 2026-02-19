export type MessageFormat = {
  id: string;
  content: string;
  // content: string | Record<string, any>;
  time: string;
  // @ts-ignore
  type: ChatType.AI | ChatType.HUMAN;
  regen?: boolean;
  elapsedTime?: number; // milliseconds
};
