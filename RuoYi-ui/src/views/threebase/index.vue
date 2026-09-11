<template>
  <div class="threebase-page">
    <section class="workbench-head">
      <div class="head-copy">
        <span class="system-kicker">Yangzi Three-Base MIS</span>
        <h1>体系三基融合工作平台</h1>
      </div>
      <el-radio-group v-model="viewMode" size="large" class="view-switch">
        <el-radio-button label="module">模块视图</el-radio-button>
        <el-radio-button label="interface">接口视图</el-radio-button>
        <el-radio-button label="todo">待办视图</el-radio-button>
      </el-radio-group>
    </section>

    <section class="platform-shell">
      <aside class="module-rail">
        <div class="rail-title">
          <el-icon><Menu /></el-icon>
          <span>模块导航</span>
        </div>
        <div v-for="group in navigationGroups" :key="group.key" class="nav-group">
          <button class="nav-group-head" type="button" @click="toggleNavGroup(group.key)">
            <span class="group-title">{{ group.name }}</span>
            <el-icon class="group-arrow" :class="{ collapsed: collapsedGroups[group.key] }"><ArrowDown /></el-icon>
          </button>
          <div v-show="!collapsedGroups[group.key]" class="group-modules">
            <div v-for="item in group.items" :key="item.code" class="module-entry">
              <button
                class="module-item"
                :class="{ active: item.code === activeCode }"
                type="button"
                @click="selectModule(item.code)"
              >
                <span class="module-name">{{ item.name }}</span>
              </button>
              <div v-if="item.code === activeCode" class="rail-feature-list">
                <button
                  v-for="feature in activeFeatures"
                  :key="feature.code"
                  class="rail-feature-item"
                  :class="{ active: feature.code === activeFeatureCode }"
                  type="button"
                  @click.stop="selectFeature(feature)"
                >
                  <span class="rail-feature-name">{{ feature.name }}</span>
                </button>
                <span v-if="activeFeatures.length === 0" class="rail-feature-empty">该模块暂无功能</span>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <main class="module-board">
        <div class="module-banner">
          <div class="banner-copy">
            <p class="eyebrow">{{ activeModule.category || '业务模块' }}</p>
            <h2>{{ activeModule.name || '模块总览' }}</h2>
            <p>{{ activeModule.summary || '请选择左侧模块查看业务功能。' }}</p>
          </div>
          <div class="module-metrics">
            <div>
              <span>办理入口</span>
              <strong>{{ activeFeatures.length }}</strong>
            </div>
            <div>
              <span>业务状态</span>
              <strong>{{ statusLabel(activeModule.status) }}</strong>
            </div>
          </div>
          <div class="owner-box">
            <span>责任部门</span>
            <strong>{{ activeModule.owner || '待配置' }}</strong>
          </div>
        </div>

        <div v-if="viewMode === 'module'" class="content-grid">
          <section class="panel operation-panel">
            <div class="panel-head compact">
              <div>
                <h3>{{ activeFeature.name || '功能办理区' }}</h3>
                <p>{{ activeFeature.description || '请选择左侧功能点查看业务数据。' }}</p>
              </div>
              <el-tag type="success" effect="plain">接口已接入</el-tag>
            </div>

            <template v-if="isSystemUserFeature">
              <div class="record-toolbar user-toolbar">
                <el-input v-model="systemUserQuery.userName" clearable placeholder="请输入用户名称" @keyup.enter="loadSystemUsers" />
                <el-input v-model="systemUserQuery.phonenumber" clearable placeholder="请输入手机号码" @keyup.enter="loadSystemUsers" />
                <el-select v-model="systemUserQuery.status" clearable placeholder="用户状态" style="width: 130px">
                  <el-option label="正常" value="0" />
                  <el-option label="停用" value="1" />
                </el-select>
                <el-button type="primary" :icon="Search" @click="handleSystemUserQuery">查询</el-button>
                <el-button @click="resetSystemUserQuery">重置</el-button>
                <el-button :icon="Plus" @click="openSystemUserDialog()">新增</el-button>
              </div>

              <el-table v-loading="systemUserLoading" :data="systemUsers" border class="record-table" empty-text="暂无用户数据">
                <el-table-column prop="userId" label="用户编号" width="100" align="center" />
                <el-table-column prop="userName" label="用户名称" min-width="150" show-overflow-tooltip />
                <el-table-column prop="nickName" label="用户昵称" min-width="150" show-overflow-tooltip />
                <el-table-column prop="dept.deptName" label="部门" min-width="150" show-overflow-tooltip />
                <el-table-column prop="phonenumber" label="手机号码" width="130" />
                <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
                <el-table-column prop="status" label="状态" width="100" align="center">
                  <template #default="scope">
                    <el-switch
                      v-model="scope.row.status"
                      active-value="0"
                      inactive-value="1"
                      :disabled="scope.row.userId === 1"
                      @change="handleSystemUserStatusChange(scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="170">
                  <template #default="scope">
                    <span>{{ formatDateTime(scope.row.createTime) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="190" fixed="right">
                  <template #default="scope">
                    <el-button link type="primary" @click="openSystemUserDialog(scope.row)">编辑</el-button>
                    <el-button link type="primary" @click="resetSystemUserPwd(scope.row)" :disabled="scope.row.userId === 1">重置密码</el-button>
                    <el-button link type="danger" @click="removeSystemUser(scope.row)" :disabled="scope.row.userId === 1">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>

              <pagination
                v-show="systemUserTotal > 0"
                :total="systemUserTotal"
                v-model:page="systemUserQuery.pageNum"
                v-model:limit="systemUserQuery.pageSize"
                @pagination="loadSystemUsers"
              />
            </template>

            <template v-else-if="isSystemRoleFeature">
              <div class="record-toolbar role-toolbar">
                <el-input v-model="systemRoleQuery.roleName" clearable placeholder="请输入角色名称" @keyup.enter="loadSystemRoles" />
                <el-input v-model="systemRoleQuery.roleKey" clearable placeholder="请输入权限字符" @keyup.enter="loadSystemRoles" />
                <el-select v-model="systemRoleQuery.status" clearable placeholder="角色状态" style="width: 130px">
                  <el-option label="正常" value="0" />
                  <el-option label="停用" value="1" />
                </el-select>
                <el-button type="primary" :icon="Search" @click="handleSystemRoleQuery">查询</el-button>
                <el-button @click="resetSystemRoleQuery">重置</el-button>
                <el-button :icon="Plus" @click="openSystemRoleDialog()">新增</el-button>
              </div>

              <el-table v-loading="systemRoleLoading" :data="systemRoles" border class="record-table" empty-text="暂无角色数据">
                <el-table-column prop="roleId" label="角色编号" width="100" align="center" />
                <el-table-column prop="roleName" label="角色名称" min-width="160" show-overflow-tooltip />
                <el-table-column prop="roleKey" label="权限字符" min-width="160" show-overflow-tooltip />
                <el-table-column prop="roleSort" label="显示顺序" width="100" align="center" />
                <el-table-column prop="status" label="状态" width="100" align="center">
                  <template #default="scope">
                    <el-switch
                      v-model="scope.row.status"
                      active-value="0"
                      inactive-value="1"
                      :disabled="scope.row.roleId === 1"
                      @change="handleSystemRoleStatusChange(scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="170">
                  <template #default="scope">
                    <span>{{ formatDateTime(scope.row.createTime) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150" fixed="right">
                  <template #default="scope">
                    <el-button link type="primary" @click="openSystemRoleDialog(scope.row)">编辑</el-button>
                    <el-button link type="danger" @click="removeSystemRole(scope.row)" :disabled="scope.row.roleId === 1">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>

              <pagination
                v-show="systemRoleTotal > 0"
                :total="systemRoleTotal"
                v-model:page="systemRoleQuery.pageNum"
                v-model:limit="systemRoleQuery.pageSize"
                @pagination="loadSystemRoles"
              />
            </template>

            <template v-else>
            <div class="record-toolbar">
              <el-input v-model="query.keyword" clearable placeholder="搜索标题、责任人、部门" @keyup.enter="loadRecords" />
              <el-select v-model="query.status" clearable placeholder="状态" style="width: 130px">
                <el-option label="草稿" value="draft" />
                <el-option label="已提交" value="submitted" />
                <el-option label="已确认" value="confirmed" />
                <el-option label="已归档" value="archived" />
              </el-select>
              <el-button type="primary" :icon="Search" @click="loadRecords">查询</el-button>
              <el-button :icon="Plus" @click="openRecordDialog()">新增</el-button>
            </div>

            <el-table v-loading="recordLoading" :data="records" border class="record-table" empty-text="暂无业务记录">
              <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
              <el-table-column prop="responsibleDept" label="责任部门" width="140" show-overflow-tooltip />
              <el-table-column prop="owner" label="责任人" width="100" />
              <el-table-column prop="planTime" label="计划时间" width="110" />
              <el-table-column prop="status" label="状态" width="96">
                <template #default="scope">
                  <el-tag :type="recordStatusType(scope.row.status)" effect="plain">{{ recordStatusText(scope.row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="workflowNode" label="流程节点" width="110" />
              <el-table-column label="操作" width="250" fixed="right">
                <template #default="scope">
                  <el-button link type="primary" @click="openRecordDialog(scope.row)">编辑</el-button>
                  <el-button link type="primary" @click="runRecordAction(scope.row, 'submit')">提交</el-button>
                  <el-button link type="success" @click="runRecordAction(scope.row, 'confirm')">确认</el-button>
                  <el-button link type="warning" @click="runRecordAction(scope.row, 'archive')">归档</el-button>
                  <el-button link type="danger" @click="removeRecord(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="process-preview">
              <div class="process-step done">发起</div>
              <div class="process-line"></div>
              <div class="process-step">部门确认</div>
              <div class="process-line"></div>
              <div class="process-step">归档</div>
            </div>
            </template>
          </section>
        </div>

        <div v-else-if="viewMode === 'interface'" class="panel">
          <div class="panel-head">
            <div>
              <h3>前后端接口联通</h3>
              <p>前端通过统一请求封装访问后端，开发环境由 Vite 代理到 8080。</p>
            </div>
            <el-tag :type="apiState.type">{{ apiState.text }}</el-tag>
          </div>
          <el-table :data="apiRows" border>
            <el-table-column prop="name" label="数据对象" width="180" />
            <el-table-column prop="method" label="方法" width="90" />
            <el-table-column prop="path" label="后端接口" />
            <el-table-column prop="mapping" label="页面对应区域" />
          </el-table>
        </div>

        <div v-else class="todo-layout">
          <section class="panel">
            <div class="panel-head compact">
              <h3>待办入口</h3>
              <el-tag type="success" effect="plain">待办理</el-tag>
            </div>
            <el-timeline>
              <el-timeline-item
                v-for="task in todoRows"
                :key="task.title"
                :timestamp="task.time"
                placement="top"
              >
                <strong>{{ task.title }}</strong>
                <p>{{ task.desc }}</p>
              </el-timeline-item>
            </el-timeline>
          </section>
          <section class="panel">
            <div class="panel-head compact">
              <h3>集成状态</h3>
              <el-tag effect="plain">{{ integrations.length }} 个系统</el-tag>
            </div>
            <div class="integration-list">
              <article v-for="item in integrations" :key="item.code">
                <span>{{ item.name }}</span>
                <el-tag :type="item.status === 'connected' ? 'success' : 'info'" effect="plain">
                  {{ item.status === 'connected' ? '已联通' : '待联调' }}
                </el-tag>
              </article>
            </div>
          </section>
        </div>
      </main>
    </section>

    <el-dialog v-model="recordDialog.visible" :title="recordDialog.title" width="620px" append-to-body>
      <el-form :model="recordForm" label-width="92px">
        <el-form-item label="所属功能">
          <el-input :model-value="activeFeature.name" disabled />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="recordForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="责任部门">
          <el-input v-model="recordForm.responsibleDept" placeholder="请输入责任部门" />
        </el-form-item>
        <el-form-item label="责任人">
          <el-input v-model="recordForm.owner" placeholder="请输入责任人" />
        </el-form-item>
        <el-form-item label="计划时间">
          <el-date-picker v-model="recordForm.planTime" value-format="YYYY-MM-DD" type="date" placeholder="请选择计划时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="recordForm.status" style="width: 100%">
            <el-option label="草稿" value="draft" />
            <el-option label="已提交" value="submitted" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已归档" value="archived" />
          </el-select>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="recordForm.description" type="textarea" :rows="3" placeholder="请输入说明" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="recordForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveRecord">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="systemUserDialog.visible" :title="systemUserDialog.title" width="640px" append-to-body>
      <el-form ref="systemUserFormRef" :model="systemUserForm" :rules="systemUserRules" label-width="92px">
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="用户名称" prop="userName">
              <el-input v-model="systemUserForm.userName" :disabled="Boolean(systemUserForm.userId)" maxlength="30" placeholder="请输入用户名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="systemUserForm.nickName" maxlength="30" placeholder="请输入用户昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="systemUserForm.phonenumber" maxlength="11" placeholder="请输入手机号码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="systemUserForm.email" maxlength="50" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item v-if="!systemUserForm.userId" label="初始密码" prop="password">
              <el-input v-model="systemUserForm.password" type="password" show-password maxlength="20" placeholder="请输入初始密码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="systemUserForm.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="systemUserForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="systemUserDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveSystemUser">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="systemRoleDialog.visible" :title="systemRoleDialog.title" width="680px" append-to-body>
      <el-form ref="systemRoleFormRef" :model="systemRoleForm" :rules="systemRoleRules" label-width="92px">
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="systemRoleForm.roleName" maxlength="30" placeholder="请输入角色名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限字符" prop="roleKey">
              <el-input v-model="systemRoleForm.roleKey" maxlength="100" placeholder="请输入权限字符" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="显示顺序" prop="roleSort">
              <el-input-number v-model="systemRoleForm.roleSort" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="systemRoleForm.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="菜单权限">
          <div class="permission-tree-toolbar">
            <el-checkbox v-model="systemRoleMenuExpand" @change="handleSystemRoleTreeExpand">展开/折叠</el-checkbox>
            <el-checkbox v-model="systemRoleMenuNodeAll" @change="handleSystemRoleTreeCheckAll">全选/全不选</el-checkbox>
            <el-checkbox v-model="systemRoleForm.menuCheckStrictly">父子联动</el-checkbox>
          </div>
          <el-tree
            ref="systemRoleMenuRef"
            class="permission-tree"
            :data="systemRoleMenuOptions"
            show-checkbox
            node-key="id"
            :check-strictly="!systemRoleForm.menuCheckStrictly"
            empty-text="加载中，请稍候"
            :props="{ label: 'label', children: 'children' }"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="systemRoleForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="systemRoleDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveSystemRole">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from '@/utils/auth'
import {
  ArrowDown,
  Calendar,
  Connection,
  DataAnalysis,
  Document,
  Download,
  Finished,
  Grid,
  List,
  Menu,
  Plus,
  Search,
  Tickets,
  Upload,
  View,
  Warning
} from '@element-plus/icons-vue'
import {
  actionThreebaseRecord,
  addThreebaseRecord,
  deleteThreebaseRecord,
  getThreebaseDashboard,
  listThreebaseFeatures,
  listThreebaseIntegrations,
  listThreebaseModules,
  listThreebaseRecords,
  listThreebaseTodos,
  updateThreebaseRecord
} from '@/api/threebase'
import { addUser, changeUserStatus, delUser, getUser, listUser, resetUserPwd, updateUser } from '@/api/system/user'
import { addRole, changeRoleStatus, delRole, getRole, listRole, updateRole } from '@/api/system/role'
import { roleMenuTreeselect, treeselect as menuTreeselect } from '@/api/system/menu'
import fallbackData from './data/modules.json'

const route = useRoute()
const router = useRouter()

const viewMode = ref('module')
const modules = ref([])
const dashboard = ref({})
const integrations = ref([])
const activeCode = ref(route.params.code || 'leader-dashboard')
const activeFeatures = ref([])
const activeFeatureCode = ref('')
const records = ref([])
const recordLoading = ref(false)
const systemUsers = ref([])
const systemUserTotal = ref(0)
const systemUserLoading = ref(false)
const systemUserFormRef = ref()
const systemRoles = ref([])
const systemRoleTotal = ref(0)
const systemRoleLoading = ref(false)
const systemRoleFormRef = ref()
const systemRoleMenuRef = ref()
const systemRoleMenuOptions = ref([])
const systemRoleMenuExpand = ref(false)
const systemRoleMenuNodeAll = ref(false)
const apiAvailable = ref(false)
const query = ref({ keyword: '', status: '' })
const systemUserQuery = ref({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  phonenumber: '',
  status: ''
})
const systemUserDialog = ref({ visible: false, title: '新增用户' })
const systemUserForm = ref(defaultSystemUserForm())
const systemRoleQuery = ref({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleKey: '',
  status: ''
})
const systemRoleDialog = ref({ visible: false, title: '新增角色' })
const systemRoleForm = ref(defaultSystemRoleForm())
const systemUserRules = {
  userName: [
    { required: true, message: '用户名称不能为空', trigger: 'blur' },
    { min: 2, max: 30, message: '用户名称长度应为 2 到 30 个字符', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '用户昵称不能为空', trigger: 'blur' },
    { max: 30, message: '用户昵称不能超过 30 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '初始密码不能为空', trigger: 'blur' },
    { min: 5, max: 20, message: '密码长度应为 5 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phonenumber: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}
const systemRoleRules = {
  roleName: [
    { required: true, message: '角色名称不能为空', trigger: 'blur' },
    { max: 30, message: '角色名称不能超过 30 个字符', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '权限字符不能为空', trigger: 'blur' },
    { max: 100, message: '权限字符不能超过 100 个字符', trigger: 'blur' }
  ],
  roleSort: [
    { required: true, message: '显示顺序不能为空', trigger: 'blur' }
  ]
}
const recordDialog = ref({ visible: false, title: '新增记录' })
const recordForm = ref(defaultRecordForm())
const collapsedGroups = reactive({})

const defaultIntegrations = [
  { code: 'sso', name: '统一身份认证', status: 'reserved' },
  { code: 'org', name: '组织人员同步', status: 'reserved' },
  { code: 'bpm', name: '流程引擎', status: 'reserved' },
  { code: 'message', name: '消息平台', status: 'reserved' },
  { code: 'file', name: '附件服务', status: 'reserved' },
  { code: 'data', name: '数据中台', status: 'reserved' }
]

const navigationGroupConfigs = [
  { key: 'post', name: '基层岗位', codes: ['user-permission', 'post-workbench', 'team-workbench'] },
  { key: 'construction', name: '基层建设', codes: ['policy-catalog', 'work-standard', 'special-task'] },
  { key: 'work', name: '基层工作', codes: ['leader-dashboard', 'operator-dashboard', 'work-task', 'report-ledger', 'calendar-message', 'assistant-tools', 'others'] },
  { key: 'assessment', name: '考评', codes: ['assessment', 'inspection-ledger'] }
]

const activeModule = computed(() => {
  return modules.value.find(item => item.code === activeCode.value) || {}
})

const navigationGroups = computed(() => {
  const usedCodes = new Set()
  const groups = navigationGroupConfigs.map(group => {
    const items = group.codes
      .map(code => modules.value.find(module => module.code === code))
      .filter(Boolean)
    items.forEach(item => usedCodes.add(item.code))
    return { ...group, items }
  }).filter(group => group.items.length > 0)

  const uncategorized = modules.value.filter(module => !usedCodes.has(module.code))
  if (uncategorized.length > 0) {
    groups.push({ key: 'uncategorized', name: '其他', codes: uncategorized.map(item => item.code), items: uncategorized })
  }
  return groups
})

const activeFeature = computed(() => {
  return activeFeatures.value.find(item => item.code === activeFeatureCode.value) || {}
})

const isSystemUserFeature = computed(() => activeFeatureCode.value === 'user-permission-user')
const isSystemRoleFeature = computed(() => activeFeatureCode.value === 'user-permission-role')

const apiState = computed(() => {
  return apiAvailable.value
    ? { type: 'success', text: '后端接口已响应' }
    : { type: 'warning', text: '使用前端兜底数据' }
})

const apiRows = [
  { name: '模块清单', method: 'GET', path: '/system/threebase/modules', mapping: '左侧模块导航、模块主体区' },
  { name: '办理项数据', method: 'GET', path: '/system/threebase/modules/{code}/features', mapping: '办理区标签与数据' },
  { name: '驾驶舱统计', method: 'GET', path: '/system/threebase/dashboard', mapping: '模块总览数据' },
  { name: '集成状态', method: 'GET', path: '/system/threebase/integrations', mapping: '集成状态列表' },
  { name: '业务记录', method: 'GET/POST/PUT/DELETE', path: '/system/threebase/records', mapping: '功能办理区' },
  { name: '流程动作', method: 'POST', path: '/system/threebase/records/{id}/action/{action}', mapping: '提交、确认、归档' }
]

const todoRows = reactive([
  { title: '模块字段确认', time: '本阶段', desc: '根据模块办理项确认字段、枚举和附件要求。' },
  { title: '接口联调', time: '下一阶段', desc: '按模块接入查询、新增、编辑、删除和流程动作接口。' },
  { title: '流程配置', time: '下一阶段', desc: '接入审批流、催办消息和归档规则。' }
])

function featureTotal(item) {
  return Array.isArray(item.features) ? item.features.length : Number(item.featureCount || 0)
}

function toggleNavGroup(key) {
  collapsedGroups[key] = !collapsedGroups[key]
}

function unwrapList(response, key) {
  if (Array.isArray(response)) return response
  if (Array.isArray(response?.data)) return response.data
  if (Array.isArray(response?.rows)) return response.rows
  if (Array.isArray(response?.data?.[key])) return response.data[key]
  if (Array.isArray(response?.[key])) return response[key]
  return []
}

function unwrapRows(response) {
  if (Array.isArray(response?.rows)) return { rows: response.rows, total: Number(response.total || response.rows.length) }
  if (Array.isArray(response?.data?.rows)) return { rows: response.data.rows, total: Number(response.data.total || response.data.rows.length) }
  if (Array.isArray(response?.data)) return { rows: response.data, total: response.data.length }
  return { rows: [], total: 0 }
}

function defaultRecordForm() {
  return {
    id: undefined,
    moduleCode: '',
    moduleName: '',
    featureCode: '',
    featureName: '',
    title: '',
    responsibleDept: '',
    owner: '',
    planTime: '',
    status: 'draft',
    description: '',
    remark: ''
  }
}

function defaultSystemUserForm() {
  return {
    userId: undefined,
    deptId: undefined,
    userName: '',
    nickName: '',
    password: '123456',
    phonenumber: '',
    email: '',
    sex: '2',
    status: '0',
    postIds: [],
    roleIds: [],
    remark: ''
  }
}

function defaultSystemRoleForm() {
  return {
    roleId: undefined,
    roleName: '',
    roleKey: '',
    roleSort: 0,
    status: '0',
    menuIds: [],
    menuCheckStrictly: true,
    deptCheckStrictly: true,
    remark: ''
  }
}

async function loadAll() {
  if (!getToken()) {
    modules.value = fallbackData.modules
    dashboard.value = {
      moduleCount: fallbackData.modules.length,
      featureCount: fallbackData.modules.reduce((sum, item) => sum + featureTotal(item), 0),
      integrationCount: defaultIntegrations.length
    }
    integrations.value = defaultIntegrations
    apiAvailable.value = false
  } else {
    try {
      const [moduleRes, dashboardRes, integrationRes] = await Promise.all([
        listThreebaseModules(),
        getThreebaseDashboard(),
        listThreebaseIntegrations()
      ])
      const remoteModules = unwrapList(moduleRes, 'modules')
      modules.value = remoteModules.length ? remoteModules : fallbackData.modules
      dashboard.value = dashboardRes?.data || dashboardRes || {}
      integrations.value = unwrapList(integrationRes, 'integrations')
      if (integrations.value.length === 0) integrations.value = defaultIntegrations
      apiAvailable.value = remoteModules.length > 0
    } catch (error) {
      modules.value = fallbackData.modules
      dashboard.value = {
        moduleCount: fallbackData.modules.length,
        featureCount: fallbackData.modules.reduce((sum, item) => sum + featureTotal(item), 0),
        integrationCount: defaultIntegrations.length
      }
      integrations.value = defaultIntegrations
      apiAvailable.value = false
    }
  }

  if (!modules.value.some(item => item.code === activeCode.value)) {
    activeCode.value = modules.value[0]?.code || ''
  }
  await loadFeatures(activeCode.value)
  await loadTodos()
}

async function loadFeatures(code) {
  const moduleItem = modules.value.find(item => item.code === code)
  if (!code) {
    activeFeatures.value = []
    return
  }

  if (apiAvailable.value) {
    try {
      const response = await listThreebaseFeatures(code)
      const remoteFeatures = unwrapList(response, 'features')
      activeFeatures.value = remoteFeatures.length ? remoteFeatures : (moduleItem?.features || [])
    } catch (error) {
      activeFeatures.value = moduleItem?.features || []
    }
  } else {
    activeFeatures.value = moduleItem?.features || []
  }
  activeFeatureCode.value = activeFeatures.value[0]?.code || ''
  await loadRecords()
}

async function selectModule(code) {
  if (!code || code === activeCode.value) return
  activeCode.value = code
  query.value.keyword = ''
  query.value.status = ''
  await loadFeatures(code)
  router.replace(`/threebase/${code}`)
}

async function loadTodos() {
  if (!apiAvailable.value) return
  try {
    const response = await listThreebaseTodos()
    const remoteTodos = unwrapList(response, 'rows')
    if (remoteTodos.length) {
      todoRows.splice(0, todoRows.length, ...remoteTodos)
    }
  } catch (error) {
    // 保留本地待办兜底数据
  }
}

function selectFeature(feature) {
  activeFeatureCode.value = feature.code
  query.value.keyword = ''
  query.value.status = ''
  systemUserQuery.value.pageNum = 1
  systemRoleQuery.value.pageNum = 1
  loadRecords()
}

async function loadRecords() {
  if (!activeCode.value || !activeFeatureCode.value) {
    records.value = []
    return
  }
  if (isSystemUserFeature.value) {
    await loadSystemUsers()
    return
  }
  if (isSystemRoleFeature.value) {
    await loadSystemRoles()
    return
  }
  recordLoading.value = true
  try {
    const response = await listThreebaseRecords({
      moduleCode: activeCode.value,
      featureCode: activeFeatureCode.value,
      keyword: query.value.keyword,
      status: query.value.status,
      pageNum: 1,
      pageSize: 50
    })
    records.value = unwrapRows(response).rows
  } catch (error) {
    records.value = []
    ElMessage.warning('业务记录接口暂不可用，请确认后端服务已重启。')
  } finally {
    recordLoading.value = false
  }
}

async function loadSystemUsers() {
  systemUserLoading.value = true
  try {
    const response = await listUser({
      pageNum: systemUserQuery.value.pageNum,
      pageSize: systemUserQuery.value.pageSize,
      userName: systemUserQuery.value.userName || undefined,
      phonenumber: systemUserQuery.value.phonenumber || undefined,
      status: systemUserQuery.value.status || undefined
    })
    systemUsers.value = response.rows || []
    systemUserTotal.value = Number(response.total || 0)
  } catch (error) {
    systemUsers.value = []
    systemUserTotal.value = 0
    ElMessage.warning('用户管理接口暂不可用，请确认后端服务与权限配置。')
  } finally {
    systemUserLoading.value = false
  }
}

function handleSystemUserQuery() {
  systemUserQuery.value.pageNum = 1
  loadSystemUsers()
}

function resetSystemUserQuery() {
  systemUserQuery.value = {
    pageNum: 1,
    pageSize: systemUserQuery.value.pageSize,
    userName: '',
    phonenumber: '',
    status: ''
  }
  loadSystemUsers()
}

function handleSystemUserStatusChange(row) {
  const nextText = row.status === '0' ? '启用' : '停用'
  ElMessageBox.confirm(`确认要${nextText}用户“${row.userName}”吗？`, '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return changeUserStatus(row.userId, row.status)
  }).then(() => {
    ElMessage.success(`${nextText}成功`)
  }).catch(() => {
    row.status = row.status === '0' ? '1' : '0'
  })
}

async function openSystemUserDialog(row) {
  systemUserForm.value = defaultSystemUserForm()
  systemUserFormRef.value?.clearValidate?.()
  if (!row) {
    systemUserDialog.value = { visible: true, title: '新增用户' }
    return
  }
  try {
    const response = await getUser(row.userId)
    systemUserForm.value = {
      ...defaultSystemUserForm(),
      ...(response.data || row),
      password: '',
      postIds: response.postIds || [],
      roleIds: response.roleIds || []
    }
    systemUserDialog.value = { visible: true, title: '编辑用户' }
  } catch (error) {
    ElMessage.warning('用户详情接口暂不可用。')
  }
}

async function saveSystemUser() {
  if (!systemUserFormRef.value) return
  await systemUserFormRef.value.validate(async valid => {
    if (!valid) return
    const payload = { ...systemUserForm.value }
    if (payload.userId) {
      delete payload.password
      await updateUser(payload)
      ElMessage.success('用户已更新')
    } else {
      await addUser(payload)
      ElMessage.success('用户已新增')
    }
    systemUserDialog.value.visible = false
    await loadSystemUsers()
  })
}

function removeSystemUser(row) {
  ElMessageBox.confirm(`确认删除用户“${row.userName}”吗？`, '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delUser(row.userId)
  }).then(() => {
    ElMessage.success('用户已删除')
    loadSystemUsers()
  }).catch(() => {})
}

function resetSystemUserPwd(row) {
  ElMessageBox.prompt(`请输入用户“${row.userName}”的新密码`, '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue: '123456',
    inputPattern: /^.{5,20}$/,
    inputErrorMessage: '密码长度应为 5 到 20 个字符'
  }).then(({ value }) => {
    return resetUserPwd(row.userId, value)
  }).then(() => {
    ElMessage.success('密码已重置')
  }).catch(() => {})
}

async function loadSystemRoles() {
  systemRoleLoading.value = true
  try {
    const response = await listRole({
      pageNum: systemRoleQuery.value.pageNum,
      pageSize: systemRoleQuery.value.pageSize,
      roleName: systemRoleQuery.value.roleName || undefined,
      roleKey: systemRoleQuery.value.roleKey || undefined,
      status: systemRoleQuery.value.status || undefined
    })
    systemRoles.value = response.rows || []
    systemRoleTotal.value = Number(response.total || 0)
  } catch (error) {
    systemRoles.value = []
    systemRoleTotal.value = 0
    ElMessage.warning('岗位角色管理接口暂不可用，请确认后端服务与权限配置。')
  } finally {
    systemRoleLoading.value = false
  }
}

function handleSystemRoleQuery() {
  systemRoleQuery.value.pageNum = 1
  loadSystemRoles()
}

function resetSystemRoleQuery() {
  systemRoleQuery.value = {
    pageNum: 1,
    pageSize: systemRoleQuery.value.pageSize,
    roleName: '',
    roleKey: '',
    status: ''
  }
  loadSystemRoles()
}

function handleSystemRoleStatusChange(row) {
  const nextText = row.status === '0' ? '启用' : '停用'
  ElMessageBox.confirm(`确认要${nextText}角色“${row.roleName}”吗？`, '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return changeRoleStatus(row.roleId, row.status)
  }).then(() => {
    ElMessage.success(`${nextText}成功`)
  }).catch(() => {
    row.status = row.status === '0' ? '1' : '0'
  })
}

async function openSystemRoleDialog(row) {
  systemRoleForm.value = defaultSystemRoleForm()
  systemRoleMenuExpand.value = false
  systemRoleMenuNodeAll.value = false
  systemRoleFormRef.value?.clearValidate?.()
  try {
    const menuResponse = row ? await roleMenuTreeselect(row.roleId) : await menuTreeselect()
    systemRoleMenuOptions.value = menuResponse.menus || menuResponse.data || []
    if (row) {
      const response = await getRole(row.roleId)
      systemRoleForm.value = {
        ...defaultSystemRoleForm(),
        ...(response.data || row),
        roleSort: Number((response.data || row).roleSort || 0)
      }
      systemRoleDialog.value = { visible: true, title: '编辑角色' }
      setTimeout(() => {
        const checkedKeys = menuResponse.checkedKeys || []
        checkedKeys.forEach(key => systemRoleMenuRef.value?.setChecked?.(key, true, false))
      }, 0)
      return
    }
    systemRoleDialog.value = { visible: true, title: '新增角色' }
  } catch (error) {
    ElMessage.warning('角色权限数据暂不可用，请确认菜单接口权限。')
  }
}

function getSystemRoleMenuAllCheckedKeys() {
  const checkedKeys = systemRoleMenuRef.value?.getCheckedKeys?.() || []
  const halfCheckedKeys = systemRoleMenuRef.value?.getHalfCheckedKeys?.() || []
  return [...checkedKeys, ...halfCheckedKeys]
}

async function saveSystemRole() {
  if (!systemRoleFormRef.value) return
  const valid = await systemRoleFormRef.value.validate()
  if (!valid) return
  const payload = {
    ...systemRoleForm.value,
    menuIds: getSystemRoleMenuAllCheckedKeys()
  }
  if (payload.roleId) {
    await updateRole(payload)
    ElMessage.success('角色已更新')
  } else {
    await addRole(payload)
    ElMessage.success('角色已新增')
  }
  systemRoleDialog.value.visible = false
  await loadSystemRoles()
}

function removeSystemRole(row) {
  ElMessageBox.confirm(`确认删除角色“${row.roleName}”吗？`, '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delRole(row.roleId)
  }).then(() => {
    ElMessage.success('角色已删除')
    loadSystemRoles()
  }).catch(() => {})
}

function handleSystemRoleTreeExpand(value) {
  setRoleTreeExpanded(systemRoleMenuOptions.value, value)
}

function setRoleTreeExpanded(nodes, value) {
  nodes.forEach(node => {
    const treeNode = systemRoleMenuRef.value?.store?.nodesMap?.[node.id]
    if (treeNode) treeNode.expanded = value
    if (node.children) setRoleTreeExpanded(node.children, value)
  })
}

function handleSystemRoleTreeCheckAll(value) {
  systemRoleMenuRef.value?.setCheckedKeys?.(value ? collectTreeIds(systemRoleMenuOptions.value) : [])
}

function collectTreeIds(nodes) {
  return nodes.flatMap(node => [
    node.id,
    ...(node.children ? collectTreeIds(node.children) : [])
  ])
}

function formatDateTime(value) {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 19)
}

function openRecordDialog(row) {
  const feature = activeFeature.value
  recordDialog.value = { visible: true, title: row ? '编辑记录' : '新增记录' }
  recordForm.value = {
    ...defaultRecordForm(),
    moduleCode: activeCode.value,
    moduleName: activeModule.value.name,
    featureCode: feature.code,
    featureName: feature.name,
    responsibleDept: activeModule.value.owner || '',
    description: feature.description || '',
    ...(row || {})
  }
}

async function saveRecord() {
  if (!recordForm.value.title) {
    ElMessage.warning('请输入标题')
    return
  }
  if (recordForm.value.id) {
    await updateThreebaseRecord(recordForm.value.id, recordForm.value)
    ElMessage.success('记录已更新')
  } else {
    await addThreebaseRecord(recordForm.value)
    ElMessage.success('记录已新增')
  }
  recordDialog.value.visible = false
  await loadRecords()
}

async function removeRecord(row) {
  await deleteThreebaseRecord(row.id)
  ElMessage.success('记录已删除')
  await loadRecords()
}

async function runRecordAction(row, action) {
  await actionThreebaseRecord(row.id, action)
  ElMessage.success('流程已' + actionLabel(action))
  await loadRecords()
}

function actionLabel(action) {
  const labels = { submit: '提交', confirm: '确认', archive: '归档', return: '退回' }
  return labels[action] || '处理'
}

function recordStatusText(status) {
  const labels = { draft: '草稿', submitted: '已提交', confirmed: '已确认', archived: '已归档', processing: '处理中' }
  return labels[status] || '草稿'
}

function recordStatusType(status) {
  const types = { draft: 'info', submitted: 'primary', confirmed: 'success', archived: 'warning', processing: 'primary' }
  return types[status] || 'info'
}


function featureIcon(type) {
  const icons = {
    analysis: DataAnalysis,
    archive: Document,
    assessment: Finished,
    calendar: Calendar,
    dashboard: DataAnalysis,
    document: Document,
    inspection: Warning,
    integration: Connection,
    matrix: Grid,
    permission: Grid,
    report: DataAnalysis,
    search: Search,
    task: Tickets,
    warning: Warning,
    workflow: Connection
  }
  return icons[type] || List
}

function typeLabel(type) {
  const labels = {
    analysis: '分析',
    archive: '归档',
    assessment: '评价',
    calendar: '日程',
    catalog: '目录',
    certificate: '证书',
    config: '配置',
    dashboard: '看板',
    document: '文档',
    event: '活动',
    export: '导出',
    form: '表单',
    inspection: '检查',
    integration: '集成',
    ledger: '台账',
    matrix: '矩阵',
    message: '消息',
    monitor: '监控',
    permission: '权限',
    plan: '计划',
    progress: '进度',
    project: '项目',
    quality: '质量',
    record: '记录',
    rectify: '整改',
    report: '报表',
    search: '查询',
    standard: '标准',
    task: '任务',
    template: '模板',
    training: '培训',
    warning: '预警',
    workflow: '流程'
  }
  return labels[type] || '功能'
}

function statusLabel(status) {
  return status === 'implemented' ? '已接入' : '待联调'
}

watch(() => route.params.code, async code => {
  if (code && code !== activeCode.value) {
    activeCode.value = code
    await loadFeatures(code)
  }
})

onMounted(loadAll)
</script>

<style lang="scss" scoped>
.threebase-page {
  min-height: 100vh;
  padding: 0;
  color: #182230;
  background:
    linear-gradient(135deg, rgba(230, 244, 241, 0.78) 0%, rgba(245, 247, 250, 0.92) 44%, rgba(255, 251, 235, 0.55) 100%);
  font-family: "Inter", "DIN Alternate", "Microsoft YaHei", "PingFang SC", Arial, sans-serif;
}

.workbench-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  min-height: 88px;
  padding: 18px 28px 16px;
  color: #0f172a;
  background: rgba(255, 255, 255, 0.86);
  border-bottom: 1px solid rgba(148, 163, 184, 0.28);
  backdrop-filter: blur(16px);

  :deep(.el-radio-button__inner) {
    height: 36px;
    padding: 0 18px;
    color: #475569;
    font-weight: 700;
    line-height: 36px;
    background: transparent;
    border-color: #d6dee8;
    box-shadow: none;
  }

  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    color: #ffffff;
    background: #0f766e;
    border-color: #0f766e;
  }

  h1 {
    margin: 0;
    font-size: 28px;
    line-height: 1.25;
    font-weight: 800;
    letter-spacing: 0;
  }
}

.head-copy {
  display: grid;
  gap: 5px;
}

.system-kicker {
  color: #64748b;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0;
  text-transform: uppercase;
}

.view-switch {
  flex-shrink: 0;
}

.eyebrow {
  margin: 0;
  color: #0f766e;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0;
  text-transform: uppercase;
}

.platform-shell {
  display: grid;
  grid-template-columns: 330px minmax(0, 1fr);
  min-height: calc(100vh - 88px);
  margin-top: 0;
  background: transparent;
  border: 0;
  border-radius: 0;
  overflow: hidden;
}

.module-rail {
  padding: 16px 14px;
  background: #0f1f2e;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

.rail-title {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 42px;
  padding: 0 10px;
  color: #d9f4ef;
  font-size: 13px;
  font-weight: 800;
}

.nav-group {
  margin-top: 8px;
}

.nav-group-head {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 18px;
  align-items: center;
  gap: 8px;
  width: 100%;
  min-height: 40px;
  padding: 0 12px;
  color: #cbd5e1;
  text-align: left;
  background: rgba(255, 255, 255, 0.045);
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 8px;
  cursor: pointer;
  transition: background-color .16s ease, border-color .16s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.075);
    border-color: rgba(94, 234, 212, 0.22);
  }
}

.group-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 800;
}

.group-arrow {
  transition: transform .18s ease;
}

.group-arrow.collapsed {
  transform: rotate(-90deg);
}

.group-modules {
  display: grid;
  gap: 3px;
  padding: 6px 0 6px 11px;
  border-left: 1px solid rgba(94, 234, 212, 0.18);
}

.module-entry {
  min-width: 0;
}

.module-item {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  align-items: center;
  gap: 8px;
  width: 100%;
  min-height: 36px;
  padding: 0 10px 0 13px;
  color: #9fb0c3;
  text-align: left;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: color .16s ease, background-color .16s ease, transform .16s ease;

  &:hover {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.06);
  }

  &.active {
    color: #ffffff;
    background: linear-gradient(135deg, #0f766e 0%, #155e75 100%);
    border-color: rgba(94, 234, 212, 0.34);
    box-shadow: 0 10px 24px rgba(8, 47, 73, 0.28);
    transform: translateX(2px);
  }
}

.module-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 700;
}

.rail-feature-list {
  display: grid;
  gap: 8px;
  max-height: 480px;
  margin: 6px 0 10px;
  padding: 0 0 0 11px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(148, 163, 184, 0.48);
    border-radius: 999px;
  }
}

.rail-feature-item {
  display: block;
  width: 100%;
  min-height: 34px;
  padding: 8px 10px;
  color: #cbd5e1;
  text-align: left;
  background: rgba(255, 255, 255, 0.045);
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 8px;
  cursor: pointer;
  transition: color .16s ease, background-color .16s ease, border-color .16s ease;

  &:hover {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.075);
    border-color: rgba(94, 234, 212, 0.3);
  }

  &.active {
    color: #ffffff;
    background: rgba(20, 184, 166, 0.16);
    border-color: rgba(94, 234, 212, 0.62);
    box-shadow: inset 3px 0 0 #2dd4bf;
  }
}

.rail-feature-name {
  display: block;
  overflow: hidden;
  color: #f8fafc;
  font-size: 13px;
  font-weight: 800;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rail-feature-empty {
  padding: 10px;
  color: #8ea4ba;
  font-size: 12px;
}

.module-board {
  min-width: 0;
  padding: 22px 24px 28px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.72) 0%, rgba(248, 250, 252, 0.94) 100%);
}

.module-banner {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  align-items: stretch;
  gap: 14px;
  padding: 22px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(203, 213, 225, 0.74);
  border-radius: 14px;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.08);
  overflow: hidden;

  &::before {
    position: absolute;
    inset: 0 auto 0 0;
    width: 5px;
    content: "";
    background: linear-gradient(180deg, #0f766e, #f59e0b);
  }

  h2 {
    margin: 4px 0 8px;
    color: #0f172a;
    font-size: 28px;
    font-weight: 850;
    letter-spacing: 0;
  }

  p {
    max-width: 820px;
    margin: 0;
    color: #526071;
    font-size: 14px;
    line-height: 1.75;
  }
}

.banner-copy {
  min-width: 0;
}

.module-metrics {
  display: grid;
  grid-template-columns: repeat(2, 104px);
  gap: 10px;

  div {
    display: grid;
    align-content: center;
    gap: 5px;
    min-height: 84px;
    padding: 12px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
  }

  span {
    color: #64748b;
    font-size: 12px;
    font-weight: 700;
  }

  strong {
    color: #0f766e;
    font-size: 20px;
    font-weight: 850;
    line-height: 1.1;
  }
}

.owner-box {
  min-width: 178px;
  padding: 14px 16px;
  background: #10243a;
  border: 1px solid rgba(15, 118, 110, 0.24);
  border-radius: 10px;

  span,
  strong {
    display: block;
  }

  span {
    color: #9fb3c8;
    font-size: 12px;
    font-weight: 700;
  }

  strong {
    margin-top: 6px;
    color: #ffffff;
    font-size: 15px;
    line-height: 1.45;
  }
}

.content-grid,
.todo-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 18px;
  margin-top: 18px;
}

.panel {
  min-width: 0;
  padding: 18px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(203, 213, 225, 0.74);
  border-radius: 14px;
  box-shadow: 0 14px 34px rgba(15, 23, 42, 0.055);
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 12px;

  &.compact {
    align-items: center;
  }

  h3 {
    margin: 0;
    color: #0f172a;
    font-size: 18px;
    font-weight: 850;
    letter-spacing: 0;
  }

  p {
    margin: 6px 0 0;
    color: #64748b;
    font-size: 13px;
    line-height: 1.6;
  }
}

.record-toolbar {
  display: grid;
  grid-template-columns: minmax(210px, 1fr) 138px auto auto;
  gap: 10px;
  margin-bottom: 12px;

  .el-button {
    margin-left: 0;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper) {
    min-height: 36px;
    border-radius: 9px;
    box-shadow: 0 0 0 1px #dbe4ee inset;
  }
}

.role-toolbar {
  grid-template-columns: minmax(170px, 1fr) minmax(170px, 1fr) 130px auto auto auto;
}

.permission-tree-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 14px;
  width: 100%;
  margin-bottom: 8px;
}

.permission-tree {
  width: 100%;
  max-height: 280px;
  padding: 8px 10px;
  overflow-y: auto;
  background: #f8fafc;
  border: 1px solid #dbe4ee;
  border-radius: 9px;
}

.record-table {
  width: 100%;

  :deep(.el-table__header th) {
    color: #334155;
    font-weight: 850;
    background: #f8fafc;
  }

  :deep(.el-table__row:hover > td.el-table__cell) {
    background: #f0fdfa;
  }

  :deep(.el-button.is-link) {
    height: auto;
    min-height: 0;
    padding: 0;
    background: transparent;
    border: 0;
    box-shadow: none;
  }

  :deep(.el-button.is-link:hover),
  :deep(.el-button.is-link:focus),
  :deep(.el-button.is-link:active) {
    background: transparent;
    border: 0;
    box-shadow: none;
    text-decoration: underline;
    text-underline-offset: 3px;
  }
}

.process-preview {
  display: grid;
  grid-template-columns: auto 1fr auto 1fr auto;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
}

.process-step {
  min-width: 76px;
  padding: 7px 12px;
  color: #64748b;
  font-size: 13px;
  font-weight: 800;
  text-align: center;
  background: #ffffff;
  border: 1px solid #dbe4ee;
  border-radius: 999px;

  &.done {
    color: #0f766e;
    background: #ccfbf1;
    border-color: #5eead4;
  }
}

.process-line {
  height: 2px;
  background: linear-gradient(90deg, #5eead4, #cbd5e1);
}

.integration-list {
  display: grid;
  gap: 10px;

  article {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 12px 14px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
  }
}

:deep(.el-button) {
  border-radius: 9px;
  font-weight: 800;
}

:deep(.el-button--primary) {
  background: #0f766e;
  border-color: #0f766e;
}

:deep(.el-button--primary:hover) {
  background: #115e59;
  border-color: #115e59;
}

:deep(.el-tag) {
  font-weight: 700;
  border-radius: 999px;
}

@media (max-width: 1180px) {
  .platform-shell,
  .content-grid,
  .todo-layout {
    grid-template-columns: 1fr;
  }

  .module-rail {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 6px;
  }

  .rail-title {
    grid-column: 1 / -1;
  }

  .module-banner {
    grid-template-columns: 1fr;
  }

  .module-metrics {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .threebase-page {
    padding: 0;
  }

  .workbench-head,
  .module-banner {
    align-items: stretch;
    flex-direction: column;
  }

  .workbench-head {
    display: grid;
  }

  .module-rail,
  .record-toolbar,
  .role-toolbar {
    grid-template-columns: 1fr;
  }

  .feature-row {
    grid-template-columns: 38px minmax(0, 1fr);
  }

  .feature-main {
    padding-right: 0;
  }
}
</style>

