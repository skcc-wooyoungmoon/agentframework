export type Data = {
    uuid: string;
    name: string;
    ptype: number;
    release_version?: number;
    latest_version: number;
    created_at: string;
    tags?: InferencePromptsTags[];
    data?: {
        uuid: string;
        name: string;
        ptype: number;
        release_version?: number;
        latest_version: number;
        created_at: string;
        tags?: InferencePromptsTags[];
    };
};

export type InferencePromptsTags = {
    tag_uuid: string;
    tag: string;
    uuid: string;
    version_id: string;
};
