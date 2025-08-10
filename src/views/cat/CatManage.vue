<script setup>
import {
    Edit,
    Delete
} from '@element-plus/icons-vue'
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useTokenStore } from '@/stores/token.js'
import {
    catCategoryListService,
    catListService,
    catAddService,
    catDeleteService,
    catUpdateService,
    findFeatureCatService
} from '@/api/cat.js'
import { featureService } from '@/api/featrue.js'

const tokenStore = useTokenStore()

// 猫分类数据
const categories = ref([])

// 猫列表数据
const cats = ref([])

// 分页相关
const pageNum = ref(1)
const total = ref(0)
const pageSize = ref(3)

// 搜索条件
const categoryId = ref('')
const state = ref('')

// 抽屉控制
const visibleDrawer = ref(false)
const title = ref('')
const currentCat = ref({})

// 猫表单模型
const catModel = ref({
    catId: null,
    name: '',
    breed: '',
    age: '',
    gender: '',
    description: '',
    catImg: '',
    adoptionStatus: ''
})

// 获取分类列表
const getCatCategories = async () => {
    try {
        const result = await catCategoryListService()
        categories.value = result.data
    } catch (error) {
        ElMessage.error('Failed to obtain categories: ' + error.message)
    }
}

// 获取猫列表
const getCatList = async () => {
    try {
        const result = await catListService()

        // 先处理所有数据，添加分类名称和性别文本
        let allCats = result.data.map(cat => {
            // breed字段存储的是分类ID，需要转换为数字进行匹配
            const categoryId = parseInt(cat.breed)
            const category = categories.value.find(c => c.id === categoryId)
            return {
                ...cat,
                categoryName: category?.catCategoryName || 'Unknown category',
                genderText: cat.gender === 'male' ? 'Male' : cat.gender === 'female' ? 'Female' : 'Unknown'
            }
        })

        // 前端过滤：根据选择的分类和性别进行过滤
        if (categoryId.value) {
            allCats = allCats.filter(cat => parseInt(cat.breed) === categoryId.value)
        }

        if (state.value) {
            allCats = allCats.filter(cat => cat.gender === state.value)
        }

        cats.value = allCats
    } catch (error) {
        ElMessage.error('Failed to retrieve cat list: ' + error.message)
    }
}

// 重置搜索条件
const resetSearch = () => {
    categoryId.value = ''
    state.value = ''
    getCatList()
}

// 上传图片
const uploadSuccess = (result) => {
    catModel.value.catImg = result.data
}

// 添加猫信息
const addCat = async () => {
    try {
        await catAddService(catModel.value);
        ElMessage.success('Operation successful')
        visibleDrawer.value = false
        resetForm()
        getCatList()
    } catch (error) {
        ElMessage.error('Operation failed: ' + error.message)
    }
}

// 特征弹窗
const featureDialogVisible = ref(false);

// 特征信息
const featureData = ref(null);

const findFeatureCat = async (paths) => {
    const findFeatureData = await findFeatureCatService({
        paths
    });
    // console.log(findFeatureData, '------');
    featureData.value = findFeatureData.data.map(cat => {
        // breed字段存储的是分类ID，需要转换为数字进行匹配
        const categoryId = parseInt(cat.breed)
        const category = categories.value.find(c => c.id === categoryId)
        return {
            ...cat,
            categoryName: category?.catCategoryName || 'Unknown category',
            genderText: cat.gender === 'male' ? 'Male' : cat.gender === 'female' ? 'Female' : 'Unknown'
        }
    })

};

// 特征识别
const featureCat = async () => {
    try {
        const { data } = await featureService(catModel.value);

        const pathList = data[data.length - 1]?.similarImages;
        const paths = pathList.join(",");

        console.log(paths);

        findFeatureCat(paths);


        // const findFeatureData = await findFeatureCatService(paths);

        // console.log(featureData.value, '------');

        featureDialogVisible.value = true;

        ElMessage.success('feature successful');
        visibleDrawer.value = false;
        resetForm();
        getCatList();
    } catch {
        ElMessage.error('Operation failed: ' + error.message);
    }
};

// 更新猫信息
const updateCat = async () => {




    try {
        await catUpdateService(catModel.value)
        ElMessage.success('Update successful')
        visibleDrawer.value = false
        resetForm()
        getCatList()
    } catch (error) {
        ElMessage.error('Update failed: ' + error.message)
    }
}

// 删除猫信息
const deleteCat = (row) => {
    ElMessageBox.confirm('Are you sure to delete this cat information?', 'Warning', {
        confirmButtonText: 'CONFIRM',
        cancelButtonText: 'CANCEL',
        type: 'warning'
    }).then(async () => {
        try {
            await catDeleteService(row.catId)
            ElMessage.success('Delete successfully')
            getCatList()
        } catch (error) {
            ElMessage.error('Delete failed: ' + error.message)
        }
    }).catch(() => {
        ElMessage.info('Operation cancelled')
    })
}

// 表单重置
const resetForm = () => {
    catModel.value = {
        catId: null,
        name: '',
        breed: '',
        age: '',
        gender: '',
        description: '',
        catImg: '',
        adoptionStatus: ''
    }
}

// 打开编辑抽屉
const showDialog = (row) => {
    visibleDrawer.value = true
    title.value = 'Edit Cat Information'
    // 确保breed字段使用正确的分类ID（数字类型）
    catModel.value = {
        catId: row.catId,
        name: row.name,
        breed: parseInt(row.breed), // 确保breed是数字类型的分类ID
        age: row.age,
        gender: row.gender,
        description: row.description,
        catImg: row.catImg,
        adoptionStatus: row.adoptionStatus
    }
}

// 初始化
getCatCategories()
getCatList()
</script>

<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>Cat Information Management</span>
                <div class="extra">
                    <el-button type="primary" @click="visibleDrawer = true; title = 'Add Cat'; resetForm()">Add
                        Cat</el-button>
                    <el-button type="primary"
                        @click="visibleDrawer = true; title = 'Feature'; resetForm()">Feature</el-button>
                </div>
            </div>
        </template>
        <!-- 搜索表单 -->
        <el-form inline>
            <el-form-item label="Cat Category">
                <el-select placeholder="Please select" v-model="categoryId">
                    <el-option v-for="c in categories" :key="c.id" :label="c.catCategoryName" :value="c.id">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="Gender">
                <el-select placeholder="Please select" v-model="state">
                    <el-option label="Male" value="male"></el-option>
                    <el-option label="Female" value="female"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="getCatList">Search</el-button>
                <el-button @click="resetSearch">Reset</el-button>
            </el-form-item>
        </el-form>
        <!-- 猫列表 -->
        <el-table :data="cats" style="width: 100%">
            <el-table-column label="Name" prop="name" width="180"></el-table-column>
            <el-table-column label="Category" prop="categoryName"></el-table-column>
            <el-table-column label="Age" prop="age"></el-table-column>
            <el-table-column label="Gender" prop="genderText"></el-table-column>
            <el-table-column label="Image">
                <template #default="{ row }">
                    <el-image v-if="row.catImg" :src="row.catImg" style="width: 60px; height: 60px"
                        :preview-src-list="[row.catImg]" preview-teleported hide-on-click-modal />
                </template>
            </el-table-column>
            <el-table-column label="Operate" width="120">
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="showDialog(row)"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="deleteCat(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="No data" />
            </template>
        </el-table>

        <!-- 抽屉 -->
        <el-drawer v-model="visibleDrawer" :title="title" direction="rtl" size="40%">
            <el-form :model="catModel" label-width="100px">
                <el-form-item label="Name">
                    <el-input v-model="catModel.name" placeholder="Please enter cat name" />
                </el-form-item>

                <el-form-item label="Category">
                    <el-select v-model="catModel.breed" placeholder="Please choose category">
                        <el-option v-for="c in categories" :key="c.id" :label="c.catCategoryName" :value="c.id" />
                    </el-select>
                </el-form-item>

                <el-form-item label="Age">
                    <el-input v-model="catModel.age" placeholder="Please enter age" />
                </el-form-item>

                <el-form-item label="Gender">
                    <el-select v-model="catModel.gender" placeholder="Please select gender">
                        <el-option label="Male" value="male" />
                        <el-option label="Female" value="female" />
                    </el-select>
                </el-form-item>

                <el-form-item label="Description">
                    <el-input v-model="catModel.description" type="textarea" :rows="3"
                        placeholder="Please enter description" />
                </el-form-item>

                <el-form-item label="Image">
                    <el-upload class="avatar-uploader" :auto-upload="true" :show-file-list="false" action="/api/upload"
                        name="file" :headers="{ 'Authorization': tokenStore.token }" :on-success="uploadSuccess">
                        <img v-if="catModel.catImg" :src="catModel.catImg" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary"
                        @click="title === 'Add Cat' ? addCat() : title === 'Feature' ? featureCat() : updateCat()">
                        {{ title === 'Add Cat' ? 'Add' : title === 'Feature' ? 'Feature' : 'Update' }}
                    </el-button>
                    <el-button @click="visibleDrawer = false">Cancel</el-button>
                </el-form-item>
            </el-form>
        </el-drawer>

        <!-- 特征识别数据弹窗 -->
        <el-dialog title="feature" v-model="featureDialogVisible" width="1000">
            <div class="dialog_con">
                <p>相似度大于50%的小猫信息</p>
                <el-table :data="featureData" height="250" style="width: 100%">
                    <el-table-column prop="name" label="name" width="180" />
                    <el-table-column prop="genderText" label="gender" width="180" />
                    <el-table-column label="Category" prop="categoryName"></el-table-column>
                    <el-table-column label="Image">
                        <template #default="{ row }">
                            <el-image v-if="row.catImg" :src="row.catImg" style="width: 60px; height: 60px"
                                :preview-src-list="[row.catImg]" preview-teleported hide-on-click-modal />
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="featureDialogVisible = false">Cancel</el-button>
                    <el-button type="primary" @click="featureDialogVisible = false">
                        Confirm
                    </el-button>
                </div>
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
</style>