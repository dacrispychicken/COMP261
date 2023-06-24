
import java.util.*;

public class LempelZiv {
    /**
     * Take uncompressed input as a text string, compress it, and return it as a
     * text string.
     *
     * @param input
     * @return String -> compressed
     */
    public static String compress(String input) {
        int cursor = 0;
        int windowSize = 100;
        StringBuilder result = new StringBuilder();

        while (cursor < input.length()) {
            String lookahead = "";
            int bestMatchDistance = -1; // Best match distance initialization
            int bestMatchLength = -1;   // Best match length initialization

            // Expand the lookahead buffer until we reach the end of the input or find a match
            for (int lookaheadLength = 1; lookaheadLength <= 8 && cursor + lookaheadLength <= input.length(); lookaheadLength++) {
                lookahead = input.substring(cursor, cursor + lookaheadLength);
                int windowStart = Math.max(0, cursor - windowSize);
                String window = input.substring(windowStart, cursor);

                // Try to find the lookahead buffer in the window
                int matchIndex = window.lastIndexOf(lookahead);
                if (matchIndex != -1) {
                    bestMatchDistance = cursor - windowStart - matchIndex;
                    bestMatchLength = lookaheadLength;
                }
            }

            if (bestMatchDistance == -1 || bestMatchLength == -1) {
                // No match found
                result.append("[").append(0).append("|").append(0).append("|").append(input.charAt(cursor)).append("]");
                cursor++;
            } else {
                // Match found
                cursor += bestMatchLength;
                if (cursor < input.length()) {
                    result.append("[").append(bestMatchDistance).append("|").append(bestMatchLength).append("|").append(input.charAt(cursor)).append("]");
                    cursor++;
                } else {
                    result.append("[").append(bestMatchDistance).append("|").append(bestMatchLength).append("]");
                }
            }
        }
        return result.toString();
    }


    /**
     * Take compressed input as a text string, decompress it, and return it as a
     * text string.
     *
     * @param compressed
     * @return Compressed -> String
     */
    public static String decompress(String compressed) {
        StringBuilder result = new StringBuilder();       //The decompressed String
        String[] tuples = compressed.split("\\]");  //Compressed String is split into chunks at each closing bracket

        // Iterate over each tuple
        for (String tuple : tuples) {
            // Making the tuple readable/able to do work on
            tuple = tuple.replace("[", "").replace("]", "");  // Remove opening/closing brackets
            String[] parts = tuple.split("\\|");  // Splits Tuple into parts based on '|'
            // Creating variables based on the values in the tuple
            int offset = Integer.parseInt(parts[0]);
            int length = Integer.parseInt(parts[1]);
            if (offset == 0 && length == 0) {
                // Single character
                result.append(parts[2]);
            } else {
                // Repeat sequence and single character
                int start = result.length() - offset;
                String repeatSeq = result.substring(start, start + length);
                result.append(repeatSeq);
                if (parts.length > 2) {
                    result.append(parts[2]);
                }
            }
        }
        return result.toString();
    }

    /**
     * The getInformation method is here for your convenience, you don't need to
     * fill it in if you don't want to. It is called on every run and its return
     * value is displayed on-screen. You can use this to print out any relevant
     * information from your compression.
     */
    public String getInformation() {
        return "";
    }
}
