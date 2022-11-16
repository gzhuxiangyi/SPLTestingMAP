/*
 * Author : Christopher Henard (christopher.henard@uni.lu)
 * Date : 21/05/2012
 * Copyright 2012 University of Luxembourg – Interdisciplinary Centre for Security Reliability and Trust (SnT)
 * All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package spl.fm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a product (i.e. a list of features, selected or not).
 * 
 * @author Christopher Henard
 */
public class Product extends HashSet<Integer> implements Comparable<Product>, Serializable{

    /* Relative coverage of this product. This value depends on the number of pairs 
     * covered by the previous products when we evaluate the coverage of a set of 
     * products.     */
    private double coverage;
    private double curiosity_score;
    private double novelty_score;
    private boolean newInserted;
    
    static final long serialVersionUID = -6618469841127325812L;
    
    public Product() {
        super();
        coverage = 0;
    }
    
    public Product(Product other) {
        super(other);
    }
    
    public double getCoverage() {
        return coverage;
    }
    
    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }
    
    
    public double getCuriosity_score() {
		return curiosity_score;
	}

	public void setCuriosity_score(double curiosity_score) {
		this.curiosity_score = curiosity_score;
	}

	public double getNovelty_score() {
		return novelty_score;
	}

	public boolean isNewInserted() {
		return newInserted;
	}

	public void setNewInserted(boolean newInserted) {
		this.newInserted = newInserted;
	}

	public void setNovelty_score(double novelty_score) {
		this.novelty_score = novelty_score;
	}

	public int getSelectedNumber() {
        int n = 0;
        for (Integer i : this) {
            if (i > 0) {
                n++;
            }
        }
        return n;
    }

    /**
     * Return the set of pairs of features covered by this product.
     * @return a set of pairs of features covered by this product.
     */
    public Set<TSet> computeCoveredPairs() {
        
        List<Integer> pl = new ArrayList<Integer>(this);
        int size = size();
        Set<TSet> pairs = new HashSet<TSet>(size * (size - 1) / 2);// 每个测试用例均覆盖Cn2个pairs
        try {
            nCk(size, 2, pairs, pl);
        } catch (Exception ex) {
            Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pairs;
    }
    
    /**
     * 判断一个product是否包含一个pair，只需检查pair中的每个元素是否在produc中
     * @param t
     * @return
     */
    
    public boolean containsPair(TSet t) {
        for (Integer i : t.getVals()) {
            if (!contains(i)) {
                return false;
            }
        }
        return true;
    }
    
    public Set<TSet> computePartialCoveredPairs(Set<TSet> allPairs) {
        Set<TSet> pairs = new HashSet<TSet>();
        
        for (TSet t : allPairs) {
            if (containsPair(t)) {
                pairs.add(t);
            }
        }
        return pairs;
    }
    
    public void nCk(int n, int k, Set<TSet> tsets, List<Integer> featuresList) throws Exception {
        int[] a = new int[k];
        nCkH(n, k, 0, a, k, tsets, featuresList);
    }
    
    public void nCkH(int n, int loopno, int ini, int[] a, int k, Set<TSet> tsets, List<Integer> featuresList) throws Exception {
        
        if (k == 0) {
            return;
        }
        
        int i;
        loopno--;
        if (loopno < 0) {
            a[k - 1] = ini - 1;
            TSet p = new TSet();
            for (i = 0; i < k; i++) {
                p.add(featuresList.get(a[i]));
            }
            tsets.add(p);
            return;
            
        }
        for (i = ini; i <= n - loopno - 1; i++) {
            a[k - 1 - loopno] = i;
            nCkH(n, loopno, i + 1, a, k, tsets, featuresList);
        }
        
        
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Product p) {
		// TODO Auto-generated method stub
		double selected1 = getSelectedNumber();
        double selected2 = p.getSelectedNumber();
        return Double.compare(selected1, selected2);
	}
}
