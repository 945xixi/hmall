package com.hmall.trace.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hmall.trace.domain.po.OrderLogistics;
import com.hmall.trace.mapper.OrderLogisticsMapper;
import com.hmall.trace.service.IOrderLogisticsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Service
public class OrderLogisticsServiceImpl extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements IOrderLogisticsService {

}
