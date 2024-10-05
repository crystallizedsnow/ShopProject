<template>
  <div>
    <h2>订单列表</h2>
    <el-row class="header" type="flex" justify="space-between">
      <el-dropdown v-if="userName" @command="handleCommand">
        <span class="el-dropdown-link">
          用户：{{ userName }} ，您好<i
            class="el-icon-arrow-down el-icon--right"
          ></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="display">首页</el-dropdown-item>
          <el-dropdown-item command="logout">注销</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <span v-else @click="goToLogin">请登录</span>
    </el-row>

    <el-menu
      :default-active="selectedState"
      mode="horizontal"
      @select="filterOrders"
    >
      <el-menu-item index="5">全部</el-menu-item>
      <el-menu-item index="0">未付款</el-menu-item>
      <el-menu-item index="1">已付款</el-menu-item>
      <el-menu-item index="2">已发货</el-menu-item>
      <el-menu-item index="3">已收货</el-menu-item>
      <el-menu-item index="4">已取消</el-menu-item>
    </el-menu>

    <div v-for="order in orders" :key="order.orderId" class="order-container">
      <h3>订单号: {{ order.orderId }}</h3>
      <p>收货地址: {{ order.address }}</p>
      <p>总金额: {{ order.totalPrice }} 元</p>
      <p>订单状态: {{ order.stateName }}</p>
      <p>创建时间: {{ new Date(order.createTime).toLocaleString() }}</p>
      <p>明细：</p>

      <el-table :data="order.itemsByshop" style="margin-bottom: 20px">
        <el-table-column prop="shopName" label="商店名称" />
        <el-table-column label="商品信息">
          <template slot-scope="scope">
            <div v-for="item in scope.row.items" :key="item.goodName">
              {{ item.goodName }} {{ item.buyNum }} × {{ item.price }} =
              {{ (item.buyNum * item.price).toFixed(2) }} 元
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-button
        v-if="order.stateName === '未付款'"
        type="danger"
        @click="cancelOrder(order.orderId)"
      >
        取消订单
      </el-button>
      <el-button
        v-if="order.stateName === '未付款'"
        type="success"
        @click="confirmReceipt(order.orderId)"
      >
        确认收货
      </el-button>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      orders: [],
      userId: "",
      userName: "",
      selectedState: "5", // 默认选项为“全部”
    };
  },
  mounted() {
    this.initialInfo();
  },
  methods: {
    async initialInfo() {
      await this.getUserName();
      this.fetchOrders();
    },
    async getUserName() {
      const token = localStorage.getItem("jwt");
      if (!token) {
        this.userName = null;
        this.$message.error("未能获取用户信息，请重新登录");
        this.$router.push("/login");
        return;
      }
      try {
        const response = await axios.get(
          "http://localhost:8080/getUsernameAndId",
          {
            headers: { token: token },
          }
        );
        if (response.data.code) {
          this.userName = response.data.data.username;
          this.userId = response.data.data.userId;
        } else {
          console.error("未能获取用户信息，请重新登录", error);
          this.$message.error("未能获取用户信息，请重新登录");
        }
      } catch (error) {
        console.error("未能获取用户信息，请登录", error);
        this.$message.error("未能获取用户信息，请登录");
        this.userName = null;
      }
    },
    async fetchOrders() {
      const token = localStorage.getItem("jwt");
      try {
        const response = await axios.get(
          `http://localhost:8080/order/getOrderUser?userId=${this.userId}`,
          {
            headers: { token: token },
          }
        );
        if (response.data.code === 1) {
          this.orders = response.data.data;
        } else {
          this.$message.error("获取订单失败，请重试！");
        }
      } catch (error) {
        console.error(error);
        this.$message.error("获取订单失败，请重试！");
      }
    },
    async selectOrders(state) {
      const token = localStorage.getItem("jwt");
      try {
        const response = await axios.get(
          `http://localhost:8080/order/getOrderUser?userId=${this.userId}&state=${state}`,
          {
            headers: { token: token },
          }
        );
        if (response.data.code === 1) {
          this.orders = response.data.data;
        } else {
          this.$message.error("获取订单失败，请重试！");
        }
      } catch (error) {
        console.error(error);
        this.$message.error("获取订单失败，请重试！");
      }
    },
    async filterOrders(selected) {
      this.selectedState = selected; // 更新选中的状态
      if (this.selectedState === "5") {
        this.fetchOrders(); // 调用获取全部订单的方法
      } else {
        await this.selectOrders(this.selectedState); // 根据选中的状态筛选订单
      }
    },
    async cancelOrder(orderId) {
      const token = localStorage.getItem("jwt");
      try {
        const response = await axios.post(
          `http://localhost:8080/order/updateState`,
          null,
          {
            headers: { token: token },
            params: { orderId: orderId, state: 4 },
          }
        );
        if (response.data.code === 1) {
          this.$message.success("订单已取消！");
          this.fetchOrders(); // Refresh the order list
        } else {
          this.$message.error("取消订单失败，请重试！");
        }
      } catch (error) {
        console.error(error);
        this.$message.error("取消订单失败，请重试！");
      }
    },
    async confirmReceipt(orderId) {
      const token = localStorage.getItem("jwt");
      try {
        const response = await axios.post(
          `http://localhost:8080/order/updateState`,
          null,
          {
            headers: { token: token },
            params: {
              orderId: orderId,
              state: 3,
            },
          }
        );

        if (response.data.code === 1) {
          this.$message.success("确认收货成功！");
          this.fetchOrders(); // 刷新订单列表
        } else {
          this.$message.error("确认收货失败，请重试！");
        }
      } catch (error) {
        console.error(error);
        this.$message.error("确认收货失败，请重试！");
      }
      
    },
    handleCommand(command) {
      if (command === "display") {
        this.goToDisplay();
      } else if (command === "logout") {
        this.goToLogin();
      }
    },
    goToLogin() {
      this.$router.push("/login");
    },
    goToDisplay() {
      this.$router.push("/display");
    },
  },
};
</script>

<style scoped>
.order-container {
  border: 1px solid #e0e0e0;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 5px;
}
</style>
