export const isEqual = (value1: any, value2: any): boolean => {
    if (value1 === value2) {
        return true;
    }
    if (value1 == null || value2 == null) {
        return value1 === value2;
    }
    if (typeof value1 !== typeof value2) {
        return false;
    }
    if (typeof value1 !== 'object') {
        return value1 === value2;
    }
    if (Array.isArray(value1) !== Array.isArray(value2)) {
        return false;
    }
    if (Array.isArray(value1)) {
        if (value1.length !== value2.length) {
            return false;
        }
        for (let i = 0; i < value1.length; i++) {
            if (!isEqual(value1[i], value2[i])) {
                return false;
            }
        }
        return true;
    }
    const keys1 = Object.keys(value1);
    const keys2 = Object.keys(value2);
    if (keys1.length !== keys2.length) {
        return false;
    }
    for (const key of keys1) {
        if (!keys2.includes(key)) {
            return false;
        }
        if (!isEqual(value1[key], value2[key])) {
            return false;
        }
    }
    return true;
}
