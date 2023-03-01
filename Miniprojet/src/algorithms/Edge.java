/**
 *  Steiner Tree Problem with/without restrictions
 *  	
 * 	Interface graphique de M. Binh-Minh Bui Xuan.
 * 	
 *  Inspired by :
 *  	 L. Kou, G. Markowsky, and L. Berman,
 *		 A fast Algorithm for Steiner Trees, IBM Thomas J. Watson Research Center,
 *  	 http://aturing.umcs.maine.edu/~markov/SteinerTrees.pdf
 *  
 *  
 *  
 * 	@author Amine Benslimane    https://github.com/bnslmn
 * 	Master 1 STL,
 * 	Sorbonne University,
 * 	Mai 2021.
 * 
 * GPLv3 Licence, feel free to use this code.
 * */
package algorithms;

import java.awt.Point;

public class Edge implements Comparable<Edge> {

	public  Point p;
	public Point q;
	public  double dist;

	public Edge(Point u, Point v) {
		this.p = u;
		this.q = v;
		this.dist = u.distance(v);
	}

	public Edge(Point u, Point v, double dist) {
		this.p = u;
		this.q = v;
		this.dist = dist;
	}
 protected double distance(){ return p.distance(q); }
	public Point getU() {
		return p;
	}

	public Point getV() {
		return q;
	}

	public double getDist() {
		return dist;
	}

	@Override
	public int compareTo(Edge e) {
		if (this.dist > e.getDist())
			return 1;
		else if (this.dist < e.getDist())
			return -1;
		else
			return 0;
	}


	
	

}