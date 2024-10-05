<template>
  <div class="shopping-page">
    <!-- 顶部导航栏，显示用户信息和购物车 -->
    <el-row class="header" type="flex" justify="space-between">
      <el-dropdown v-if="userName" @command="handleCommand">
        <span class="el-dropdown-link">
          用户：{{ userName }} ，您好<i
            class="el-icon-arrow-down el-icon--right"
          ></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="order">历史订单</el-dropdown-item>
          <el-dropdown-item command="logout">注销</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <span v-else @click="goToLogin">请登录</span>
      <span @click="goToCart">购物车</span>
    </el-row>

    <!-- 搜索栏 -->
    <el-row class="search-all">
      <el-select v-model="searchId" placeholder="商品名">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <div class="search-bar">
        <el-col :span="15">
          <el-input
            v-if="this.searchId == '选项1'"
            class="textbox"
            v-model="searchQuery"
            placeholder="请输入搜索商品名"
          ></el-input>
          <el-input
            v-else
            class="textbox"
            v-model="searchShopName"
            placeholder="请输入搜索商店名"
          ></el-input>
        </el-col>
        <el-col :span="5">
          <el-button type="primary" icon="el-icon-search" @click="searchGoods"
            >搜索</el-button
          >
        </el-col>
      </div>

      <el-col :span="3"> 价格范围：</el-col>
      <el-col :span="1">
        <el-input class="prizebox" v-model="plow"></el-input>
      </el-col>
      ~
      <el-col :span="1">
        <el-input class="prizebox" v-model="phigh"></el-input>
      </el-col>
      <el-col :span="3">
        <el-button type="primary" @click="clearAllFilters"
          >清空所有条件</el-button
        >
      </el-col>
    </el-row>

    <!-- 左侧分类栏 -->
    <el-row class="main-content" type="flex">
      <el-col :span="4" class="category-sidebar">
        <el-menu @select="filterByCategory">
          <el-menu-item index="0">所有品类</el-menu-item>
          <el-menu-item
            v-for="type in types"
            :key="type.type"
            :index="String(type.type)"
          >
            {{ type.typeName }}
          </el-menu-item>
        </el-menu>
      </el-col>

      <!-- 商品展示区域 -->
      <el-col :span="20">
        <el-row :gutter="20">
          <el-col
            v-for="good in goods"
            :key="good.id"
            :span="6"
            @click.native="openInfoDialog(good)"
          >
            <el-card :body-style="{ padding: '20px' }">
              <img :src="good.image" class="image" />
              <div>{{ good.name }}</div>
              <div>{{ good.price }}元</div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 滚动加载更多 -->
        <div class="scroll-loading" @scroll="loadMore">
          <el-spinner v-if="loading"></el-spinner>
        </div>
      </el-col>
    </el-row>

    <!-- 展示详情，购买，加入购物车按钮 -->
    <el-dialog :visible.sync="infoDialogVisible" title="商品详情" width="50%">
      <el-row>
        <div>商品名称: {{ selectedGood.name }}</div>
      </el-row>

      <!-- <el-row v-if="this.detail.types">
    <span>类型：</span>
    <el-radio-group v-model="selectedType">
      <el-radio
        v-for="type in this.detail.types"
        :key="type.id"
        :label="type.id"
      >{{ type.name }}</el-radio>
    </el-radio-group>
  </el-row> -->

      <el-row v-if="this.detail.text">
        <span>详情：</span>
        <span>{{ detail.text }}</span>
      </el-row>
      <el-row>
        <span>数量：</span>
        <el-input-number
          v-model.number="buyNum"
          :min="1"
          :max="selectedGood.num"
          class="input-number"
        ></el-input-number>
        <span>还剩{{ selectedGood.num }}件</span>
      </el-row>
      <el-row>
        <span>价格：{{ selectedGood.price }}元</span>
      </el-row>
      <el-row>
        <el-button type="primary" @click="handleBuy">立即购买</el-button>
        <el-button type="primary" @click="addCart">加入购物车</el-button>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      userName: "",
      userId: "",
      searchQuery: "",
      goods: [],
      page: 1,
      pageSize: 20,
      loading: false,
      type: null,
      plow: null,
      phigh: null,
      // types: [],//具体商品类型
      buyNum: 1,
      infoDialogVisible: false,
      selectedGood: {},
      detail: {},
      selectedType: null,
      options: [
        {
          value: "选项1",
          label: "商品名",
        },
        {
          value: "选项2",
          label: "商店名",
        },
      ],
      searchId: "选项1",
      searchShopName: "",
    };
  },
  mounted() {
    this.getAllTypes();
    this.fetchUserName();
    this.fetchGoods();
  },
  methods: {
    // 获取用户名
    fetchUserName() {
      const token = localStorage.getItem("jwt"); // 从本地存储中获取JWT
      console.log("获得jwt:", token);
      if (!token) {
        this.userName = null; // 如果没有JWT，显示“请登录”
        return;
      }

      axios
        .get("http://localhost:8080/getUsernameAndId", {
          headers: {
            token: token, // 将 token 作为请求头传递
          },
        })
        .then((response) => {
          console.log(response);
          this.userName = response.data.data.username;
          this.userId = response.data.data.userId;
        })
        .catch((error) => {
          console.error("未能获取用户信息", error);
          this.userName = null; // 如果获取用户名失败，显示“请登录”
        });
    },
    // 注销
    logout() {
      localStorage.removeItem("jwt"); // 删除本地存储中的JWT
      this.userName = null;
      this.$router.push("/login"); // 路由到登录页面
    },
    // 跳转到订单页面
    goToOrder() {
      this.$router.push("/showorder");
    },
        // 跳转到登录页面
    goToLogin() {
      this.$router.push("/login");
    },
    handleCommand(command) {
      if (command === 'order') {
        this.goToOrder();
      } else if (command === 'logout') {
          this.goToLogin();
      }
    },
    // 跳转购物车
    goToCart() {
      this.$router.push("/cart");
    },
    // 搜索商品
    searchGoods() {
      this.page = 1;
      this.goods = [];
      if (this.searchId == "选项1") {
        this.searchShopName = null;
      } else {
        this.searchQuery = null;
      }
      this.fetchGoods();
    },
    // 分类过滤商品
    filterByCategory(categoryIndex) {
      this.type = categoryIndex;
      if (categoryIndex == 0) {
        this.type = null;
      }
      this.page = 1;
      this.goods = [];
      this.fetchGoods();
    },
    // 获取商品
    fetchGoods() {
      this.loading = true;
      axios
        .get("http://localhost:8080/goods", {
          params: {
            page: this.page,
            pageSize: this.pageSize,
            goodName: this.searchQuery,
            shopName: this.searchShopName,
            type: this.type,
            plow: this.plow,
            phigh: this.phigh,
          },
        })
        .then((response) => {
          if (response.data && response.data.data && response.data.data.rows) {
            this.goods = [...this.goods, ...response.data.data.rows];
          } else {
            console.error("未找到记录:", response.data);
            // 处理没有记录的情况，例如提示用户
          }
          this.loading = false;
        });
    },
    // 加载更多商品
    loadMore() {
      if (!this.loading) {
        this.page += 1;
        this.fetchGoods();
      }
    },
    //获取所有类型名
    async getAllTypes() {
      this.loading = true;
      try {
        const response = await axios.get(
          "http://localhost:8080/getTypeName/all"
        );
        if (response.data && response.data.data) {
          this.types = response.data.data;
        } else {
          console.error("获取 types 失败");
        }
      } catch (error) {
        console.error("获取 types 请求失败", error);
      } finally {
        this.loading = false;
      }
    },
    // 打开详情对话框并加载商品信息
    openInfoDialog(good) {
      this.selectedGood = good; // 将当前商品信息存储到 selectedGood
      this.infoDialogVisible = true;
      this.buyNum = 1;
      this.selectedType = null; // 重置类型选择
      this.loadProductDetails(good.goodId); // 加载商品的详细信息（包括类型）
    },
    // 加载商品详情
    loadProductDetails(goodId) {
      const token = localStorage.getItem("jwt");
      axios
        .get(`http://localhost:8080/getDetail`, {
          header: {
            token: token,
          },
          params: {
            goodId: goodId,
          },
        })
        .then((response) => {
          if (response.data && response.data.data) {
            this.detail = response.data.data; // 更新商品详情
          } else {
            console.error("无法加载商品详情");
          }
        })
        .catch((error) => {
          console.error("加载商品详情失败", error);
        });
    },
    // 立即购买
    handleBuy() {
      const orderDetails = {
        userId: this.userId,
        goodId: this.selectedGood.id,
        buyNum: this.buyNum,
        // typeId: this.selectedType,
      };
      this.$router.push({ path: "/order", query: orderDetails });
    },
    // 加入购物车
    addCart() {
      const token = localStorage.getItem("jwt");
      axios
        .post(
          "http://localhost:8080/cart/insert",
          {
            userId: this.userId,
            goodId: this.selectedGood.goodId,
            num: this.buyNum,
            // typeId: this.selectedType,
          },
          {
            headers: {
              token: token,
            },
          }
        )
        .then((response) => {
          console.log("商品已加入购物车", response);
          this.infoDialogVisible = false;
          this.$message.success("商品已加入购物车");
        })
        .catch((error) => {
          console.error("加入购物车失败", error);
        });
    },
    clearAllFilters() {
      this.searchQuery = "";
      this.searchShopName = "";
      this.type = null;
      this.plow = null;
      this.phigh = null;
      this.goods = [];
      this.fetchGoods();
    },
  },
};
</script>

<style scoped>
.shopping-page {
  padding: 20px;
}
.header {
  padding: 10px 20px;
  background-color: #f5f5f5;
}

.search-bar {
  margin-left: auto;
  margin-right: auto;
  width: 50%;
  display: flex;
  justify-content: center; /* 使整个搜索栏水平居中 */
  align-items: center;
}
.search-all {
  margin-left: auto;
  margin-right: auto;
  width: 100%;
  display: flex;
  justify-content: center; /* 使整个搜索栏水平居中 */
  align-items: center;
}
.textbox {
  width: 100%;
}

.category-sidebar {
  background-color: #f0f0f0;
  padding: 20px;
}
.image {
  width: 100%;
  height: auto;
}
.scroll-loading {
  text-align: center;
  margin-top: 20px;
}
</style>
