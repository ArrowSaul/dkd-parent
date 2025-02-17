package com.dkd.manage.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.dkd.common.constant.DkdContants;
import com.dkd.common.utils.DateUtils;
import com.dkd.common.utils.uuid.UUIDUtils;
import com.dkd.manage.domain.Channel;
import com.dkd.manage.domain.Node;
import com.dkd.manage.domain.VmType;
import com.dkd.manage.service.IChannelService;
import com.dkd.manage.service.INodeService;
import com.dkd.manage.service.IVmTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dkd.manage.mapper.VendingMachineMapper;
import com.dkd.manage.domain.VendingMachine;
import com.dkd.manage.service.IVendingMachineService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 设备管理Service业务层处理
 * 
 * @author itheima
 * @date 2025-02-17
 */
@Service
public class VendingMachineServiceImpl implements IVendingMachineService 
{
    @Autowired
    private VendingMachineMapper vendingMachineMapper;
    @Autowired
    private IVmTypeService vmTypeService;
    @Autowired
    private INodeService nodeService;
    @Autowired
    private IChannelService channelService;

    /**
     * 查询设备管理
     * 
     * @param id 设备管理主键
     * @return 设备管理
     */
    @Override
    public VendingMachine selectVendingMachineById(Long id)
    {
        return vendingMachineMapper.selectVendingMachineById(id);
    }

    /**
     * 查询设备管理列表
     * 
     * @param vendingMachine 设备管理
     * @return 设备管理
     */
    @Override
    public List<VendingMachine> selectVendingMachineList(VendingMachine vendingMachine)
    {
        return vendingMachineMapper.selectVendingMachineList(vendingMachine);
    }

    /**
     * 新增设备管理
     * 
     * @param vendingMachine 设备管理
     * @return 结果
     */
    @Transactional
    @Override
    public int insertVendingMachine(VendingMachine vendingMachine)
    {
        //1. 新增设备
        //1-1 生成8位的唯一标识，补充货道编号
        String innerCode = UUIDUtils.getUUID();
        vendingMachine.setInnerCode(innerCode);
        //1-2 查询售货机类型表，补充设备容量
        VmType vmType = vmTypeService.selectVmTypeById(vendingMachine.getVmTypeId());
        vendingMachine.setChannelMaxCapacity(vmType.getChannelMaxCapacity());
        //1-3 查询点位表补充区域点位合作商等信息
        Node node = nodeService.selectNodeById(vendingMachine.getNodeId());
        BeanUtils.copyProperties(node,vendingMachine,"id");//商圈类型，区域，合作商
        vendingMachine.setAddr(node.getAddress());//设备地址
        //1-4 设备状态
        vendingMachine.setVmStatus(DkdContants.VM_STATUS_NODEPLOY);//0-代表未投放
        vendingMachine.setCreateTime(DateUtils.getNowDate());//创建时间
        vendingMachine.setUpdateTime(DateUtils.getNowDate());//更新时间
        //1-5 保存
        int result = vendingMachineMapper.insertVendingMachine(vendingMachine);
        //2. 新增货道
        //2-1 声明货道集合
        List<Channel> channelList = new ArrayList<>();
        //2-2 双重for循环
        for (int i = 1; i <= vmType.getVmRow(); i++) {//外层行
            for (int j = 1; j <= vmType.getVmCol(); j++) {//内层列
                //2-3 封装channel对象
                Channel channel = new Channel();
                channel.setChannelCode(i + "-" + j);//货道编号
                channel.setVmId(vendingMachine.getId());//售货机id
                channel.setInnerCode(vendingMachine.getInnerCode());//售货机编号
                channel.setMaxCapacity(vmType.getChannelMaxCapacity());//货道最大容量
                channel.setCreateTime(DateUtils.getNowDate());//创建时间
                channel.setUpdateTime(DateUtils.getNowDate());//更新时间
                channelList.add(channel);
            }
        }
        //2-4 批量保存
        channelService.batchInsertChannels(channelList);
        return result;
    }

    /**
     * 修改设备管理
     * 
     * @param vendingMachine 设备管理
     * @return 结果
     */
    @Override
    public int updateVendingMachine(VendingMachine vendingMachine)
    {
        // 查询点位表，补充区域点位合作商等信息
        Node node = nodeService.selectNodeById(vendingMachine.getNodeId());
        BeanUtils.copyProperties(node,vendingMachine,"id");//包含商圈类型，区域，合作商
        vendingMachine.setAddr(node.getAddress());//设备地址
        vendingMachine.setUpdateTime(DateUtils.getNowDate());//更新时间
        return vendingMachineMapper.updateVendingMachine(vendingMachine);
    }

    /**
     * 批量删除设备管理
     * 
     * @param ids 需要删除的设备管理主键
     * @return 结果
     */
    @Override
    public int deleteVendingMachineByIds(Long[] ids)
    {
        return vendingMachineMapper.deleteVendingMachineByIds(ids);
    }

    /**
     * 删除设备管理信息
     * 
     * @param id 设备管理主键
     * @return 结果
     */
    @Override
    public int deleteVendingMachineById(Long id)
    {
        return vendingMachineMapper.deleteVendingMachineById(id);
    }
}
