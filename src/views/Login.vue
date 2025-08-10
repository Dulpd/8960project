<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { ref } from 'vue'

import { ElMessage } from 'element-plus'
//控制注册与登录表单的显示， 默认显示注册
const isRegister = ref(false)
//定义数据模型
const registerData = ref({
    username:'',
    password:'',
    rePassword:''
})
//校验密码的函数
const checkRePassword = (rule,value,callback)=>{
    if(value===''){
        callback(new Error('please check your password again~'))
    }else if(value !== registerData.value.password){
        callback(new Error('Please ensure that the passwords entered twice are consistent!'))
    }else{
        callback()
    }

}

//定义表单校验规则
const rules={
    username:[
        {required:true,message:'please input username',trigger:'blur'},
        {min:2,max:10,message:'2-10 non empty characters in length',trigger:'blur'}
],
    password:[
        {required:true,message:'please input password',trigger:'blur'},
        {min:5,max:20,message:'5-20 non empty characters in length',trigger:'blur'}],
    rePassword:[
        {validator:checkRePassword,trigger:'blur'}
    ]
}

//调用后台接口，完成注册
import{userRegisterService,userLoginService} from '@/api/user.js'
const register = async()=>{
    //registerData是一个响应式对象，如果需要获取值，需要。value
    let result = await userRegisterService(registerData.value);
    ElMessage.success(result.msg ? result.msg : 'registered successfully');
}

//绑定数据 复用注册表单的数据类型
//表单数据校验
//登录函数
import {useTokenStore } from '@/stores/token.js'
import {useRouter} from 'vue-router'

const router = useRouter()
const tokenStore =useTokenStore();
const login =async ()=>{
    //调用接口完成登录
    let result = await userLoginService(registerData.value);
    ElMessage.success(result.msg? result.msg : 'Login succeeded')

    //把得到的token存到pinia中
    tokenStore.setToken(result.data)
    //跳转到首页 使用路由
    router.push('/')
}

//定义函数,清空数据模型的数据
const clearRegisterData = ()=>{
    registerData.value={
        username:'',
        password:'',
        rePassword:''
    }
}


</script>

<template>
    <el-row class="login-page">
        <el-col :span="12" class="bg"></el-col>
        <el-col :span="6" :offset="3" class="form">
            <!-- 注册表单 -->
            <el-form ref="form" size="large" autocomplete="off" v-if="isRegister" :model="registerData" :rules="rules">
                <el-form-item>
                    <h1>Register</h1>
                </el-form-item>
                <el-form-item prop="username">
                    <el-input :prefix-icon="User" placeholder="enter user name" v-model="registerData.username"></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input :prefix-icon="Lock" type="password" placeholder="Please input the password" v-model="registerData.password"></el-input>
                </el-form-item>
                <el-form-item prop="rePassword">
                    <el-input :prefix-icon="Lock" type="password" placeholder="Please input the password again" v-model="registerData.rePassword"></el-input>
                </el-form-item>
                <!-- 注册按钮 -->
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="register">
                        Register
                    </el-button>
                </el-form-item>
                <el-form-item class="flex">
                    <el-link type="info" :underline="false" @click="isRegister = false;clearRegisterData()">
                        ← BACK
                    </el-link>
                </el-form-item>
            </el-form>
            <!-- 登录表单 -->
            <el-form ref="form" size="large" autocomplete="off" v-else :model="registerData" :rules="rules">
                <el-form-item>
                    <h1>Log in</h1>
                </el-form-item>
                <el-form-item prop="username">
                    <el-input :prefix-icon="User" placeholder="enter user name" v-model="registerData.username"></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input name="password" :prefix-icon="Lock" type="password" placeholder="Please input the password" v-model="registerData.password"></el-input>
                </el-form-item>
                <el-form-item class="flex">
                    <div class="flex">
                        <el-checkbox>remember me</el-checkbox>
                        <el-link type="primary" :underline="false">forget password??</el-link>
                    </div>
                </el-form-item>
                <!-- 登录按钮 -->
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="login">LOGIN</el-button>
                </el-form-item>
                <el-form-item class="flex">
                    <el-link type="info" :underline="false" @click="isRegister = true;clearRegisterData()">
                        Register →
                    </el-link>
                </el-form-item>
            </el-form>
        </el-col>
    </el-row>
</template>

<style lang="scss" scoped>
/* 样式 */
.login-page {
    height: 100vh;
    background-color: #fff;

    .bg {
        background: url('@/assets/logo2.png') no-repeat 60% center / 600px auto,
            url('@/assets/login_bg.jpg') no-repeat center / cover;
        border-radius: 0 20px 20px 0;
    }

    .form {
        display: flex;
        flex-direction: column;
        justify-content: center;
        user-select: none;

        .title {
            margin: 0 auto;
        }

        .button {
            width: 100%;
        }

        .flex {
            width: 100%;
            display: flex;
            justify-content: space-between;
        }
    }
}
</style>