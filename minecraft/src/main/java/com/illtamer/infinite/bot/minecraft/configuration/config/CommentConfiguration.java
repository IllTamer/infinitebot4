package com.illtamer.infinite.bot.minecraft.configuration.config;


import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentConfiguration extends YamlConfiguration {

    protected static String newLine = "\n";
    // 新增保留注释字段
    protected static String commentPrefixSymbol = "'注释 ";
    protected static String commentSuffixSymbol = "': 注释";
    protected static String fromRegex = "( *)(#.*)";
    protected static Pattern fromPattern = Pattern.compile(fromRegex);
    protected static String toRegex = "( *)(- )*" + "(" + commentPrefixSymbol + ")" + "(#.*)" + "(" + commentSuffixSymbol + ")";
    protected static Pattern toPattern = Pattern.compile(toRegex);
    protected static Pattern countSpacePattern = Pattern.compile("( *)(- )*(.*)");
    protected static int commentSplitWidth = 90;

    @Override
    public void loadFromString(String contents) throws InvalidConfigurationException {
        String[] parts = contents.split(newLine);
        List<String> lastComments = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            Matcher matcher = fromPattern.matcher(part);
            if (matcher.find()) {
                String originComment = matcher.group(2);
                String[] splitComments = split(originComment, commentSplitWidth);
                for (int i = 0; i < splitComments.length; ++ i) {
                    String comment = splitComments[i];
                    if (i == 0) {
                        comment = comment.substring(1);
                    }
                    comment = COMMENT_PREFIX + comment;
                    lastComments.add(comment.replaceAll("\\.", "．").replaceAll("'", "＇").replaceAll(":", "："));
                }
            } else {
                matcher = countSpacePattern.matcher(part);
                if (matcher.find() && !lastComments.isEmpty()) {
                    for (String comment : lastComments) {
                        builder.append(matcher.group(1));
                        builder.append(checkNull(matcher.group(2)));
                        builder.append(commentPrefixSymbol);
                        builder.append(comment);
                        builder.append(commentSuffixSymbol);
                        builder.append(newLine);
                    }
                    lastComments.clear();
                }
                builder.append(part);
                builder.append(newLine);
            }
        }
        super.loadFromString(builder.toString());
    }

    @NotNull
    @Override
    public String saveToString() {
        String contents = super.saveToString();
        StringBuilder saveContent = new StringBuilder();
        String[] parts = contents.split(newLine);
        for (String part : parts) {
            Matcher matcher = toPattern.matcher(part);
            if (matcher.find() && matcher.groupCount() == 5) {
                part = checkNull(matcher.group(1)) + matcher.group(4);
            }
            saveContent.append(part.replaceAll("．", ".").replaceAll("＇", "'").replaceAll("：", ":"));
            saveContent.append(newLine);
        }
        return saveContent.toString();
    }

    private static String[] split(String string, int partLength) {
        String[] array = new String[string.length() / partLength + 1];
        for (int i = 0; i < array.length; i++) {
            int beginIndex = i * partLength;
            int endIndex = beginIndex + partLength;
            if (endIndex > string.length()) {
                endIndex = string.length();
            }
            array[i] = string.substring(beginIndex, endIndex);
        }
        return array;
    }

    private static String checkNull(String string) {
        return string == null ? "" : string;
    }

}
