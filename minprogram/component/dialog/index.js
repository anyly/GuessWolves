// component/dialog/index.js
Component({
  created() {

  },
  /**
   * 组件的属性列表
   */
  properties: {
    isModal: {
      type: Boolean,
      value: false
    },
    isShow: {
      type: Boolean,
      value: false,
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
  },

  /**
   * 组件的方法列表
   */
  methods: {
    //隐藏弹框
    hideDialog() {
      this.setData({
        isShow: !this.data.isShow
      })
    },
    //展示弹框
    showDialog() {
      this.setData({
        isShow: !this.data.isShow
      })
    },
    modalHandler() {
      if (!this.data.isModal) {
        this.hideDialog();
      }
    },
    stopPropagation() {
      
    }
  }
})
