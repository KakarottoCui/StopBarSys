//$(document).bind("contextmenu", function () { return false; });
//document.oncontextmenu = function () { return false; };
//document.onkeydown = function () {
//    if (window.event && window.event.keyCode == 123) {
//        event.keyCode = 0;
//        event.returnValue = false;
//        return false;
//    }
//};

window.onload=function(){
    var imagesPath = "../images/";
    var img =["bg0.jpg", "bg1.jpg", "bg2.jpg", "bg3.jpg", "bg4.jpg","bg5.jpg", "bg6.jpg", "bg7.jpg","bg8.jpg"];    //（设定想要显示的图片）
    var i = 0;
    var head=document.getElementById("login");//获取DIV对象
    head.style.width="100%";
    head.style.height="100%";
    head.style.position="relative";
    function time(){
       i++;
       i=i%9;
       console.log(imagesPath+img[i])
       head.style.backgroundImage="url("+imagesPath+img[i]+")";
    }
    setInterval(time,10000);//循环调用time1()函数，时间间隔为2000ms
}