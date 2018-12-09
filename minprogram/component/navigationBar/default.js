// component/navigationBar/default.js
Component({
  attached() {
    var sys = wx.getSystemInfoSync();
    let totalTopHeight = 68
    if (sys.model.indexOf('iPhone X') !== -1) {
      totalTopHeight = 88
    } else if (sys.model.indexOf('iPhone') !== -1) {
      totalTopHeight = 64
    }

    var canBack = this.data.forceBack || getCurrentPages().length > 1;
    this.setData({
      statusBarHeight: sys.statusBarHeight,
      titleBarHeight: totalTopHeight - sys.statusBarHeight,
      canBack: canBack
    });
  },
  /**
   * 组件的属性列表
   */
  properties: {
    backicon: {
      type: String,
      value: 'https://www.idearfly.com/static/image/backicon.svg',
    },
    title: {
      type: String,
      value: __wxConfig.global.window.navigationBarTitleText
    },
    forceBack: {
      type: Boolean,
      value: false
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
    back() {
      this.triggerEvent('back');
    },
    config() {
      this.triggerEvent('config');
    }
  }
})
