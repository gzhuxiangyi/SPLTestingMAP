//  NumberOfConstraintsRanking.java
//
//  Author:
//      Yi Xiang <gzhuxiang_yi@163.com>
//       
//

package jmetal.util.ranking;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jmetal.core.SolutionSet;
import jmetal.util.Utils;

/**
 * This class implements some facilities for ranking solutions. Solutions with equal number of constraints belong
 * to the same layer. It is used in problems whose constraints are integer numbers.
 */
public class NumberOfConstraintsRanking implements Ranking {

	/**
	 * The <code>SolutionSet</code> to rank
	 */
	private SolutionSet solutionSet_;

	/**
	 * An array containing all the fronts found during the search
	 */
	private SolutionSet[] ranking_;


	/**
	 * Constructor.
	 * 
	 * @param solutionSet
	 *            The <code>SolutionSet</code> to be ranked.
	 */
	public NumberOfConstraintsRanking(SolutionSet solutionSet) {
		solutionSet_ = solutionSet;
		int numberOfSolutions = solutionSet_.size();

		int [] numOfViolatedConstraint = new int [numberOfSolutions];
		int [] index          =  new int [numberOfSolutions];
		
		for (int i = 0; i < numberOfSolutions;i++) {
			index [i]                   = i;
			numOfViolatedConstraint [i] = solutionSet_.get(i).getNumberOfViolatedConstraint();
		}
		
		Utils.QuickSort(numOfViolatedConstraint, index, 0, numberOfSolutions - 1);
		
		// front[i] contains the list of individuals belonging to the front i
		List<Integer>[] front = new List[solutionSet_.size() + 1];
		
		// Initialize the fronts
		for (int i = 0; i < front.length; i++)
			front[i] = new LinkedList<Integer>();
		
		int front_num = 0;
	
		int current_p = numOfViolatedConstraint[0];
		
		for (int i = 0; i < numberOfSolutions; i ++) {
			
			if (numOfViolatedConstraint[i] == current_p) {
				front[front_num].add(index[i]);
				solutionSet_.get(index[i]).setRank(front_num);
			} else {
				current_p = numOfViolatedConstraint[i];
				front_num = front_num  + 1;
				front[front_num].add(index[i]);
				solutionSet_.get(index[i]).setRank(front_num);
			}
		}
				

		ranking_ = new SolutionSet[front_num  + 1];
		// 0,1,2,....,i-1 are front, then i fronts
		for (int j = 0; j < ranking_.length; j++) {
			ranking_[j] = new SolutionSet(front[j].size());
			Iterator<Integer> it1 = front[j].iterator();
			while (it1.hasNext()) {				
				ranking_[j].add(solutionSet.get(it1.next()));
			}
		}

	} // Ranking

	/**
	 * Returns a <code>SolutionSet</code> containing the solutions of a given
	 * rank.
	 * 
	 * @param rank
	 *            The rank
	 * @return Object representing the <code>SolutionSet</code>.
	 */
	public SolutionSet getSubfront(int rank) {
		return ranking_[rank];
	} // getSubFront

	/**
	 * Returns the total number of subFronts founds.
	 */
	public int getNumberOfSubfronts() {
		return ranking_.length;
	} // getNumberOfSubfronts
} // Ranking
