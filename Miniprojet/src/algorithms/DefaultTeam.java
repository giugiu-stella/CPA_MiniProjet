package algorithms;

import java.awt.Point;
import java.util.ArrayList;

import javax.security.auth.kerberos.KerberosCredMessage;

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
		Utile utilitaire = new Utile();
		// graphe complet qui couvre tous le points de hitpoints
		Tree2D steinerTree = utilitaire.Kruskal(hitPoints);

		// replace arretes par le chemin le plus court entre deux points
		int[][] paths = calculShortestPaths(points, edgeThreshold);

		Tree2D newSteinerTree = algo(paths, steinerTree, points);

		return newSteinerTree;
	}

	private Tree2D algo(int[][] paths, Tree2D steinerTree, ArrayList<Point> points) {
		Point racine = steinerTree.getRoot();
		ArrayList<Tree2D> newChildreen = new ArrayList<>();
		Tree2D notreSuperArbre;
		for (Tree2D subTree : steinerTree.getSubTrees()) {
			Point currentChild = subTree.getRoot();
			
			//le sucesseur directe de la racine  dans le plus court chemain entre i et j
			int k = paths[points.indexOf(racine)][points.indexOf(currentChild)];

			//Cas1: le sucessseur directe a la racine est le meme que curentchild (aucune modification)  etant donne que le plus cours chemain entre (racine-curentchild) est (racine-curentchild)
			if (points.get(k).equals(currentChild)) {
				System.out.println("[-] pas interessant");
				ArrayList<Tree2D> temp = new ArrayList<Tree2D>();
				Tree2D newSubTree = algo(paths, subTree, points);
				newChildreen.add(newSubTree);
			} else {
				//cas2: existance d'un point k (racine--- k--- ....curentCHILD) qui fait que il existe un autre chemain  entre racine et curentchild plus court 
				//remplacer  :r(acine--- curentchild) par ( racine ----k---curentchild)
				System.out.println("[+] interessant !");
				Point newChild = points.get(k);
				ArrayList<Tree2D> temp = new ArrayList<Tree2D>();
				temp.add(subTree);
				Tree2D newTree = new Tree2D(newChild, temp);
				Tree2D newSubTree = algo(paths, newTree, points);
				newChildreen.add(newSubTree);
			}
		}
		notreSuperArbre = new Tree2D(racine, newChildreen);
		return notreSuperArbre;
	}
	
	
	public Tree2D calculSteinerBudget(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		
		return null ;
	}
}






class Utile {

	private boolean contains(ArrayList<Edge> edges, Point p, Point q) {
		for (Edge e : edges) {
			if (e.p.equals(p) && e.q.equals(q) || e.p.equals(q) && e.q.equals(p))
				return true;
		}
		return false;
	}

	public Tree2D Kruskal(ArrayList<Point> points) {
		// KRUSKAL ALGORITHM, NOT OPTIMAL FOR STEINER!
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (Point p : points) {
			for (Point q : points) {
				if (p.equals(q) || contains(edges, p, q))
					continue;
				edges.add(new Edge(p, q));
			}
		}
		edges = sort(edges);

		ArrayList<Edge> kruskal = new ArrayList<Edge>();
		Edge current;
		NameTag forest = new NameTag(points);
		while (edges.size() != 0) {
			current = edges.remove(0);
			if (forest.tag(current.p) != forest.tag(current.q)) {
				kruskal.add(current);
				forest.reTag(forest.tag(current.p), forest.tag(current.q));
			}
		}

		return edgesToTree(kruskal, kruskal.get(0).p);
	}

	private Tree2D edgesToTree(ArrayList<Edge> edges, Point root) {
		ArrayList<Edge> remainder = new ArrayList<Edge>();
		ArrayList<Point> subTreeRoots = new ArrayList<Point>();
		Edge current;
		while (edges.size() != 0) {
			current = edges.remove(0);
			if (current.p.equals(root)) {
				subTreeRoots.add(current.q);
			} else {
				if (current.q.equals(root)) {
					subTreeRoots.add(current.p);
				} else {
					remainder.add(current);
				}
			}
		}

		ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
		for (Point subTreeRoot : subTreeRoots)
			subTrees.add(edgesToTree((ArrayList<Edge>) remainder.clone(), subTreeRoot));

		return new Tree2D(root, subTrees);
	}

	private ArrayList<Edge> sort(ArrayList<Edge> edges) {
		if (edges.size() == 1)
			return edges;

		ArrayList<Edge> left = new ArrayList<Edge>();
		ArrayList<Edge> right = new ArrayList<Edge>();
		int n = edges.size();
		for (int i = 0; i < n / 2; i++) {
			left.add(edges.remove(0));
		}
		while (edges.size() != 0) {
			right.add(edges.remove(0));
		}
		left = sort(left);
		right = sort(right);

		ArrayList<Edge> result = new ArrayList<Edge>();
		while (left.size() != 0 || right.size() != 0) {
			if (left.size() == 0) {
				result.add(right.remove(0));
				continue;
			}
			if (right.size() == 0) {
				result.add(left.remove(0));
				continue;
			}
			if (left.get(0).distance() < right.get(0).distance())
				result.add(left.remove(0));
			else
				result.add(right.remove(0));
		}
		return result;
	}
}



