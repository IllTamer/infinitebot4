package com.illtamer.infinite.bot.minecraft.expansion;

import com.illtamer.infinite.bot.api.util.Assert;
import com.illtamer.infinite.bot.minecraft.api.IExpansion;

/**
 * 语言消息配置类
 * */
public class Language {

    private final ExpansionConfig lanConfig;

    protected Language(String fileName, IExpansion expansion) {
        this.lanConfig = new ExpansionConfig(fileName + ".yml", expansion);
    }

    /**
     * 获取语言文本
     * @param nodes 配置节点
     * */
    public String get(String... nodes) {
        Assert.notEmpty(nodes, "Language nodes can not be empty!");
        StringBuilder builder = new StringBuilder();
        for (String node : nodes) {
            Assert.notEmpty(node, "Language node can not be null!");
            builder.append('.').append(node);
        }
        builder.deleteCharAt(0);
        return lanConfig.getConfig().getString(builder.toString());
    }

    /**
     * 重新载入语言文件
     * */
    public void reload() {
        lanConfig.reload();
    }

    public static Language of(IExpansion expansion) {
        return of("language", expansion);
    }

    /**
     * @param name 语言文件前缀名 language
     * */
    public static Language of(String name, IExpansion expansion) {
        return of(name, "zh_CN", expansion);
    }

    /**
     * @param name 语言文件前缀名 language
     * @param type 语言类型后缀 zh_CN
     * */
    public static Language of(String name, String type, IExpansion expansion) {
        return new Language(name + '-' + type, expansion);
    }

}
