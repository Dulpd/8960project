<script setup>
import {
    Edit,
    Delete
} from '@element-plus/icons-vue'
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { Plus } from '@element-plus/icons-vue'
import { useTokenStore } from '@/stores/token.js'
import {
    articleCategoryListService,
    articleListService,
    articleAddService,
    articleDeleteService,
    articleUpdateService
} from '@/api/article.js'

const tokenStore = useTokenStore()

// 文章分类数据
const categorys = ref([
    // 原始数据保持不变
])

// 文章列表数据
const articles = ref([
    // 原始数据保持不变
])

// 分页相关
const pageNum = ref(1)
const total = ref(20)
const pageSize = ref(3)

// 搜索条件
const categoryId = ref('')
const state = ref('')

// 抽屉控制
const visibleDrawer = ref(false)
const contentDrawerVisible = ref(false)
const title = ref('')
const currentArticle = ref({})

// 文章表单模型
const articleModel = ref({
    id: null,
    title: '',
    categoryId: '',
    coverImg: '',
    content: '',
    state: ''
})

// 获取分类列表
const articleCategoryList = async () => {
    try {
        const result = await articleCategoryListService()
        categorys.value = result.data
    } catch (error) {
        ElMessage.error('Failed to obtain classification: ' + error.message)
    }
}

// 获取文章列表
const articleList = async () => {
    try {
        const params = {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            categoryId: categoryId.value || null,
            state: state.value || null
        }
        const result = await articleListService(params)

        total.value = result.data.total
        articles.value = result.data.items.map(article => {
            const category = categorys.value.find(c => c.id === article.categoryId)
            return { ...article, categoryName: category?.categoryName || 'Unknown classification' }
        })
    } catch (error) {
        ElMessage.error('Failed to retrieve the list of articles:' + error.message)
    }
}

// 分页处理
const onSizeChange = (size) => {
    pageSize.value = size
    articleList()
}

const onCurrentChange = (num) => {
    pageNum.value = num
    articleList()
}

// 上传封面
const uploadSuccess = (result) => {
    articleModel.value.coverImg = result.data
}

// 添加文章
const addArticle = async (state) => {
    try {
        articleModel.value.state = state
        await articleAddService(articleModel.value)
        ElMessage.success('Operation successful')
        visibleDrawer.value = false
        resetForm()
        articleList()
    } catch (error) {
        ElMessage.error('Operation failed: ' + error.message)
    }
}

// 更新文章
const updateArticle = async (state) => {
    try {
        // 状态验证
        if (!['已发布', '草稿'].includes(state)) {
            ElMessage.warning('Invalid state value')
            return
        }

        const updateData = {
            id: articleModel.value.id,
            title: articleModel.value.title,
            categoryId: articleModel.value.categoryId,
            coverImg: articleModel.value.coverImg,
            content: articleModel.value.content,
            state: state 
        }

        await articleUpdateService(updateData)
        ElMessage.success(state === '已发布' ? 'The article has been published' : 'Saved as draft')
        visibleDrawer.value = false
        resetForm()
        articleList()
    } catch (error) {
        ElMessage.error('Operation failed: ' + (error.response?.data?.msg || error.message))
    }
}

// 删除文章
const deleteArticle = (row) => {
    ElMessageBox.confirm('Are you sure to delete this article?', 'Warning', {
        confirmButtonText: 'CONFIRM',
        cancelButtonText: 'CANCEL',
        type: 'warning'
    }).then(async () => {
        try {
            await articleDeleteService(row.id)
            ElMessage.success('Delete successfully')
            articleList()
        } catch (error) {
            ElMessage.error('Delete failed: ' + error.message)
        }
    }).catch(() => {
        ElMessage.info('Operation cancelled')
    })
}

// 表单重置
const resetForm = () => {
    articleModel.value = {
        id: null,
        title: '',
        categoryId: '',
        coverImg: '',
        content: '',
        state: ''
    }
}

// 打开编辑抽屉
const showDialog = (row) => {
    visibleDrawer.value = true
    title.value = 'Edit Article'
    articleModel.value = {
        ...row,
        // 强制转换可能存在的英文状态值
        state: row.state === 'Draft' ? '草稿' :
            row.state === 'Published' ? '已发布' :
                row.state
    }
}

// 展示内容抽屉
const showContent = (row) => {
    currentArticle.value = { ...row }
    contentDrawerVisible.value = true
}

// 初始化
articleCategoryList()
articleList()


</script>
<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>Article Management</span>
                <div class="extra">
                    <el-button type="primary" @click="visibleDrawer = true; title = 'Add Article'">Add
                        Article</el-button>
                </div>
            </div>
        </template>
        <!-- 搜索表单 -->
        <el-form inline>
            <el-form-item label="Article Category">
                <el-select placeholder="Please select" v-model="categoryId">
                    <el-option v-for="c in categorys" :key="c.id" :label="c.categoryName" :value="c.id">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="Release status:">
                <el-select placeholder="Please select" v-model="state">
                    <el-option label="Published" value="已发布"></el-option>
                    <el-option label="Draft" value="草稿"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="articleList">Search</el-button>
                <el-button @click="categoryId = ''; state = ''">Reset</el-button>
            </el-form-item>
        </el-form>
        <!-- 文章列表 -->
        <el-table :data="articles" style="width: 100%">
            <el-table-column label="Article Title" width="400">
                <template #default="{ row }">
                    <el-link type="primary" @click="showContent(row)">{{ row.title }}</el-link>
                </template>
            </el-table-column>
            <el-table-column label="Category" prop="categoryName"></el-table-column>
            <el-table-column label="Publication time" prop="createTime"> </el-table-column>
            <el-table-column label="State" prop="state"></el-table-column>
            <el-table-column label="Operate" width="100">
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="showDialog(row)"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="deleteArticle(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="No data" />
            </template>
        </el-table>
        <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[3, 5, 10, 15]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />

        <!-- 抽屉 -->
        <el-drawer v-model="visibleDrawer" :title="title" direction="rtl" size="50%">
            <el-form :model="articleModel" label-width="100px">
                <!-- 标题输入 -->
                <el-form-item label="Article Title">
                    <el-input v-model="articleModel.title" placeholder="Please enter the title" />
                </el-form-item>

                <!-- 分类选择 -->
                <el-form-item label="article Category">
                    <el-select v-model="articleModel.categoryId" placeholder="Please choose a category">
                        <el-option v-for="c in categorys" :key="c.id" :label="c.categoryName" :value="c.id" />
                    </el-select>
                </el-form-item>

                <!-- 封面图片 -->
                <el-form-item label="Article Cover">
                    <el-upload class="avatar-uploader" :auto-upload="true" :show-file-list="false" action="/api/upload"
                        name="file" :headers="{ 'Authorization': tokenStore.token }" :on-success="uploadSuccess">
                        <img v-if="articleModel.coverImg" :src="articleModel.coverImg" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>

                <!-- 内容编辑器 -->
                <el-form-item label="Article Content">
                    <div class="editor">
                        <quill-editor theme="snow" v-model:content="articleModel.content" contentType="html" />
                    </div>
                </el-form-item>

                <!-- 操作按钮 -->
                <el-form-item>
                    <el-button type="primary"
                        @click="title === 'Add Article' ? addArticle('已发布') : updateArticle('已发布')">
                        {{ title === 'Add Article' ? 'Publish Now' : 'Update and publish' }}
                    </el-button>
                    <el-button type="info" @click="title === 'Add Article' ? addArticle('草稿') : updateArticle('草稿')">
                        {{ title === 'Add Article' ? 'Save as draft' : 'Update to draft' }}
                    </el-button>
                </el-form-item>
            </el-form>
        </el-drawer>

        <!-- 内容展示抽屉 -->
        <el-drawer v-model="contentDrawerVisible" title="Article Content" direction="rtl" size="50%">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="Title">{{ currentArticle.title }}</el-descriptions-item>
                <el-descriptions-item label="Category">{{ currentArticle.categoryName }}</el-descriptions-item>
                <el-descriptions-item label="Publish Time">{{ currentArticle.createTime }}</el-descriptions-item>
                <el-descriptions-item label="State">{{ currentArticle.state }}</el-descriptions-item>
                <el-descriptions-item label="Cover">
                    <img v-if="currentArticle.coverImg" :src="currentArticle.coverImg"
                        style="max-width: 200px; max-height: 200px;" />
                </el-descriptions-item>
                <el-descriptions-item label="Content">
                    <div class="content" v-html="currentArticle.content"></div>
                </el-descriptions-item>
            </el-descriptions>
        </el-drawer>
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

/* 抽屉样式 */
.avatar-uploader {
    :deep() {
        .avatar {
            width: 178px;
            height: 178px;
            display: block;
        }

        .el-upload {
            border: 1px dashed var(--el-border-color);
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            transition: var(--el-transition-duration-fast);
        }

        .el-upload:hover {
            border-color: var(--el-color-primary);
        }

        .el-icon.avatar-uploader-icon {
            font-size: 28px;
            color: #8c939d;
            width: 178px;
            height: 178px;
            text-align: center;
        }
    }
}

.editor {
    width: 100%;

    :deep(.ql-editor) {
        min-height: 200px;
    }
}
</style>