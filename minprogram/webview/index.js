Page({
  onLoad: function (option) {
    var that = this;

    var url = 'https://www.idearfly.com/GuessWolves/index.html';
    if (option) {
      if (option.user) {
        url += '?user=' + option.user + '&img=' + option.img +'#login';
      }
    }
    
    that.setData({
      'url': url
    });

    this.recordStatus = 'ready';
  },
  onShow() {
    if (!this.webpage) {
      return;
    }
    var url = this.webpage + this.webhash;
    console.log(url);
    this.setData({
      url: url
    });
  },
  onHide() {
    this.stopRecord();
    this.uploadRecordname = null;
    this.recordStatus = 'ready';
  },
  startRecord() {
    var that = this;
    if (this.recordStatus == 'ready') {
      this.recorderManager = wx.getRecorderManager();
      var options = {
        duration: 60000,
        sampleRate: 16000,
        numberOfChannels: 1,
        encodeBitRate: 64000,
        format: 'mp3',
        frameSize: 50
      }
      wx.authorize({
        scope: 'scope.record',
        success() {
          // 识别授权
          console.log("录音授权成功");
          that.recorderManager.start(options);
          that.recordStatus = 'start';

          wx.setKeepScreenOn({
            keepScreenOn: true
          });

          that.recorderManager.onInterruptionBegin(function () {
            // that.recorderManager.
          });
          that.recorderManager.onInterruptionEnd(function () {
            // that.recorderManager.
          });
          that.recorderManager.onError(function (res) {
            console.log('recorder error', res);
            var url = that.webpage + '?result=' + that.webhash;
            that.setData({
              url: url
            });
          });
          that.recorderManager.onStop(function (res) {
            console.log('recorder stop', res);

            that.tempFilePath = res.tempFilePath;
            // 是否上传录音
            if (!that.uploadRecordname) {
              return;
            }
            console.log("语音识别");
            var url = 'https://www.idearfly.com/GuessWolves/mp3recognition';
            //var url = 'https://www.idearfly.com/server-1.0.war/mp3recognition';
            wx.uploadFile({
              url: url,
              filePath: that.tempFilePath,
              name: that.uploadRecordname,
              formData: {
                'user': 'test'
              },
              success(res) {
                console.log(res);
                console.log(res.data);
                var url = that.webpage + '?result=' + res.data + that.webhash;
                that.setData({
                  url: url
                });
              },
              fail() {
                console.log("语音识别失败");
                var url = that.webpage + '?result=' + that.webhash;
                that.setData({
                  url: url
                });
              },
              complete() {
                wx.setKeepScreenOn({
                  keepScreenOn: false
                });
                that.uploadRecordname = null;
                that.tempFilePath = null;
                this.recordStatus = 'ready';
              }
            })
          });
        },
        fail() {
          wx.showModal({
            title: '提示',
            content: '您未授权录音，功能将无法使用',
            showCancel: true,
            confirmText: "授权",
            confirmColor: "#52a2d8",
            success: function (res) {
            },
            fail: function () {
              console.log("openfail");
            }
          });
        }
      });
    }
  },
  stopRecord() {
    if (this.recorderManager && this.recordStatus == 'start') {
      this.recorderManager.stop();
      this.recordStatus = 'stop';
    }
  },
  playRecord() {

  },
  recognition(parseParams) {
    var that = this;

    that.uploadRecordname = parseParams.filename;
    that.stopRecord();
  },
  parseParams(url) {
    var sindex = url.indexOf('?');
    if (sindex != -1) {
      this.webpage = url.substring(0, sindex);
      sindex += 1;
      var eindex = url.indexOf('#', sindex);
      if (eindex != -1) {
        this.webhash = url.substring(eindex);
      } else {
        eindex = url.length;
      }
      var urlsearch = url.substring(sindex, eindex);
      this.websearch = urlsearch;
      var kvs = urlsearch.split('&');
      var result = null;
      for (var i=0; i<kvs.length; i++) {
        var kv = kvs[i];
        var ks = kv.split('=');
        if (ks.length == 2) {
          if (result == null) {
            result = {};
          }
          result[ks[0]] = decodeURI(ks[1]);
        }
      }
      return result;
    }
  },
  webMessage(e) {
    console.log(e.detail);
  },
  webLoad(e) {
    console.log(this.data.url);
    var url = e.detail.src;
    
    var curParams = this.parseParams(url);

    if (curParams) {
      var nactive = curParams.nactive;
      if (nactive) {
        if (this[nactive]) {
          this[nactive](curParams);
        }
      }
    }
    console.log(e.detail);
  },
  webError(e) {
    console.log(e.detail);
  }
})
