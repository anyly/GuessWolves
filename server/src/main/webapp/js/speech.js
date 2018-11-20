(function (window) {
    var Speech = {};
    if (isWeiXin()) {//微信版本
        var sindex = window.location.search.indexOf('?nactive=');
        if (sindex != -1) {
            sindex += '?nactive='.length;
            var eindex = window.location.search.indexOf('&', sindex);
            if (eindex == -1) {
                eindex = window.location.search.length;
            }
            var nactive = window.location.search.substring(sindex, eindex);
            if (nactive == 'startRecord') {
                $('mic').addClass('quiet');
            }
        }

        loadScript('https://res.wx.qq.com/open/js/jweixin-1.3.2.js', function(){
            Speech.start = function (callback, config) {
                window.location.search = '?nactive=startRecord';
            };
            Speech.stop = function() {
                window.location.search = '?nactive=stopRecord';
            };
            Speech.play = function(audio) {
                window.location.search = '?nactive=playRecord';
            };
            Speech.recognition = function (id, url, callback) {
                this.stop();
                //window.location.search = '?nactive=stopRecord';
            };
            // $.ajax({
            //     url : window.location.basepath+'/minprogram/authorize?url='+window.location.href,
            //     dataType : 'json',
            //     success: function (data) {
            //         data.debug = true;
            //         data.jsApiList = [
            //             'startRecord', 'stopRecord', 'onVoiceRecordEnd', 'playVoice', 'uploadVoice', 'translateVoice'
            //         ];
            //         wx.config(data);
            //         wx.ready(function () {
            //             var recoredLocalId;
            //             Speech.get = function (callback, config) {
            //                 callback({
            //                     start: function () {
            //                         wx.startRecord({
            //                             success: function () {
            //                                 alert('wx.startRecord success');
            //                             },
            //                             fail: function (e) {
            //                                 alert('wx.startRecord fail' + JSON.stringify(e));
            //                             }
            //                         });
            //                         wx.onVoiceRecordEnd({// 录音时间超过一分钟没有停止的时候会执行 complete 回调
            //                             complete: function (res) {
            //                                 alert(res.localId);
            //                                 recoredLocalId = res.localId;
            //                             }
            //                         });
            //                     },
            //                     stop: function () {
            //                         wx.stopRecord({
            //                             success: function (res) {
            //                                 alert(res.localId);
            //                                 recoredLocalId = res.localId;
            //                             }
            //                         });
            //                     },
            //                     play: function (audio) {
            //                         wx.playVoice({
            //                             localId: recoredLocalId // 需要播放的音频的本地ID，由stopRecord接口获得
            //                         });
            //                     },
            //                     recognition: function (id, url, callback) {
            //                         this.stop();
            //                         if (!id) {
            //                             throw new Error('recognition() id is null');
            //                         }
            //                         this.play();
            //                         // wx.translateVoice({
            //                         //     url: url,
            //                         //     localId: recoredLocalId,
            //                         //     success (res){
            //                         //         // var serverId = res.serverId;
            //                         //         // var data = res.data;
            //                         //         callback(res.translateResult);
            //                         //     },
            //                         //     fail : Speech.throwError
            //                         // })
            //                     }
            //                 });
            //             };
            //             Speech.throwError = function (e) {
            //
            //             };
            //         });
            //         wx.error(function () {
            //             alert('wx.error发生错误');
            //         });
            //     }
            // });
        });

    } else {//浏览器版本
        //兼容
        window.URL = window.URL || window.webkitURL;
        navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;


        var Recorder = function (stream, config) {
            var context = new AudioContext();
            var audioInput = context.createMediaStreamSource(stream);
            var recorder = context.createScriptProcessor(4096, 1, 1);

            var inputSampleRate = context.sampleRate;

            config = config || {};
            var sampleBits = 16;      //采样数位 8, 16
            //音频质量,识别率有要求 sampleQuality = sampleBits x sampleRate / 1000
            config.sampleQuality = config.sampleQuality || 256;// 识别要求在256kbps左右
            var divisor = Math.round(inputSampleRate / 1000 * sampleBits / config.sampleQuality);
            var sampleRate = inputSampleRate / divisor;// 输出采样率,必须是输入采样率的整除数

            var audioData = {
                size: 0          //录音文件长度
                , buffer: []     //录音缓存
                , inputSampleRate: inputSampleRate    //输入采样率
                , inputSampleBits: 16       //输入采样数位 8, 16
                , outputSampleRate: sampleRate    //输出采样率
                , oututSampleBits: sampleBits       //输出采样数位 8, 16
                , input: function (data) {
                    this.buffer.push(new Float32Array(data));
                    this.size += data.length;
                }
                , compress: function () { //合并压缩
                    //合并
                    var data = new Float32Array(this.size);
                    var offset = 0;
                    for (var i = 0; i < this.buffer.length; i++) {
                        data.set(this.buffer[i], offset);
                        offset += this.buffer[i].length;
                    }
                    //压缩，间隔取帧，整除倍数才不会失真
                    var compression = parseInt(this.inputSampleRate / this.outputSampleRate);
                    var length = data.length / compression;
                    var result = new Float32Array(length);
                    var index = 0, j = 0;
                    while (index < length) {
                        result[index] = data[j];
                        j += compression;
                        index++;
                    }
                    return result;
                }
                , encodeWAV: function () {
                    var sampleRate = Math.min(this.inputSampleRate, this.outputSampleRate);
                    var sampleBits = Math.min(this.inputSampleBits, this.oututSampleBits);
                    var bytes = this.compress();
                    var dataLength = bytes.length * (sampleBits / 8);
                    var buffer = new ArrayBuffer(44 + dataLength);
                    var data = new DataView(buffer);


                    var channelCount = 1;//单声道
                    var offset = 0;


                    var writeString = function (str) {
                        for (var i = 0; i < str.length; i++) {
                            data.setUint8(offset + i, str.charCodeAt(i));
                        }
                    }

                    // 资源交换文件标识符 
                    writeString('RIFF');
                    offset += 4;
                    // 下个地址开始到文件尾总字节数,即文件大小-8 
                    data.setUint32(offset, 36 + dataLength, true);
                    offset += 4;
                    // WAV文件标志
                    writeString('WAVE');
                    offset += 4;
                    // 波形格式标志 
                    writeString('fmt ');
                    offset += 4;
                    // 过滤字节,一般为 0x10 = 16 
                    data.setUint32(offset, 16, true);
                    offset += 4;
                    // 格式类别 (PCM形式采样数据) 
                    data.setUint16(offset, 1, true);
                    offset += 2;
                    // 通道数 
                    data.setUint16(offset, channelCount, true);
                    offset += 2;
                    // 采样率,每秒样本数,表示每个通道的播放速度 
                    data.setUint32(offset, sampleRate, true);
                    offset += 4;
                    // 波形数据传输率 (每秒平均字节数) 单声道×每秒数据位数×每样本数据位/8 
                    data.setUint32(offset, channelCount * sampleRate * (sampleBits / 8), true);
                    offset += 4;
                    // 快数据调整数 采样一次占用字节数 单声道×每样本的数据位数/8 
                    data.setUint16(offset, channelCount * (sampleBits / 8), true);
                    offset += 2;
                    // 每样本数据位数 
                    data.setUint16(offset, sampleBits, true);
                    offset += 2;
                    // 数据标识符 
                    writeString('data');
                    offset += 4;
                    // 采样数据总数,即数据总大小-44 
                    data.setUint32(offset, dataLength, true);
                    offset += 4;
                    // 写入采样数据 
                    if (sampleBits === 8) {
                        for (var i = 0; i < bytes.length; i++, offset++) {
                            var s = Math.max(-1, Math.min(1, bytes[i]));
                            var val = s < 0 ? s * 0x8000 : s * 0x7FFF;
                            val = parseInt(255 / (65535 / (val + 32768)));
                            data.setInt8(offset, val, true);
                        }
                    } else {
                        for (var i = 0; i < bytes.length; i++, offset += 2) {
                            var s = Math.max(-1, Math.min(1, bytes[i]));
                            data.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
                        }
                    }


                    return new Blob([data], {type: 'audio/wav'});
                }
            };


            //开始录音
            this.start = function () {
                audioInput.connect(recorder);
                recorder.connect(context.destination);
            }


            //停止
            this.stop = function () {
                recorder.disconnect();
            }


            //获取音频文件
            this.getBlob = function () {
                this.stop();
                return audioData.encodeWAV();
            }


            //回放
            this.play = function (audio) {
                audio.src = window.URL.createObjectURL(this.getBlob());
            }


            //上传
            this.recognition = function (id, url, callback) {
                this.stop();
                if (!id) {
                    throw new Error('recognition() id is null');
                }
                var fd = new FormData();
                fd.append(id, this.getBlob());
                var xhr = new XMLHttpRequest();
                if (callback) {
                    xhr.upload.addEventListener("progress", function (e) {
                        callback('uploading', e);
                    }, false);
                    xhr.addEventListener("load", function (e) {
                        var data = JSON.parse(e.target.responseText);
                        callback('ok', data);
                    }, false);
                    xhr.addEventListener("error", function (e) {
                        callback('error', e);
                    }, false);
                    xhr.addEventListener("abort", function (e) {
                        callback('cancel', e);
                    }, false);
                }
                xhr.open("POST", url);
                xhr.send(fd);
            }


            //音频采集
            recorder.onaudioprocess = function (e) {
                audioData.input(e.inputBuffer.getChannelData(0));
                //record(e.inputBuffer.getChannelData(0));
            }


        };

        //抛出异常
        Speech.throwError = function (message) {
            alert(message);
            throw new function () {
                this.toString = function () {
                    return message;
                }
            }
        }
        //是否支持录音
        Speech.canRecording = (navigator.getUserMedia != null);
        //获取录音机
        Speech.start = function (callback, config) {
            if (callback) {
                if (navigator.getUserMedia) {
                    navigator.getUserMedia(
                        {audio: true} //只启用音频
                        , function (stream) {
                            var rec = new Recorder(stream, config);
                            Speech.recorder = rec;
                            callback(rec);
                        }
                        , function (error) {
                            switch (error.code || error.name) {
                                case 'PERMISSION_DENIED':
                                case 'PermissionDeniedError':
                                    Speech.throwError('用户拒绝提供信息。');
                                    break;
                                case 'NOT_SUPPORTED_ERROR':
                                case 'NotSupportedError':
                                    Speech.throwError('浏览器不支持硬件设备。');
                                    break;
                                case 'MANDATORY_UNSATISFIED_ERROR':
                                case 'MandatoryUnsatisfiedError':
                                    Speech.throwError('无法发现指定的硬件设备。');
                                    break;
                                default:
                                    Speech.throwError('无法打开麦克风。异常信息:' + (error.code || error.name));
                                    break;
                            }
                        });
                } else {
                    Speech.throwError('当前浏览器不支持录音功能。');
                    return;
                }
            }
        }
        Speech.stop = function() {
            if (Speech.recorder) {
                Speech.recorder.stop();
            }
        };
        Speech.play = function(audio) {
            if (Speech.recorder) {
                Speech.recorder.play(audio);
            }
        };
        Speech.recognition = function (id, url, callback) {
            if (Speech.recorder) {
                Speech.recorder.recognition(id, url, callback);
            }
        };
    }
    window.Speech = Speech;
})(window);