package com.example.rgwaimai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rgwaimai.entity.OrderDetail;
import com.example.rgwaimai.mapper.OrderDetailMapper;
import com.example.rgwaimai.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}