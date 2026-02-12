package com.illtamer.infinite.bot.minecraft.api.distribute;

import com.illtamer.perpetua.sdk.entity.transfer.entity.Client;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 分布式事件执行结果
 * 包含所有客户端执行结果的汇总
 */
@Data
public class DistributedResult<T> {

    /**
     * 所有客户端执行结果列表
     * 包括本地客户端和其他客户端的结果
     */
    private List<T> dataList = Collections.emptyList();

    /**
     * 执行失败的客户端列表
     */
    private List<Client> failedClientList = Collections.emptyList();

    /**
     * 执行成功的客户端数量
     */
    public int getSuccessCount() {
        return dataList.size();
    }

    /**
     * 执行失败的客户端数量
     */
    public int getFailedCount() {
        return failedClientList.size();
    }

    /**
     * 总客户端数量
     */
    public int getTotalCount() {
        return getSuccessCount() + getFailedCount();
    }

    /**
     * 是否全部成功
     */
    public boolean isAllSuccess() {
        return failedClientList.isEmpty();
    }

    /**
     * 是否部分成功
     */
    public boolean isPartialSuccess() {
        return !dataList.isEmpty() && !failedClientList.isEmpty();
    }

    /**
     * 是否全部失败
     */
    public boolean isAllFailed() {
        return dataList.isEmpty() && !failedClientList.isEmpty();
    }

    /**
     * 获取第一个成功的数据
     */
    public T getFirstData() {
        return dataList.isEmpty() ? null : dataList.get(0);
    }

    /**
     * 获取所有成功数据的合并结果
     * 适用于数据可合并的场景
     */
    public <R> R mergeData(MergeFunction<T, R> mergeFunction) {
        return mergeFunction.merge(dataList);
    }

    /**
     * 数据合并函数接口
     */
    public interface MergeFunction<T, R> {
        R merge(List<T> dataList);
    }

}