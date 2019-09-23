package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods implements Serializable {
    private TbGoods goods; //商品的SPU
    private TbGoodsDesc goodsDesc;  //商品的SPU拓展
    private List<TbItem> itemList; //商品SKU列表
}
