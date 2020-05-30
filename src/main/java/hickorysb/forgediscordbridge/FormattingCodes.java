package hickorysb.forgediscordbridge;

@SuppressWarnings("unused")
public enum FormattingCodes {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    OBFUSCATED('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),
    RESET('r');

    private final char code;
    private final String stringValue;

    FormattingCodes(char code) {
        this.code = code;
        this.stringValue = String.valueOf("\u00a7" + code);
    }

    public static FormattingCodes getByCode(char code) {
        for (FormattingCodes minecraftFormattingCode : FormattingCodes.values()) {
            if (minecraftFormattingCode.code == code) {
                return minecraftFormattingCode;
            }
        }

        return null;
    }

    public char getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.stringValue;
    }
}