/**
 * Created by idear on 2018/9/30.
 */

$.fn.tipsPoker = function (color) {
    var ns = "http://www.w3.org/2000/svg";
    this.each(function () {
        var self = $(this);
        var svg = document.createElementNS(ns,"svg");
        var path = document.createElementNS(ns, 'path');
        svg.appendChild(path);
        self.before(svg);

        var width = 56-2;
        var height = 75-2;
        jq_svg = $(svg);
        jq_svg.attr('name', 'tips');
        jq_path = $(path);
        jq_path.attr({
            d:'M 0 0 V '+height+' H '+width+' V 0 Z'
        });
        if (!color) color='red';
        jq_path.css({
            fill:"none",
            stroke: color,
            'stroke-width' : 4,
            'stroke-linecap' : 'round',
            'stroke-dasharray': 10,
            'stroke-dashoffset': -100,
            animation: 'act 3s linear infinite'
        });
    });
    return this;
};
$.fn.cancelTips = function () {
    this.prev('svg').remove();
};
var cancelAllTips = function () {
    $('svg[name=tips]').remove();
};
var cancelSpell = function () {
    $('page poker').each(function () {
        var self = $(this);
        try {
            self.draggable('disable');
        } catch (e) {

        }
        try {
            self.droppable('disable');
        } catch (e) {

        }
    });
};

$.fn.castSpellBy = function (targetSelector, callback) {
    var from;
    this.draggable({
        disabled: false,
        containment: 'page',
        revert: 'invalid',
        helper: "clone",
        zIndex: 3000,
        cursor: "move",
        start: function (event, ui) {
        },
        drag: function (event, ui ) {
            from = this;
        },
        stop: function (event, ui ) {

        }
    });

    $('page poker').droppable({
        disabled: false,
        accept: targetSelector,
        drop: function( event, ui ) {
            //$(from).draggable('disable');
            if (callback) {
                callback(from, this);
            }
        }
    });
    return this;
};

var castSpell = function (callback, callerfilter, targetfilter) {
    var from;
    $('page poker').each(function () {
        var self = $(this);
        if (callerfilter && !callerfilter(this)) {

        } else {
            self.draggable({
                containment: 'page',
                revert: 'invalid',
                helper: "clone",
                zIndex: 3000,
                cursor: "move",
                start: function (event, ui) {
                },
                drag: function (event, ui ) {
                    from = this;
                },
                stop: function (event, ui ) {

                }
            });
        }
    });
    $('page poker').droppable({
        accept: function () {
            if (targetfilter && targetfilter(form, this)) {
                return true;
            }
            return false;
        },
        drop: function( event, ui ) {
            //$(from).draggable('disable');
            if (callback) {
                callback(from, this);
            }
        }
    });
};

(function () {
    window.WolvesAnimation = {};
    WolvesAnimation.boom = function (ele, callback) {
        if (!(ele instanceof jQuery)) {
            ele = $(ele);
        }
        var animate = $('<animate></animate>');
        animate.css({
            'width': '100%',
            'height': '100%',
            'background': 'url("img/boom.png") no-repeat',
            'background-size': 'cover',
            'background-position-x': '0px',
            'z-index': 100
        });
        ele.before(animate);
        
        createAnimation(
            animate.get(0),
            '0%, 20%, 35% {\n' +
            ' background-position-x: 0px;\n' +
            ' transform: scale(1);\n' +
            '}\n' +
            '10%, 30%, 40% {\n' +
            '    background-position-x: -53px;\n' +
            '}\n' +
            '25%, 45%, 55%, 65%, 75%{\n' +
            '    background-position-x: -106px;\n' +
            '}\n' +
            '50%, 60%, 70%, 80% {\n' +
            '    background-position-x: -159px;\n' +
            '}\n' +
            '90%, 100% {\n' +
            '    background-position-x: -212px;\n' +
            '    transform: scale(1);\n' +
            '}',
            '1.3s steps(1) both',
            function () {
                animate.remove();
                if (callback) {
                    callback();
                }
            }
        )
    };
    WolvesAnimation.gleam = function (ele, callback, playStyle) {
        if (!(ele instanceof jQuery)) {
            ele = $(ele);
        }
        return CSSAnimation.gleam(ele.get(0), callback, playStyle);
    };
    WolvesAnimation.bump = function (ele, callback) {
        if (!(ele instanceof jQuery)) {
            ele = $(ele);
        }
        CSSAnimation.bump(ele.get(0), callback);
    };
    WolvesAnimation.move = function (a, b, callback, playStyle) {
        if (!(a instanceof jQuery)) {
            a = $(a);
        }
        if (!(b instanceof jQuery)) {
            b = $(b);
        }
        var animateA = $('<animate></animate>');
        animateA.css({
            'z-index': '10',
            'display': 'block',
            'width': '100%',
            'height': '100%',
            'background-repeat': 'no-repeat',
            'background-size': '100%',
            'background-position': 'center',
            'background-image': a.css('background-image'),
        });
        a.before(animateA);
        a.css('opacity', 0);

        CSSAnimation.move(animateA.get(0), b.get(0), function () {
            animateA.remove();
            if (callback) {
                callback();
            }
            createAnimation(
                a.get(0),
                '0% {\n' +
                '    opacity: 0;\n' +
                '}\n' +
                '100% {\n' +
                '    opacity: 1;\n' +
                '}\n'
                ,
                '0.3s ease-in',
                function () {
                    a.css('opacity', 1);
                }
            );
        }, playStyle);
    };
    WolvesAnimation.rob = function (a, b, callback, playStyle) {
        if (!(a instanceof jQuery)) {
            a = $(a);
        }
        if (!(b instanceof jQuery)) {
            b = $(b);
        }
        var animateA = $('<animate></animate>');
        animateA.css({
            'z-index': '10',
            'display': 'block',
            'width': '100%',
            'height': '100%',
            'background-repeat': 'no-repeat',
            'background-size': '100%',
            'background-position': 'center',
            'background-image': a.css('background-image'),
        });
        a.before(animateA);
        a.css('opacity', 0);

        var animateB = $('<animate></animate>');
        animateB.css({
            'z-index': '10',
            'display': 'block',
            'width': '100%',
            'height': '100%',
            'background-repeat': 'no-repeat',
            'background-size': '100%',
            'background-position': 'center',
            'background-image': b.css('background-image'),
        });
        b.before(animateB);
        b.css('opacity', 0);

        CSSAnimation.rob(animateA.get(0), animateB.get(0), function () {
            animateA.remove();
            animateB.remove();
            a.css('opacity', 1);
            b.css('opacity', 1);
            if (callback) {
                callback();
            }
        }, playStyle);
    };
    WolvesAnimation.swap = function (a, b, callback, playStyle) {
        if (!(a instanceof jQuery)) {
            a = $(a);
        }
        if (!(b instanceof jQuery)) {
            b = $(b);
        }
        var animateA = $('<animate></animate>');
        animateA.css({
            'z-index': '10',
            'display': 'block',
            'width': '100%',
            'height': '100%',
            'background-repeat': 'no-repeat',
            'background-size': '100%',
            'background-position': 'center',
            'background-image': a.css('background-image'),
        });
        a.before(animateA);
        a.css('opacity', 0);

        var animateB = $('<animate></animate>');
        animateB.css({
            'z-index': '10',
            'display': 'block',
            'width': '100%',
            'height': '100%',
            'background-repeat': 'no-repeat',
            'background-size': '100%',
            'background-position': 'center',
            'background-image': b.css('background-image'),
        });
        b.before(animateB);
        b.css('opacity', 0);

        CSSAnimation.swap(animateA.get(0), animateB.get(0), function () {
            animateA.remove();
            a.css('opacity', '');
            animateB.remove();
            b.css('opacity', '');
            if (callback) {
                callback();
            }
        }, playStyle);
    };
    WolvesAnimation.flop = function (a, poker, callback) {
        if (!(a instanceof jQuery)) {
            a = $(a);
        }
        var part1 = createAnimation(
            a.get(0),
            '0% {\n' +
            '    transform: rotateY(0deg);\n' +
            '}\n' +
            '100% {\n' +
            '    transform: rotateY(90deg);\n' +
            '}\n'
            ,
            '0.4s ease',
            function () {
                a.css({
                    'background-image': 'url(img/'+poker+'.png)',
                    'transform': 'rotateY(270deg)'
                });
                createAnimation(
                    a.get(0),
                    '0% {\n' +
                    '    transform: rotateY(270deg);\n' +
                    '}\n' +
                    '100% {\n' +
                    '    transform: rotateY(360deg);\n' +
                    '}\n'
                    ,
                    '0.4s ease',
                    function () {
                        a.css({
                            'background-image': '',
                            'transform': ''
                        });
                        if (callback) {
                            callback();
                        }
                    }
                );
            }
        )
    };
})();
