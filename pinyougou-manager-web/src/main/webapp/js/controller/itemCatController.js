 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
		$scope.findTypeTmpList();
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
				for(var j=0;j<$scope.typeTemList.data.length;j++){
					if($scope.typeTemList.data[j].id==response.typeId){
						$scope.entity.typeId=$scope.typeTemList.data[j];
						//alert($scope.entity.typeId);
					}
				}
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
		var serviceObject;//服务层对象
		//种类id和例外一张表关联
		$scope.entity.typeId=$scope.entity.typeId.id;
		alert($scope.parentId);
		$scope.entity.parentId=$scope.parentId;
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					$scope.findByParentId($scope.parentId);
					//重新查询 
					//$scope.findByParentId($scope.parentId);
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.findByParentId($scope.parentId);
					//$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}



	//查询分类
	$scope.findByParentId=function (parentId) {
		itemCatService.findByParentId(parentId).success(
			function (response) {
				$scope.list=response;
			}
		);
	}

	$scope.grade=1; //定义级别
	//设置级别
	$scope.setGrade=function (value) {
		$scope.grade=value;
	}

	$scope.parentId=0;   //商品的父id
	$scope.selectList=function(p_entity){
		$scope.parentId=p_entity.id;
		//alert($scope.parentId);
		if($scope.grade==1){
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if($scope.grade==2){
			$scope.entity_1=p_entity;
			$scope.entity_2=null;
		}
		if($scope.grade==3){
			$scope.entity_2=p_entity;
		}
		$scope.findByParentId(p_entity.id);
	}



	//查找模板的id集合
	$scope.typeTemList={data:[]};
	$scope.findTypeTmpList=function(){
		itemCatService.findTypeTmpList().success(
			function (response) {
				$scope.typeTemList.data=response.list;
			}
		);
	}

    
});	
