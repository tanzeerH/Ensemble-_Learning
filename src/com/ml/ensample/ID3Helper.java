package com.ml.ensample;
import java.util.ArrayList;

public class ID3Helper {
	ArrayList<ArrayList<Integer>> examplList;
	ArrayList<Integer> selectedAts;
	private int targetAtt=9;
	private int TOTAL_ATTRIBUTES=10;
	private ArrayList<ArrayList<Integer>> attValues;
	
	ID3Helper(ArrayList<ArrayList<Integer>> examplList,int targetAtt,ArrayList<Integer> attList,ArrayList<ArrayList<Integer>> attValues)
	{
		
		this.examplList=examplList;
		this.selectedAts=attList;
		this.targetAtt=targetAtt;
		this.attValues=attValues;
		//for(int i=0;i<TOTAL_ATTRIBUTES;i++)
			//System.out.println(""+attValues.get(i).size());
	}
	

	public boolean checkForAllPositive()
	{
		int length=examplList.get(0).size();
		boolean allPositive=true;
		for(int i=0;i<length;i++)
		{
			if(examplList.get(targetAtt).get(i)!=1)
			{
				allPositive=false;
				break;
			}
		}


		//System.out.println(allPositive);
		return allPositive;
	}
	public boolean checkForAllNegative()
	{
		int length=examplList.get(0).size();
		boolean allNegative=true;
		for(int i=0;i<length;i++)
		{
			if(examplList.get(targetAtt).get(i)!=0)
			{
				allNegative=false;
				break;
			}
		}

		return allNegative;
	}
	public int getMostCommonTargetAtt()
	{
		int length=examplList.get(0).size();
		int one_count=0;
		int zero_count=0;
		for(int i=0;i<length;i++)
		{
			if(examplList.get(targetAtt).get(i)==0)
			   zero_count++;
			else
				one_count++;
		}

		//System.out.println("zero "+zero_count+" one count"+ one_count);
	 return zero_count>=one_count?0:1;
	}
	public int getMostCommonTargetAtt(ArrayList<ArrayList<Integer>> list)
	{
		int length=list.get(0).size();
		int one_count=0;
		int zero_count=0;
		for(int i=0;i<length;i++)
		{
			if(list.get(targetAtt).get(i)==0)
			   zero_count++;
			else
				one_count++;
		}

	 return zero_count>one_count?0:1;
	}
	public int  getBestAttribute()
	{
		double bestGain=-10000;
		int bestAtt=-1;
		double sys_entropy=getExamplesEntropy(examplList);
		
		for(int i=0;i<selectedAts.size();i++)
		{
			
			  double gain =getInfoGain(selectedAts.get(i),sys_entropy);
			  if(gain>bestGain)
			  {
				  bestGain=gain;
				  bestAtt=selectedAts.get(i);
			  }
		}
		//System.out.println("best gain"+bestGain+ "att size"+ selectedAts.size());
		
		return bestAtt;
	}
	public double getInfoGain(int att,double sysEntropy)
	{
		int length=examplList.get(0).size();
		int attSize=attValues.get(att).size();
		double total_value=0;
		for(int i=0;i<attSize;i++)
		{
			int zero_count=0;
			int one_count=0;
			for(int j=0;j<length;j++)
			{
			
				if(examplList.get(att).get(j)==attValues.get(att).get(i))
				{
					if(examplList.get(targetAtt).get(j)==0)
						zero_count++;
					else
						one_count++;
				}
			}
			double total=one_count+zero_count;
			if(total>0)
			{
			double entropy=-(zero_count/total)* get2baseLog((zero_count)/total)- (one_count/total)*get2baseLog((one_count/total));
			total_value+=total/(double)(length)*entropy;
			}
			//System.out.println("zero and one count"+ zero_count+"  "+one_count+ "entropy "+ entropy);
		}
	//System.out.println("  gain "+ (sysEntropy-total_value) );
		return sysEntropy-total_value;
	}
	public double getExamplesEntropy(ArrayList<ArrayList<Integer>> examplList)
	{
		int length=examplList.get(0).size();
		double one_count=0;
		double zero_count=0;
		for(int i=0;i<length;i++)
		{
			if(examplList.get(targetAtt).get(i)==0)
			   zero_count++;
			else
				one_count++;
		}
		
		double total=one_count+zero_count;
	
		//System.out.println(total+" "+zero_count+" "+ one_count);
		double entropy= - (zero_count/total)* get2baseLog((zero_count)/total)- (one_count/total)*get2baseLog((one_count/total));
		
		//System.out.println("system entropy: "+ entropy+" "+one_count);
		
		return entropy;
	}
	public ArrayList<Integer> getAttributeValueLisByIndex(int att)
	{
		return attValues.get(att);
	}
	public ArrayList<ArrayList<Integer>> getExamplesByAttribute(int att,int value)
	{
		 ArrayList<ArrayList<Integer>> newExamples=new ArrayList<ArrayList<Integer>>();
		 
		 for(int i=0;i<TOTAL_ATTRIBUTES;i++)
			{
				ArrayList<Integer> list=new ArrayList<Integer>();
				newExamples.add(list);
			}
		 
		 int length=examplList.get(0).size();
		 for(int i=0;i<length;i++)
			 if(examplList.get(att).get(i)==value)
			 {
				 for(int j=0;j<TOTAL_ATTRIBUTES;j++)
					 newExamples.get(j).add(examplList.get(j).get(i));
			 }
			
		 
		 return newExamples;
		 
	}
	private double get2baseLog(double value)
	{
		if(value==0)
			return 0;
		double logs=Math.log(value)/Math.log(2);
		//System.out.println("2base log"+ logs+" input: "+ value);
		return logs;
	}
}
