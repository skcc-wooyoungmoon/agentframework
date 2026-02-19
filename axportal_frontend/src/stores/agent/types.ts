export interface Node {
  id: string;
  type: string;
  position: { x: number; y: number };
  data: {
    label: string;
    config?: any;
  };
}

export interface Edge {
  id: string;
  source: string;
  target: string;
  type?: string;
}
