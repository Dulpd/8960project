<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import useUserInfoStore from '@/stores/userInfo.js'
import { userPwdUpdateService } from '@/api/user.js'

const userInfoStore = useUserInfoStore()

// 密码表单数据
const pwdForm = ref({
  old_pwd: '',
  new_pwd: '',
  re_pwd: ''
})

// 表单验证规则
const rules = {
  old_pwd: [
    { required: true, message: 'Please enter the original password', trigger: 'blur' },
    {
      pattern: /^\S{5,20}$/,
      message: 'The password must be a non empty string of 5-20 digits',
      trigger: 'blur'
    }
  ],
  new_pwd: [
    { required: true, message: 'Please enter the new password', trigger: 'blur' },
    {
      pattern: /^\S{5,20}$/,
      message: 'The password must be a non empty string of 5-20 digits',
      trigger: 'blur'
    }
  ],
  re_pwd: [
    { required: true, message: 'Please confirm the new password', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.value.new_pwd) {
          callback(new Error('The new passwords entered twice do not match'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 提交修改密码
import {useTokenStore } from '@/stores/token.js'
import {useRouter} from 'vue-router'

const router = useRouter()
const tokenStore =useTokenStore();
const updatePassword = async () => {
    // 调用API接口
    const res = await userPwdUpdateService(pwdForm.value)
    
    // 处理成功响应
    ElMessage.success(res.msg || 'Password changed successfully')
    
    // 清除本地token和用户信息
    //userInfoStore.clear()
    
    // 跳转到登录页
    router.push('/login')
  } 


// 提交前的二次确认
const confirmUpdate = () => {
  ElMessageBox.confirm('After changing the password, you need to log in again. Do you want to continue?', 'Warning', {
    confirmButtonText: 'CONFIRM',
    cancelButtonText: 'CANCEL',
    type: 'warning'
  }).then(() => updatePassword())
}
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>Reset Password</span>
      </div>
    </template>
    
    <el-row>
      <el-col :span="12">
        <el-form 
          :model="pwdForm" 
          :rules="rules" 
          label-width="150px" 
          size="large"
        >
          <el-form-item label="Original Password" prop="old_pwd">
            <el-input 
              v-model="pwdForm.old_pwd" 
              type="password" 
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item label="New Password" prop="new_pwd">
            <el-input 
              v-model="pwdForm.new_pwd" 
              type="password" 
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item label="Confirm Password" prop="re_pwd">
            <el-input 
              v-model="pwdForm.re_pwd" 
              type="password" 
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="confirmUpdate"
            >Reset Password</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </el-card>
</template>

<style scoped>
.page-container {
  min-height: calc(100vh - 130px);
}
</style>