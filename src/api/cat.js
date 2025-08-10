import request from '@/utils/request.js'
import {useTokenStore} from '@/stores/token.js'

// 猫分类列表查询
export const catCategoryListService = ()=>{
    return request.get('/catCategory')
}

// 猫分类添加
export const catCategoryAddService = (categoryData)=>{
    return request.post('/catCategory', categoryData)
}

// 猫分类修改
export const catCategoryUpdateService = (categoryData)=>{
    return request.put('/catCategory', categoryData)
}

// 猫分类删除
export const catCategoryDeleteService = (id)=>{
    return request.delete('/catCategory?id=' + id)
}

// 猫列表查询
export const catListService = (params)=>{
    return request.get('/cat/all', {params: params})
}

// 猫添加
export const catAddService = (catData)=>{
    return request.post('/cat/add', catData)
}

// 猫删除
export const catDeleteService = (id)=>{
    return request.delete('/cat/delete/' + id)
}

// 猫修改
export const catUpdateService = (catData)=>{
    return request.put('/cat', catData)
}

// 
export const findFeatureCatService = (paths) => {
    return request.get('/cat/path', {
        params: paths
    });
}