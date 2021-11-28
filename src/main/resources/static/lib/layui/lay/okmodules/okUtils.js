"use strict";
layui.define(["layer"], function (exprots) {
    var $ = layui.jquery;
    var okUtils = {
        /**
         * 是否前后端分离
         */
        isFrontendBackendSeparate: false,
        /**
         * 服务器地址
         */
        baseUrl: "https://pay.cloudbed.vip/tenant",
        /**
         * 获取body的总宽度
         */
        getBodyWidth: function () {
            return document.body.scrollWidth;
        },
        /**
         * 主要用于对ECharts视图自动适应宽度
         */
        echartsResize: function (element) {
            var element = element || [];
            window.addEventListener("resize", function () {
                var isResize = localStorage.getItem("isResize");
                // if (isResize == "false") {
                for (let i = 0; i < element.length; i++) {
                    element[i].resize();
                }
                // }
            });
        },
        /**
         * ajax()函数二次封装
         */
        ajaxCloud: function (options) {
            var defaults = {
                param : {},
                type : "POST",
                async :false,
                dataType : "json",
                success : null,
                load : false,
                close: true,
                json:false
            };
            options.url = okUtils.isFrontendBackendSeparate ? okUtils.baseUrl + options.url : options.url
            var options = $.extend(defaults, options);
            var loadIndex;
            var $layer = okUtils.getLayer();
            $.ajax({
                url: options.url,
                type: options.type,
                data : options.json?JSON.stringify(options.param):options.param,
                dataType: options.dataType,
                contentType : options.json?'application/json':'application/x-www-form-urlencoded; charset=UTF-8',
                async:options.async,
                beforeSend: function () {
                    if (options.load) {
                        loadIndex = $layer.load(0, {shade: false});
                    }
                },
                success: function (data) {
                    if (data.code == 0) {
                        // 业务正常
                        options.success(data);
                        if(options.close){
                            okUtils.dialogClose();
                        }
                    } else {
                        // 业务异常
                        $layer.msg(data.msg, {icon: 7, time: 2000});
                    }
                },
                complete: function () {
                    if (options.load) {
                        $layer.close(loadIndex);
                    }
                },
                error: function () {
                   if (options.load) {
                       $layer.close(loadIndex);
                   }
                   $layer.msg("服务器错误", {icon: 2, time: 2000});
                }
            });
        },
        /**
         * ajax()函数二次封装
         * @param url
         * @param type
         * @param param
         * @param load
         * @returns {*|never|{always, promise, state, then}}
         */
        ajax: function (url, type, param, load) {
            var deferred = $.Deferred();
            var loadIndex;
            var $layer = okUtils.getLayer();
            $.ajax({
                url: okUtils.isFrontendBackendSeparate ? okUtils.baseUrl + url : url,
                type: type || "get",
                data: param || {},
                dataType: "json",
                async:false,
                beforeSend: function () {
                    if (load) {
                        loadIndex = $layer.load(0, {shade: false});
                    }
                },
                success: function (data) {
                    if (data.code == 0) {
                        // 业务正常
                        deferred.resolve(data)
                    } else {
                        // 业务异常
                        $layer.msg(data.msg, {icon: 7, time: 2000});
                        deferred.reject("okUtils.ajax warn: " + data.msg);
                    }
                },
                complete: function () {
                    if (load) {
                        $layer.close(loadIndex);
                    }
                },
                error: function () {
                    $layer.close(loadIndex);
                    $layer.msg("服务器错误", {icon: 2, time: 2000});
                    deferred.reject("okUtils.ajax error: 服务器错误");
                }
            });
            return deferred.promise();
        },
        dialogClose:function() {
        	 var index = top.layer.getFrameIndex(window.name); // 先得到当前iframe层的索引
             top.layer.close(index); // 再执行关闭
             //var $layer = okUtils.getLayer();
             //$layer.close($layer.index); // 再执行关闭
        },
        table: {
            /**
             * 主要用于针对表格批量操作操作之前的检查
             * @param table
             * @returns {string}
             */
            batchCheck: function (table) {
                var checkStatus = table.checkStatus("tableId");
                var rows = checkStatus.data.length;
                if (rows > 0) {
                    var idsStr = "";
                    for (var i = 0; i < checkStatus.data.length; i++) {
                        idsStr += checkStatus.data[i].id + ",";
                    }
                    return idsStr;
                } else {
                    layer.msg("未选择有效数据", {offset: "t", anim: 6});
                }
            },
            /**
             * 在表格页面操作成功后弹窗提示
             * @param content
             */
            successMsg: function (content) {
                layer.msg(content, {icon: 1, time: 1000}, function () {
                    // 刷新当前页table数据
                    $(".layui-laypage-btn")[0].click();
                });
            }
        },
        /**
         * 获取父窗体的okTab
         * @returns {string}
         */
        getOkTab: function () {
            return parent.objOkTab;
        },
        /**
         * 格式化当前日期
         * @param date
         * @param fmt
         * @returns {void | string}
         */
        dateFormat: function (date, fmt) {
            var o = {
                "M+": date.getMonth() + 1,
                "d+": date.getDate(),
                "h+": date.getHours(),
                "m+": date.getMinutes(),
                "s+": date.getSeconds(),
                "q+": Math.floor((date.getMonth() + 3) / 3),
                "S": date.getMilliseconds()
            };
            if (/(y+)/.test(fmt))
                fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (var k in o)
                if (new RegExp("(" + k + ")").test(fmt))
                    fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            return fmt;
        },
        getLayer:function(){
        	var $layer = undefined;
        	if (top.layer){
        		$layer = top.layer;
        	}else if (parent.layer){
        		$layer = parent.layer;
        	}else if (layer){
        		$layer = layer;
        	}
        	return $layer;
        },
        dialogOpen:function(opt){
            var defaults = {
            		id : 'layerForm',
            		title : '',
            		width: '',
            		height: '',
            		url : null,
            		scroll : false,
            		closeBtn : 1,
                    zIndex:100,
            		data : {},
            		btn: ['确定', '取消'],
            		maxmin:true,
            		success: function(){},
            		yes: function(){}
            }
            var option = $.extend({}, defaults, opt), content = null;
            if(option.scroll){
                content = [option.url]
            }else{
                content = [option.url, 'no']
            }
            var $layer = okUtils.getLayer();
            var layerIndex = $layer.open({
                type : 2,
                id : option.id,
                title : option.title,
                closeBtn : 1,
                anim: -1,
                isOutAnim: false,
                shadeClose : false,
                shade : 0.3,
                area : [option.width, option.height],
                content : content,
                btn: option.btn,
                maxmin:option.maxmin,
                success: function(layero, index){
                    var dialog = layero.find("iframe")[0].contentWindow;
                	option.success(dialog,layero,index);
                },
                yes: function(index, layero){
                    var dialog = layero.find("iframe")[0].contentWindow;
                    option.yes(dialog,layero,index);
                    //$layer.close(layerIndex);
                }
            });
            return layerIndex;
        },
        addData:function(num){
            var day = new Date();
            day.setDate(day.getDate() + num);
            return day.format("yyyy-MM-dd")+" 00:00:00";
        },
        subData:function(num){
            var day = new Date();
            day.setDate(day.getDate() - num);
            return day.format("yyyy-MM-dd")+" 00:00:00";
        }
    };
    exprots("okUtils", okUtils);
});
