<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script setup>
const url = ref('/threebase/leader-dashboard')

function goto() {
  window.open(url.value, '_self')
}
</script>
