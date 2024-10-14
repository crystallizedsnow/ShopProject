import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)
const routes = [
  {
    path: '/',
    redirect: 'display'
  },
  {
    path:'/display',
    name:'display',
    component:() => import('../views/Elements/DisplayGoodView.vue'),
    meta: {
      title: '商品展示首页_购物系统',
    }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Elements/LoginView.vue'),
    meta: {
      title: '登录_购物系统',
    }
  },
  {
    path:'/register',
    name:'register',
    component:() => import('../views/Elements/RegisterView.vue'),
    meta: {
      title: '注册_购物系统',
    }
  },
  {
    path:'/manageGood',
    name:'manageGood',
    component:() => import('../views/Elements/ManageGoodView.vue'),
    meta: {
      title: '商户管理商品_购物系统',
    }
  },
  {
    path:'/cart',
    name:'cart',
    component:()=>import('../views/Elements/CartView.vue'),
    meta: {
      title: '购物车_购物系统',
    }
  },
  {
    path:'/addorder',
    name:'addorder',
    component:()=>import('../views/Elements/AddOrderView.vue'),
    meta: {
      title: '添加订单_购物系统',
    }
  },
  {
    path:'/payment',
    name:'payment',
    component:()=>import('../views/Elements/PayView.vue'),
    meta: {
      title: '支付_购物系统',
    }
  },
  {
    path:'/showorder',
    name:'showorder',
    component:()=>import('../views/Elements/DisplayOrderView.vue'),
    meta: {
      title: '查看用户订单_购物系统',
    }
  },
  {
    path:'/manageorder',
    name:'manageorder',
    component:()=>import('../views/Elements/ManageOrderView.vue'),
    meta: {
      title: '商户操作订单_购物系统',
    }
  },
  {
    path:'/paymentsuccess',
    name:'paymentsuccess',
    component:()=>import('../views/Elements/PaySuccessView.vue'),
    meta: {
      title: '支付成功_购物系统',
    }
  },
  {
    path:'/managedata',
    name:'managedata',
    component:()=>import('../views/Elements/ManageDataView.vue'),
    meta: {
      title: '商户查看数据_购物系统',
    }
  },
]
const router = new VueRouter({
  mode: 'history',  // 使用 history 模式，去掉 URL 中的 `#`
  routes
})
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title;
  }
  next();
});
export default router
