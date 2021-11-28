//除法函数，用来得到精确的除法结果
function accDiv(arg1, arg2, num) {
    arg1 = arg1 == null ? 0 : arg1;
    arg2 = arg2 == null ? 0 : arg2;
    var t1 = 0, t2 = 0, r1, r2;
    try {
        t1 = arg1.toString().split(".")[1].length;
    } catch (e) {
    }
    try {
        t2 = arg2.toString().split(".")[1].length;
    } catch (e) {
    }
    with (Math) {
        r1 = Number(arg1.toString().replace(".", ""));
        r2 = Number(arg2.toString().replace(".", ""));
        num = num==undefined?2:num;
        return ((r1 / r2) * pow(10, t2 - t1)).toFixed(num);
    }
}

//乘法函数，用来得到精确的乘法结果
function accMul(arg1, arg2) {
    arg1 = arg1 == null ? 0 : arg1;
    arg2 = arg2 == null ? 0 : arg2;
    var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
    try {
        m += s1.split(".")[1].length;
    } catch (e) {
    }
    try {
        m += s2.split(".")[1].length;
    } catch (e) {
    }
    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
}

//加法函数，用来得到精确的加法结果
function accAdd(arg1, arg2) {
    arg1 = arg1 == null ? 0 : arg1;
    arg2 = arg2 == null ? 0 : arg2;
    var r1, r2, m;
    try {
        r1 = arg1.toString().split(".")[1].length;
    } catch (e) {
        r1 = 0;
    }
    try {
        r2 = arg2.toString().split(".")[1].length;
    } catch (e) {
        r2 = 0;
    }
    m = Math.pow(10, Math.max(r1, r2));
    return (accMul(arg1, m) + accMul(arg2, m)) / m;
}

//减法函数
function accSub(arg1, arg2) {
    arg1 = arg1 == null ? 0 : arg1;
    arg2 = arg2 == null ? 0 : arg2;
    var r1, r2, m, n;
    try {
        r1 = arg1.toString().split(".")[1].length;
    } catch (e) {
        r1 = 0;
    }
    try {
        r2 = arg2.toString().split(".")[1].length;
    } catch (e) {
        r2 = 0;
    }
    m = Math.pow(10, Math.max(r1, r2));
    //last modify by deeka
    //动态控制精度长度
    n = (r1 >= r2) ? r1 : r2;
    return ((arg2 * m - arg1 * m) / m).toFixed(n);
}
/**
 * 不足digit的前面补0
 * @param num
 * @param digit
 * @returns {string}
 */
function zeroPadding(num, digit) {
    var zero = '';
    for (var i = 0; i < digit; i++) {
        zero += '0';
    }
    return (zero + num).slice(-digit);
}
