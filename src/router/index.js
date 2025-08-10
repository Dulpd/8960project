import { createRouter, createWebHistory } from 'vue-router'
import LoginVue from '@/views/Login.vue'
import LayoutVue from '@/views/Layout.vue'
import ArticleCategoryVue from '@/views/article/ArticleCategory.vue'
import ArticleManageVue from '@/views/article/ArticleManage.vue'
import UserAvatarVue from '@/views/user/UserAvatar.vue'
import UserInfoVue from '@/views/user/UserInfo.vue'
import UserResetPasswordVue from '@/views/user/UserResetPassword.vue'
import CatCategoryVue from '@/views/cat/CatCategory.vue'
import CatManageVue from '@/views/cat/CatManage.vue' // 新增猫管理组件导入

const routes = [
    { path: '/login', component: LoginVue },
    {
        path: '/', 
        component: LayoutVue,
        redirect: '/article/manage', 
        children: [
            { path: '/cat/category', component: CatCategoryVue },
            { path: '/cat/manage', component: CatManageVue }, // 新增猫管理路由
            { path: '/article/category', component: ArticleCategoryVue },
            { path: '/article/manage', component: ArticleManageVue },
            { path: '/user/info', component: UserInfoVue },
            { path: '/user/avatar', component: UserAvatarVue },
            { path: '/user/resetPassword', component: UserResetPasswordVue }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

export default router