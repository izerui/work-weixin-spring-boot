package com.github.izerui.weixin;

import com.github.izerui.weixin.impl.mapping.*;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;

import java.util.List;

/**
 * 服务商license服务
 */
public interface TpLicenseService {

    /**
     * 获取第三方服务商服务对象
     *
     * @return
     */
    TpService getTpService();

    /**
     * 下单购买帐号
     *
     * @param tenantId   租户ID
     * @param accountNum 购买账号数量
     * @param external   是否互通账号，默认基础账号
     * @param months     月份数量
     * @return 订单号
     */
    String createOrder(String tenantId, Integer accountNum, boolean external, Integer months) throws WxErrorException;

    /**
     * 创建续期任务
     *
     * @param tenantId    租户ID
     * @param authUserIds 续期企业的成员userid。只支持加密的userid   续期帐号类型。1:基础帐号，2:互通帐号
     * @return
     * @throws WxErrorException
     */
    RenewUserJobResp renewOrderJob(String tenantId, List<RenewUserJobReq> authUserIds) throws WxErrorException;


    /**
     * 提交续期订单
     *
     * @param jobId jobId
     * @param months 月份数量
     * @return 订单Id
     * @throws WxErrorException
     */
    String submitOrderJob(String jobId, Integer months) throws WxErrorException;


    /**
     * 获取订单中的帐号列表
     *
     * @param orderId 订单号
     * @param limit   返回的最大记录数，整型，最大值1000，默认值500
     * @param cursor  用于分页查询的游标，字符串类型，由上一次调用返回，首次调用可不填
     * @return
     */
    ListOrderAccountsResp listOrderAccounts(String orderId, Integer limit, String cursor) throws WxErrorException;

     /**
     * https://developer.work.weixin.qq.com/document/path/95553
     * 激活账号，以进一步可使第三方应用拥有接口的调用许可
     *
     * @param tenantId   租户ID
     * @param activeCode 帐号激活码
     * @param authUserId 待绑定激活的企业成员userid 。只支持加密的userid
     * @return
     * @throws WxErrorException
     */
    WxCpBaseResp activeAccount(String tenantId, String activeCode, String authUserId) throws WxErrorException;

    /**
     * 批量激活账号
     * @param tenantId 租户ID
     * @param  activeAccountReps 激活码、企业成员id
     * @return ActiveAccountResp
     * @Throws WxErrorException
     */
    ActiveAccountResp batchActiveAccount(String tenantId,List<ActiveAccountRep> activeAccountReps) throws WxErrorException;

    /**
     *
     * 批量转移成员
     * @param tenantId 租户ID
     * @param transferLicenseReps handoverUserId:企业转移成员userid takeoverUserId:接收成员userid 均只支持加密的userid
     * @return WxCpTransferRespVo
     * @throws WxErrorException
     */
    TransferLicenseResp batchTransferLicense(String tenantId, List<TransferLicenseRep> transferLicenseReps) throws WxErrorException;

}
