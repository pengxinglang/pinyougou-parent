package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbBrandExample;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;


@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;
	
	@Override
	public List<TbBrand> findAll() {

		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int pageNum, int pageSize) {

		PageHelper.startPage(pageNum,pageSize);
		Page<TbBrand> tbBrands = (Page<TbBrand>) brandMapper.selectByExample(null);

		return new PageResult(tbBrands.getTotal(),tbBrands.getResult());
	}

	@Override
	public void addBrand(TbBrand tbBrand) {
		brandMapper.insert(tbBrand);
	}

	@Override
	public void updateBrand(TbBrand tbBrand) {
		brandMapper.updateByPrimaryKey(tbBrand);
	}

	@Override
	public TbBrand findOne(long id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void deleteAll(Long[] ids) {
		for (Long id : ids) {
			brandMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {

		PageHelper.startPage(pageNum,pageSize);
		TbBrandExample example=new TbBrandExample();

		if(tbBrand != null){
			TbBrandExample.Criteria criteria = example.createCriteria();
			if(tbBrand.getName() !=null && tbBrand.getName().trim().length()>0){
				criteria.andNameLike("%"+tbBrand.getName().trim()+"%");
			}
			if(tbBrand.getFirstChar()!=null && tbBrand.getFirstChar().trim().length()>0){
				criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
			}
		}
		Page<TbBrand> page= (Page<TbBrand>)brandMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());

	}

	@Override
	public List<Map> selectOptionList() {
		return brandMapper.selectOptionList();
	}

}
