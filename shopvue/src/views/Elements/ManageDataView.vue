<template>
  <div>
    <el-row class="header" type="flex" justify="space-between">
      <span>商店名：{{ shopName }}</span>
      <el-dropdown class="manage-dropdown" @command="handleCommand">
        <span class="el-dropdown-link">
          管理 <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="good"> 管理商品</el-dropdown-item>
          <el-dropdown-item command="order">管理订单</el-dropdown-item>
          <el-dropdown-item command="logout">注销</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-row>
    <el-card>
      <h3>销售统计报表</h3>
      <el-form :inline="true" :model="dateRange">
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="dateRange.createTimeLow"
            type="datetime"
            placeholder="选择开始时间"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="dateRange.createTimeHigh"
            type="datetime"
            placeholder="选择结束时间"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchSalesData">筛选</el-button>
        </el-form-item>
      </el-form>
      <p>总销售额: {{ salesData.saleMoney }}元</p>
      <p>订单成功完成数: {{ salesData.orderSaleNum }}次</p>
    <p>退款率: {{ (salesData.returnRate * 100).toFixed(2) }}%</p>
      <p>平均每个订单销售额: {{ salesData.avgSaleMoney }}元</p>
      <p>购买客户数:{{salesData.TotalCustomers}}人</p>
      <h4>本店商品销量排行榜</h4>
      <el-table :data="salesData.goodsBySale">
        <el-table-column prop="goodName" label="商品名称" />
        <el-table-column prop="buyNum" label="购买数量" />
      </el-table>
    </el-card>

    <el-card>
      <h3>客户浏览日志记录</h3>
      <el-table :data="userLogData.userLogView">
        <el-table-column prop="goodName" label="商品名称" />
        <el-table-column prop="goodView" label="浏览次数" />
      </el-table>
      <h4>客户加入购物车记录</h4>
      <el-table :data="userLogData.userLogCart">
        <el-table-column prop="goodName" label="商品名称" />
        <el-table-column prop="goodView" label="加入购物车次数" />
      </el-table>
      <el-table :data="userLogData.userLogCartdelete">
        <el-table-column prop="goodName" label="商品名称" />
        <el-table-column prop="goodView" label="删除购物车次数" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      salesData: {},
      userLogData: {},
      dateRange: {
        createTimeLow: "",
        createTimeHigh: "",
      },
      userId: null,
      shopName: "",
    };
  },
  mounted() {
    this.initialPage();
  },
  methods: {
    async initialPage() {
      await this.getUserName();
      await this.getShopName();
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
          console.log(response.data.data);
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
        this.fetchSalesData();
        this.fetchUserLogData();  
      } catch (error) {
        console.error("未能获取用户信息", error);
        this.shopName = null; // 如果获取失败，显示“请登录”
        this.shopId = null;
      }
    },
    fetchSalesData() {
      const token = localStorage.getItem("jwt"); // 从本地存储中获取JWT
      const { createTimeLow, createTimeHigh } = this.dateRange;
      const params = {
        shopId: 1,
   createTimeLow: createTimeLow ? new Date(createTimeLow).toISOString() : null,
    createTimeHigh: createTimeHigh ? new Date(createTimeHigh).toISOString() : null,
      };
      axios
        .get("http://localhost:8080/shopdata/getsale", { params ,headers:{token:token}})
        .then((response) => {
          if (response.data.code === 1) {
            this.salesData = response.data.data;
          }
        });
    },
    fetchUserLogData() {
      const token = localStorage.getItem("jwt"); // 从本地存储中获取JWT
      axios
        .get("http://localhost:8080/userlog/get?shopId=1",{headers:{token:token}})
        .then((response) => {
          if (response.data.code === 1) {
            this.userLogData = response.data.data;
          }
        });
    },
    handleCommand(command) {
      if (command === "order") {
        this.goToManageOrder();
      } else if (command === "good") {
        this.goToManageGood();
      } else if (command === "logout") {
        this.logout();
      }
    },
    goToManageOrder() {
      this.$router.push("/manageOrder");
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
