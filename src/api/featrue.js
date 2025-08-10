import request from '@/utils/request.js'

export const featureService = (catData) => {
    return request.post('/feature/add', catData);
}