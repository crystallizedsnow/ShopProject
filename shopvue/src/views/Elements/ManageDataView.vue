<template>
  <div>
    <!-- 顶部栏 -->
    <el-row class="header" type="flex" justify="space-between" align="middle">
      <span class="shop-name">商店名：{{ shopName }}</span>
      <el-dropdown class="manage-dropdown" @command="handleCommand">
        <span class="el-dropdown-link">
          菜单 <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu class="manage-dropdown-menu" slot="dropdown">
          <el-dropdown-item command="data"> 管理商店数据</el-dropdown-item>
          <el-dropdown-item command="order">管理订单</el-dropdown-item>
          <el-dropdown-item command="logout">注销</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-row>

    <!-- 销售统计报表 -->
    <el-card class="data-table">
      <h2 class="report-title">销售统计报表</h2>
      <el-form :inline="true"  :model="dateRange">
        <el-form-item >
          <span slot="label"
            ><span style="font-size: 18px">开始时间</span>
          </span>
          <el-date-picker
            v-model="dateRange.createTimeLow"
            type="datetime"
            placeholder="选择开始时间"
          />
        </el-form-item>
        <el-form-item
          label-style="font-size: 16px;"
        >
        <span slot="label"
            ><span style="font-size: 18px">开始时间</span>
          </span>
          <el-date-picker
            v-model="dateRange.createTimeHigh"
            type="datetime"
            placeholder="选择结束时间"
          />
        </el-form-item>
        <el-form-item>
          <el-button style="font-size: 15px" type="primary" @click="fetchSalesData">筛选</el-button>
        </el-form-item>
      </el-form>
      <p>总销售额: {{ salesData.saleMoney }}元</p>
      <p>订单成功完成数: {{ salesData.orderSaleNum }}次</p>
      <p>退款率: {{ (salesData.returnRate * 100).toFixed(2) }}%</p>
      <p>平均每个订单销售额: {{ salesData.avgSaleMoney.toFixed(2) }}元</p>
      <p>购买客户数: {{ salesData.TotalCustomers }}人</p>
    </el-card>
    <el-card>
      <h4>本店商品销量排行榜</h4>
      <el-table
        :data="salesData.goodsBySale"
        class="custom-table"
        style="font-size: 18px"
      >
        <el-table-column prop="goodName" label="商品名称" />
        <el-table-column prop="buyNum" label="购买数量" />
      </el-table>
    </el-card>

    <!-- 客户浏览日志记录 -->
    <el-card>
      <h4>客户浏览日志记录</h4>
      <el-table
        :data="userLogData.userLogView"
        class="custom-table"
        style="font-size: 18px"
      >
        <el-table-column prop="goodName" label="商品名称" />
        <el-table-column prop="goodView" label="浏览次数" />
      </el-table>
    </el-card>
    <el-card>
      <h4>客户加入购物车记录</h4>
      <el-table
        :data="userLogData.userLogCart"
        class="custom-table"
        style="font-size: 18px"
      >
        <el-table-column prop="goodName" label="商品名称" />
        <el-table-column prop="goodView" label="加入购物车次数" />
      </el-table>

      <el-table
        :data="userLogData.userLogCartdelete"
        class="custom-table"
        style="font-size: 18px"
      >
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
        createTimeLow: createTimeLow
          ? new Date(createTimeLow).toISOString()
          : null,
        createTimeHigh: createTimeHigh
          ? new Date(createTimeHigh).toISOString()
          : null,
      };
      axios
        .get("http://localhost:8080/shopdata/getsale", {
          params,
          headers: { token: token },
        })
        .then((response) => {
          if (response.data.code === 1) {
            this.salesData = response.data.data;
          }
        });
    },
    fetchUserLogData() {
      const token = localStorage.getItem("jwt"); // 从本地存储中获取JWT
      axios
        .get("http://localhost:8080/userlog/get?shopId=1", {
          headers: { token: token },
        })
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

.report-title {
  font-weight: bold;
}

.custom-table th,
.custom-table td {
  font-size: 18px; /* 表格字体大小 */
}

.custom-table {
  margin-bottom: 20px; /* 增加表格之间的间距 */
}

.el-card {
  margin-top: 40px;
  margin-bottom: 40px; /* 增加卡片之间的明显界限 */
}
.custom-table .el-table {
  table-layout: auto; /* 表格宽度根据内容动态调整 */
  width: auto; /* 让表格宽度自适应 */
}
p {
  font-size: 18px;
}
h4 {
  font-size: 20px;
}
</style>
