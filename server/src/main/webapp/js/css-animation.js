(function () {
    window.CSSEditor = {
        id : 'css-animation'
    };
    CSSEditor.styleSheet = function () {
        var sheet = document.querySelector('#'+this.id);
        if (!sheet) {
            sheet = document.createElement('style');
            sheet.setAttribute('id', this.id);
            var head = document.head || document.getElementsByTagName('head')[0];
            head.appendChild(sheet);
        }
        return sheet.sheet;
    };
    CSSEditor.getStyle = function(selectorText) {
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;
        for (var rule in list) {
            if (rule.selectorText = selectorText) {
                return rule;
            }
        }
        return null;
    };
    CSSEditor.addStyle = function(selectorText, cssText) {
        if (!cssText) {
            cssText = '';
        }
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;
        for (var rule in list) {
            if (rule.selectorText = selectorText) {
                rule.cssText = cssText;
                return;
            }
        }
        sheet.insertRule(selectorText+'{'+cssText+'}', 0);
    };
    CSSEditor.removeStyle = function (selectorText) {
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;

        for (var i = 0; i<list.length; i++) {
            var rule = list[i];
            if (rule.selectorText = selectorText) {
                list.deleteRule(i);
                return;
            }
        }
    };
    CSSEditor.getFrame = function () {
        if (!cssText) {
            cssText = '';
        }
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;
        for (var rule in list) {
            if (rule.name == name && rule.type == CSSRule.KEYFRAMES_RULE) {
                return rule;
            }
        }
        return null
    };
    CSSEditor.addFrame = function(name, cssText) {
        if (!cssText) {
            cssText = '';
        }
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;
        for (var i=0; i<list.length; i++) {
            var rule = list[i];
            if (rule.name == name && rule.type == CSSRule.KEYFRAMES_RULE) {
                rule.cssText = cssText;
                return;
            }
        }
        sheet.insertRule('@-webkit-keyframes '+name+' {'+cssText+'} ', 0);
    };
    CSSEditor.removeFrame = function(name) {
        var sheet = this.styleSheet();
        var list = sheet.rules || sheet.cssRules;

        for (var i = 0; i<list.length; i++) {
            var rule = list[i];
            if (rule.name == name && rule.type == CSSRule.KEYFRAMES_RULE) {
                sheet.deleteRule(i);
                return;
            }
        }
    };
})();

(function () {
    var genId = 0;
    var prefix = 'css-animation-';
    var handlers = {};
    window.createAnimation = function (ele, cssText, playStyle, callback) {
        if (!cssText) {
            return;
        }
        var id = this;
        if (typeof(this) != 'string') {
            id = prefix+(genId++);
        }
        CSSEditor.addFrame(id, cssText);

        ele.style.animation = id + ' ' + playStyle;

        var call = {
            id : id,
            clear: function () {
                ele.style.animation = '';
                CSSEditor.removeFrame(id);
            }
        };

        var handler = handlers[id] = function () {
            //console.debug(id+'animationend');
            ele.removeEventListener("animationend",handler, false);
            if (playStyle.indexOf('forwards')<0) {
                call.clear();
            }
            if (callback) {
                callback.apply(call, arguments);
            }
        };

        ele.addEventListener('animationend', handler, false);

        var f1 = function (e) {
            ele.removeEventListener('animationiteration', f1);
            //console.debug(id + ' '+JSON.stringify(e));
        };
        ele.addEventListener('animationiteration', f1);
        var f2 = function (e) {
            ele.removeEventListener('animationstart', f2);
            //console.debug(id + 'animationstart');
        };
        ele.addEventListener('animationstart', f2, false);
        return id;
    };
    window.CSSAnimation = {};
    CSSAnimation.float = function (a, callback, playStyle) {
        if (typeof(a) == 'string') {
            a = document.querySelector(a);
        }

        if (!playStyle) {
            playStyle = '1s ease-in-out forwards';
        }

        return createAnimation(
            a,
            '0% {box-shadow: 0px 0px 0px #000;}'+
            '100% {transform: translate(10px, 0px); box-shadow: -10px 10px 5px #888888;}',
            playStyle,
            callback
        );
    };
    CSSAnimation.bump = function (a, callback, playStyle) {
        if (typeof(a) == 'string') {
            a = document.querySelector(a);
        }

        if (!playStyle) {
            playStyle = '4s ease both alternate';
        }

        return createAnimation(
            a,
            '0% {transform: scale(1);}' +
            '30% {transform: scale(1.3);}' +
            '66% {transform: scale(1.3);}' +
            '100% {transform: scale(1);}',
            playStyle,
            callback
        );
    };
    CSSAnimation.gleam = function (a, callback, playStyle) {
        if (typeof(a) == 'string') {
            a = document.querySelector(a);
        }

        if (!playStyle) {
            playStyle = '1s ease both alternate 5';
        }

        return createAnimation(
            a,
            '0% { opacity:1; } ' +
            '100% { opacity:0; }',
            playStyle,
            callback
        );
    };
    CSSAnimation.swap = function (a, b, callback, playStyle) {
        if (typeof(a) == 'string') {
            a = document.querySelector(a);
        }
        if (typeof(b) == 'string') {
            b = document.querySelector(b);
        }
        //////
        var rect1 = a.getBoundingClientRect();
        var x1 = rect1.left;
        var y1 = rect1.top;
        var rect2 = b.getBoundingClientRect();
        var x2 = rect2.left;
        var y2 = rect2.top;

        if (!playStyle) {
            playStyle = '2.5s ease-in-out forwards';
        }

        var count = 0;



        createAnimation(
            a,
            '0% {box-shadow: 0px 0px 0px #000;}'+
            '15%, 30% {transform: translate(10px, 0px); box-shadow: -10px 10px 5px #888888;}'+
            '75%, 90% { transform: translate('+(x2-x1)+'px,'+(y2-y1)+'px); }'+
            '100% { transform: translate('+(x2-x1)+'px,'+(y2-y1)+'px); box-shadow: 0px 0px 0px #000;}',
            playStyle,
            function () {
                this.clear();
                if (++count == 2 && callback) {
                    callback.apply(this, arguments);
                }
            }
        );

        createAnimation(
            b,
            '0% {box-shadow: 0px 0px 0px #000;}'+
            '15%, 30% {transform: translate(10px, 0px); box-shadow: -10px 10px 5px #888888;}'+
            '75%, 90% { transform: translate('+(x1-x2)+'px,'+(y1-y2)+'px); }'+
            '100% { transform: translate('+(x1-x2)+'px,'+(y1-y2)+'px); box-shadow: 0px 0px 0px #000;}',
            playStyle,
            function () {
                this.clear();
                if (++count == 2 && callback) {
                    callback.apply(this, arguments);
                }
            }
        );
    };
    CSSAnimation.move = function (a, b, callback, playStyle) {
        if (typeof(a) == 'string') {
            a = document.querySelector(a);
        }
        if (typeof(b) == 'string') {
            b = document.querySelector(b);
        }
        //////
        var rect1 = a.getBoundingClientRect();
        var x1 = rect1.left;
        var y1 = rect1.top;
        var rect2 = b.getBoundingClientRect();
        var x2 = rect2.left;
        var y2 = rect2.top;

        if (!playStyle) {
            playStyle = '2.5s ease-in-out';
        }

        createAnimation(
            a,
            '0% {box-shadow: 0px 0px 0px #000;}'+
            '15%, 30% {transform: translate(10px, 0px); box-shadow: -10px 10px 5px #888888;}'+
            '75%, 90% { transform: translate('+(x2-x1)+'px,'+(y2-y1)+'px); }'+
            '100% { transform: translate('+(x2-x1)+'px,'+(y2-y1)+'px); box-shadow: 0px 0px 0px #000;}',
            playStyle,
            callback
        );
    };
    CSSAnimation.rob = function (a, b, callback, playStyle) {
        if (typeof(a) == 'string') {
            a = document.querySelector(a);
        }
        if (typeof(b) == 'string') {
            b = document.querySelector(b);
        }
        //////
        var rect1 = a.getBoundingClientRect();
        var x1 = rect1.left;
        var y1 = rect1.top;
        var rect2 = b.getBoundingClientRect();
        var x2 = rect2.left;
        var y2 = rect2.top;

        if (!playStyle) {
            playStyle = '2.5s ease-in-out forwards';
        }


        createAnimation(
            a,
            '0% {box-shadow: 0px 0px 0px #000;}'+
            '15%, 30% {transform: translate(10px, 0px); box-shadow: -10px 10px 5px #888888;}'+
            '75%, 90% { transform: translate('+(x2-x1)+'px,'+(y2-y1)+'px); }'+
            '100% { transform: translate('+(x2-x1)+'px,'+(y2-y1)+'px); box-shadow: 0px 0px 0px #000;}',
            playStyle,
            function () {
                var thisA = this;
                createAnimation(
                    b,
                    '0% {transform: translate(10px, 0px);}'+
                    '100% { transform: translate('+(x1-x2)+'px,'+(y1-y2)+'px);}',
                    '0.3s ease-out forwards',
                    function () {
                        thisA.clear();
                        this.clear();
                        if (callback) {
                            callback.apply(this, arguments);
                        }
                    }
                );
            }
        );
    };
})();