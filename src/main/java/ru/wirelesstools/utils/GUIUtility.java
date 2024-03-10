package ru.wirelesstools.utils;

import net.minecraft.util.text.TextFormatting;

public class GUIUtility {
    
    public static String formatNumber(double number) {
        double logarithm = Math.log10(number);
        if(logarithm < 3.0)
            return String.format("%.0f", number);
        else if(logarithm >= 3.0 && logarithm < 6.0)
            return String.format("%.2f K", number / 1E3);
        else if(logarithm >= 6.0 && logarithm < 9.0)
            return String.format("%.2f M", number / 1E6);
        else if(logarithm >= 9.0 && logarithm < 12.0)
            return String.format("%.2f G", number / 1E9);
        else if(logarithm >= 12.0 && logarithm < 15.0)
            return String.format("%.2f T", number / 1E12);
        else if(logarithm >= 15.0 && logarithm < 18.0)
            return String.format("%.2f P", number / 1E15);
        else if(logarithm >= 18.0 && logarithm < 21.0)
            return String.format("%.2f E", number / 1E18);
        else if(logarithm >= 21.0 && logarithm < 24.0)
            return String.format("%.2f Z", number / 1E21);
        else if(logarithm >= 24.0 && logarithm < 27.0)
            return String.format("%.2f Y", number / 1E24);
        else
            return String.valueOf(number);
    }
    
    public enum TextColorsMC {
        black(TextFormatting.BLACK, 0),
        dark_blue(TextFormatting.DARK_BLUE, 170),
        dark_green(TextFormatting.DARK_GREEN, 43520),
        dark_aqua(TextFormatting.DARK_AQUA, 43690),
        dark_red(TextFormatting.DARK_RED, 11141120),
        dark_purple(TextFormatting.DARK_PURPLE, 11141290),
        gold(TextFormatting.GOLD, 16755200),
        gray(TextFormatting.GRAY, 11184810),
        dark_gray(TextFormatting.DARK_GRAY, 5592405),
        blue(TextFormatting.BLUE, 5592575),
        green(TextFormatting.GREEN, 5635925),
        aqua(TextFormatting.AQUA, 5636095),
        red(TextFormatting.RED, 16733525),
        light_purple(TextFormatting.LIGHT_PURPLE, 16733695),
        yellow(TextFormatting.YELLOW, 16777045),
        white(TextFormatting.WHITE, 16777215);
        
        private final TextFormatting textFormatting;
        private final int intColor;
        
        TextColorsMC(TextFormatting tf, int colorInt) {
            this.textFormatting = tf;
            this.intColor = colorInt;
        }
        
        public TextFormatting getTextFormat() {
            return this.textFormatting;
        }
        
        public int getIntColor() {
            return this.intColor;
        }
    }
    
}
