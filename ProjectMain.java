// File: ProjectMain.java
// Author: Shawn Yeng Wei Xen (2395121Y)
// Main class file for the project, this file produces the ARFF files based on the training data and testing data 
// and outputs results from these ARFF files via
// Naive Bayes and SMO in a text file.

public class ProjectMain {

	public static void main (String [] args) throws Exception {

		// Loading in the lexicon and getting the size of the lexicon
		System.out.println("Loading lexicon......");
		LoadLexicon lexicon = new LoadLexicon();

		// Loading in a class loading in IDs, and then loading assessor files
		System.out.println("Loading assessor files......");
		IDList idList = new IDList();
		idList.loadAssessors();

		// Preparing an ArrayList of ArrayLists of objects containing attributes with ArffList to be output to 
		// ARFF files:
		ArffList arffListings = new ArffList();		

		// Getting the number of tweets that contain a token via HashMap:
		TokenTweetCount tweetCountList = new TokenTweetCount();		

		// Loading in the tweet files into the arffListings:
		System.out.println("Loading the tweet files......");
		arffListings.loadTweets(idList, lexicon, tweetCountList);

		// Creating ARFF files based on the tweet file processings..
		System.out.println("Creating the ARFF files......");
		arffListings.writeARFF(lexicon, tweetCountList);
		
		// Exporting the results files:
		System.out.println("Printing out results files......");
		ExportResults expoResults = new ExportResults();
		expoResults.wekaCalculate();
		expoResults.aggregateResults();
	}

}
