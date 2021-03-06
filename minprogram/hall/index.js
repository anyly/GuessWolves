// hall/index.js
const websocket = require('../js/websocket');
const app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    dialog: {
      gameSetting: false,
      gameNumber: false
    },
    pokerNumber: 0,
    isDisconnect: true,
    userbar: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    const that = this;

    var url = app.wss + '/hall';
    var success = false;
    var client = websocket.connectSocket({
      url: url,
      success() {
        success = true;
      }, 
      fail() {
        success = false;
      }
    });
    if (!success) {
      return;
    }
    that.websocket = client;
    that.websocket.onOpen(function() {
      that.setData({
        isDisconnect: false
      })
      that.login();
    });
    that.websocket.onClose(function() {
      that.setData({
        isDisconnect: true
      })
    })
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
    this.onUnload();
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
    const that = this;
    if (that.websocket) {
      that.websocket.close();
      that.websocket = null;
    }
  },
  newGame() {
    this.setData({
      dialog: {
        gameSetting: true
      }
    });
  },
  findGame() {
    this.setData({
      dialog: {
        gameNumber: true
      }
    });
  },
  login() {
    this.websocket.http({
      action:'login',
      data: {
        user: wx.getStorageSync('user'),
        img: wx.getStorageSync('img')
      },
      success(res) {
        var data = res.data;
      }
    });
  },
  logout() {

  },
  gameStart() {
    var that=this;
    this.websocket.http({
      action: 'newGame',
      data: {
        poker: this.data.poker,
        unlucky: true
      },
      success(no) {
        that.websocket.close();
        wx.redirectTo({
          url: '/game/index?no='+no,
        })
      }
    });
  },
  gameCancel() {
    this.setData({
      dialog: {
        gameSetting: false
      }
    });
  },
  changePoker(res) {
    var poker = res.detail.value;
    this.setData({
      poker: poker,
      pokerNumber: poker.length
    });
    
  },
  forNo(res) {
    var text = res.detail.value;
    if (/^[0-9]+$/.test(text)) {
      var no = parseInt(text);
      this.setData({
        no: no
      })
    } else {
      this.setData({
        no: null
      })
      wx.showToast({
        title: '无效的房间号!',
        icon: 'success',
        duration: 5000
      })
    }
  },
  joinGame() {
    var that = this;
    var no = this.data.no;
    if (no) {
      this.websocket.http({
        action: 'joinGame',
        data: no,
        success() {
          that.websocket.close();
          wx.redirectTo({
            url: '/game/index?no=' + no,
          })
        },
        fail() {
          wx.showToast({
            title: '房间号不存在!',
            icon: 'success',
            duration: 5000
          })
        }
      });
    }

  },
  reconnect() {
    this.onShow();
  },
  touchstart(res) {
    if (this.data.userbar) {
      this.setData({
        userbar: false
      });
    } else {
      try {
        this.startX = res.changedTouches[0].clientX;
        this.startY = res.changedTouches[0].clientY;
      } catch(e) {

      }
    }
  },
  touchend(res) {
    if (this.startX == null) {
      return;
    }
    try {
      var endX = res.changedTouches[0].clientX;
      var endY = res.changedTouches[0].clientY;

      if (endX < this.startX) {
        this.startX = null;
        this.startY = null;
        return;
      }

      var absX = Math.abs(endX - this.startX);
      var absY = Math.abs(endY - this.startY);

      this.startX = null;
      this.startY = null;

      if (absX > absY && absX > 60) {
        this.setData({
          userbar: true
        });
      }
    } catch(e) {

    }
  }
})