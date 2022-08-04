package com.illtamer.infinite.bot.api.message;

import com.illtamer.infinite.bot.api.exception.ExclusiveMessageException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class Message implements Cloneable {

   /**
    * 获取消息元素链
    * */
   @NotNull
   abstract public MessageChain getMessageChain();

   /**
    * 仅获取消息中的 text 元素
    * */
   @NotNull
   abstract public List<String> getCleanMessage();

   /**
    * 可重复种类消息添加
    * */
   abstract protected void add(String type, Map<String, @Nullable Object> data);

   /**
    * @throws ExclusiveMessageException 单一消息类型异常
    * */
   abstract protected void addExclusive(String type, Map<String, @Nullable Object> data);

   /**
    * 是否仅为文本消息
    * */
   abstract public boolean isTextOnly();

   abstract public Message clone();

   abstract public String toString();

}
