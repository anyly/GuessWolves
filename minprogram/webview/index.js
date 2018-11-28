Page({
  onLoad: function (option) {
    var that = this;
    
    var urlparams = '';
    if (option) {
      if (option.user) {
        urlparams += '#login?user=' + option.user + '&img=' + option.img;
      }
    }
    that.setData({
      'urlparams': urlparams
    });
      
    
  },
  startRecord() {
    var that = this;
    if (this.recordStatus != 'start') {
      this.recorderManager = wx.getRecorderManager();
      var options = {
        duration: 10000,
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

          that.recorderManager.onError(function (res) {
            console.log('recorder error', res);
          });
          that.recorderManager.onStop(function (res) {
            console.log('recorder stop', res)
            that.tempFilePath = res.tempFilePath;
            // 是否上传录音
            if (!that.uploadRecordname) {
              return;
            }
            console.log("语音识别");
            wx.uploadFile({
              url: 'https://www.idearfly.com/server-1.0.war/mp3recognition',
              filePath: that.tempFilePath,
              name: that.uploadRecordname,
              formData: {
                'user': 'test'
              },
              success(res) {
                console.log(res); console.log(res.data);
              },
              fail() {
                console.log("语音识别失败");
              },
              complete() {
                that.uploadRecordname = null;
                that.tempFilePath = null;
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
    if (this.recorderManager && this.recordStatus != 'stop') {
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
      sindex += 1;
      var eindex = url.indexOf('#', sindex);
      if (eindex == -1) {
        eindex = url.length;
      }
      var urlsearch = url.substring(sindex, eindex);
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
