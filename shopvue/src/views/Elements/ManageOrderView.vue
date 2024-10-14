<template>
  <div>
    <el-row class="header" type="flex" justify="space-between">
      <span>商店名：{{ shopName }}</span>
      <el-dropdown class="manage-dropdown" @command="handleCommand">
        <span class="el-dropdown-link">
          管理 <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu class="manage-dropdown-menu" slot="dropdown">
          <el-dropdown-item command="good"> 管理商品</el-dropdown-item>
          <el-dropdown-item command="data">管理商店数据</el-dropdown-item>
          <el-dropdown-item command="logout">注销</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-row>

    <el-menu
      :default-active="selectedState"
      mode="horizontal"
      @select="filterOrders"
    >
      <el-menu-item index="5"><span class="menu-items">全部</span></el-menu-item>
      <el-menu-item index="0"><span class="menu-items">未付款</span></el-menu-item>
      <el-menu-item index="1"><span class="menu-items">已付款</span></el-menu-item>
      <el-menu-item index="2"><span class="menu-items">已发货</span></el-menu-item>
      <el-menu-item index="3"><span class="menu-items">已收货</span></el-menu-item>
      <el-menu-item index="4"><span class="menu-items">已取消</span></el-menu-item>
    </el-menu>

    <div v-for="order in orders" :key="order.orderId" class="order-container">
      <h3>订单号: {{ order.orderId }}</h3>
      <p>客户名：{{ order.userName }}</p>
      <p>收货地址: {{ order.address }}</p>
      <p>总金额: {{ order.totalMoney }} 元</p>
      <p>订单状态: {{ order.stateName }}</p>
      <p>创建时间: {{ new Date(order.createTime).toLocaleString() }}</p>
      <p>明细：</p>

      <el-table :data="order.items" style="margin-bottom: 20px">
        <el-table-column label="商品名" prop="goodName"></el-table-column>
        <el-table-column label="数量" prop="buyNum"></el-table-column>
        <el-table-column label="单价" prop="price"></el-table-column>
        <el-table-column label="总价">
          <template slot-scope="scope">
            {{ (scope.row.buyNum * scope.row.price).toFixed(2) }} 元
          </template>
        </el-table-column>
      </el-table>
      <el-row>
        <el-col :span="24" style="text-align: right">
          <strong
            >订单总金额:
            {{
              order.items
                .reduce((total, item) => total + item.buyNum * item.price, 0)
                .toFixed(2)
            }}
            元</strong
          >
        </el-col>
      </el-row>

      <el-button
        v-if="order.stateName === '未发货'"
        type="danger"
        @click="deliverOrder(order.orderId)"
      >
        发货
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
      shopId: "",
      shopName: "",
    };
  },
  mounted() {
    this.initialInfo();
  },
  methods: {
    async initialInfo() {
      await this.getUserName();
      await this.getShopName();
      this.fetchOrders();
    },
    async getUserName() {
      const token = localStorage.getItem("jwt"); // 从本地存储中获取JWT

      if (!token) {
        this.userName = null; // 如果没有JWT，显示“请登录”
        return;
      }

      await axios
        .get("http://localhost:8080/getUsernameAndId", {
          headers: {
            token: token,
          },
        })
        .then((response) => {
          this.userName = response.data.data.username;
          this.userId = response.data.data.userId;
        })
        .catch((error) => {
          console.error("未能获取用户信息", error);
          this.userName = null; // 如果获取用户名失败，显示“请登录”
        });
    },
    async getShopName() {
      const token = localStorage.getItem("jwt");

      if (!token) {
        this.shopName = null; // 如果没有JWT，显示“请登录”
        return;
      }
      try {
        const response = await axios.get(
          "http://localhost:8080/manageGood/getShop",
          {
            headers: {
              token: token, // 将 token 作为请求头传递
            },
            params: {
              userName: this.userName,
            },
          }
        );
        this.shopName = response.data.data.name || null; // 确保正确赋值
        this.shopId = response.data.data.shopId;
      } catch (error) {
        console.error("未能获取用户信息", error);
        this.shopName = null; // 如果获取失败，显示“请登录”
        this.shopId = null;
      }
    },
    async fetchOrders() {
      const token = localStorage.getItem("jwt");
      try {
        const response = await axios.get(
          `http://localhost:8080/order/getOrderShop?shopId=${this.shopId}`,
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
          `http://localhost:8080/order/getOrderShop?shopId=${this.shopId}&state=${state}`,
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
    async deliverOrder(orderId) {
      const token = localStorage.getItem("jwt");
      try {
        const response = await axios.post(
          `http://localhost:8080/order/updateState`,
          null,
          {
            headers: { token: token },
            params: { orderId: orderId, state: 2 },
          }
        );
        if (response.data.code === 1) {
          this.$message.success("订单已发货！");
          await this.selectOrders(this.selectedState);
        } else {
          this.$message.error("订单发货失败，请重试！");
        }
      } catch (error) {
        console.error(error);
        this.$message.error("订单发货失败，请重试！");
      }
      try {
        const response = await axios.post(
          `http://localhost:8080/order/sendEmail`,
          null,
          {
            headers: { token: token },
            params: { orderId: orderId, shopName: this.shopName },
          }
        );
        if (response.data.code === 1) {
          this.$message.success("发送邮件成功！");
        } else {
          this.$message.error("发送邮件失败，请重试！");
        }
      } catch (error) {
        console.error(error);
        this.$message.error("发送邮件失败，请重试！");
      }
    },
    handleCommand(command) {
      if (command === "data") {
        this.goToManageData();
      } else if (command === "good") {
        this.goToManageGood();
      } else if (command === "logout") {
        this.logout();
      }
    },
    goToManageData() {
      this.$router.push("/manageData");
    },
    goToManageGood() {
      this.$router.push("/manageGood");
    },
    logout() {
      localStorage.removeItem("jwt"); // 删除本地存储中的JWT
      this.userName = null;
      this.$router.push("/login"); // 路由到登录页面
    },
  },
};
</script>

<style scoped>
.header {
  padding: 10px 20px;
  background-color: #f5f5f5;
}
.el-dropdown-link {
  font-size: 18px;
  font-weight: bold;
}
.manage-dropdown-menu {
  font-size: 18px;
  font-weight: bold;
}
.order-container {
  border: 1px solid #e0e0e0;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 5px;
}
p{
  font-size:16px;
}
.menu-items{
  font-size:15px;
}
.el-table {
  padding: 10px;
  font-size: 16px; /* 字体大小调整 */
}
</style>
