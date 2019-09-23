package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

/**
 * 品牌接口
 * @author Administrator
 *
 */
public interface BrandService {

	public List<TbBrand> findAll();

	/**
	 * 分页查询
	 * @param pageNum 分页页数
	 * @param pageSize 每页行数
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);

	/**
	 * 新增商品种类
	 * @param tbBrand
	 */
	public  void addBrand(TbBrand tbBrand);


	/**
	 *
	 * @param tbBrand 修改后的参数
	 */
	public void updateBrand(TbBrand tbBrand);


	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public TbBrand findOne(long id);


	/**
	 * 多个删除
	 * @param ids
	 */
	public void deleteAll(Long[] ids);


    /**
     *
     * @param tbBrand
     * @param pageNum
     * @param pageSize
     * @return
     */
	public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);


	/**
	 * 查找所有品牌的id和name
	 * @return
	 */
	List<Map> selectOptionList();
}
