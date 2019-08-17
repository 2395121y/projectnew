// File: ExportResults.java
// Author: Shawn Yeng Wei Xen (2395121Y)
// This file produces the results based on the training data and test data for Naive Bayes and SMO, along with aggregate
// for all of the ARFF files.

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import weka.classifiers.AggregateableEvaluation;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class ExportResults {

	ArrayList<Evaluation> evalNaiveBayesList;
	ArrayList<Evaluation> evalSMOList;

	// Constructor file, adding in all the evaluations for the Naive Bayes and SMO.
	public ExportResults()
	{
		this.evalNaiveBayesList = new ArrayList<Evaluation>();
		this.evalSMOList = new ArrayList<Evaluation>();
	}

	// Similar to Weka, this function outputs the results for each category and each event.
	public void wekaCalculate()
	{
		for (int categoryStep = 0; categoryStep < 6; categoryStep++)
		{
			String trainString = "Train";
			String testString = "Test";
			String categoryString;
			String resultString = "Results";
			String textString;
			String eventString;

			switch (categoryStep)
			{
			case 0: categoryString = "Cont.arff"; break;
			case 1: categoryString = "Dona.arff"; break;
			case 2: categoryString = "Offi.arff"; break;
			case 3: categoryString = "Advi.arff"; break;
			case 4: categoryString = "Mult.arff"; break;
			default: categoryString = "Good.arff";
			}
			
			switch (categoryStep)
			{
			case 0: textString = "Cont.txt"; break;
			case 1: textString = "Dona.txt"; break;
			case 2: textString = "Offi.txt"; break;
			case 3: textString = "Advi.txt"; break;
			case 4: textString = "Mult.txt"; break;
			default: textString = "Good.txt";
			}
			
			for (int eventStep = 0; eventStep < 15; eventStep++)
			{
				String trainingData;
				String testData;
				String resultText;

				switch (eventStep)
				{
				case 0: eventString = "2011Joplin"; break;
				case 1: eventString = "2012Guatemala"; break; 
				case 2: eventString = "2012Italy"; break;
				case 3: eventString = "2012Philipinne"; break;
				case 4: eventString = "2013Alberta";	break;
				case 5: eventString = "2013Australia"; break;
				case 6: eventString = "2013Boston"; break;				
				case 7: eventString = "2013Manila"; break;
				case 8: eventString = "2013Queens"; break;
				case 9: eventString = "2013Yolanda"; break;
				case 10: eventString = "2014Chile"; break;
				case 11: eventString = "2014Hagupit"; break;
				case 12: eventString = "2015Nepal"; break;
				case 13: eventString = "2015Paris"; break;
				default: eventString = "2018Florida"; 				
				}

				trainingData = eventString;
				trainingData += trainString;
				trainingData += categoryString;

				testData = eventString;
				testData += testString;
				testData += categoryString;
				
				

				resultText = eventString;
				resultText += resultString;
				resultText += textString;
				

				try {
					ConverterUtils.DataSource  loader1 = new ConverterUtils.DataSource(trainingData);
					ConverterUtils.DataSource  loader2 = new ConverterUtils.DataSource(testData);


					BufferedWriter bw = new BufferedWriter(new FileWriter(resultText));
					Instances trainData = loader1.getDataSet();
					trainData.setClassIndex(trainData.numAttributes() - 1);

					Instances testingData = loader2.getDataSet();
					testingData.setClassIndex(testingData.numAttributes() - 1);

					Classifier cls1 = new NaiveBayes();					
					cls1.buildClassifier(trainData);			
					Evaluation eval1 = new Evaluation(trainData);
					eval1.evaluateModel(cls1, testingData);	
					bw.write("=== Summary of Naive Bayes ===");
					bw.write(eval1.toSummaryString());
					bw.write(eval1.toClassDetailsString());
					bw.write(eval1.toMatrixString());
					bw.write("\n");

					this.evalNaiveBayesList.add(eval1);

					Classifier cls2 = new SMO();
					cls2.buildClassifier(trainData);
					Evaluation eval2 = new Evaluation(trainData);
					eval2.evaluateModel(cls2, testingData);
					bw.write("=== Summary of SMO ===");
					bw.write(eval2.toSummaryString());
					bw.write(eval2.toClassDetailsString());
					bw.write(eval2.toMatrixString());

					this.evalSMOList.add(eval2);

					bw.close();
				} catch (Exception e) {			
					e.printStackTrace();
				}
			}
		}
	}

	// This function outputs results for aggregates for all categories and overall.
	public void aggregateResults()
	{
		try {
			AggregateableEvaluation aggContNaive = new AggregateableEvaluation(this.evalNaiveBayesList.get(0));
			AggregateableEvaluation aggContSMO = new AggregateableEvaluation(this.evalSMOList.get(0));
			AggregateableEvaluation aggDonaNaive = new AggregateableEvaluation(this.evalNaiveBayesList.get(15));
			AggregateableEvaluation aggDonaSMO = new AggregateableEvaluation(this.evalSMOList.get(15));
			AggregateableEvaluation aggOffiNaive = new AggregateableEvaluation(this.evalNaiveBayesList.get(30));
			AggregateableEvaluation aggOffiSMO = new AggregateableEvaluation(this.evalSMOList.get(30));
			AggregateableEvaluation aggAdviNaive = new AggregateableEvaluation(this.evalNaiveBayesList.get(45));
			AggregateableEvaluation aggAdviSMO = new AggregateableEvaluation(this.evalSMOList.get(45));
			AggregateableEvaluation aggMultNaive = new AggregateableEvaluation(this.evalNaiveBayesList.get(60));
			AggregateableEvaluation aggMultSMO = new AggregateableEvaluation(this.evalSMOList.get(60));
			AggregateableEvaluation aggGoodNaive = new AggregateableEvaluation(this.evalNaiveBayesList.get(75));
			AggregateableEvaluation aggGoodSMO = new AggregateableEvaluation(this.evalSMOList.get(75));
			AggregateableEvaluation aggCompleteNaive = new AggregateableEvaluation(this.evalNaiveBayesList.get(0));
			AggregateableEvaluation aggCompleteSMO = new AggregateableEvaluation(this.evalSMOList.get(0));

			for (int i = 1; i < 15; i++)
			{
				aggContNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggContSMO.aggregate(this.evalSMOList.get(i));
				aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggCompleteSMO.aggregate(this.evalSMOList.get(i));
			}

			aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(15));
			aggCompleteSMO.aggregate(this.evalSMOList.get(15));

			for (int i = 16; i < 30; i++)
			{
				aggDonaNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggDonaSMO.aggregate(this.evalSMOList.get(i));
				aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggCompleteSMO.aggregate(this.evalSMOList.get(i));
			}

			aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(30));
			aggCompleteSMO.aggregate(this.evalSMOList.get(30));

			for (int i = 31; i < 45; i++)
			{
				aggOffiNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggOffiSMO.aggregate(this.evalSMOList.get(i));
				aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggCompleteSMO.aggregate(this.evalSMOList.get(i));
			}

			aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(45));
			aggCompleteSMO.aggregate(this.evalSMOList.get(45));

			for (int i = 46; i < 60; i++)
			{
				aggAdviNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggAdviSMO.aggregate(this.evalSMOList.get(i));
				aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggCompleteSMO.aggregate(this.evalSMOList.get(i));
			}

			aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(60));
			aggCompleteSMO.aggregate(this.evalSMOList.get(60));

			for (int i = 61; i < 75; i++)
			{
				aggMultNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggMultSMO.aggregate(this.evalSMOList.get(i));
				aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggCompleteSMO.aggregate(this.evalSMOList.get(i));
			}

			aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(75));
			aggCompleteSMO.aggregate(this.evalSMOList.get(75));

			for (int i = 76; i < 90; i++)
			{
				aggGoodNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggGoodSMO.aggregate(this.evalSMOList.get(i));
				aggCompleteNaive.aggregate(this.evalNaiveBayesList.get(i));
				aggCompleteSMO.aggregate(this.evalSMOList.get(i));
			}

			for (int a = 0; a < 7; a++)
			{
				String aggregateText;
				switch (a)
				{
				case 0: aggregateText = "ContinuingNewsAggregateResults.txt"; break;
				case 1: aggregateText = "DonationsAggregateResults.txt"; break;
				case 2: aggregateText = "OfficialAggregateResults.txt"; break;
				case 3: aggregateText = "AdviceAggregateResults.txt"; break;
				case 4: aggregateText = "MultimediaAggregateResults.txt"; break;
				case 5: aggregateText = "GoodsAggregateResults.txt"; break;
				default: aggregateText = "CompleteAggregateResults.txt";
				}

				BufferedWriter bw = new BufferedWriter(new FileWriter(aggregateText));
				bw.write("=== Summary of Naive Bayes ===");

				switch (a)
				{
				case 0:	bw.write(aggContNaive.toSummaryString());
				bw.write(aggContNaive.toClassDetailsString());
				bw.write(aggContNaive.toMatrixString()); break;
				case 1:	bw.write(aggDonaNaive.toSummaryString());
				bw.write(aggDonaNaive.toClassDetailsString());
				bw.write(aggDonaNaive.toMatrixString()); break;
				case 2:	bw.write(aggOffiNaive.toSummaryString());
				bw.write(aggOffiNaive.toClassDetailsString());
				bw.write(aggOffiNaive.toMatrixString()); break;
				case 3:	bw.write(aggAdviNaive.toSummaryString());
				bw.write(aggAdviNaive.toClassDetailsString());
				bw.write(aggAdviNaive.toMatrixString()); break;
				case 4:	bw.write(aggMultNaive.toSummaryString());
				bw.write(aggMultNaive.toClassDetailsString());
				bw.write(aggMultNaive.toMatrixString()); break;
				case 5:	bw.write(aggGoodNaive.toSummaryString());
				bw.write(aggGoodNaive.toClassDetailsString());
				bw.write(aggGoodNaive.toMatrixString()); break;
				default:	bw.write(aggCompleteNaive.toSummaryString());
				bw.write(aggCompleteNaive.toClassDetailsString());
				bw.write(aggCompleteNaive.toMatrixString()); 
				}
				
				bw.write("=== Summary of SMO ===");

				switch (a)
				{
				case 0:	bw.write(aggContSMO.toSummaryString());
				bw.write(aggContSMO.toClassDetailsString());
				bw.write(aggContSMO.toMatrixString()); break;
				case 1:	bw.write(aggDonaSMO.toSummaryString());
				bw.write(aggDonaSMO.toClassDetailsString());
				bw.write(aggDonaSMO.toMatrixString()); break;
				case 2:	bw.write(aggOffiSMO.toSummaryString());
				bw.write(aggOffiSMO.toClassDetailsString());
				bw.write(aggOffiSMO.toMatrixString()); break;
				case 3:	bw.write(aggAdviSMO.toSummaryString());
				bw.write(aggAdviSMO.toClassDetailsString());
				bw.write(aggAdviSMO.toMatrixString()); break;
				case 4:	bw.write(aggMultSMO.toSummaryString());
				bw.write(aggMultSMO.toClassDetailsString());
				bw.write(aggMultSMO.toMatrixString()); break;
				case 5:	bw.write(aggGoodSMO.toSummaryString());
				bw.write(aggGoodSMO.toClassDetailsString());
				bw.write(aggGoodSMO.toMatrixString()); break;
				default:	bw.write(aggCompleteSMO.toSummaryString());
				bw.write(aggCompleteSMO.toClassDetailsString());
				bw.write(aggCompleteSMO.toMatrixString()); 
				}
				
				bw.close();
			}


		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
