// game/index.js
const websocket = require('../js/websocket');
const app = getApp();
const encoder = require('../js/encoder');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    isDisconnect: true,
    host: 1,
    user: wx.getStorageSync('user'),
    img: wx.getStorageSync('img'),
    tips: '准备开始',
    description: '选择座位并就绪',
    targetCount: 0
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    var no = parseInt(options.no);
    this.setData({
      no: no
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {
    const that = this;

    var url = app.wss + '/game/' + this.data.no;
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
      that.websocketListener();
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
  websocketListener() {
    const that = this;
    var client = that.websocket;

    // 游戏通知类
    client.admit('syncLeave', that.loadGame);
    client.admit('syncGame', that.loadGame);
    client.admit('syncPoker', that.loadGame);
    client.admit('syncStatus', that.loadGame);
    client.admit('syncSeat', that.loadGame);
    client.admit("GameStart", that.GameStart);
    client.admit("syncHunter", that.loadGame);
    // 游戏中
    client.admit("Doppel", that.loadGame);
    client.admit("Wolf", that.loadGame);
    client.admit("AsMysticWolf", that.loadGame);
    client.admit("MysticWolf", that.loadGame);
    client.admit("AsSeer", that.loadGame);
    client.admit("Seer", that.loadGame);
    client.admit("AsApprenticeSeer", that.loadGame);
    client.admit("ApprenticeSeer", that.loadGame);
    client.admit("AsRobber", that.loadGame);
    client.admit("Robber", that.loadGame); 
    client.admit("AsWitch", that.loadGame);
    client.admit("Witch", that.loadGame);
    client.admit("AsTroubleMarker", that.loadGame);
    client.admit("TroubleMarker", that.loadGame);
    client.admit("AsDrunk", that.loadGame);
    client.admit("Drunk", that.loadGame);
    // 发言环节
    client.admit("Speak", that.loadGame);
    // 最后投票
    client.admit("Vote", that.loadGame);
    // 猎人权力
    client.admit("Hunter", that.loadGame);

    // 上帝视角
    client.admit("God", that.loadGame);
    // 结果报告
    client.admit("Result", that.loadGame);
  },
  wairForResult() {
    this.setData({
      tips: '等待操作结果',
      description: '请耐心等待操作结果'
    });
  },
  loadGame(game) {
    var stage = game.stage;
    if (!stage || stage == 'Result') {
      stage = null;
    }
    
    var player = game.allPlayers[this.data.user];
    var movements = player.movements;
    var viewport = {};
    if (movements.length>0) {
      viewport = movements[movements.length-1].viewport;
    }
    this.mission = player['mission'];
    if (this.mission) {
      if (this.mission.indexOf('As') == 0) {
        this.mission = this.mission.substring(2);
      }
    }
    this.setData({
      game: game,
      stage: stage,
      player: player,
      desktop: game.desktop,
      movements: movements,
      viewport: viewport
    });

    if (this.mission) {
      if (this.tips[this.mission]) {// 当前有任务
        this.tips[this.mission].call(this);
      }
    }

    if (stage && !this.mission) {
      wairForResult();
    }
    if (!stage && game.prevGame && game.prevGame.allPlayers[this.data.user]) {
      // 回放
      this.Result.call(this, game);
    }
  },
  GameStart(game) {
    this.loadGame(game);
    this.setData({
      tips: '游戏开始',
      description: '耐心等待行动',
    })
    this.prevGame = null;
    this.playback = null;
  },
  Result(game) {
    this.prevGame = game.prevGame;
    this.playback = game.playback;
    var player = this.prevGame.allPlayers[this.data.user];
    
    var win = player.win;
    var report = this.prevGame.report;
    var text = report.description;
    if (win == true) {
      text = '赢了！' + text;
    } else if (win == false) {
      text = '输了！' + text;
    }
    this.setData({
      tips: text,
      description: '点击查看回放',
      targetCount: 0
    });
  },
  tips: {
    Doppel() {
      this.setData({
        tips: '化身幽灵行动',
        description: '你要复制谁？'
      });
      this.playerTarget = 1;
      this.coexist = false;
      this.pokerTarget = 0;
    },
    Wolf() {
      this.setData({
        tips: '狼人行动',
        description: '你可以查看底牌！'
      })
      this.playerTarget = 0;
      this.coexist = false;
      this.pokerTarget = 1;
    },
    MysticWolf() {
      this.setData({
        tips: '狼先知行动',
        description: '你可以查看一个人！'
      })
      this.playerTarget = 1;
      this.coexist = false;
      this.pokerTarget = 0;
    },
    Seer() {
      this.setData({
        tips: '预言家行动',
        description: '你看一个人或两张底牌！'
      });
      this.playerTarget = 1;
      this.coexist = false;
      this.pokerTarget = 2;
    },
    ApprenticeSeer() {
      this.setData({
        tips: '见习预言家行动',
        description: '你要看哪张底牌？'
      });
      this.playerTarget = 0;
      this.coexist = false;
      this.pokerTarget = 1;
    },
    Robber() {
      this.setData({
        tips: '强盗行动',
        description: '告诉我，你要抢谁的牌？'
      })
      this.playerTarget = 1;
      this.coexist = false;
      this.pokerTarget = 0;
    },
    Witch() {
      this.setData({
        tips: '女巫行动',
        description: '一张底牌还给一个人，你决定了吗？'
      })
      this.playerTarget = 1;
      this.coexist = true;
      this.pokerTarget = 1;
    },
    TroubleMarker() {
      this.setData({
        tips: '捣蛋鬼行动',
        description: '对调两个人的身份，你心中有人选吗？'
      })
      this.playerTarget = 2;
      this.coexist = false;
      this.pokerTarget = 0;
    },
    Drunk() {
      this.setData({
        tips: '捣蛋鬼行动',
        description: '稀里糊涂，随便拿张底牌吧'
      })
      this.playerTarget = 0;
      this.coexist = false;
      this.pokerTarget = 1;
    },
    Vote() {
      this.setData({
        tips: '开始投票了',
        description: '你想投给谁？'
      })
      this.playerTarget = 1;
      this.coexist = false;
      this.pokerTarget = 0;
    }
  },
  spell: {
    Doppel() {
      const seat = this.data.targets[0];
      if (this.data.player.seat && this.data.player.seat != seat ) {
        this.websocket.http({
          action: 'Doppel',
          data: seat,
          success: this.loadGame
        });
        this.setData({
          tips: null,
          description: null
        })
      }
    },
    Wolf() {
      const seat = this.data.targets[0];
      if (this.data.player.seat) {
        this.websocket.http({
          action: 'Wolf',
          data: seat,
          success: this.loadGame
        });
        this.setData({
          tips: null,
          description: null
        })
      }
    },
    MysticWolf() {
      const seat = this.data.targets[0];
      if (this.data.player.seat) {
        this.websocket.http({
          action: 'MysticWolf',
          data: seat,
          success: this.loadGame
        });
        this.setData({
          tips: null,
          description: null
        })
      }
    },
    Seer() {
      const seat = this.data.targets[0];
      if (this.data.player.seat) {
        this.websocket.http({
          action: 'Seer',
          data: seat,
          success: this.loadGame
        });
        this.setData({
          tips: null,
          description: null
        })
      }
    },
    ApprenticeSeer() {
      const seat = this.data.targets[0];
      if (this.data.player.seat) {
        this.websocket.http({
          action: 'ApprenticeSeer',
          data: seat,
          success: this.loadGame
        });
        this.setData({
          tips: null,
          description: null
        })
      }
    },
    Robber() {
      const seat = this.data.targets[0];
      if (this.data.player.seat) {
        this.websocket.http({
          action: 'Robber',
          data: seat,
          success: this.loadGame
        });
        this.setData({
          tips: null,
          description: null
        })
      }
    },
    Witch() {
      const seat = this.data.targets[0];
      if (this.data.player.seat) {
        this.websocket.http({
          action: 'Witch',
          data: seat,
          success: this.loadGame
        });
        this.setData({
          tips: null,
          description: null
        })
      }
    },
    TroubleMarker() {
      const seat = this.data.targets[0];
      if (this.data.player.seat) {
        this.websocket.http({
          action: 'TroubleMarker',
          data: seat,
          success: this.loadGame
        });
        this.setData({
          tips: null,
          description: null
        })
      }
    },
    Drunk() {
      const seat = this.data.targets[0];
      if (this.data.player.seat) {
        this.websocket.http({
          action: 'Drunk',
          data: seat,
          success: this.loadGame
        });
        this.setData({
          tips: null,
          description: null
        })
      }
    },
    Vote() {
      var seat = 0;
      if (this.data.targets && this.data.targets.length>0) {
        var seat = this.data.targets[0];
      }
      
      if (this.data.player.seat) {
        this.websocket.http({
          action:'Vote',
          data: seat,
          success: this.loadGame
        });
        this.setData({
          tips: null,
          description: null
        })
      }
    }
  },
  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {
    this.onUnload();
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {
    const that = this;
    if (that.websocket) {
      that.websocket.close();
      that.websocket = null;
    }
  },
  login() {
    const that = this;
    this.websocket.http({
      action: 'login',
      data: {
        user: this.data.user,
        img: this.data.img
      },
      success(data) {
        that.joinGame();
      }
    });
  },
  joinGame() {
    const that = this;
    this.websocket.http({
      action: 'joinGame',
      data: this.data.no,
      success(game) {
        if (!game) {
          wx.showToast({
            title: '无效的房间号!',
            icon: 'success',
            duration: 5000
          })
          that.websocket.close();
          wx.redirectTo({
            url: '/hall/index',
          })
          return;
        }
        console.log(game);
        that.loadGame(game);
      },
      fail() {

      }
    });
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
      } catch (e) {

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
    } catch (e) {

    }
  },
  selectPlayer(res) {
    var seat = res.detail;
    this.selectTarget(seat);
  },
  selectPoker(event) {
    var seat = event.currentTarget.dataset.seat;
    this.selectTarget(seat);
  },
  forPlayertarget(seat) {
    if (seat>0) {

    }
    var targets = [];
    var ori = this.data.targets;

    for (let i = 0; i < ori.length; i++) {
      var s = ori[i];
      if (s != seat) {
        if (s > 0) {
          targets.push(s);
        }
      }
    }
  },
  selectTarget(seat) {
    const that = this;

    var stage = this.data.stage;
    if (!stage) {// 游戏未开始
      this.sitdown(seat);
    } else {
      if (this.data.game.deck[seat] &&
        this.data.cast && 
        this.mission &&
        this.spell[this.mission]) {
        //
        var playerNumber = 0;
        var firstPlayer = -1;
  
        var pokerNumber = 0;
        var firstPoker = -1;
        
        var targets = [];
        var ori = this.data.targets;

        if (this.playerTarget) {

        }
        if (this.pokerTarget) {

        }

        if (this.coexist) {// 并存
          // 合并两个数量
        } else {// 排他
          // 以第一个类型决定选项
          if (ori.length == 0) {
            
          }
        }

        // 满足大小限制要求
        if (seat > 0) {
          if (playerNumber > 0 && playerNumber + 1 > this.playerTarget) {
            targets.splice(firstPlayer, 1);
          }
        } else {
          if (pokerNumber > 0 && pokerNumber + 1 > this.pokerTarget) {
            targets.splice(firstPoker, 1);
          }
        }


        if (ori) {
          if (ori.length == 0) {
            this.playerTarget
          }
          for (let i = 0; i < ori.length; i++) {
            var s = ori[i];
            if (s != seat) {
              if (s > 0) {
                if (firstPlayer<0) {
                  firstPlayer = i;
                }
                playerNumber++;
              } else {
                if (firstPoker<0) {
                  firstPoker = i;
                }
                pokerTarget++;
              }
              targets.push(s);
            }
          }
        }
        
        
        targets.push(seat);
        if (targets.length>this.data.targetCount) {
          targets.shift();
        }
        this.setData({
          targets: targets
        })
      }
    }
  },
  cast() {
    var cast = false;
    if (this.mission 
      && this.spell[this.mission]) {
        if (!this.data.cast) {
          cast = true;
        }
    }
    this.setData({
      cast: cast,
      targets: []
    });
  },
  castYes() {
    if (this.data.cast &&
      this.mission &&
      this.spell[this.mission]) {
      wairForResult();
      this.spell[this.mission].call(this);
      this.setData({
        cast: false,
        targets: null
      });
    }
  },
  castNo() {
    this.setData({
      targets: null
    })
  },
  voteYes() {
    if (!this.data.targets || this.data.targets.length == 0) {
      return;
    }
    var seat = this.data.targets[0];
    if (this.data.game.deck[seat] &&
      this.data.cast) {
      wairForResult();
      this.spell.Vote.call(this);
      this.setData({
        cast: false,
        targets: null
      });
    }
  },
  voteNo() {// 弃权票
    this.setData({
      cast: false,
      targets: null
    });
    this.spell.Vote.call(this);
  },
  sitdown(seat) {
    if (!this.data.player.ready) {
      this.websocket.emit({
        action: 'sitdown',
        data: seat
      });
    }
  },
  cue() {

  },
  leaveGame() {
    if (this.data.stage) {
      // 弹窗确认
      this.back();
    } else {
      this.back();
    }
  },
  back() {
    var that = this;
    if (this.websocket.readyState>1) {
      that.websocket.close();
      // 关闭状态
      wx.redirectTo({
        url: '/hall/index',
      })
    } else {
      this.websocket.http({
        action: 'leaveGame',
        data: null,
        success() {
          that.websocket.close();
          wx.redirectTo({
            url: '/hall/index',
          })
        }
      });
    }
  },
  startGame(){
    const that = this;
    this.websocket.http({
      action: 'ready',
      data: true,
      success() {
        that.websocket.http({
          action: 'startGame',
        });
      }
    });
  },
  ready() {
    this.websocket.emit({
      action: 'ready',
      data: true
    });
  },
  cancel() {
    this.websocket.emit({
      action: 'ready',
      data: false
    });
  },
  tipsTap() {
    if (this.playback) {
      var url = app.https + '/playback/' + this.playback;
      wx.navigateTo({
         url: '/playback/index?url=' + url,
      })
    }
  },
  showSetting() {
    if (this.data.movements) {
      this.setData({
        showSetting: true
      });
    }
  },
  hideSetting() {
    if (this.data.movements) {
      this.setData({
        showSetting: false
      });
    }
  }
})