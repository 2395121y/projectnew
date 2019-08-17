// File: TextUtils.java
// Author: Shawn Yeng Wei Xen (2395121Y)
// This class helps in replacing or removing certain characters in a line when being read
// from a tweet.

public class TextUtils {

	public static String normaliseString(String text) {
		boolean ignore = false;
		String normaltext = text.toLowerCase();
		if (normaltext.contains("http")) ignore = true;
		if (normaltext.startsWith("@")) normaltext="*MENTIONS*";
		if (normaltext.startsWith("$")) normaltext="*CASH*";
		if ( (normaltext.startsWith("am")) || (normaltext.startsWith("pm")))
			normaltext="*TIME*";
		
		if (!ignore)
		{
		normaltext = normaltext.replace(".", " ");
		normaltext = normaltext.replace("/", " ");
		normaltext = normaltext.replace(":", " ");
		}
		normaltext = normaltext.replace("\\n", " ");
		normaltext = normaltext.replace("(", "");
		normaltext = normaltext.replace(")", "");
		normaltext = normaltext.replace("u201d", "");
		normaltext = normaltext.replace("\"", "'");
		normaltext = normaltext.replace("$", "DollarSymbol");
		normaltext = normaltext.replace("[", "");
		normaltext = normaltext.replace("]", "");
		normaltext = normaltext.replace(")", "");
		normaltext = normaltext.replace("(", "");
		normaltext = normaltext.replace("\\", "");
		normaltext = normaltext.replace("'", "");
		normaltext = normaltext.replace("...", "");
		normaltext = normaltext.replace("#", "");
		normaltext = normaltext.replace(";", "");
		normaltext = normaltext.replace(",", "");
		normaltext = normaltext.replace("?", " ");
		normaltext = normaltext.replace("!", " ");		
		normaltext = normaltext.replace("  ", " ");
	
		normaltext = normaltext.trim();
		return normaltext;
	}
	
	public static String normaliseStringIgnore(String text) {
		String normaltext = text.toLowerCase();
		normaltext = normaltext.replace(".", " ");
		normaltext = normaltext.replace("/", " ");
		normaltext = normaltext.replace(":", " ");
		normaltext = normaltext.replace("-", " ");
		normaltext = normaltext.trim();
		return normaltext;
	}
	
}