// component/game/player.js
Component({
  attached() {
    if (!this.data.img || this.data.img == '') {
      this.setData({
        img: 'https://www.idearfly.com/static/image/img0.jpg'
      });
    }
    
  },
  /**
   * 组件的属性列表
   */
  properties: {
    user: String,
    img: String,
    tag: String,
    poker: String,
    status: String,
    vote: String,
    seat: String,
    disconnect: {
      type: Boolean,
      value: false
    }
    //emoji: String
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

  }
})
