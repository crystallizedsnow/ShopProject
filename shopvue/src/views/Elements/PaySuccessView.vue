<template>
  <div>
    <h2>付款成功！</h2>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<script>
export default {
  methods: {
    goHome() {
      this.$router.push("/display");
    },
  },
};
</script>
