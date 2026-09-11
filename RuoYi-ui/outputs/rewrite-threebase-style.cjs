const fs = require('fs');
const p = 'src/views/threebase/index.vue';
let text = fs.readFileSync(p, 'utf8');
const style = String.raw`<style lang="scss" scoped>
.threebase-page {
  min-height: 100vh;
  padding: 16px;
  color: #172033;
  background: #eef3f8;
}

.workbench-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 18px 24px;
  color: #fff;
  background: #111927;
  border: 1px solid #1e2a3d;
  border-radius: 6px;

  h1 {
    margin: 0;
    font-size: 26px;
    line-height: 1.25;
    font-weight: 700;
  }
}

.eyebrow {
  margin: 0;
  color: #4aa3ff;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.platform-shell {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  min-height: 720px;
  margin-top: 14px;
  background: #fff;
  border: 1px solid #d9e4f2;
  border-radius: 6px;
  overflow: hidden;
}

.module-rail {
  padding: 14px;
  background: #182233;
  border-right: 1px solid #27364f;
}

.rail-title {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 38px;
  color: #dbeafe;
  font-weight: 700;
}

.module-item {
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr) 28px;
  align-items: center;
  gap: 8px;
  width: 100%;
  min-height: 42px;
  margin-top: 6px;
  padding: 0 10px;
  color: #b9c7d9;
  text-align: left;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 6px;
  cursor: pointer;

  &.active {
    color: #fff;
    background: #1f6feb;
    border-color: #4aa3ff;
  }
}

.module-mark {
  color: inherit;
  opacity: .75;
  font-size: 12px;
}

.module-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 700;
}

.module-count {
  text-align: right;
  font-family: Consolas, Monaco, monospace;
}

.module-board {
  padding: 16px;
  background: #f7faff;
}

.module-banner {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 18px;
  background: #fff;
  border: 1px solid #d9e4f2;
  border-left: 4px solid #1f6feb;
  border-radius: 6px;

  h2 {
    margin: 4px 0 8px;
    font-size: 22px;
  }

  p {
    max-width: 820px;
    margin: 0;
    color: #64748b;
    line-height: 1.7;
  }
}

.owner-box {
  min-width: 160px;
  padding: 12px 14px;
  background: #f1f6fc;
  border: 1px solid #d9e4f2;
  border-radius: 6px;

  span,
  strong {
    display: block;
  }

  span {
    color: #64748b;
    font-size: 12px;
  }

  strong {
    margin-top: 6px;
    color: #0f4c9a;
  }
}

.content-grid,
.todo-layout {
  display: grid;
  grid-template-columns: 410px minmax(0, 1fr);
  gap: 14px;
  margin-top: 14px;
}

.panel {
  padding: 16px;
  background: #fff;
  border: 1px solid #d9e4f2;
  border-radius: 6px;
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
    font-size: 16px;
  }

  p {
    margin: 6px 0 0;
    color: #64748b;
    line-height: 1.6;
  }
}

.feature-list {
  display: grid;
  gap: 8px;
  max-height: 560px;
  overflow-y: auto;
  padding-right: 4px;
}

.feature-row {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) auto;
  align-items: center;
  gap: 12px;
  min-height: 58px;
  padding: 10px 12px;
  background: #f8fbff;
  border: 1px solid #e2eaf5;
  border-radius: 6px;
  cursor: pointer;

  &.active {
    background: #edf5ff;
    border-color: #4aa3ff;
    box-shadow: inset 3px 0 0 #1f6feb;
  }
}

.feature-icon {
  display: grid;
  place-items: center;
  width: 38px;
  height: 38px;
  color: #0f4c9a;
  background: #e9f2ff;
  border-radius: 6px;
}

.feature-main {
  min-width: 0;

  strong,
  span {
    display: block;
  }

  strong {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  span {
    margin-top: 4px;
    color: #64748b;
    font-size: 12px;
    line-height: 1.45;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.record-toolbar {
  display: grid;
  grid-template-columns: minmax(180px, 1fr) auto auto auto;
  gap: 10px;
  margin-bottom: 12px;

  .el-button {
    margin-left: 0;
  }
}

.record-table {
  width: 100%;
}

.process-preview {
  display: grid;
  grid-template-columns: auto 1fr auto 1fr auto;
  align-items: center;
  gap: 8px;
  margin-top: 18px;
}

.process-step {
  padding: 7px 10px;
  color: #64748b;
  background: #f1f6fc;
  border: 1px solid #d9e4f2;
  border-radius: 6px;

  &.done {
    color: #0f6b39;
    background: #eaf8ef;
    border-color: #b7e3c6;
  }
}

.process-line {
  height: 1px;
  background: #d9e4f2;
}

.integration-list {
  display: grid;
  gap: 10px;

  article {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 12px;
    background: #f8fbff;
    border: 1px solid #e2eaf5;
    border-radius: 6px;
  }
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
}

@media (max-width: 720px) {
  .threebase-page {
    padding: 10px;
  }

  .workbench-head,
  .module-banner {
    align-items: stretch;
    flex-direction: column;
  }

  .module-rail,
  .record-toolbar {
    grid-template-columns: 1fr;
  }

  .feature-row {
    grid-template-columns: 38px minmax(0, 1fr);

    .el-button {
      grid-column: 1 / -1;
      justify-content: flex-start;
    }
  }
}
</style>`;
text = text.replace(/<style lang="scss" scoped>[\s\S]*?<\/style>\s*$/, style + '\n');
fs.writeFileSync(p, text, 'utf8');
