$ErrorActionPreference = 'Stop'

function Assert-File {
  param([string]$Path)
  if (-not (Test-Path -LiteralPath $Path)) {
    throw "Missing required file: $Path"
  }
}

function Assert-Contains {
  param(
    [string]$Path,
    [string]$Needle
  )
  $content = Get-Content -LiteralPath $Path -Raw -Encoding UTF8
  if (-not $content.Contains($Needle)) {
    throw "Expected '$Needle' in $Path"
  }
}

function Assert-NotContains {
  param(
    [string]$Path,
    [string]$Needle
  )
  $content = Get-Content -LiteralPath $Path -Raw -Encoding UTF8
  if ($content.Contains($Needle)) {
    throw "Unexpected '$Needle' in $Path"
  }
}
$root = Split-Path -Parent $PSScriptRoot
$router = Join-Path $root 'src/router/index.js'
$permission = Join-Path $root 'src/permission.js'
$api = Join-Path $root 'src/api/threebase.js'
$view = Join-Path $root 'src/views/threebase/index.vue'
$data = Join-Path $root 'src/views/threebase/data/modules.json'
$vite = Join-Path $root 'vite.config.js'
$login = Join-Path $root 'src/views/login.vue'
$homePage = Join-Path $root 'src/views/index.vue'
$variables = Join-Path $root 'src/assets/styles/variables.module.scss'
$sidebarStyle = Join-Path $root 'src/assets/styles/sidebar.scss'
$navbar = Join-Path $root 'src/layout/components/Navbar.vue'
$settings = Join-Path $root 'src/settings.js'
$permissionStore = Join-Path $root 'src/store/modules/permission.js'

Assert-File $router
Assert-File $permission
Assert-File $api
Assert-File $view
Assert-File $data
Assert-File $login
Assert-File $homePage
Assert-File $variables
Assert-File $sidebarStyle
Assert-File $navbar
Assert-File $settings
Assert-File $permissionStore

Assert-Contains $router '/threebase'
Assert-Contains $router "path: '/threebase/:code?'"
Assert-Contains $router "component: () => import('@/views/threebase/index')"
Assert-NotContains $router "path: '/threebase',`n    component: Layout"
Assert-Contains $router ([regex]::Unescape('\u4f53\u7cfb\u4e09\u57fa\u878d\u5408\u5de5\u4f5c\u5e73\u53f0'))
Assert-Contains $permission "const whiteList = ['/login', '/register']"
Assert-Contains $permission '/threebase/leader-dashboard'

Assert-Contains $api '/system/threebase/modules'
Assert-Contains $api '/system/threebase/dashboard'
Assert-Contains $api '/system/threebase/integrations'
$apiText = Get-Content -LiteralPath $api -Raw -Encoding UTF8
if ($apiText -like '*isToken: false*') { throw 'Threebase API must use platform token by default, not isToken: false' }

Assert-Contains $view ([regex]::Unescape('\u4f53\u7cfb\u4e09\u57fa\u878d\u5408\u5de5\u4f5c\u5e73\u53f0'))
Assert-Contains $view 'getToken'
Assert-Contains $view 'listThreebaseModules'
Assert-Contains $view 'listThreebaseFeatures'
Assert-Contains $view 'await loadFeatures(code)'
Assert-Contains $view 'getThreebaseDashboard'
Assert-Contains $view 'listThreebaseIntegrations'
Assert-Contains $view ([regex]::Unescape('\u529f\u80fd\u6e05\u5355'))
Assert-Contains $view ([regex]::Unescape('\u57fa\u5c42\u5c97\u4f4d'))
Assert-Contains $view ([regex]::Unescape('\u57fa\u5c42\u5efa\u8bbe'))
Assert-Contains $view ([regex]::Unescape('\u57fa\u5c42\u5de5\u4f5c'))
Assert-Contains $view ([regex]::Unescape('\u8003\u8bc4'))
Assert-Contains $view 'navigationGroups'
Assert-Contains $view 'toggleNavGroup'
Assert-Contains $view 'const collapsedGroups = reactive({})'
Assert-Contains $vite 'open: false'

Assert-Contains $login ([regex]::Unescape('\u4f53\u7cfb\u4e09\u57fa\u878d\u5408\u5de5\u4f5c\u5e73\u53f0'))
Assert-Contains $login ([regex]::Unescape('\u626c\u5b50\u77f3\u5316'))
Assert-Contains $login 'yangzi-login'
Assert-Contains $login 'login-center'
Assert-Contains $login 'place-items: center'
Assert-Contains $login 'captchaEnabled'
Assert-Contains $login 'getCode'
Assert-Contains $login 'code-row'
Assert-Contains $login ([regex]::Unescape('\u9a8c\u8bc1\u7801'))
Assert-Contains $login '#101826'
Assert-Contains $login '#1f6feb'
Assert-Contains $homePage ([regex]::Unescape('\u4f53\u7cfb\u4e09\u57fa\u878d\u5408\u5de5\u4f5c\u5e73\u53f0'))
Assert-Contains $homePage 'home-dashboard'
Assert-Contains $homePage '/threebase/leader-dashboard'
Assert-Contains $variables '--navbar-bg: #111927'
Assert-Contains $sidebarStyle '#1f6feb'
Assert-Contains $sidebarStyle 'rgba(31, 111, 235, 0.22)'
Assert-Contains $navbar '#dbeafe'
Assert-Contains $permissionStore 'isLegacyFrameworkMenu'
Assert-Contains $permissionStore ([regex]::Unescape('\u82e5\u4f9d\u5b98\u7f51'))
Assert-Contains $permissionStore 'ruoyi.vip'

$loginText = Get-Content -LiteralPath $login -Raw -Encoding UTF8
$loginForbiddenControls = @(
  'rememberMe',
  [regex]::Unescape('\u8bb0\u4f4f\u5bc6\u7801')
)
foreach ($word in $loginForbiddenControls) {
  if ($loginText -like "*$word*") {
    throw ("Login page should only keep account and password controls; found {0}" -f $word)
  }
}

$payload = Get-Content -LiteralPath $data -Raw -Encoding UTF8 | ConvertFrom-Json
if ($payload.modules.Count -lt 12) {
  throw "Expected at least 12 modules, found $($payload.modules.Count)"
}

$featureCount = 0
foreach ($module in $payload.modules) {
  if (-not $module.code -or -not $module.name) {
    throw 'Every module must include code and name'
  }
  $featureCount += @($module.features).Count
}

if ($featureCount -lt 60) {
  throw "Expected at least 60 features, found $featureCount"
}

$visibleFiles = @($login, $homePage, $view, $navbar, $settings, $vite)
$forbidden = @(
  'RuoYi',
  'ruoyi.vip',
  'gitee.com/y_project',
  [regex]::Unescape('\u6e90\u7801\u5730\u5740'),
  [regex]::Unescape('\u6587\u6863\u5730\u5740'),
  [regex]::Unescape('\u540e\u53f0\u7ba1\u7406\u6846\u67b6'),
  [regex]::Unescape('\u514d\u8d39\u5f00\u6e90')
)
foreach ($file in $visibleFiles) {
  $content = Get-Content -LiteralPath $file -Raw -Encoding UTF8
  foreach ($word in $forbidden) {
    if ($content -like "*$word*") {
      throw ("Forbidden visible framework reference {0} in {1}" -f $word, $file)
    }
  }
}

Write-Host "Threebase frontend verification passed: $($payload.modules.Count) modules, $featureCount features."









