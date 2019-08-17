// File: ArffList.java
// Author: Shawn Yeng Wei Xen (2395121Y)
// This file helps to store all the attributes to be output to ARFF files, as well as loading tweet files to be loaded into the 
// double arraylists. Also stores the number of tweets loaded to be used for TF-IDF calculations.

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ArffList {
	ArrayList<ArrayList> arffListing;

	// Used to calculate the amount of tweets that are read in from the tweet files, 
	// helpful to calculate TF-IDF values.
	int tweetCount;

	// How many files are loaded? Would be useful to split for how many for training when one of it is removed
	// for test data...
	int noEvents;

	// Constructor, creates the double ArrayList.
	public ArffList()
	{
		this.arffListing = new ArrayList<ArrayList>();
		this.tweetCount = 0;
		this.noEvents = 0;
	}

	// Returns an ArrayList from the double ArrayList, separated based on event.
	public ArrayList returnArff (int index)
	{
		return this.arffListing.get(index);
	}

	// Returns the total amount of tweets processed.
	public int returnTweetNo()
	{
		return this.tweetCount;
	}

	// Loading the tweet files to be processed for the double ArrayList, to be used for the ARFF files.
	public void loadTweets(IDList idList, LoadLexicon lexicon, TokenTweetCount tweetCountList)
	{
		// Preparing the tweet files:
		String[] tweetFiles = {
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


		JSONParser parser = new JSONParser();	

		// For each tweet file, it reads a line.
		for (String file : tweetFiles) {
			this.noEvents++;
			try {
				BufferedReader br= new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
				String line;	
				ArrayList<ArrayList> arffList = new ArrayList<ArrayList>();	
				while ((line = br.readLine())!=null) {
					JSONObject obj = (JSONObject) parser.parse(line);
					String extractID = (String) obj.get("id_str");
					BigInteger testID = new BigInteger (extractID);

					// After it reads a line, it checks if the ID obtained is included in the idList from any of the assessor files.
					// If it is, it will proceed to token processing and attribute processing in the tweet and increments the tweet
					// number.
					if (idList.hasCompleteID(testID))
					{
						this.tweetCount++;
						boolean continuingNews = false;
						boolean donations = false;
						boolean official = false;
						boolean advice = false;
						boolean mult = false;
						boolean goods = false;
						boolean isVerified = false;
						boolean hasMedia = false;
						boolean userDescription = false;

						// Checks if the tokens are repeated within the tweet so that it would not affect
						// amount of tweets that contain the token, affecting the TF-IDF value.
						Set<String> repeatedTokens = new HashSet<String>();

						// Extracting text and user from the tweets. Text is used for the tokens, while
						// user is for other information such as user description or whether the user
						// is verified or not.
						String actualText = (String) obj.get("text");						
						StringTokenizer textTokenizer = new StringTokenizer(actualText);
						JSONObject userProfile = (JSONObject) obj.get("user");
						JSONObject media = (JSONObject) obj.get("extended_entities");
						isVerified = (boolean) userProfile.get("verified");								

						// Checking if the tweet IDs are included in any of the category IDs:
						if (idList.hasContinuingID(testID))
							continuingNews = true;
						else
							continuingNews = false;

						if (idList.hasDonationsID(testID))
							donations = true;
						else
							donations = false;

						if (idList.hasOfficialID(testID))
							official = true;
						else
							official = false;

						if (idList.hasAdviceID(testID))
							advice = true;
						else
							advice = false;

						if (idList.hasMultimediaID(testID))
							mult = true;
						else
							mult = false;

						if (idList.hasGoodsID(testID))
							goods = true;
						else
							goods = false;

						if (media != null)
							hasMedia = true;

						// Checking if the user description containing any of the terms to imply 
						// if the user is a journalist:
						String userDesc = (String) userProfile.get("description");
						userDesc.toLowerCase();			

						if ( (userDesc.contains("news")) || (userDesc.contains("journalism")) 
								|| (userDesc.contains("blogger")) || (userDesc.contains("coverage"))
								|| (userDesc.contains("global")))
							userDescription = true;

						ArrayList arr = new ArrayList();	

						// For each token, it is checked if it is in the lexicon. If it is not, it skips to the next one.
						// If it is, it will be processed based on the index in the lexicon as well as taking note 
						// of the amount of tweets that have the token. If the token starts with "http", it implies
						// that it is a URL, and processed via unshortening the URL to obtain any terms which
						// could help in the tweet.
						while (textTokenizer.hasMoreTokens()) {
							String token = TextUtils.normaliseString(textTokenizer.nextToken());

							if (token!=null && token.length()>0) {								
								if (token.startsWith("http"))
								{
									try
									{

										String realString = new String();
										realString = UrlCleaner.unshortenUrl(token);
										StringTokenizer textTokenizerURL = new StringTokenizer(realString);


										String tokenURL = TextUtils.normaliseStringIgnore(textTokenizerURL.nextToken());
										String [] tokensURL = tokenURL.split(" ");

										for (int i = 0; i < tokensURL.length; i++)
										{	
											if (lexicon.containsToken(tokensURL[i]))
											{
												int tokenIndex = lexicon.toIndex(tokensURL[i]);
												if (tokensURL[i]!=null && tokensURL[i].length()>0 && lexicon.containsToken(tokensURL[i]) && !tweetCountList.containsToken(tokenIndex))
												{			
													arr.add(lexicon.toIndex(tokensURL[i]));												
													tweetCountList.addToken(tokenIndex, 1);											

												}
												else if (tweetCountList.containsToken(tokenIndex))
												{
													if (!repeatedTokens.contains(tokensURL[i]))
													{
														repeatedTokens.add(tokensURL[i]);
														tweetCountList.incrementToken(tokenIndex);													
													}
												}
											}
										}
									}
									catch (Exception e)
									{
										e.printStackTrace();

									}
								}										
								else if (lexicon.containsToken(token))
								{		

									int tokenIndex = lexicon.toIndex(token);
									arr.add(lexicon.toIndex(token));

									if (!tweetCountList.containsToken(tokenIndex))
										tweetCountList.addToken(tokenIndex, 1);	
									else
									{									
										if (!repeatedTokens.contains(token))
										{
											repeatedTokens.add(token);
											tweetCountList.incrementToken(tokenIndex);
										}
									}

								}																		
							}							
						}

						// The attributes are then sorted in ascending order based on their index in the lexicon. Lastly,
						// it then adds the categories to be used for the ARFF files.
						//System.out.println(arr);
						Collections.sort(arr);

						if (isVerified)
							arr.add("verified");
						else
							arr.add("notverified");

						if (hasMedia)
							arr.add("hasmedia");
						else
							arr.add("nomedia");

						if (userDescription)
							arr.add("desc");
						else
							arr.add("notdesc");														

						if (continuingNews)
							arr.add("continuingnews");
						else
							arr.add("notcontinuingnews");

						if (donations)
							arr.add("donations");
						else
							arr.add("notdonations");

						if (official)
							arr.add("official");
						else
							arr.add("notofficial");

						if (advice)
							arr.add("advice");
						else
							arr.add("notadvice");

						if (mult)
							arr.add("multi");
						else
							arr.add("notmulti");								

						if (goods)
							arr.add("goods");
						else
							arr.add("notgoods");

						arffList.add(arr);								
					}
				}
				br.close();
				this.arffListing.add(arffList);
			} catch (Exception e) { e.printStackTrace(); }


		}
	}

	// Creates the ARFF files based on the tweet file processings.
	public void writeARFF(LoadLexicon lexicon, TokenTweetCount tweetCountList)
	{
		for (int steppingStone = 0; steppingStone < this.noEvents; steppingStone++)
		{
			System.out.println("Currently writing For Event #" + steppingStone + "......");
			try {				
				String whatToWrite1, whatToWrite2, whatToWrite3, whatToWrite4, whatToWrite5, whatToWrite6;
				String startingPoint;

				String tC = "TrainCont.arff";
				String tD = "TrainDona.arff";
				String tO = "TrainOffi.arff";
				String tA = "TrainAdvi.arff";
				String tM = "TrainMult.arff";
				String tG = "TrainGood.arff";

				// For each event, the naming system is different:				
				switch (steppingStone)
				{
				case 0: startingPoint = "2011Joplin"; break;
				case 1: startingPoint = "2012Guatemala"; break; 
				case 2: startingPoint = "2012Italy"; break;
				case 3: startingPoint = "2012Philipinne"; break;
				case 4: startingPoint = "2013Alberta";	break;
				case 5: startingPoint = "2013Australia"; break;
				case 6: startingPoint = "2013Boston"; break;				
				case 7: startingPoint = "2013Manila"; break;
				case 8: startingPoint = "2013Queens"; break;
				case 9: startingPoint = "2013Yolanda"; break;
				case 10: startingPoint = "2014Chile"; break;
				case 11: startingPoint = "2014Hagupit"; break;
				case 12: startingPoint = "2015Nepal"; break;
				case 13: startingPoint = "2015Paris"; break;
				default: startingPoint = "2018Florida"; 				
				}

				// This is used for various categories too:
				whatToWrite1 = startingPoint;
				whatToWrite1 += tC;
				whatToWrite2 = startingPoint;
				whatToWrite2 += tD;
				whatToWrite3 = startingPoint;
				whatToWrite3 += tO;
				whatToWrite4 = startingPoint;
				whatToWrite4 += tA;
				whatToWrite5 = startingPoint;
				whatToWrite5 += tM;
				whatToWrite6 = startingPoint;
				whatToWrite6 += tG;

				ArrayList <ArrayList> arffSelect = new ArrayList<ArrayList>();

				// For training data, it loads all the processed tweet data except for one that is used
				// for test data.
				for (int thearffs = 0; thearffs < this.noEvents; thearffs++)
				{
					if (thearffs != steppingStone)
						arffSelect.addAll(returnArff(thearffs));
				}

				// Each BufferedWriter is used to write for each different category.
				BufferedWriter bw1 = new BufferedWriter(new FileWriter(whatToWrite1));
				BufferedWriter bw2 = new BufferedWriter(new FileWriter(whatToWrite2));
				BufferedWriter bw3 = new BufferedWriter(new FileWriter(whatToWrite3));
				BufferedWriter bw4 = new BufferedWriter(new FileWriter(whatToWrite4));
				BufferedWriter bw5 = new BufferedWriter(new FileWriter(whatToWrite5));
				BufferedWriter bw6 = new BufferedWriter(new FileWriter(whatToWrite6));

				bw1.write("@RELATION" + "\t" + "ContinuingNews" + "\n");
				bw2.write("@RELATION" + "\t" + "Donations" + "\n");
				bw3.write("@RELATION" + "\t" + "Official" + "\n");
				bw4.write("@RELATION" + "\t" + "Advice" + "\n");
				bw5.write("@RELATION" + "\t" + "MultimediaShare" + "\n");
				bw6.write("@RELATION" + "\t" + "GoodsServices" + "\n");

				for (int i = 0; i < lexicon.getList().size(); i++)
				{
					bw1.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
					bw2.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
					bw3.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
					bw4.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
					bw5.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
					bw6.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
				}

				bw1.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw1.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw1.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw2.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw2.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw2.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw3.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw3.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw3.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw4.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw4.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw4.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw5.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw5.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw5.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw6.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw6.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw6.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw1.write("@ATTRIBUTE" + "\t" + "ContinuingNews" + "\t" + "{\"continuingnews\",\"notcontinuingnews\"}" + "\n" );
				bw2.write("@ATTRIBUTE" + "\t" + "Donations" + "\t" + "{\"donations\",\"notdonations\"}" + "\n" );
				bw3.write("@ATTRIBUTE" + "\t" + "Official" + "\t" + "{\"official\",\"notofficial\"}" + "\n" );
				bw4.write("@ATTRIBUTE" + "\t" + "Advice" + "\t" + "{\"advice\",\"notadvice\"}" + "\n" );
				bw5.write("@ATTRIBUTE" + "\t" + "MultimediaShare" + "\t" + "{\"multi\",\"notmulti\"}" + "\n" );
				bw6.write("@ATTRIBUTE" + "\t" + "GoodsServices" + "\t" + "{\"goods\",\"notgoods\"}" + "\n" );

				bw1.write("@DATA" + "\n");
				bw2.write("@DATA" + "\n");
				bw3.write("@DATA" + "\n");
				bw4.write("@DATA" + "\n");
				bw5.write("@DATA" + "\n");
				bw6.write("@DATA" + "\n");

				for (int i = 0; i < arffSelect.size(); i++) {		
					bw1.write("{");
					bw2.write("{");
					bw3.write("{");
					bw4.write("{");
					bw5.write("{");
					bw6.write("{");
					ArrayList<Integer> existingInteger = new ArrayList<Integer>();

					for (int j = 0; j < arffSelect.get(i).size() - 9; j++)
					{						
						int no = (int) arffSelect.get(i).get(j);						

						if (!existingInteger.contains(no))
						{
							bw1.write(String.valueOf(no));
							bw1.write("\t");
							bw2.write(String.valueOf(no));
							bw2.write("\t");
							bw3.write(String.valueOf(no));
							bw3.write("\t");
							bw4.write(String.valueOf(no));
							bw4.write("\t");
							bw5.write(String.valueOf(no));
							bw5.write("\t");
							bw6.write(String.valueOf(no));
							bw6.write("\t");							

							// TF IDF 1, calculating the TF-IDF value:
							double tf = 1.0 * Collections.frequency(arffSelect.get(i), no);
							double idf = Math.log(1.0 * returnTweetNo() / tweetCountList.returnNoTweets(no) );
							double tfidf = tf*idf;


							tfidf = Math.round(tfidf * 100.0) / 100.0;
							bw1.write(String.valueOf(tfidf) + ",");
							bw2.write(String.valueOf(tfidf) + ",");
							bw3.write(String.valueOf(tfidf) + ",");
							bw4.write(String.valueOf(tfidf) + ",");
							bw5.write(String.valueOf(tfidf) + ",");
							bw6.write(String.valueOf(tfidf) + ",");

							bw1.write("\t");
							bw2.write("\t");
							bw3.write("\t");
							bw4.write("\t");
							bw5.write("\t");
							bw6.write("\t");
							existingInteger.add(no);
						}
					}

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 9).equals("verified"))
					{
						bw1.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
						bw2.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
						bw3.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
						bw4.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
						bw5.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
						bw6.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
					}

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 8).equals("hasmedia"))
					{
						bw1.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
						bw2.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
						bw3.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
						bw4.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
						bw5.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
						bw6.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
					}

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 7).equals("desc"))
					{
						bw1.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
						bw2.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
						bw3.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
						bw4.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
						bw5.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
						bw6.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
					}

					bw1.write(String.valueOf(lexicon.getList().size() + 3));
					bw1.write("\t");
					bw2.write(String.valueOf(lexicon.getList().size() + 3));
					bw2.write("\t");
					bw3.write(String.valueOf(lexicon.getList().size() + 3));
					bw3.write("\t");
					bw4.write(String.valueOf(lexicon.getList().size() + 3));
					bw4.write("\t");
					bw5.write(String.valueOf(lexicon.getList().size() + 3));
					bw5.write("\t");
					bw6.write(String.valueOf(lexicon.getList().size() + 3));
					bw6.write("\t");				

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 6) == "continuingnews")
						bw1.write("\"continuingnews\"}" + "\n");
					else
						bw1.write("\"notcontinuingnews\"}" + "\n");

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 5) == "donations")
						bw2.write("\"donations\"}" + "\n");
					else
						bw2.write("\"notdonations\"}" + "\n");

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 4) == "official")
						bw3.write("\"official\"}" + "\n");
					else
						bw3.write("\"notofficial\"}" + "\n");

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 3) == "advice")
						bw4.write("\"advice\"}" + "\n");
					else
						bw4.write("\"notadvice\"}" + "\n");

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 2) == "multi")
						bw5.write("\"multi\"}" + "\n");
					else
						bw5.write("\"notmulti\"}" + "\n");

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 1) == "goods")
						bw6.write("\"goods\"}" + "\n");
					else
						bw6.write("\"notgoods\"}" + "\n");

				}

				bw1.close();
				bw2.close();
				bw3.close();
				bw4.close();
				bw5.close();
				bw6.close();

				// Once it's done, the same will be done for the test files:
				tC = "TestCont.arff";
				tD = "TestDona.arff";
				tO = "TestOffi.arff";
				tA = "TestAdvi.arff";
				tM = "TestMult.arff";
				tG = "TestGood.arff";

				whatToWrite1 = startingPoint;
				whatToWrite1 += tC;
				whatToWrite2 = startingPoint;
				whatToWrite2 += tD;
				whatToWrite3 = startingPoint;
				whatToWrite3 += tO;
				whatToWrite4 = startingPoint;
				whatToWrite4 += tA;
				whatToWrite5 = startingPoint;
				whatToWrite5 += tM;
				whatToWrite6 = startingPoint;
				whatToWrite6 += tG;

				arffSelect.clear();
				arffSelect = returnArff(steppingStone);				

				bw1 = new BufferedWriter(new FileWriter(whatToWrite1));		
				bw2 = new BufferedWriter(new FileWriter(whatToWrite2));
				bw3 = new BufferedWriter(new FileWriter(whatToWrite3));
				bw4 = new BufferedWriter(new FileWriter(whatToWrite4));
				bw5 = new BufferedWriter(new FileWriter(whatToWrite5));
				bw6 = new BufferedWriter(new FileWriter(whatToWrite6));

				bw1.write("@RELATION" + "\t" + "ContinuingNews" + "\n");
				bw2.write("@RELATION" + "\t" + "Donations" + "\n");
				bw3.write("@RELATION" + "\t" + "Official" + "\n");
				bw4.write("@RELATION" + "\t" + "Advice" + "\n");
				bw5.write("@RELATION" + "\t" + "MultimediaShare" + "\n");
				bw6.write("@RELATION" + "\t" + "GoodsServices" + "\n");

				for (int i = 0; i < lexicon.getList().size(); i++)
				{
					bw1.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
					bw2.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
					bw3.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
					bw4.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
					bw5.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
					bw6.write("@ATTRIBUTE" + "\t" + "attr" + i + "\t" + "NUMERIC" + "\n");
				}

				bw1.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw1.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw1.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw2.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw2.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw2.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw3.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw3.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw3.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw4.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw4.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw4.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw5.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw5.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw5.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw6.write("@ATTRIBUTE" + "\t" + "attr" + lexicon.getList().size() + "\t" + "NUMERIC" + "\n" );
				bw6.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 1) + "\t" + "NUMERIC" + "\n" );
				bw6.write("@ATTRIBUTE" + "\t" + "attr" + (lexicon.getList().size() + 2) + "\t" + "NUMERIC" + "\n" );

				bw1.write("@ATTRIBUTE" + "\t" + "ContinuingNews" + "\t" + "{\"continuingnews\",\"notcontinuingnews\"}" + "\n" );
				bw2.write("@ATTRIBUTE" + "\t" + "Donations" + "\t" + "{\"donations\",\"notdonations\"}" + "\n" );
				bw3.write("@ATTRIBUTE" + "\t" + "Official" + "\t" + "{\"official\",\"notofficial\"}" + "\n" );
				bw4.write("@ATTRIBUTE" + "\t" + "Advice" + "\t" + "{\"advice\",\"notadvice\"}" + "\n" );
				bw5.write("@ATTRIBUTE" + "\t" + "MultimediaShare" + "\t" + "{\"multi\",\"notmulti\"}" + "\n" );
				bw6.write("@ATTRIBUTE" + "\t" + "GoodsServices" + "\t" + "{\"goods\",\"notgoods\"}" + "\n" );

				bw1.write("@DATA" + "\n");
				bw2.write("@DATA" + "\n");
				bw3.write("@DATA" + "\n");
				bw4.write("@DATA" + "\n");
				bw5.write("@DATA" + "\n");
				bw6.write("@DATA" + "\n");

				for (int i = 0; i < arffSelect.size(); i++) {		
					bw1.write("{");
					bw2.write("{");
					bw3.write("{");
					bw4.write("{");
					bw5.write("{");
					bw6.write("{");
					ArrayList<Integer> existingInteger = new ArrayList<Integer>();					

					for (int j = 0; j < arffSelect.get(i).size() - 9; j++)
					{						
						int no = (int) arffSelect.get(i).get(j);						

						if (!existingInteger.contains(no))
						{
							bw1.write(String.valueOf(no));
							bw1.write("\t");
							bw2.write(String.valueOf(no));
							bw2.write("\t");
							bw3.write(String.valueOf(no));
							bw3.write("\t");
							bw4.write(String.valueOf(no));
							bw4.write("\t");
							bw5.write(String.valueOf(no));
							bw5.write("\t");
							bw6.write(String.valueOf(no));
							bw6.write("\t");

							// TF IDF 1 
							double tf = 1.0 * Collections.frequency(arffSelect.get(i), no);
							double idf = Math.log(1.0 * returnTweetNo() / tweetCountList.returnNoTweets(no));
							double tfidf = tf*idf;							
							tfidf = Math.round(tfidf * 100.0) / 100.0;
							bw1.write(String.valueOf(tfidf) + ",");
							bw2.write(String.valueOf(tfidf) + ",");
							bw3.write(String.valueOf(tfidf) + ",");
							bw4.write(String.valueOf(tfidf) + ",");
							bw5.write(String.valueOf(tfidf) + ",");
							bw6.write(String.valueOf(tfidf) + ",");

							bw1.write("\t");
							bw2.write("\t");
							bw3.write("\t");
							bw4.write("\t");
							bw5.write("\t");
							bw6.write("\t");
							existingInteger.add(no);
						}
					}

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 9).equals("verified"))
					{
						bw1.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
						bw2.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
						bw3.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
						bw4.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
						bw5.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
						bw6.write(String.valueOf(lexicon.getList().size()) + "\t" + "1.0," + "\t");
					}

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 8).equals("hasmedia"))
					{
						bw1.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
						bw2.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
						bw3.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
						bw4.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
						bw5.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
						bw6.write(String.valueOf(lexicon.getList().size() + 1) + "\t" + "1.0," + "\t");
					}

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 7).equals("desc"))
					{
						bw1.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
						bw2.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
						bw3.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
						bw4.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
						bw5.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
						bw6.write(String.valueOf(lexicon.getList().size() + 2) + "\t" + "1.0," + "\t");
					}

					bw1.write(String.valueOf(lexicon.getList().size() + 3));
					bw1.write("\t");
					bw2.write(String.valueOf(lexicon.getList().size() + 3));
					bw2.write("\t");
					bw3.write(String.valueOf(lexicon.getList().size() + 3));
					bw3.write("\t");
					bw4.write(String.valueOf(lexicon.getList().size() + 3));
					bw4.write("\t");
					bw5.write(String.valueOf(lexicon.getList().size() + 3));
					bw5.write("\t");
					bw6.write(String.valueOf(lexicon.getList().size() + 3));
					bw6.write("\t");				

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 6) == "continuingnews")
						bw1.write("\"continuingnews\"}" + "\n");
					else
						bw1.write("\"notcontinuingnews\"}" + "\n");

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 5) == "donations")
						bw2.write("\"donations\"}" + "\n");
					else
						bw2.write("\"notdonations\"}" + "\n");

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 4) == "official")
						bw3.write("\"official\"}" + "\n");
					else
						bw3.write("\"notofficial\"}" + "\n");

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 3) == "advice")
						bw4.write("\"advice\"}" + "\n");
					else
						bw4.write("\"notadvice\"}" + "\n");

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 2) == "multi")
						bw5.write("\"multi\"}" + "\n");
					else
						bw5.write("\"notmulti\"}" + "\n");

					if (arffSelect.get(i).get(arffSelect.get(i).size() - 1) == "goods")
						bw6.write("\"goods\"}" + "\n");
					else
						bw6.write("\"notgoods\"}" + "\n");


				}	
				bw1.close();
				bw2.close();
				bw3.close();
				bw4.close();
				bw5.close();
				bw6.close();
			} catch (Exception e) {e.printStackTrace();}
		}
	}
}
