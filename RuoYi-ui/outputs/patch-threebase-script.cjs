const fs = require('fs');
const p = 'src/views/threebase/index.vue';
let text = fs.readFileSync(p, 'utf8');
text = text.replace(`  List,\n  Menu,\n  Plus,`, `  List,\n  Menu,\n  Plus,\n  Search,`);
text = text.replace(`  getThreebaseDashboard,\n  listThreebaseFeatures,\n  listThreebaseIntegrations,\n  listThreebaseModules\n} from '@/api/threebase'`, `  actionThreebaseRecord,\n  addThreebaseRecord,\n  deleteThreebaseRecord,\n  getThreebaseDashboard,\n  listThreebaseFeatures,\n  listThreebaseIntegrations,\n  listThreebaseModules,\n  listThreebaseRecords,\n  listThreebaseTodos,\n  updateThreebaseRecord\n} from '@/api/threebase'`);
text = text.replace(`const activeFeatures = ref([])\nconst apiAvailable = ref(false)`, `const activeFeatures = ref([])\nconst activeFeatureCode = ref('')\nconst records = ref([])\nconst recordLoading = ref(false)\nconst apiAvailable = ref(false)\nconst query = ref({ keyword: '', status: '' })\nconst recordDialog = ref({ visible: false, title: '新增记录' })\nconst recordForm = ref(defaultRecordForm())`);
text = text.replace(`const activeModule = computed(() => {\n  return modules.value.find(item => item.code === activeCode.value) || {}\n})`, `const activeModule = computed(() => {\n  return modules.value.find(item => item.code === activeCode.value) || {}\n})\n\nconst activeFeature = computed(() => {\n  return activeFeatures.value.find(item => item.code === activeFeatureCode.value) || {}\n})`);
text = text.replace(`  { name: '集成状态', method: 'GET', path: '/system/threebase/integrations', mapping: '集成状态列表' }\n]`, `  { name: '集成状态', method: 'GET', path: '/system/threebase/integrations', mapping: '集成状态列表' },\n  { name: '业务记录', method: 'GET/POST/PUT/DELETE', path: '/system/threebase/records', mapping: '功能办理区' },\n  { name: '流程动作', method: 'POST', path: '/system/threebase/records/{id}/action/{action}', mapping: '提交、确认、归档' }\n]`);
const insertAfterUnwrap = String.raw`

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
}`;
text = text.replace(`function unwrapList(response, key) {\n  if (Array.isArray(response)) return response\n  if (Array.isArray(response?.data)) return response.data\n  if (Array.isArray(response?.rows)) return response.rows\n  if (Array.isArray(response?.data?.[key])) return response.data[key]\n  if (Array.isArray(response?.[key])) return response[key]\n  return []\n}\n`, `function unwrapList(response, key) {\n  if (Array.isArray(response)) return response\n  if (Array.isArray(response?.data)) return response.data\n  if (Array.isArray(response?.rows)) return response.rows\n  if (Array.isArray(response?.data?.[key])) return response.data[key]\n  if (Array.isArray(response?.[key])) return response[key]\n  return []\n}\n${insertAfterUnwrap}\n`);
text = text.replace(`  await loadFeatures(activeCode.value)\n}`, `  await loadFeatures(activeCode.value)\n  await loadTodos()\n}`);
text = text.replace(`  if (apiAvailable.value) {\n    try {\n      const response = await listThreebaseFeatures(code)\n      const remoteFeatures = unwrapList(response, 'features')\n      activeFeatures.value = remoteFeatures.length ? remoteFeatures : (moduleItem?.features || [])\n      return\n    } catch (error) {\n      activeFeatures.value = moduleItem?.features || []\n      return\n    }\n  }\n  activeFeatures.value = moduleItem?.features || []\n}`, `  if (apiAvailable.value) {\n    try {\n      const response = await listThreebaseFeatures(code)\n      const remoteFeatures = unwrapList(response, 'features')\n      activeFeatures.value = remoteFeatures.length ? remoteFeatures : (moduleItem?.features || [])\n    } catch (error) {\n      activeFeatures.value = moduleItem?.features || []\n    }\n  } else {\n    activeFeatures.value = moduleItem?.features || []\n  }\n  activeFeatureCode.value = activeFeatures.value[0]?.code || ''\n  await loadRecords()\n}`);
const functions = String.raw`

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
}`;
text = text.replace(`function selectModule(code) {\n  activeCode.value = code\n  router.replace(\`/threebase/${code}\`)\n}\n`, `function selectModule(code) {\n  activeCode.value = code\n  router.replace(\`/threebase/${code}\`)\n}\n${functions}\n`);
text = text.replace(`function statusLabel(status) {\n  return status === 'placeholder' ? '功能占位' : '已接入'\n}\n`, `function statusLabel(status) {\n  return status === 'implemented' ? '已实现' : '功能占位'\n}\n`);
fs.writeFileSync(p, text, 'utf8');

