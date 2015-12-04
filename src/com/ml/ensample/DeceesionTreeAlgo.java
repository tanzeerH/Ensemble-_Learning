package com.ml.ensample;

import java.beans.FeatureDescriptor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

import javax.net.ssl.TrustManagerFactorySpi;
import javax.print.attribute.standard.Severity;
import javax.print.attribute.standard.Sides;

public class DeceesionTreeAlgo {

	ArrayList<ArrayList<Integer>> attributeList = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> tempAttList = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> completeAttList = new ArrayList<ArrayList<Integer>>();
	ArrayList<Integer> selectedAttributes = new ArrayList<Integer>();
	private int targetAtt = 9;
	private int NUM_OF_COLUMS = 10;
	private int NUM_OF_ROUNDS = 30;
	int h = 0;
	int count = 0;
	int trainCount = 0;
	private ArrayList<Integer> suffleList = new ArrayList<Integer>();
	private ArrayList<ArrayList<Integer>> attValues = new ArrayList<ArrayList<Integer>>();

	private ArrayList<Double> weights = new ArrayList<Double>();
	private ArrayList<Double> normalWeights = new ArrayList<Double>();
	private ArrayList<Double> weightLimits = new ArrayList<Double>();
	ArrayList<ArrayList<Integer>> selectedTrainList = new ArrayList<ArrayList<Integer>>();
	ArrayList<TreeNode> hypothesisList = new ArrayList<TreeNode>();
	ArrayList<Double> betaList= new ArrayList<Double>();
	

	public DeceesionTreeAlgo() {

		this.targetAtt = 9;
		createArraylists();
		readFile();

		// runID3Algo();
		runAdaBoost();

	}

	private ArrayList<Integer> runID3Algo() {

		TreeNode root = algorithmID3(selectedTrainList, targetAtt,
				selectedAttributes);
		hypothesisList.add(root);
		// System.out.println("root att: " + root.getAttribute() + " child: "
		// + root.getChildList().size());
		ArrayList<Integer> results = new Tester(root, selectedTrainList,
				targetAtt, selectedAttributes, 0, trainCount, 0).testTrainSet(0);
		// Tester t=new Tester(root, completeAttList, targetAtt,
		// selectedAttributes,0,60);
		return results;

	}

	private void selectSample() {

		Random rand = new Random();
		for (int i = 0; i < NUM_OF_COLUMS; i++) {
			selectedTrainList.get(i).clear();
		}
		for (int i = 0; i < trainCount; i++) {
			double val = rand.nextDouble();
			for (int j = 0; j < trainCount; j++) {
				if (val <= weightLimits.get(j)) {
					// System.out.println(""+j);
					for (int k = 0; k < NUM_OF_COLUMS; k++) {
						selectedTrainList.get(k).add(
								attributeList.get(k).get(j));
					}
					break;
				}
			}
		}
		//System.out.println("selected train size: "
			//	+ selectedTrainList.get(0).size() + "  train_count: "
			//	+ trainCount);
	}

	private void runAdaBoost() {
		
		ArrayList<String> list=new ArrayList<String>();
		copyAttributes();
		//initShufflelist(count);
		//shuffleList();
		copyWithoutShuffling();
		createAttValuelist();

		double sum = 0;
		for (int i = 0; i < trainCount; i++) {
			double w = 1 / (double) trainCount;
			sum += w;
			weights.add(w);
		}
		
		for (int i = 0; i < NUM_OF_ROUNDS; i++) {
			normalWeights.clear();
			weightLimits.clear();
			double w_sum = 0;
			for (int j = 0; j < trainCount; j++) {
				double nw = weights.get(j) / sum;
				w_sum += nw;
				normalWeights.add(nw);
				weightLimits.add(w_sum);

			}
			// System.out.println("weight sum: "+ w_sum);
			selectSample();
			ArrayList<Integer> results = runID3Algo();

			int size = results.size();
			double error=0;
			for (int j = 0; j < size; j++) {
				double mismatch=0;
				if(selectedTrainList.get(targetAtt).get(j)!=results.get(j))
					mismatch=1;
				error+=normalWeights.get(j)*mismatch;
			}
			double beta=error/(1- error);
			betaList.add(beta);
			if(error>.5)
				break;
			String str="Number of rounds:  "+ NUM_OF_ROUNDS+ " decession tree: "+ (i+1)+" accuracy: "+(1-error)*100+" %";
			System.out.println(str);
			list.add(str);
			
			sum=0;
			for (int j = 0; j < size; j++) {
				
				if(selectedTrainList.get(targetAtt).get(j)==results.get(j))
				{
					weights.set(j,weights.get(j)*beta);
					sum+=weights.get(j);
				}
			}
			
			
		}
	
		Tester tester=new Tester(completeAttList);
		int count_x=0;
		for(int i=trainCount;i<count;i++)
		{
			double x=0;
			double y=0;
			for(int j=0;j<hypothesisList.size();j++)
			{
				x+=Math.log(1/betaList.get(j))*tester.testTestData(hypothesisList.get(j), 0,i);
				y+=Math.log(1/betaList.get(j))*tester.testTestData(hypothesisList.get(j), 1,i);
			}
			int output;
			if(x>y)
				output=0;
			else
				output=1;
			
			if(output==completeAttList.get(targetAtt).get(i))
				count_x++;
		}
		double res=((double)count_x/(count-trainCount))*100;
		String str="Total accuracy: "+res+" %\n\n";
		System.out.println(str);
		list.add(str);
		writeInFile(list);
	}

	private void createArraylists() {
		for (int i = 0; i < NUM_OF_COLUMS; i++) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			ArrayList<Integer> list1 = new ArrayList<Integer>();
			attributeList.add(list);
			selectedTrainList.add(list1);
		}

		for (int i = 0; i < NUM_OF_COLUMS; i++) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			tempAttList.add(list);
		}

		for (int i = 0; i < NUM_OF_COLUMS; i++) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			completeAttList.add(list);
		}

		for (int i = 0; i < NUM_OF_COLUMS; i++) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			attValues.add(list);
		}

	}

	private void copyAttributes() {
		selectedAttributes.clear();
		for (int i = 0; i < NUM_OF_COLUMS - 1; i++)
			selectedAttributes.add(i);

	}

	private void readFile() {
		count = 0;
		BufferedReader br = null;
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("data.csv"));

			while ((sCurrentLine = br.readLine()) != null) {

				String[] values = sCurrentLine.split(",");
				organizeValues(values);

				// for testing purpose
				count++;
				// if(count>200)
				// break;

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		// Collections.shuffle(attributeList);
		// printValues();
	}

	private void initShufflelist(int count) {
		suffleList.clear();
		for (int i = 0; i < count; i++)
			suffleList.add(i);
	}

	private void shuffleList() {
		Collections.shuffle(suffleList);

		for (int j = 0; j < NUM_OF_COLUMS; j++)
			tempAttList.get(j).clear();

		for (int i = 0; i < count; i++)
			for (int j = 0; j < NUM_OF_COLUMS; j++)
				tempAttList.get(j).add(completeAttList.get(j).get(i));

		for (int j = 0; j < NUM_OF_COLUMS; j++)
			completeAttList.get(j).clear();

		// System.out.println("size"+tempAttList.size());
		for (int i = 0; i < count; i++)
			for (int j = 0; j < NUM_OF_COLUMS; j++) {
				completeAttList.get(j).add(
						tempAttList.get(j).get(suffleList.get(i)));
				// completeAttList.get(j).add(tempAttList.get(j).get(i));
			}

		for (int j = 0; j < NUM_OF_COLUMS; j++)
			attributeList.get(j).clear();

		trainCount = (int) (count * .8);

		for (int i = 0; i < trainCount; i++) {
			for (int j = 0; j < NUM_OF_COLUMS; j++) {
				attributeList.get(j).add(completeAttList.get(j).get(i));
			}
		}

	}
	private void copyWithoutShuffling()
	{
		trainCount = (int) (count * .8);
		for (int i = 0; i < trainCount; i++) {
			for (int j = 0; j < NUM_OF_COLUMS; j++) {
				attributeList.get(j).add(completeAttList.get(j).get(i));
			}
		}
	}

	public void createAttValuelist() {
		for (int j = 0; j < NUM_OF_COLUMS; j++) {
			attValues.get(j).clear();
			// System.out.println("val"+ j+"  "+val);

		}

		int length = attributeList.get(0).size();

		for (int i = 0; i < length; i++) {
			for (int j = 0; j < NUM_OF_COLUMS; j++) {
				int val = attributeList.get(j).get(i);
				if (!attValues.get(j).contains(val))
					attValues.get(j).add(val);
			}
		}

		// System.out.println("Printing values");
		for (int j = 0; j < NUM_OF_COLUMS; j++) {
			int val = attValues.get(j).size();
			// System.out.println("val"+ j+"  "+val);

		}
		// System.out.println("Printing values again");
		for (int j = 0; j < attValues.get(0).size(); j++) {
			int val = attValues.get(0).get(j);
			// System.out.println("val"+ j+"  "+val);

		}

	}

	public TreeNode algorithmID3(ArrayList<ArrayList<Integer>> attList,
			int targetAtt, ArrayList<Integer> selectedAtts) {
		/*
		 * h++; if(h>10) return null;
		 */
		ID3Helper id3Helper = new ID3Helper(attList, targetAtt, selectedAtts,
				attValues);

		TreeNode treeNode = new TreeNode();
		boolean check = id3Helper.checkForAllPositive();
		if (check) {
			treeNode.setAttribute(targetAtt);
			treeNode.setAttributeValue(1);

			//System.out.println("All positive found");
			return treeNode;
		}
		check = id3Helper.checkForAllNegative();
		if (check) {
			treeNode.setAttribute(targetAtt);
			treeNode.setAttributeValue(0);

			//System.out.println("All negative found");
			return treeNode;
		}
		if (selectedAtts.size() == 0) {
			treeNode.setAttribute(targetAtt);
			treeNode.setAttributeValue(id3Helper.getMostCommonTargetAtt());
			// System.out.println("attlist empty. most common value :"+targetAtt);

			return treeNode;
		}
		int bestAtt = id3Helper.getBestAttribute();

		treeNode.setAttribute(bestAtt); // setting best attribute for tree node

		//System.out.println("best attr " + bestAtt);
		ArrayList<Integer> valueList = id3Helper
				.getAttributeValueLisByIndex(bestAtt);
		// System.out.println("best attr "+ bestAtt +
		// "child size "+valueList.size());
		for (int i = 0; i < valueList.size(); i++) {
			TreeNode branchNode = new TreeNode();
			branchNode.setAttribute(bestAtt);
			branchNode.setAttributeValue(valueList.get(i));
			treeNode.getChildList().add(branchNode);
			ArrayList<ArrayList<Integer>> newExList = id3Helper
					.getExamplesByAttribute(bestAtt, valueList.get(i));
			// System.out.println("att size"+ selectedAtts.size());
			// System.out.println("new example size:"+
			// newExList.get(bestAtt).size());

			if (newExList.isEmpty()) {
				TreeNode leafNode = new TreeNode();
				leafNode.setAttribute(targetAtt);
				leafNode.setAttributeValue(id3Helper.getMostCommonTargetAtt());
				branchNode.getChildList().add(leafNode);
			} else {
				selectedAtts.remove(new Integer(bestAtt));
				branchNode.getChildList().add(
						algorithmID3(newExList, targetAtt,
								new ArrayList<Integer>()));
			}
		}
		return treeNode;

	}
	private void writeInFile(ArrayList<String> lines) {

		BufferedReader br = null;
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("output.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				
				lines.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		
		try {

			File file = new File("output.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			// writing results
			for(int i=0;i<lines.size();i++)
			{
				String str = lines.get(i);
				
			//System.out.println(str);
			bw.write(str + "\n");
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void printValues() {
		int length = completeAttList.get(0).size();
		for (int i = 0; i < length; i++) {
			System.out.println("" + completeAttList.get(9).get(i) + " i" + i);
		}
	}

	private void organizeValues(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			completeAttList.get(i).add(Integer.parseInt(arr[i]));
		}
	}

}
