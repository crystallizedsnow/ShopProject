<template>
  <div class="payment-page">
    <el-card class="payment-card">
    <h2>支付页面</h2>
    <p>总金额: {{ totalPrice }} 元</p>
    <el-button type="primary" @click="handlePayment">确认付款</el-button>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'; 
export default {
  data() {
    return {
      totalPrice: this.$route.query.totalPrice,
      orderId:this.$route.query.orderId,
      paymentSuccess: false,
    };
  },
  methods: {
    async handlePayment() {
      this.paymentSuccess = true;
        const token = localStorage.getItem("jwt");
      try {
        const response = await axios.post(
          `http://8.155.18.88/api/order/updateState`,
          null,
          {
            headers: { token: token },
            params: {
              orderId: this.orderId,
              state: 1,
            },
          }
        );
        if (response.data.code === 1) {
          this.$message.success("支付成功！");
        } else {
          this.$message.error("支付失败！");
        }
      } catch (error) {
        this.$message.error(error);
      }
      this.$router.push("/paymentsuccess");
    },
  },
};
</script>
<style scoped>
.header {
  padding: 10px 20px;
  background-color: #f5f5f5;
}

.header span {
  font-size: 16px;
}
.payment-page {
  height: 100vh; /* 设置页面高度等于视口高度 */
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center;     /* 垂直居中 */
}

.payment-card {
  width: 200px;  /* 卡片宽度 */
  height: 200px; /* 卡片高度 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 阴影效果 */
  padding: 40px;
  display: flex;
  flex-direction: column; /* 子元素排列方向为列 */
  justify-content: center; /* 垂直居中 */
  align-items: center; /* 水平居中 */
}
</style>