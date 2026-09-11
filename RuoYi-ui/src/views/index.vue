<template>
  <div class="home-dashboard">
    <section class="home-hero">
      <div>
        <p class="eyebrow">Yangzi Petrochemical IMS</p>
        <h1>体系三基融合工作平台</h1>
        <p>面向管理体系、基层建设、基础管理和基本功训练的统一工作入口，聚合模块、任务、风险和数据看板。</p>
      </div>
      <el-button type="primary" :icon="Monitor" @click="goWorkbench">进入平台工作台</el-button>
    </section>

    <section class="metric-grid">
      <article v-for="item in metrics" :key="item.label" class="metric-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.desc }}</small>
      </article>
    </section>

    <section class="home-main">
      <article class="panel module-panel">
        <div class="panel-head">
          <div>
            <h2>模块外围框架</h2>
            <p>当前阶段先建立页面、入口和数据对应关系，后续逐步接入具体业务功能。</p>
          </div>
          <el-tag effect="plain">15 个模块</el-tag>
        </div>
        <div class="module-grid">
          <button v-for="item in moduleCards" :key="item.code" type="button" @click="openModule(item.code)">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.name }}</span>
            <small>{{ item.desc }}</small>
          </button>
        </div>
      </article>

      <article class="panel status-panel">
        <div class="panel-head compact">
          <h2>联通状态</h2>
          <el-tag type="success" effect="plain">前端已配置</el-tag>
        </div>
        <div class="status-list">
          <div v-for="item in statusRows" :key="item.name">
            <span>{{ item.name }}</span>
            <strong>{{ item.status }}</strong>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup name="Index">
import { DataAnalysis, Grid, Monitor, Operation, Tickets, Warning } from '@element-plus/icons-vue'

const router = useRouter()

const metrics = [
  { label: '平台模块', value: '15', desc: '覆盖体系、三基、协同、评价、支撑' },
  { label: '功能占位', value: '94', desc: '保留入口与字段映射，不实现业务逻辑' },
  { label: '接口对象', value: '4', desc: '模块、功能、驾驶舱、集成状态' },
  { label: '视觉风格', value: '统一', desc: '深色导航、蓝色选中、浅色业务区' }
]

const moduleCards = [
  { code: 'leader-dashboard', name: '领导驾驶舱', desc: '综合态势、重点任务、预警排名', icon: DataAnalysis },
  { code: 'system-management', name: '体系管理', desc: '体系文件、流程、职责和审核', icon: Grid },
  { code: 'threebase-plan', name: '三基计划', desc: '计划编制、任务分解、节点推进', icon: Tickets },
  { code: 'grassroots-building', name: '基层建设', desc: '组织、班组、岗位和现场标准化', icon: Operation },
  { code: 'risk-control', name: '风险隐患', desc: '风险分级、隐患排查、整改闭环', icon: Warning },
  { code: 'report-analysis', name: '数据报表', desc: '统计报表、趋势分析、穿透查询', icon: Monitor }
]

const statusRows = [
  { name: '前端路由', status: '/threebase 已挂载' },
  { name: '开发代理', status: '/dev-api -> 8080' },
  { name: '登录态接口', status: '自动携带 token' },
  { name: '未登录预览', status: '使用本地框架数据' }
]

function goWorkbench() {
  router.push('/threebase/leader-dashboard')
}

function openModule(code) {
  router.push(`/threebase/${code}`)
}
</script>

<style scoped lang="scss">
.home-dashboard {
  min-height: calc(100vh - 84px);
  padding: 16px;
  color: #172033;
  background: #eef3f8;
}

.home-hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  padding: 24px;
  color: #fff;
  background: #111927;
  border: 1px solid #25344f;
  border-radius: 6px;

  h1 {
    margin: 6px 0 10px;
    font-size: 28px;
    line-height: 1.25;
  }

  p:last-child {
    max-width: 760px;
    margin: 0;
    color: #b8c7dc;
    line-height: 1.7;
  }
}

.eyebrow {
  margin: 0;
  color: #4aa3ff;
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin: 14px 0;
}

.metric-card,
.panel {
  background: #fff;
  border: 1px solid #d9e4f2;
  border-radius: 6px;
}

.metric-card {
  min-height: 96px;
  padding: 16px;

  span,
  small {
    display: block;
    color: #64748b;
  }

  strong {
    display: block;
    margin: 8px 0 4px;
    color: #0f4c9a;
    font-size: 30px;
    line-height: 1;
  }
}

.home-main {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 14px;
}

.panel {
  padding: 16px;
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;

  &.compact {
    align-items: center;
  }

  h2 {
    margin: 0;
    font-size: 18px;
  }

  p {
    margin: 6px 0 0;
    color: #64748b;
  }
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;

  button {
    min-height: 126px;
    padding: 14px;
    color: #172033;
    text-align: left;
    background: #f8fbff;
    border: 1px solid #e2eaf5;
    border-radius: 6px;
    cursor: pointer;

    &:hover {
      border-color: #1f6feb;
      box-shadow: 0 8px 20px rgba(31, 111, 235, 0.12);
    }
  }

  .el-icon {
    display: grid;
    place-items: center;
    width: 36px;
    height: 36px;
    margin-bottom: 12px;
    color: #0f4c9a;
    background: #e9f2ff;
    border-radius: 6px;
  }

  span,
  small {
    display: block;
  }

  span {
    font-weight: 700;
  }

  small {
    margin-top: 6px;
    color: #64748b;
    line-height: 1.6;
  }
}

.status-list {
  display: grid;
  gap: 10px;

  div {
    padding: 12px;
    background: #f8fbff;
    border: 1px solid #e2eaf5;
    border-radius: 6px;
  }

  span,
  strong {
    display: block;
  }

  span {
    color: #64748b;
    font-size: 12px;
  }

  strong {
    margin-top: 5px;
    color: #172033;
  }
}

@media (max-width: 1180px) {
  .metric-grid,
  .module-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .home-main {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .home-dashboard {
    padding: 10px;
  }

  .home-hero {
    align-items: stretch;
    flex-direction: column;
  }

  .metric-grid,
  .module-grid {
    grid-template-columns: 1fr;
  }
}
</style>