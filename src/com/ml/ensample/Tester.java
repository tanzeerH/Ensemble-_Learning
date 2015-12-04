package com.ml.ensample;

import java.util.ArrayList;

public class Tester {

	private TreeNode rootNode;
	ArrayList<ArrayList<Integer>> examplList;
	// ArrayList<Integer> selectedAts;
	private int targetAtt = 9;
	private int TOTAL_ATTRIBUTES = 10;
	int start = 0;
	int end = 0;
	int count = 0;
	int right_count = 0;
	double accuracy = 0;
	double precession = 0;
	double negative_presession = 0;
	double recall = 0;
	double f_measure = 0;
	double g_mean = 0;
	int true_positive = 0;
	int false_negative = 0;
	int false_positive = 0;
	int true_negative = 0;
	int round = 0;

	public Tester(TreeNode rootNode, ArrayList<ArrayList<Integer>> examplList,
			int targetAtt, ArrayList<Integer> attList, int s, int e, int round) {

		this.rootNode = rootNode;
		this.examplList = examplList;
		// this.selectedAts = attList;
		this.targetAtt = targetAtt;
		start = s;
		end = e;
		this.round = round;
		// testDataSet();
	}

	public Tester(ArrayList<ArrayList<Integer>> examplList) {
		this.examplList = examplList;
	}

	public ArrayList<Integer> testTrainSet(int flag) {
		ArrayList<Integer> results = new ArrayList<Integer>();

		for (int i = start; i < end; i++)
			results.add(testData(i, rootNode, flag));

		if (flag == 1) {
			accuracy = right_count / (double) (end - start) * 100;
			Constants.g_accuracy += accuracy;

			precession = (true_positive)
					/ (double) (true_positive + false_negative) * 100;
			Constants.g_precession += precession;

			negative_presession = (true_negative)
					/ (double) (true_negative + false_positive) * 100;
			Constants.g_neg_accuracy += negative_presession;
			// System.out.println("neg : "+ negative_presession + "pos "+
			// precession);
			recall = (true_positive)
					/ (double) (true_positive + false_positive) * 100;
			Constants.g_recall += recall;

			f_measure = (2 * recall * precession) / (recall + precession);
			Constants.f_measure += f_measure;

			g_mean = Math.sqrt(negative_presession * precession);
			Constants.g_mean += g_mean;
			// System.out.println("gmean : "+ g_mean);

			// if(round==99)
			// {
			System.out.println("accuracy:  "
					+ (Constants.g_accuracy / (double) (round + 1)) + "%");
			System.out.println("precession:  "
					+ (Constants.g_precession / (double) (round + 1)) + "%");
			System.out.println("recall:  "
					+ (Constants.g_recall / (double) (round + 1)) + "%");
			System.out.println("f-measure:  "
					+ (Constants.f_measure / (double) (round + 1)) + "%");
			System.out.println("gmean:  "
					+ (Constants.g_mean / (double) (round + 1)) + "%");
			// }
		}
		return results;
	}

	public int testData(int index, TreeNode rn, int flag) {
		TreeNode tempNode = rn;

		while (tempNode.getAttribute() != targetAtt) {
			// System.out.println("temp node "+ tempNode.getAttribute()+
			// "value "+tempNode.getAttributeValue());
			int att_no = tempNode.getAttribute();
			// int att_value=tempNode.getAttributeValue();
			for (int i = 0; i < tempNode.getChildList().size(); i++) {

				if (tempNode.getChildList().get(i).getAttribute() == targetAtt
						|| tempNode.getChildList().get(i).getAttributeValue() == examplList
								.get(att_no).get(index)) {
					tempNode = tempNode.getChildList().get(i);
					break;
				}
			}

		}

		// System.out.println(tempNode.getAttributeValue()+"   "+examplList.get(targetAtt).get(index));
		if (flag == 1) {
			if (tempNode.getAttributeValue() == examplList.get(targetAtt).get(
					index))
				right_count++;
			if (tempNode.getAttributeValue() == 1
					&& examplList.get(targetAtt).get(index) == 1)
				true_positive++;

			if (tempNode.getAttributeValue() == 0
					&& examplList.get(targetAtt).get(index) == 1)
				false_negative++;

			if (tempNode.getAttributeValue() == 1
					&& examplList.get(targetAtt).get(index) == 0)
				false_positive++;

			if (tempNode.getAttributeValue() == 0
					&& examplList.get(targetAtt).get(index) == 0)
				true_negative++;

		}
		return tempNode.getAttributeValue();

	}

	public double testTestData(TreeNode root_node, int output, int index) {
		TreeNode tempNode = root_node;

		while (tempNode.getAttribute() != targetAtt) {
			// System.out.println("temp node "+ tempNode.getAttribute()+
			// "value "+tempNode.getAttributeValue());
			int att_no = tempNode.getAttribute();
			// int att_value=tempNode.getAttributeValue();
			for (int i = 0; i < tempNode.getChildList().size(); i++) {

				if (tempNode.getChildList().get(i).getAttribute() == targetAtt
						|| tempNode.getChildList().get(i).getAttributeValue() == examplList
								.get(att_no).get(index)) {
					tempNode = tempNode.getChildList().get(i);
					break;
				}
			}

		}
		if (output == 1 && tempNode.getAttributeValue() == 1)
			return 1;
		else if (output == 0 && tempNode.getAttributeValue() == 0)
			return 1;
		else
			return 0;
	}

}
