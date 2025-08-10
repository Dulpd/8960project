<script setup>
import { ref } from 'vue'
import useUserInfoStore from '@/stores/userInfo.js'
const userInfoStore = useUserInfoStore();

const userInfo = ref({...userInfoStore.info})

const rules = {
    nickname: [
        { required: true, message: 'Please enter user nickname', trigger: 'blur' },
        {
            pattern: /^\S{2,10}$/,
            message: 'Nickname must be a non empty string of 2-10 digits',
            trigger: 'blur'
        }
    ],
    email: [
        { required: true, message: 'Please enter the user email address', trigger: 'blur' },
        { type: 'email', message: 'Email format incorrect', trigger: 'blur' }
    ]
}

//修改个人信息
import {userInfoUpdateService} from '@/api/user.js'
import {ElMessage} from 'element-plus'
const updateUserInfo = async ()=>{
    //调用接口
    let result = await userInfoUpdateService(userInfo.value);
    ElMessage.success(result.msg? result.msg : 'Modified successfully');
    
    //修改pinia中的个人信息
    userInfoStore.setInfo(userInfo.value)
}

</script>
<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>Basic Information</span>
            </div>
        </template>
        <el-row>
            <el-col :span="12">
                <el-form :model="userInfo" :rules="rules" label-width="130px" size="large">
                    <el-form-item label="User name">
                        <el-input v-model="userInfo.username" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="User nickname" prop="nickname">
                        <el-input v-model="userInfo.nickname"></el-input>
                    </el-form-item>
                    <el-form-item label="User Mailbox" prop="email">
                        <el-input v-model="userInfo.email"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="updateUserInfo">Submit</el-button>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
    </el-card>
</template>