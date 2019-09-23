package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();		
	}



	@RequestMapping("/findPage")
	public PageResult findPage(int pageNum,int pageSize){
		return brandService.findPage(pageNum,pageSize);
	}


	@RequestMapping("/add")
	public Result addBrand(@RequestBody TbBrand tbBrand){

		/**
		 *
		 * 		缺少数据的校验，空指针，重复数据的校验，、
		 * 		TbBrand{id=null, name='null', firstChar='null'}
		 * 		重复校验可以先检查数据有无，然后判断校验
		 * 		try-catch捕获数据库抛出的异常
		 */


		//在springmvc中使用hibernate的校验框架validation
		//https://blog.csdn.net/lpckr94/article/details/80962084
		try {
			brandService.addBrand(tbBrand);
			return new Result(true,"添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"添加失败");
		}
	}


	@RequestMapping("/findOne")
	public TbBrand findOne(Long id){
		return brandService.findOne(id);
	}


	@RequestMapping("/update")
	public Result updateBrand(@RequestBody TbBrand tbBrand){
		try {
			brandService.updateBrand(tbBrand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}

	@RequestMapping("/deletAll")
	public  Result deleteAllBrand(Long[] ids){
		try {
			brandService.deleteAll(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}

	@RequestMapping("search")
	public PageResult search(@RequestBody TbBrand tbBrand, int pageNum,int pageSize){
		return brandService.findPage(tbBrand,pageNum,pageSize);
	}

	/**
	 * 查询所有品牌的id和name
	 * @return
	 */
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList() {
		return  brandService.selectOptionList();
	}

}
