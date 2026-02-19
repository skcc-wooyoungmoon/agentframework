export type FewShots = {
  uuid: string;
  project_id?: string;
  name: string;
  dependency?: Array<string>;
  latest_version?: number;
  release_version?: number;
  created_at: string;
  tags?: string[];
  hit_rate?: number;
  data?: {
    uuid: string;
    project_id?: string;
    name: string;
    dependency?: Array<string>;
    latest_version?: number;
    release_version?: number;
    created_at: string;
    tags?: string[];
    hit_rate?: number;
  };
};