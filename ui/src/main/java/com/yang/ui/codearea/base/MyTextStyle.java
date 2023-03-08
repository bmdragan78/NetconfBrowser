package com.yang.ui.codearea.base;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yang.ui.codearea.fxmisc.richtext.model.Codec;

import javafx.scene.paint.Color;


/**
 * Holds information about the style of a text fragment.
 */
public class MyTextStyle {

    public static final MyTextStyle EMPTY = new MyTextStyle();
    
    public static final Codec<MyTextStyle> CODEC = new Codec<MyTextStyle>() {

        private final Codec<Optional<String>> OPT_STRING_CODEC =
                Codec.optionalCodec(Codec.STRING_CODEC);
        private final Codec<Optional<Color>> OPT_COLOR_CODEC =
                Codec.optionalCodec(Codec.COLOR_CODEC);

        @Override
        public String getName() {
            return "text-style";
        }

        @Override
        public void encode(DataOutputStream os, MyTextStyle s)
                throws IOException {
            os.writeByte(encodeBoldItalicUnderlineStrikethrough(s));
            os.writeInt(encodeOptionalUint(s.fontSize));
            OPT_STRING_CODEC.encode(os, s.fontFamily);
            OPT_COLOR_CODEC.encode(os, s.textColor);
            OPT_COLOR_CODEC.encode(os, s.backgroundColor);
        }

        @Override
        public MyTextStyle decode(DataInputStream is) throws IOException {
            byte bius = is.readByte();
            Optional<Integer> fontSize = decodeOptionalUint(is.readInt());
            Optional<String> fontFamily = OPT_STRING_CODEC.decode(is);
            Optional<Color> textColor = OPT_COLOR_CODEC.decode(is);
            Optional<Color> bgrColor = OPT_COLOR_CODEC.decode(is);
            return new MyTextStyle(
                    bold(bius), italic(bius), underline(bius), strikethrough(bius),
                    fontSize, fontFamily, textColor, bgrColor);
        }

        private int encodeBoldItalicUnderlineStrikethrough(MyTextStyle s) {
            return encodeOptionalBoolean(s.bold) << 6 |
                   encodeOptionalBoolean(s.italic) << 4 |
                   encodeOptionalBoolean(s.underline) << 2 |
                   encodeOptionalBoolean(s.strikethrough);
        }

        private Optional<Boolean> bold(byte bius) throws IOException {
            return decodeOptionalBoolean((bius >> 6) & 3);
        }

        private Optional<Boolean> italic(byte bius) throws IOException {
            return decodeOptionalBoolean((bius >> 4) & 3);
        }

        private Optional<Boolean> underline(byte bius) throws IOException {
            return decodeOptionalBoolean((bius >> 2) & 3);
        }

        private Optional<Boolean> strikethrough(byte bius) throws IOException {
            return decodeOptionalBoolean((bius >> 0) & 3);
        }

        private int encodeOptionalBoolean(Optional<Boolean> ob) {
            return ob.map(b -> 2 + (b ? 1 : 0)).orElse(0);
        }

        private Optional<Boolean> decodeOptionalBoolean(int i) throws IOException {
            switch(i) {
                case 0: return Optional.empty();
                case 2: return Optional.of(false);
                case 3: return Optional.of(true);
            }
            throw new MalformedInputException(0);
        }

        private int encodeOptionalUint(Optional<Integer> oi) {
            return oi.orElse(-1);
        }

        private Optional<Integer> decodeOptionalUint(int i) {
            return (i < 0) ? Optional.empty() : Optional.of(i);
        }
    };

    public static MyTextStyle bold(boolean bold) { return EMPTY.updateBold(bold); }
    public static MyTextStyle italic(boolean italic) { return EMPTY.updateItalic(italic); }
    public static MyTextStyle underline(boolean underline) { return EMPTY.updateUnderline(underline); }
    public static MyTextStyle strikethrough(boolean strikethrough) { return EMPTY.updateStrikethrough(strikethrough); }
    public static MyTextStyle fontSize(int fontSize) { return EMPTY.updateFontSize(fontSize); }
    public static MyTextStyle fontFamily(String family) { return EMPTY.updateFontFamily(family); }
    public static MyTextStyle textColor(Color color) { return EMPTY.updateTextColor(color); }
    public static MyTextStyle backgroundColor(Color color) { return EMPTY.updateBackgroundColor(color); }

    static String cssColor(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        return "rgb(" + red + ", " + green + ", " + blue + ")";
    }

    final public Optional<Boolean> bold;
    final public Optional<Boolean> italic;
    final public Optional<Boolean> underline;
    final public Optional<Boolean> strikethrough;
    final public Optional<Integer> fontSize;
    final public Optional<String> fontFamily;
    final public Optional<Color> textColor;
    final public Optional<Color> backgroundColor;

    public MyTextStyle() {
        this(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
    }

    public MyTextStyle(
            Optional<Boolean> bold,
            Optional<Boolean> italic,
            Optional<Boolean> underline,
            Optional<Boolean> strikethrough,
            Optional<Integer> fontSize,
            Optional<String> fontFamily,
            Optional<Color> textColor,
            Optional<Color> backgroundColor) {
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikethrough = strikethrough;
        this.fontSize = fontSize;
        this.fontFamily = fontFamily;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                bold, italic, underline, strikethrough,
                fontSize, fontFamily, textColor, backgroundColor);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof MyTextStyle) {
            MyTextStyle that = (MyTextStyle) other;
            return Objects.equals(this.bold,            that.bold) &&
                   Objects.equals(this.italic,          that.italic) &&
                   Objects.equals(this.underline,       that.underline) &&
                   Objects.equals(this.strikethrough,   that.strikethrough) &&
                   Objects.equals(this.fontSize,        that.fontSize) &&
                   Objects.equals(this.fontFamily,      that.fontFamily) &&
                   Objects.equals(this.textColor,       that.textColor) &&
                   Objects.equals(this.backgroundColor, that.backgroundColor);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        List<String> styles = new ArrayList<>();

        bold           .ifPresent(b -> styles.add(b.toString()));
        italic         .ifPresent(i -> styles.add(i.toString()));
        underline      .ifPresent(u -> styles.add(u.toString()));
        strikethrough  .ifPresent(s -> styles.add(s.toString()));
        fontSize       .ifPresent(s -> styles.add(s.toString()));
        fontFamily     .ifPresent(f -> styles.add(f.toString()));
        textColor      .ifPresent(c -> styles.add(c.toString()));
        backgroundColor.ifPresent(b -> styles.add(b.toString()));

        return String.join(",", styles);
    }

    public String toCss() {
        StringBuilder sb = new StringBuilder();

        if(fontSize.isPresent()) {
            sb.append("-fx-font-size: " + fontSize.get() + "pt;");
        }

        if(fontFamily.isPresent()) {
            sb.append("-fx-font-family: " + fontFamily.get() + ";");
        }

        if(backgroundColor.isPresent()) {
            Color color = backgroundColor.get();
            sb.append("-fx-background-color: " + cssColor(color) + ";");//rgb(122, 111, 100)
        }

        return sb.toString();
    }
    
    
    private static Pattern FONT_SIZE_PATTERN = Pattern.compile("-fx-font-size: (\\d+)pt;", Pattern.DOTALL);
    private static Pattern FONT_FAMILY_PATTERN = Pattern.compile("-fx-font-family: (.+?);", Pattern.DOTALL);
    private static Pattern BACKGROUND_COLOR_PATTERN = Pattern.compile("-fx-background-color: (.+?);", Pattern.DOTALL);
    
    public String mergeIntoCss(String css) {//merge this.toCss() into css
    	if(fontSize.isPresent()) {
    		Matcher matcher = FONT_SIZE_PATTERN.matcher(css);
    		if (matcher.find()) {
    			StringBuilder sb = new StringBuilder(css);
    			css = sb.replace(matcher.start(1), matcher.end(1), fontSize.get()+"").toString();
    		}else {
    			css += "-fx-font-size: " + fontSize.get() + "pt;";
    		}
        }

        if(fontFamily.isPresent()) {
        	Matcher matcher = FONT_FAMILY_PATTERN.matcher(css);
    		if (matcher.find()) {
    			StringBuilder sb = new StringBuilder(css);
    			css = sb.replace(matcher.start(1), matcher.end(1), fontFamily.get()).toString();
    		}else {
    			css += "-fx-font-family: " + fontFamily.get() + ";";
    		}
        }
        
        if(backgroundColor.isPresent()) {
        	Color color = backgroundColor.get();
        	Matcher matcher = BACKGROUND_COLOR_PATTERN.matcher(css);
    		if (matcher.find()) {
    			StringBuilder sb = new StringBuilder(css);
    			css = sb.replace(matcher.start(1), matcher.end(1), cssColor(color)).toString();
    		}else {
    			//css += "-rtfx-background-color: " + cssColor(color) + ";";
    			css += "-fx-background-color: " + cssColor(color) + ";";
    		}
        }
        
    	return css;
    }

    public MyTextStyle updateWith(MyTextStyle mixin) {
        return new MyTextStyle(
                mixin.bold.isPresent() ? mixin.bold : bold,
                mixin.italic.isPresent() ? mixin.italic : italic,
                mixin.underline.isPresent() ? mixin.underline : underline,
                mixin.strikethrough.isPresent() ? mixin.strikethrough : strikethrough,
                mixin.fontSize.isPresent() ? mixin.fontSize : fontSize,
                mixin.fontFamily.isPresent() ? mixin.fontFamily : fontFamily,
                mixin.textColor.isPresent() ? mixin.textColor : textColor,
                mixin.backgroundColor.isPresent() ? mixin.backgroundColor : backgroundColor);
    }

    public MyTextStyle updateBold(boolean bold) {
        return new MyTextStyle(Optional.of(bold), italic, underline, strikethrough, fontSize, fontFamily, textColor, backgroundColor);
    }

    public MyTextStyle updateItalic(boolean italic) {
        return new MyTextStyle(bold, Optional.of(italic), underline, strikethrough, fontSize, fontFamily, textColor, backgroundColor);
    }

    public MyTextStyle updateUnderline(boolean underline) {
        return new MyTextStyle(bold, italic, Optional.of(underline), strikethrough, fontSize, fontFamily, textColor, backgroundColor);
    }

    public MyTextStyle updateStrikethrough(boolean strikethrough) {
        return new MyTextStyle(bold, italic, underline, Optional.of(strikethrough), fontSize, fontFamily, textColor, backgroundColor);
    }

    public MyTextStyle updateFontSize(int fontSize) {
        return new MyTextStyle(bold, italic, underline, strikethrough, Optional.of(fontSize), fontFamily, textColor, backgroundColor);
    }

    public MyTextStyle updateFontFamily(String fontFamily) {
        return new MyTextStyle(bold, italic, underline, strikethrough, fontSize, Optional.of(fontFamily), textColor, backgroundColor);
    }

    public MyTextStyle updateTextColor(Color textColor) {
        return new MyTextStyle(bold, italic, underline, strikethrough, fontSize, fontFamily, Optional.of(textColor), backgroundColor);
    }

    public MyTextStyle updateBackgroundColor(Color backgroundColor) {
        return new MyTextStyle(bold, italic, underline, strikethrough, fontSize, fontFamily, textColor, Optional.of(backgroundColor));
    }
}