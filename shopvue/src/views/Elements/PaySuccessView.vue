<template>
  <div class="payment-page">
    <el-row class="message-row">
      <h2>付款成功！</h2>
    </el-row>
    <el-row class="button-row">
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </el-row>
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
<style scoped>
.payment-page {
  height: 100vh; /* 设置页面高度等于视口高度 */
  display: flex;
  flex-direction: column; /* 子元素垂直排列 */
  justify-content: center; /* 垂直居中 */
  align-items: center; /* 水平居中 */
}

.message-row {
  margin-bottom: 10px; /* 文字与按钮之间的间距 */
}

.button-row {
  text-align: center; /* 按钮居中 */
}
</style>