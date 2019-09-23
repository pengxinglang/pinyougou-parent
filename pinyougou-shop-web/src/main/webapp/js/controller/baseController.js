app.controller("baseController",function ($scope) {

    //重新加载列表 数据
    $scope.reloadList=function(){
        //切换页码
        $scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    //分页控件配置
    /*
    * paginationConf 变量各属性的意义：
    currentPage：当前页码
    totalItems:总条数
    itemsPerPage:
    perPageOptions：页码选项
    onChange：更改页面时触发事件
    * */
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };


    $scope.selectIds=[];   //选中的id集合
    //获取选中集合
    $scope.updateSelection=function ($event,id) {
    if($event.target.checked){    //如果被选中添加到数组
        $scope.selectIds.push(id);
    }else {
        let index=$scope.selectIds.indexOf(id);   //获取该值的数组下标值
        $scope.selectIds.splice(index);  //根据下标删除值
    }
}

    //提取json字符串数据中某个属性，返回拼接字符串 逗号分隔
    $scope.jsonToString=function(jsonString,key){
        var json=JSON.parse(jsonString);//将json字符串转换为json对象
        var value="";
        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=","
            }
            value+=json[i][key];
        }
        return value;
    }

//从集合中按照key查询对象
    $scope.searchObjectByKey=function(list,key,keyValue){
        for(var i=0;i<list.length;i++){
            //i表示第几个数组，key表示键名
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }

})