package com.illtamer.infinite.bot.api.event;

/**
 * 事件取消接口
 * <p>
 * 仅可取消 invoke 其它监听方法，原 QQ 事件已既定触发
 * @apiNote 事件取消逻辑处理需自行实现
 * */
public interface Cancellable {

    /**
     * 设置取消事件
     * */
    void setCancelled(boolean cancelled);

    /**
     * @return 事件是否取消
     * */
    boolean isCancelled();

}
