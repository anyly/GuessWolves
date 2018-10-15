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

        var width = 53-2;
        var height = 75-2;
        jq_svg = $(svg);
        jq_svg.attr('name', 'tips');
        /*jq_svg.css({
            position: 'absolute',
            'margin-top': '1px',
            'margin-left': '-2px',
            'z-index': -2,
            width: width,
            height: height
        });*/
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

// var castSpell = function (index, callback) {
//     var from;
//     $('page poker:eq('+index+')').draggable({
//         containment: 'page',
//         revert: 'invalid',
//         helper: "clone",
//         zIndex: 3000,
//         cursor: "move",
//         start: function (event, ui) {
//         },
//         drag: function (event, ui ) {
//             from = this;
//         },
//         stop: function (event, ui ) {
//
//         }
//     });
//     $('page poker:not(":eq('+index+')")').droppable({
//         accept: 'page poker:eq('+index+')',
//         drop: function( event, ui ) {
//             //$(from).draggable('disable');
//             if (callback) {
//                 callback(from, this);
//             }
//         }
//     });
// };
