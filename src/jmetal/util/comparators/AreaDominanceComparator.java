//  LDominanceComparator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.util.comparators;

import java.util.Comparator;

import jmetal.core.Solution;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on a constraint violation test + 
 * dominance checking, as in NSGA-II.
 */
public class AreaDominanceComparator implements Comparator {
  IConstraintViolationComparator violationConstraintComparator_ ;
 
  /**
   * Constructor
   */
  public AreaDominanceComparator() {
    violationConstraintComparator_ = new OverallConstraintViolationComparator(); 
    //violationConstraintComparator_ = new NumberOfViolatedConstraintComparator(); 
  }

  /**
   * Constructor
   * @param comparator
   */
  public AreaDominanceComparator(IConstraintViolationComparator comparator) {
    violationConstraintComparator_ = comparator ;
  }
 
 /**
  * Compares two solutions.
  * @param object1 Object representing the first <code>Solution</code>.
  * @param object2 Object representing the second <code>Solution</code>.
  * @return -1, or 0, or 1 if solution1 dominates solution2, both are 
  * non-dominated, or solution1  is dominated by solution22, respectively.
  */
  public int compare(Object object1, Object object2) {
    if (object1==null)
      return 1;
    else if (object2 == null)
      return -1;
    
    Solution solution1 = (Solution)object1;
    Solution solution2 = (Solution)object2;

    // Test to determine whether at least a solution violates some constraint
    if (violationConstraintComparator_.needToCompare(solution1, solution2))
      return violationConstraintComparator_.compare(solution1, solution2) ;
 
    double norm1 = solution1.getDistanceToIdealPoint();//ie,the norm 
    double norm2 = solution2.getDistanceToIdealPoint();    
    
    int obj = solution1.getNormalizedObjectives().length;
    int div = 10;
    
    double delta = (double)Math.sqrt(obj)/div;
    
    if (norm1 < norm2 - delta )
    	return -1;
    
    if (norm1 > norm2 + delta )
    	return 1;
    
    
    return 0; 
    
   
  } // compare
} // DominanceComparator
