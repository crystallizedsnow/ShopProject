<template>
  <div class="shop-manage-container">
    <el-row class="header" type="flex" justify="space-between">
      <span>商店名：{{ shopName }}</span>
      <el-dropdown class="manage-dropdown" @command="handleCommand">
        <span class="el-dropdown-link">
          管理 <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="user"> 管理用户</el-dropdown-item>
          <el-dropdown-item command="order">管理订单</el-dropdown-item>
          <el-dropdown-item command="logout">注销</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-row>

    <!-- 搜索功能 -->
    <el-input v-model="searchName" placeholder="搜索商品名称"></el-input>
    <el-select v-model="searchType" placeholder="选择类型">
      <el-option label="电子产品" value="1"></el-option>
      <el-option label="食品" value="10"></el-option>
    </el-select>
    <el-button type="primary" @click="searchGoods">搜索</el-button>

    <!-- 商品展示表格 -->
    <el-table :data="goods" style="width: 100%">
      <el-table-column
        prop="goodId"
        label="商品编号"
        width="180"
      ></el-table-column>
      <el-table-column prop="name" label="名称" width="180"></el-table-column>
      <el-table-column prop="price" label="价格" width="100"></el-table-column>
      <el-table-column prop="num" label="数量" width="100"></el-table-column>
      <el-table-column
        prop="typeName"
        label="类型"
        width="100"
      ></el-table-column>
      <el-table-column prop="image" label="图片" width="120">
        <template slot-scope="scope">
          <img
            :src="scope.row.image"
            alt="商品图片缺失"
            width="50"
            height="50"
          />
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            @click="deleteGood(scope.row.goodId)"
            type="danger"
            size="small"
            >删除</el-button
          >
          <el-button
            @click="openUpdateDialog(scope.row)"
            type="primary"
            size="small"
            >更新</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 插入商品按钮 -->
    <el-button type="success" @click="openInsertDialog">增加商品</el-button>

    <!-- 插入/更新商品弹窗 -->
    <el-dialog :visible.sync="dialogVisible" title="商品信息">
      <el-form :model="form">
        <el-form-item label="名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="form.price"></el-input>
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="form.num"></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type">
            <el-option
              v-for="item in types"
              :key="item.type"
              :label="item.typeName"
              :value="item.type"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            action="upload-url"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGood">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      shopId: "",
      shopName: "", // 商户名称
      userName: "", //用户名称
      searchName: "", // 搜索框商品名称
      searchType: null, // 搜索框商品类型
      goods: [], // 商品列表
      message: "",
      form: {
        name: "",
        price: "",
        num: "",
        type: null,
        image: "",
        shopId: "",
        goodId: "",
        typeName: "",
      },
      types: [],
      dialogVisible: false, // 控制弹窗显示
    };
  },
  mounted() {
    this.getAllTypes();
    this.getUserName();
  },
  methods: {
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
          this.getShopName();
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
        this.getGoods();
      } catch (error) {
        console.error("未能获取用户信息", error);
        this.shopName = null; // 如果获取失败，显示“请登录”
        this.shopId = null;
      }
    },

    async getGoods() {
      this.loading = true;
      this.clearGoods();
      try {
        const response = await axios.get("http://localhost:8080/goods", {
          params: {
            shopId: this.shopId,
            goodName: this.searchName,
            type: this.searchType,
          },
        });
        if (response.data && response.data.data && response.data.data.rows) {
          this.goods = [...this.goods, ...response.data.data.rows];

          // 为每个商品匹配 typeName
          this.goods.forEach((good) => {
            const matchedType = this.types.find(
              (typeItem) => typeItem.type === good.type
            );
            if (matchedType) {
              good.typeName = matchedType.typeName; // 匹配到 typeName
            } else {
              good.typeName = "未知类型"; // 如果没有匹配到
            }
          });
        } else {
          console.error("未找到记录:", response.data);
        }
      } catch (error) {
        console.error("获取商品失败", error);
      } finally {
        this.loading = false;
      }
    },

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

    clearGoods() {
      this.goods = [];
    },
    searchGoods() {
      this.getGoods(); // 调用接口获取搜索结果
    },
    openInsertDialog() {
      this.dialogVisible = true;
      this.form = {}; // 清空表单
      this.form.id = 0;
    },
    openUpdateDialog(good) {
      this.dialogVisible = true;
      this.form = { ...good }; // 填入商品信息进行更新
      this.form.id = 1;
      this.form.goodId = good.goodId;
    },
    async submitGood() {
      const token = localStorage.getItem("jwt");
      this.form.shopId = this.shopId;
      if (
        !this.form.name ||
        !this.form.price ||
        !this.form.num ||
        !this.form.type ||
        !this.form.shopId
      ) {
        this.$message.error("请填写完整的商品信息");
        return;
      }

      let response;
      const config = {
        headers: {
          token: token,
        },
      };

      if (this.form.id) {
        response = await axios.post(
          "http://localhost:8080/manageGood/updateGood",
          this.form,
          config
        );
      } else {
        response = await axios.post(
          "http://localhost:8080/manageGood/insertGood",
          this.form,
          config
        );
      }

      console.log(response.data);
      if (response.data.code) {
        this.$message.success("操作成功");
        this.dialogVisible = false;
        this.getGoods();
      } else {
        this.$message.error(response.data.message);
      }
    },
    async deleteGood(goodId) {
      const token = localStorage.getItem("jwt");
      console.log(token);
      const config = {
        headers: {
          token: token,
        },
        params: {
          goodId: goodId, // 通过 params 传递 goodId
        },
      };

      try {
        const response = await axios.delete(
          "http://localhost:8080/manageGood/deleteGood",
          config
        );
        console.log(response);

        if (response.data.code) {
          this.getGoods(); // 删除后刷新列表
        } else {
          this.$message.error(response.data.message);
        }
      } catch (error) {
        console.error("Error deleting the good:", error);
        this.$message.error("删除失败");
      }
    },
    handleUploadSuccess(response) {
      this.form.image = response.url; // 获取上传成功后的图片URL
    },
    handleCommand(command) {
      if (command === "user") {
        this.goToManageUser();
      } else if (command === "order") {
        this.goToManageOrder();
      } else if (command =="logout") {
        this.logout();
      }
    },
    goToManageUser() {
      this.$router.push("/manageUser");
    },
    goToManageOrder() {
      this.$router.push("/manageOrder");
    },
    logout() {
      localStorage.removeItem("jwt"); // 删除本地存储中的JWT
      this.userName = null;
      this.$router.push("/login"); // 路由到登录页面
    },
  },
};
</script>

<style>
.shop-manage-container {
  padding: 20px;
}

.el-upload {
  width: 100px;
  height: 100px;
}
</style>
