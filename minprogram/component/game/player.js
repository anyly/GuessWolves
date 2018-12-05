// component/game/player.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    user: String,
    img: String,
    tag: Array,
    poker: String,
    status: String,
    vote: String,
    seat: String,
    disconnect: {
      type: Boolean,
      value: false
    },
    speak: Array,
    toward: {
      type: String,
      value: 'left'
    },
    host: {
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
    triggerTapEvent() {
      this.triggerEvent('tap', this.data.seat);
    },
    triggerLongtapEvent() {
      this.triggerEvent('longtap', this.data.seat);
    },
  }
})
