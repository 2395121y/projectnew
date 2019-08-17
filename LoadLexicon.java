// File: LoadLexicon.java
// Author: Shawn Yeng Wei Xen (2395121Y)
// This file helps to output the lexicon ArrayList<String> to be used as index referencing for ARFF files.

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LoadLexicon {
	HashMap<String,Integer> lexiconList;

	// Constructor function, done by creating the lexicon file named "event.lexicon" and loads it, 
	// reads it line by line, and stores (token, index) into it.
	public LoadLexicon()
	{
		// Step 1: Load stopwords		
		// NTLK's List of Stopwords
		// Source for stopwords: https://gist.github.com/sebleier/554280
		String stopwords = "StopWordList.txt";
		Set<String> stopwordList = new HashSet<String>();
		{
			try {
				BufferedReader br= new BufferedReader(new FileReader(stopwords));
				String line;
				while ((line = br.readLine())!=null) {
					stopwordList.add(line);
				}
				br.close();
			} catch (Exception e) { e.printStackTrace(); }
		}

		// Step 2: Start processing file(s)
		String[] files = {
				"2011_Joplin_tornado.json.gz",
				"2012_Guatemala_earthquake.json.gz",					
				"2012_Italy_earthquakes.json.gz",
				"2012_Philipinnes_floods.json.gz",
				"2013_Alberta_floods.json.gz",
				"2013_Australia_bushfire.json.gz",
				"2013_Boston_bombings.json.gz",
				"2013_Manila_floods.json.gz",
				"2013_Queensland_floods.json.gz",
				"2013_Typhoon_Yolanda.json.gz",
				"2014_Chile_Earthquake.json.gz",
				"2014_Typhoon_Hagupit.json.gz",
				"2015_Nepal_Earthquake.json.gz",					
				"2015_Paris_Attacks.json.gz",
				"2018_FL_School_Shooting.json.gz"
		};

		Map<String,Integer> words = new HashMap<String,Integer>();
		JSONParser parser = new JSONParser();

		for (String file : files) {
			{
				try {
					BufferedReader br= new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
					String line;
					while ((line = br.readLine())!=null) {
						JSONObject obj = (JSONObject) parser.parse(line);
						String actualText = (String) obj.get("text");						
						StringTokenizer textTokenizer = new StringTokenizer(actualText);
						while (textTokenizer.hasMoreTokens()) {
							String token = TextUtils.normaliseString(textTokenizer.nextToken());

							if (token!=null && token.length()>0 && !stopwordList.contains(token.toLowerCase())) {
								if (words.containsKey(token))
									words.put(token, words.get(token)+1);
								else words.put(token, 1);
							}							
						}						
					}
					br.close();
				} catch (Exception e) { e.printStackTrace(); }
			}
		}

		// Step 3: write Lexicon
		{
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter("event.lexicon"));

				for (String word : words.keySet()) {
					if (words.get(word)>=3) {					
						bw.write(word + "\n");
					}
				}

				bw.close();
			} catch (Exception e) {e.printStackTrace();}
		}
		
		// Step 4: Converts lexicon into a HashMap:
		String lexicon = "event.lexicon";
		this.lexiconList = new HashMap<String,Integer>();
		int count = 0;

		try {
			BufferedReader br= new BufferedReader(new FileReader(lexicon));
			String line;
			while ((line = br.readLine())!=null) {
				this.lexiconList.put(line,count);
				count++;
			}
			br.close();
		} 
		catch (Exception e) { e.printStackTrace(); }
	}

	// Returns the lexicon list.
	public HashMap<String,Integer> getList()
	{
		return this.lexiconList;
	}

	// Returns a boolean whether the token exists in the list or not.
	public boolean containsToken(String token)
	{
		return this.lexiconList.containsKey(token);
	}

	// Returns the index number based on the token.
	public int toIndex (String token)
	{
		return this.lexiconList.get(token);
	}
}
