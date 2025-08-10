<script setup>
import { Edit, Delete } from '@element-plus/icons-vue'
import { ref } from 'vue'
import { 
    catCategoryListService, 
    catCategoryAddService, 
    catCategoryUpdateService,
    catCategoryDeleteService 
} from '@/api/cat.js'

// 猫分类数据
const categorys = ref([])

// 加载分类列表
const loadCatCategories = async () => {
    const result = await catCategoryListService()
    categorys.value = result.data
}
loadCatCategories()

// 控制弹窗显示
const dialogVisible = ref(false)

// 分类数据模型
const categoryModel = ref({
    catCategoryName: '',
    catCategoryAlias: ''
})

// 表单校验规则
const rules = {
    catCategoryName: [
        { required: true, message: '请输入分类名称', trigger: 'blur' }
    ],
    catCategoryAlias: [
        { required: true, message: '请输入分类别名', trigger: 'blur' }
    ]
}

// 添加分类
const addCategory = async () => {
    await catCategoryAddService(categoryModel.value)
    ElMessage.success('添加成功')
    loadCatCategories()
    dialogVisible.value = false
}

// 编辑分类
const updateCategory = async () => {
    await catCategoryUpdateService(categoryModel.value)
    ElMessage.success('修改成功')
    loadCatCategories()
    dialogVisible.value = false
}

// 清空表单
const clearData = () => {
    categoryModel.value = { catCategoryName: '', catCategoryAlias: '' }
}

// 删除分类
import { ElMessage, ElMessageBox } from 'element-plus'
const deleteCategory = (row) => {
    ElMessageBox.confirm(
        '确认删除该猫分类吗？',
        '警告',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        await catCategoryDeleteService(row.id)
        ElMessage.success('删除成功')
        loadCatCategories()
    }).catch(() => {
        ElMessage.info('已取消操作')
    })
}

// 控制弹窗标题
const title = ref('')
const showDialog = (row) => {
    dialogVisible.value = true
    title.value = '编辑猫分类'
    categoryModel.value.catCategoryName = row.catCategoryName;
    categoryModel.value.catCategoryAlias = row.catCategoryAlias;
    //扩展id属性,将来需要传递给后台,完成分类的修改
    categoryModel.value.id = row.id
}
</script>

<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>猫分类管理</span>
                <div class="extra">
                    <el-button type="primary" 
                        @click="dialogVisible = true; title = '添加猫分类'; clearData()">
                        添加分类
                    </el-button>
                </div>
            </div>
        </template>

        <el-table :data="categorys" style="width: 100%">
            <el-table-column label="序号" width="80" type="index" />
            <el-table-column label="分类名称" prop="catCategoryName" />
            <el-table-column label="分类别名" prop="catCategoryAlias" />
            <el-table-column label="操作" width="120">
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="showDialog(row)" />
                    <el-button :icon="Delete" circle plain type="danger" @click="deleteCategory(row)" />
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="暂无数据" />
            </template>
        </el-table>

        <!-- 添加/编辑弹窗 -->
        <el-dialog v-model="dialogVisible" :title="title" width="30%">
            <el-form :model="categoryModel" :rules="rules" label-width="100px">
                <el-form-item label="分类名称" prop="catCategoryName">
                    <el-input v-model="categoryModel.catCategoryName" placeholder="如：橘猫" />
                </el-form-item>
                <el-form-item label="分类别名" prop="catCategoryAlias">
                    <el-input v-model="categoryModel.catCategoryAlias" placeholder="如：orange" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" 
                    @click="title === '添加猫分类' ? addCategory() : updateCategory()">
                    确认
                </el-button>
            </template>
        </el-dialog>
    </el-card>
</template>

<style lang="scss" scoped>
.page-container {
    min-height: 100%;
    box-sizing: border-box;

    .header {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
}
</style>