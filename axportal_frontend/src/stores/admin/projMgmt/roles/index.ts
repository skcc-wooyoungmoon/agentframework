import { atom } from 'jotai';

export const projRoleListRefetchAtom = atom<(() => void) | null>(null);
