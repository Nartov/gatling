package com.company;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args)
    {
        // write your code here
        String input = "2as3(kd2(ab))";
        String output = new String(input);

        Pattern patternD = Pattern.compile("\\d");
        Matcher matcherD = patternD.matcher(input);

        while (matcherD.find()) {
            String regexp = "\\d\\(\\w*\\)";
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                String toRepeat = input.substring(matcher.start() + 2, matcher.end() - 1);
                int count = Integer.parseInt(input.charAt(matcher.start()) + "");
                String repeated = repeat(count, toRepeat);
                output = matcher.replaceFirst(repeated);
            } else {
                Pattern pattern1 = Pattern.compile("\\d");
                Matcher matcher1 = pattern1.matcher(input);

                matcher1.find();
                String toRepeat = input.substring(matcher1.start() + 1, matcher1.end() + 1);
                int count = Integer.parseInt(input.substring(matcher1.start(), matcher1.end()));
                String repeated = repeat(count, toRepeat);
                output = matcher1.replaceFirst(repeated);
            }
            input = output;
        }
        System.out.println(output);
    }

    public static String repeat(int count, String with)
    {
        return new String(new char[count]).replace("\0", with);
    }
}
