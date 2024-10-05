<template>
  <div>
    <h2>付款页面</h2>
    <p>总金额: {{ totalPrice }} 元</p>
    <el-button type="primary" @click="handlePayment">确认付款</el-button>
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
          `http://localhost:8080/order/updateState`,
          null,
          {
            headers: { token: token },
            params: {
              orderId: this.orderId,
              state: 1,
            },
          }
        );
        console.log(response.data.code);
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
