package hickorysb.forgediscordbridge;

import com.google.common.base.Joiner;
import hickorysb.forgediscordbridge.Patterns;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Utilities {
    public static void addPatterns() {
        Patterns.clearCustomPatterns();

        Patterns.addMinecraftFormattingUnifyPattern(Patterns.fakeMinecraftCodePattern, new Patterns.ReplacementCallback() {
            @Override
            public String pre(String text) {
                return text;
            }

            @Override
            public String replace(ArrayList<String> groups) {
                FormattingCodes minecraftFormattingCode = FormattingCodes.getByCode(groups.get(1).charAt(0));
                if (minecraftFormattingCode != null) {
                    return minecraftFormattingCode.toString();
                }
                return groups.get(0);
            }

            @Override
            public String post(String text) {
                return text;
            }
        });

        Patterns.addDiscordToMinecraftFormattingPattern(Pattern.compile("(?i)(\\*\\*|\\*|__|_|~~|`|```)"), new Patterns.ReplacementCallback() {
            private boolean bold = false;
            private boolean italic = false;
            private String lastItalic = "";
            private boolean underline = false;
            private boolean strikethrough = false;

            @Override
            public String pre(String text) {
                return text;
            }

            @Override
            public String replace(ArrayList<String> groups) {
                String modifier = groups.get(0);

                switch (modifier) {
                    case "**":
                        this.bold = !this.bold;
                        modifier = this.bold ? FormattingCodes.BOLD.toString() : resetString();
                        break;
                    case "*":
                    case "_":
                        if (this.italic && !modifier.equals(this.lastItalic)) {
                            return modifier;
                        }
                        this.lastItalic = modifier;
                        this.italic = !this.italic;
                        modifier = this.italic ? FormattingCodes.ITALIC.toString() : resetString();
                        break;
                    case "__":
                        this.underline = !this.underline;
                        modifier = this.underline ? FormattingCodes.UNDERLINE.toString() : resetString();
                        break;
                    case "~~":
                        this.strikethrough = !this.strikethrough;
                        modifier = this.strikethrough ? FormattingCodes.STRIKETHROUGH.toString() : resetString();
                        break;
                    default:
                        break;
                }

                return modifier;
            }

            private String resetString() {
                String text = FormattingCodes.RESET.toString();
                if (this.strikethrough) {
                    text += FormattingCodes.STRIKETHROUGH.toString();
                }
                if (this.underline) {
                    text += FormattingCodes.UNDERLINE.toString();
                }
                if (this.italic) {
                    text += FormattingCodes.ITALIC.toString();
                }
                if (this.bold) {
                    text += FormattingCodes.BOLD.toString();
                }
                return text;
            }

            @Override
            public String post(String text) {
                if (this.strikethrough) {
                    int lastStrikethrough = text.lastIndexOf(FormattingCodes.STRIKETHROUGH.toString());
                    text = text.substring(0, lastStrikethrough) + "~~" + text.substring(lastStrikethrough + 2);
                    this.strikethrough = false;
                }
                if (this.underline) {
                    int lastUnderline = text.lastIndexOf(FormattingCodes.UNDERLINE.toString());
                    text = text.substring(0, lastUnderline) + "__" + text.substring(lastUnderline + 2);
                    this.underline = false;
                }
                if (this.italic) {
                    int lastItalic = text.lastIndexOf(FormattingCodes.ITALIC.toString());
                    text = text.substring(0, lastItalic) + this.lastItalic + text.substring(lastItalic + 2);
                    this.italic = false;
                }
                if (this.bold) {
                    int lastBold = text.lastIndexOf(FormattingCodes.BOLD.toString());
                    text = text.substring(0, lastBold) + "**" + text.substring(lastBold + 2);
                    this.bold = false;
                }
                text = Pattern.compile("(?i)\u00a7r(\u00a7([0-9A-FK-OR]))+\u00a7r").matcher(text).replaceAll(FormattingCodes.RESET.toString());
                return text;
            }
        });

        Patterns.addMinecraftToDiscordFormattingPattern(Patterns.minecraftCodePattern, new Patterns.ReplacementCallback() {
            private boolean bold = false;
            private boolean italic = false;
            private boolean underline = false;
            private boolean strikethrough = false;

            @Override
            public String pre(String text) {
                return text;
            }

            @Override
            public String replace(ArrayList<String> groups) {
                String modifier = groups.get(0);

                if (modifier.equalsIgnoreCase(FormattingCodes.BOLD.toString())) {
                    this.bold = true;
                    modifier = "**";
                } else if (modifier.equalsIgnoreCase(FormattingCodes.ITALIC.toString())) {
                    this.italic = true;
                    modifier = "*";
                } else if (modifier.equalsIgnoreCase(FormattingCodes.UNDERLINE.toString())) {
                    this.underline = true;
                    modifier = "__";
                } else if (modifier.equalsIgnoreCase(FormattingCodes.STRIKETHROUGH.toString())) {
                    this.strikethrough = true;
                    modifier = "~~";
                } else if (modifier.equalsIgnoreCase(FormattingCodes.RESET.toString())) {
                    modifier = "";
                    if (this.bold) {
                        this.bold = false;
                        modifier += "**";
                    }
                    if (this.italic) {
                        this.italic = false;
                        modifier += "*";
                    }
                    if (this.underline) {
                        this.underline = false;
                        modifier += "__";
                    }
                    if (this.strikethrough) {
                        this.strikethrough = false;
                        modifier += "~~";
                    }
                } else {
                    modifier = "";
                }

                return modifier;
            }

            @Override
            public String post(String text) {
                if (this.strikethrough) {
                    text += "~~";
                    this.strikethrough = false;
                }
                if (this.underline) {
                    text += "__";
                    this.underline = false;
                }
                if (this.italic) {
                    text += "*";
                    this.italic = false;
                }
                if (this.bold) {
                    text += "**";
                    this.bold = false;
                }
                return text.replaceAll("\\*\\*\\*\\*\\*", "*");
            }
        });
    }

    public static String replace(Map<String, String> replaceMap, String text) {
        String[] words = text.split(" ");

        Set<Map.Entry<String, String>> entries = replaceMap.entrySet();

        for (int i = 0, j = words.length; i < j; i++) {
            String word = words[i];

            for (Map.Entry<String, String> entry : entries) {
                if (word.equals(entry.getKey())) {
                    words[i] = entry.getValue();
                }
            }
        }

        return Joiner.on(" ").join(words);
    }

    public static String tpsToColorString(double tps, boolean isDiscord) {
        if (19 <= tps) {
            return isDiscord ? "+ " : FormattingCodes.GREEN.toString();
        } else if (15 <= tps) {
            return isDiscord ? "! " : FormattingCodes.YELLOW.toString();
        } else {
            return isDiscord ? "- " : FormattingCodes.RED.toString();
        }
    }

    public static long mean(long[] values) {
        return LongStream.of(values).sum() / values.length;
    }
}