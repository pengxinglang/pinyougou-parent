 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,$location,goodsService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
		//获取路径中参数名为id的值，路径参数？前必须要有#才能读取到
		//search方法是将参数全部封装成数组
		var id =$location.search()['id'];
		//alert(id);
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//向富文本编辑器添加商品介绍
				editor.html($scope.entity.goodsDesc.introduction);
				//读取图片列表
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//显示扩展属性
				$scope.entity.goodsDesc.customAttributeItems= JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//规格
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);

				//SKU列表规格列转换
				for( var i=0;i<$scope.entity.itemList.length;i++ ){
					$scope.entity.itemList[i].spec =
						JSON.parse( $scope.entity.itemList[i].spec);
				}
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
		//editor是富文本编辑器的里面的封装的方法
		//editor.html()将富文本编辑器里面的格式转化为html格式
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;
		if($scope.entity.goods.id!=null){
			serviceObject=goodsService.update($scope.entity);
		}else{
			serviceObject=goodsService.add( $scope.entity  );
		}
		serviceObject.success(
			function(response){
				if(response.success){
					alert("保存成功");
					location.href="goods.html";//跳转到商品列表页

					/*$scope.entity={};
					editor.html('');//清空富文本编辑器*/
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


	//上传文件
	/*$scope.upload=function () {
        uploadService.upload().success(
            function (response) {
                if(response.success){

                    $scope.image_entity.url=response.message;
                }else{
                    alert(response.message);
                }
            }
        )
    }*/



	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};  //定义实体类型结构
	//将上传的图片加到列表中
	$scope.add_image_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}

	//列表中移除图片
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}


	//查询第一个列表值
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {
				$scope.itemCast1List=response;
			}
		);
	}

	//查询第二个列表值
	//$watch是系统内置的方法，用于监控数值的变化，一变化就调用该方法
	//watch方法，第一个参数：监控的变量；第二个参数：function方法
	//function方法中，第一个参数：变量更改前的数值
	$scope.$watch('entity.goods.category1Id',function (newVale, oldValue) {
		itemCatService.findByParentId(newVale).success(
			function (response) {
				$scope.itemCast2List=response;
			}
		);
	});


	//查询第三个列表值
	$scope.$watch('entity.goods.category2Id',function (newVale, oldValue) {
		itemCatService.findByParentId(newVale).success(
			function (response) {
				$scope.itemCast3List=response;
			}
		);
	});


	//查询模板id
	$scope.$watch('entity.goods.category3Id',function (newValue, oldValue) {
		itemCatService.findOne(newValue).success(
			function (response) {
				$scope.entity.goods.typeTemplateId=response.typeId;
		});
	});

	//查询品牌 扩展属性	规格列表
	$scope.$watch('entity.goods.typeTemplateId',function (newValue, oldValue) {
		typeTemplateService.findOne(newValue).success(
			function (response) {
				$scope.typeTemplate=response;
				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
				//拓展属性
				//如果没有ID，则加载模板中的扩展数据
				if($location.search()['id']==null) {
					$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
				}
			}
		);
		//查询规格列表
		typeTemplateService.findSpecList(newValue).success(
			function (response) {
				$scope.specList=response;
			}
		);
	});



	//增加选项规格选项, $event.target  表示当前的标签元素
	$scope.updateSpecAttribute=function ($event,name, value) {
		//为什么只改变obj $scope.entity.goodsDesc.specificationItems的值也会跟着改变？？？？？？？？？？？？？？？
		//js对集合数组直接引用只会进行浅复制
		var obj=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if(obj!=null){
			if($event.target.checked){
				obj.attributeValue.push(value);
			}else {
				obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);  //1表示往后移出一个
				if(obj.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj),1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}


	//显示SKU列表
	/*大体思路
		遍历 选中的规格 specificationItems
		在遍历每一行记录
		再遍历规格中每一个选项，并在每一个记录前加上规格选项，进行深拷贝赋值

		每一次规格选项的改变都要从新遍历一次

	 */

	$scope.createItemList=function () {
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];//初始，一条记录的值
		var items=  $scope.entity.goodsDesc.specificationItems;   //选中的规格，以及规格参数
		for(var k=0;k<items.length;k++){       //遍历规格
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[k].attributeName,items[k].attributeValue);
		}
	}

	//增加每行记录的方法
	addColumn=function (list, columnName, columnValues) {
		var newList=[];                                        //新的记录列表
		for(var i=0;i<list.length;i++){                               //变量记录列表
			var oldRow=list[i];
			for(var j=0;j<columnValues.length;j++){              //变量规格参数
				var newRow=JSON.parse(JSON.stringify(oldRow));     //将数据进行深拷贝，不然循环数据会一直改变，叠加
				newRow.spec[columnName]=columnValues[j];            //将值赋值到spec下
				newList.push(newRow);                     //将遍历后的值存入一个记录列表中，进行下一次的变量
			}
		}
		return newList;
	}

	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态


	$scope.itemCatList=[]  //商品分类列表

	$scope.findItemCatList=function () {
		itemCatService.findAll().success(
			function (response) {
				for(var i=0;i<response.length;i++){
					//将分类列表数据全部查询到内存中，前端查询
					//数据格式，数组的下标值为分类的id，数组的值为分类的名字
					$scope.itemCatList[response[i].id]=response[i].name;
				}

			}
		)
	}

	//检验显示规格参数和规格选项信息
	$scope.checkAttributeValue=function (specName,optionName) {
		var item=$scope.entity.goodsDesc.specificationItems;
		var obj=$scope.searchObjectByKey(item,'attributeName',specName);
		if(obj!=null){
			if(obj.attributeValue.indexOf(optionName)>=0){  //则说明存在该值
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}

	//更改状态
	$scope.updateStatus=function(status){
		goodsService.updateStatus($scope.selectIds,status).success(
			function(response){
				if(response.success){//成功
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];//清空ID集合
				}else{
					alert(response.message);
				}
			}
		);
	}


});	
