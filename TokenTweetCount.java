// File: TokenTweetCount.java
// Author: Shawn Yeng Wei Xen (2395121Y)
// This file contains objects for a HashMap to store and update how many tweets that contain a certain token among the tweets loaded.

import java.util.HashMap;

public class TokenTweetCount {
	HashMap<Integer,Integer> tokenTweetCount;
	
	// Constructor, mapping the index of the token based on the lexicon, to the amount of tweets that contain
	// that token.
	public TokenTweetCount()
	{
		this.tokenTweetCount = new HashMap<Integer,Integer>();
	}
	
	// Adds the token along with the amount of tweets that contain that token into the hashMap.
	public void addToken(int token, int no)
	{
		this.tokenTweetCount.put(token, no);
	}
	
	// Increments the number of the tweets that contain the token for that token.
	public void incrementToken(int token)
	{
		int noOfTweets = this.tokenTweetCount.get(token);
		tokenTweetCount.replace(token, noOfTweets+1);
	}
	
	// Returns a boolean whether the hashmap contains that token or not.
	public boolean containsToken(int token)
	{
		return this.tokenTweetCount.containsKey(token);
	}
	
	// Returns the number of tweets that contain a particular token.
	public Integer returnNoTweets (int token)
	{
		return this.tokenTweetCount.get(token);
	}
}
