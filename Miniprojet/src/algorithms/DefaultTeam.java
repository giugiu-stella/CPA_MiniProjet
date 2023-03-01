package algorithms;

import java.awt.Point;
import java.util.ArrayList;

public class DefaultTeam {
  
  public int[][] calculShortestPaths(ArrayList<Point> points, int edgeThreshold) {
    int[][] paths=new int[points.size()][points.size()];
    for (int i=0;i<paths.length;i++) for (int j=0;j<paths.length;j++) paths[i][j]=-1;
    double [][] dist= new double[points.size()][points.size()];
    for (int i=0;i<dist.length;i++) for (int j=0;j<dist.length;j++) dist[i][j]=Double.MAX_VALUE;
    matriceVoisins(dist,paths,points,edgeThreshold);
    for(int k=0;k<paths.length;k++) {
    	for(int i=0;i<paths.length;i++) {
    		for(int j=0;j<paths.length;j++) {
    			if (dist[i][j]>(dist[i][k]+dist[k][j])) {
    				paths[i][j]=paths[i][k];
    				dist[i][j]=(dist[i][k]+dist[k][j]);
    			}
    		}
    	}
    }
    
    return paths;
  }
  
  public void matriceVoisins(double[][] dist,int[][] paths,ArrayList<Point> points,int edgeThreshold){
	  for(int i=0;i<paths.length;i++) {
		  for (int j=0;j<paths.length;j++) {
			  if(points.get(i).distance(points.get(j)) < edgeThreshold) {
				  paths[i][j]=j; 
				  dist[i][j]=points.get(i).distance(points.get(j));
			  }
		  }
	  }
  }
  
  public Tree2D calculSteiner(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
    //REMOVE >>>>>
    Tree2D leafX = new Tree2D(new Point(700,400),new ArrayList<Tree2D>());
    Tree2D leafY = new Tree2D(new Point(700,500),new ArrayList<Tree2D>());
    Tree2D leafZ = new Tree2D(new Point(800,450),new ArrayList<Tree2D>());
    ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
    subTrees.add(leafX);
    subTrees.add(leafY);
    subTrees.add(leafZ);
    Tree2D steinerTree = new Tree2D(new Point(750,450),subTrees);
    //<<<<< REMOVE

    return steinerTree;
  }
  public Tree2D calculSteinerBudget(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
    //REMOVE >>>>>
    Tree2D leafX = new Tree2D(new Point(700,400),new ArrayList<Tree2D>());
    Tree2D leafY = new Tree2D(new Point(700,500),new ArrayList<Tree2D>());
    Tree2D leafZ = new Tree2D(new Point(800,450),new ArrayList<Tree2D>());
    ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
    subTrees.add(leafX);
    subTrees.add(leafY);
    subTrees.add(leafZ);
    Tree2D steinerTree = new Tree2D(new Point(750,450),subTrees);
    //<<<<< REMOVE

    return steinerTree;
  }
}
