var app=angular.module("pinyougou",[]);


//添加过滤器
//$sce服务中的trustAsHtml方法用于信任html格式的数据
app.filter("trustHtml",["$sce",function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]
);