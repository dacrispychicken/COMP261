/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {

	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 *
	 * @param pattern
	 * @param text
	 * @return int
	 */
	public static int search(String pattern, String text) {
        int[] M = computeMatchTable(pattern);  // Jump table calculated from method below

		int k = 0; // Start of current match in text
		int i = 0; // Position of current character in S
		int m = pattern.length();  // Length of pattern
		int n = text.length();  // Length of text

		// Main loop, while start of current match in pattern + pos of current char in text is less than texts length
		while(k + i < n){
          if(pattern.charAt(i) == text.charAt(k + i)){ // Match
			  i++;
			  if(i == m){
				  return k; // Found S
			  }
		   }
		  else if(M[i] == -1){ // Miss-match, no self overlap
			  k = k + i + 1;
			  i = 0;
		  }
		  else{
			  k = (k + i) - M[i];
			  i = M[i];
		  }
		}
		// Substring "text" not found in the pattern
		//System.out.println(text + " is not found");
		return -1;
	}

	/**
	 * Method that computes the MatchTable
	 * - Looks if there is a match between suffix and prefix in order to calculate how far we can step back
	 *
	 * @param s - String input
	 * @return - int[] that shows how far to jump back
	 */
	public static int[] computeMatchTable(String s){
		int m[] = new int[s.length()];

		m[0] = -1;
		if(s.length() > 1) {  // Avoid Array-Index-Out-Of-Bounds-Exception
			m[1] = 0;
		}

		int j = 0;   // positionin prefix
		int pos = 2; // position in table

		while(pos < s.length()){
			if(s.charAt(pos - 1) == (s.charAt(j))){  // Substring ...pos-1 and 0..j match
				m[pos] = j + 1;
				pos++;
				j++;
			}
			else if(j > 0){  // Mismatch, restart the prefix
				j = m[j];
			}
			else {  // We have run out of candidate prefixes
				m[pos] = 0;
				pos++;
			}
		}
      return m;
	}
}
