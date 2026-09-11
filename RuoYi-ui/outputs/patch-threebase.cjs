const fs = require('fs');
const p = 'src/views/threebase/index.vue';
let text = fs.readFileSync(p, 'utf8');
const newBlock = String.raw`<div v-if="viewMode === 'module'" class="content-grid">
          <section class="panel feature-panel">
            <div class="panel-head">
              <div>
                <h3>功能清单</h3>
                <p>来源于招标功能清单，当前模块共 {{ activeFeatures.length }} 项功能。</p>
              </div>
              <el-tag effect="plain">{{ activeFeatures.length }} 项</el-tag>
            </div>
            <div class="feature-list">
              <article
                v-for="feature in activeFeatures"
                :key="feature.code"
                class="feature-row"
                :class="{ active: feature.code === activeFeatureCode }"
                @click="selectFeature(feature)"
              >
                <div class="feature-icon">
                  <el-icon><component :is="featureIcon(feature.type)" /></el-icon>
                </div>
                <div class="feature-main">
                  <strong>{{ feature.name }}</strong>
                  <span>{{ typeLabel(feature.type) }} / {{ feature.description }}</span>
                </div>
                <el-button link type="primary" :icon="Connection" @click.stop="selectFeature(feature)">办理</el-button>
              </article>
              <el-empty v-if="activeFeatures.length === 0" description="该模块暂无功能" />
            </div>
          </section>

          <section class="panel operation-panel">
            <div class="panel-head compact">
              <div>
                <h3>{{ activeFeature.name || '功能办理区' }}</h3>
                <p>{{ activeFeature.description || '请选择左侧功能点查看业务数据。' }}</p>
              </div>
              <el-tag type="success" effect="plain">接口已接入</el-tag>
            </div>

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
          </section>
        </div>`;
text = text.replace(/<div v-if="viewMode === 'module'" class="content-grid">[\s\S]*?\n\s*<div v-else-if="viewMode === 'interface'"/, `${newBlock}\n\n        <div v-else-if="viewMode === 'interface'"`);
const dialog = String.raw`

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
    </el-dialog>`;
text = text.replace(/\n  <\/div>\n<\/template>/, `${dialog}\n  </div>\n</template>`);
fs.writeFileSync(p, text, 'utf8');
