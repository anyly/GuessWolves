// component/game/player.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    user: String,
    img: String,
    tag: String,
    poker: String,
    status: String,
    vote: Number,
    seat: Number,
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
