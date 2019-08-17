// File: IDList.java
// Author: Shawn Yeng Wei Xen (2395121Y)
// This file prepares the sets of IDs based on categories, and has functions dedicated to loading assessor files to load
// in tweet IDs.

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class IDList {
			// Preparing Sets for each category....
			Set<BigInteger> continuingNewsID;
			Set<BigInteger> donationsID;
			Set<BigInteger> officialID;
			Set<BigInteger> adviceID;
			Set<BigInteger> multimediaID;
			Set<BigInteger> goodsID;
			Set<BigInteger> completeID;
			
			// Constructor, creating the sets
			public IDList()
			{
				this.continuingNewsID = new HashSet<BigInteger>();
				this.donationsID = new HashSet<BigInteger>();
				this.officialID = new HashSet<BigInteger>();
				this.adviceID = new HashSet<BigInteger>();
				this.multimediaID = new HashSet<BigInteger>();
				this.goodsID = new HashSet<BigInteger>();
				this.completeID = new HashSet<BigInteger>();
			}
			
			// Loading the assessor files to be added to the ID lists..
			public void loadAssessors()
			{
				// Assessor Files to be read in
				String[] assessorFiles = {
						"assr1.test.gz",
						"assr2.test.gz",
						"assr3.test.gz",
						"assr4.test.gz",
						"assr5.test.gz",
						"assr6.test.gz"
				};
				
				JSONParser parser = new JSONParser();
				
				// Loads in assessor files one by one, loading in the tweet IDs, and outputs them to sets based on the categories
				// being chosen and all the IDs are in the complete ID set.

				for (String file: assessorFiles) {
					try
					{
						BufferedReader br= new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
						String line;			
						String combinedLine = "";					

						while ((line = br.readLine())!=null) {
							combinedLine += line;
						}
						br.close();

						JSONObject assessor = (JSONObject) parser.parse(combinedLine);
						JSONArray eventArray = (JSONArray) assessor.get("events");
						Iterator iterator = eventArray.iterator();
						while (iterator.hasNext())
						{
							JSONObject getEvent = (JSONObject) parser.parse(iterator.next().toString());
							JSONArray getTweets = (JSONArray) getEvent.get("tweets");
							
							for (int i = 0; i < getTweets.size(); i++)
							{
								JSONObject tweetAssr = (JSONObject) parser.parse(getTweets.get(i).toString());
								
								String getID = (String) tweetAssr.get("postID");
								BigInteger obtainedID = new BigInteger (getID);
								JSONArray categories = (JSONArray) tweetAssr.get("categories");

								if (categories.contains("ContinuingNews"))
									this.continuingNewsID.add(obtainedID);			           								

								if (categories.contains("Donations"))
									this.donationsID.add(obtainedID);

								if (categories.contains("Official"))
									this.officialID.add(obtainedID);	

								if (categories.contains("Advice"))
									this.adviceID.add(obtainedID);

								if (categories.contains("MultimediaShare"))
									this.multimediaID.add(obtainedID);	

								if (categories.contains("GoodsServices"))
									this.goodsID.add(obtainedID);

								this.completeID.add(obtainedID);
							}
						}
						br.close();
					}
					catch (Exception e) { e.printStackTrace(); }
				}	
				
			}
			
			// Below functions help to check if the IDs are included in the 
			// sets, and returns boolean.
			public boolean hasContinuingID(BigInteger id) {
				return this.continuingNewsID.contains(id);
			}
			
			public boolean hasDonationsID(BigInteger id) {
				return this.donationsID.contains(id);
			}
			
			public boolean hasOfficialID(BigInteger id) {
				return this.officialID.contains(id);
			}
			
			public boolean hasAdviceID(BigInteger id) {
				return this.adviceID.contains(id);
			}
			
			public boolean hasMultimediaID(BigInteger id) {
				return this.multimediaID.contains(id);
			}
			
			public boolean hasGoodsID(BigInteger id) {
				return this.goodsID.contains(id);
			}
			
			public boolean hasCompleteID(BigInteger id) {
				return this.completeID.contains(id);
			}
}
