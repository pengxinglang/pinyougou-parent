package com.pinyougou.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    private SellerService sellerService;



    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        //创建角色列表
        List<GrantedAuthority> authorities=new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        TbSeller seller = sellerService.findOne(userName);
        if(seller!=null){
            if(seller.getStatus().equals("1")){
                //security框架将传入的用户名与密码进行比较赋予角色
                return new User(userName,seller.getPassword(),authorities);
            }else {
                return null;
            }
        }else {
            return null;
        }
    }
}
