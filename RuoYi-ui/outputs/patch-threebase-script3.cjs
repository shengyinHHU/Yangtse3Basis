const fs = require('fs');
const p = 'src/views/threebase/index.vue';
let text = fs.readFileSync(p, 'utf8');
text = text.replace(/  Menu,\r?\n  Plus,/, '  Menu,\n  Plus,\n  Search,');
text = text.replace(/import \{\r?\n  getThreebaseDashboard,\r?\n  listThreebaseFeatures,\r?\n  listThreebaseIntegrations,\r?\n  listThreebaseModules\r?\n\} from '@\/api\/threebase'/, `import {
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
} from '@/api/threebase'`);
text = text.replace(/const activeFeatures = ref\(\[\]\)\r?\nconst apiAvailable = ref\(false\)/, `const activeFeatures = ref([])
const activeFeatureCode = ref('')
const records = ref([])
const recordLoading = ref(false)
const apiAvailable = ref(false)
const query = ref({ keyword: '', status: '' })
const recordDialog = ref({ visible: false, title: '新增记录' })
const recordForm = ref(defaultRecordForm())`);
text = text.replace(/const activeModule = computed\(\(\) => \{\r?\n  return modules\.value\.find\(item => item\.code === activeCode\.value\) \|\| \{\}\r?\n\}\)/, `const activeModule = computed(() => {
  return modules.value.find(item => item.code === activeCode.value) || {}
})

const activeFeature = computed(() => {
  return activeFeatures.value.find(item => item.code === activeFeatureCode.value) || {}
})`);
text = text.replace(/  \{ name: '集成状态', method: 'GET', path: '\/system\/threebase\/integrations', mapping: '集成状态列表' \}\r?\n\]/, `  { name: '集成状态', method: 'GET', path: '/system/threebase/integrations', mapping: '集成状态列表' },
  { name: '业务记录', method: 'GET/POST/PUT/DELETE', path: '/system/threebase/records', mapping: '功能办理区' },
  { name: '流程动作', method: 'POST', path: '/system/threebase/records/{id}/action/{action}', mapping: '提交、确认、归档' }
]`);
const helpers = `
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
`;
text = text.replace(/function unwrapList\(response, key\) \{[\s\S]*?\n\}\r?\n\r?\nasync function loadAll/, (m) => m.replace(/\r?\n\r?\nasync function loadAll/, `\n${helpers}\nasync function loadAll`));
text = text.replace(/  await loadFeatures\(activeCode\.value\)\r?\n\}/, '  await loadFeatures(activeCode.value)\n  await loadTodos()\n}');
text = text.replace(/  if \(apiAvailable\.value\) \{\r?\n    try \{\r?\n      const response = await listThreebaseFeatures\(code\)\r?\n      const remoteFeatures = unwrapList\(response, 'features'\)\r?\n      activeFeatures\.value = remoteFeatures\.length \? remoteFeatures : \(moduleItem\?\.features \|\| \[\]\)\r?\n      return\r?\n    \} catch \(error\) \{\r?\n      activeFeatures\.value = moduleItem\?\.features \|\| \[\]\r?\n      return\r?\n    \}\r?\n  \}\r?\n  activeFeatures\.value = moduleItem\?\.features \|\| \[\]\r?\n\}/, `  if (apiAvailable.value) {
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
}`);
const more = `
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
  loadRecords()
}

async function loadRecords() {
  if (!activeCode.value || !activeFeatureCode.value) {
    records.value = []
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
`;
text = text.replace(/function selectModule\(code\) \{\r?\n  activeCode\.value = code\r?\n  router\.replace\(`\/threebase\/\$\{code\}`\)\r?\n\}\r?\n/, (m) => m + more);
text = text.replace(/function statusLabel\(status\) \{\r?\n  return status === 'placeholder' \? '功能占位' : '已接入'\r?\n\}/, `function statusLabel(status) {
  return status === 'implemented' ? '已实现' : '功能占位'
}`);
fs.writeFileSync(p, text, 'utf8');
