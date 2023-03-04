package algorithms;

import java.awt.Point;
import java.util.ArrayList;

import javax.security.auth.kerberos.KerberosCredMessage;

public class DefaultTeam {

	public final static double BUDGET = 1664;
	
	// algorithme Floyd-Warshall
	  public int[][] calculShortestPaths(ArrayList<Point> points, int edgeThreshold) {
		    int[][] paths=new int[points.size()][points.size()];
		    for (int i=0;i<paths.length;i++) for (int j=0;j<paths.length;j++) paths[i][j]=-1;
		    double [][] dist= new double[points.size()][points.size()];
		    for (int i=0;i<dist.length;i++) for (int j=0;j<dist.length;j++) dist[i][j]=Double.MAX_VALUE;
		    matriceVoisins(dist,paths,points,edgeThreshold);
		    for(int k=0;k<paths.length;k++) {
		    	for(int i=0;i<paths.length;i++) {
		    		for(int j=0;j<paths.length;j++) {
					// on vérifie pour stocker les chemins indirects dans paths et la distance dans dist
		    			if (dist[i][j]>(dist[i][k]+dist[k][j])) {
		    				paths[i][j]=paths[i][k];
		    				dist[i][j]=(dist[i][k]+dist[k][j]);
		    			}
		    		}
		    	}
		    }
		    
		    return paths;
		  }
	
// mise à jour des matrices dist et paths avec les chemins directs 		  
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
		Kruskal k = new Kruskal();
		// calcul du graphe complet qui couvre tous les points de hitpoints
		Tree2D steinerTree = k.kruskal(hitPoints);
		//calcul du plus court chemin entre les couples (u v)
		int[][] paths = calculShortestPaths(points, edgeThreshold);
		//remplace chaque arête (u-v) de poids (p) par d'autres arêtes de poids minimum 
		Tree2D newSteinerTree = algo(paths, steinerTree, points);

		return newSteinerTree;
	}

	
	
	
	public Tree2D calculSteinerBudget(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		Kruskal k = new Kruskal();
		// calcul du graphe complet qui couvre tous les points de hitpoints
		Tree2D steinerTree = k.kruskal(hitPoints);

		// remplace les arêtes par le chemin le plus court entre deux points
		int[][] paths = calculShortestPaths(points, edgeThreshold);
		
		Tree2D newSteinerTree = algobuget(paths, steinerTree, points);

		return newSteinerTree ;
	}
	
	public int  count =0 ;
	boolean budgetAtteint=false;
	
	private Tree2D algobuget(int[][] paths, Tree2D steinerTree, ArrayList<Point> points) {
		Point maison_mere = steinerTree.getRoot();
		ArrayList<Tree2D> newChildreen = new ArrayList<>();
		 while (!  budgetAtteint) {
		for (Tree2D subTree : steinerTree.getSubTrees())
		{
		    Point currentChild =  subTree.getRoot();
			int k = paths[points.indexOf(maison_mere)][points.indexOf(currentChild)];
			if (points.get(k).equals(currentChild)) {
				if (count+maison_mere.distance(currentChild) >=BUDGET) //ajoute pas le nouveau point 
				{    budgetAtteint= true;
					 break; 	 
				}else{  //ajoute le nouveau point 
					count +=maison_mere.distance(currentChild);
					Tree2D newSubTree =algobuget (paths, subTree, points);
					newChildreen.add(newSubTree);
				}
			  }
			else{
				Point newchild = points.get(k);
				int cpt = (int) maison_mere.distance(newchild);
				int indice_next_chmain=0;
				Point next_point =newchild;
				Tree2D newTree = null ;
				ArrayList<Tree2D> temp = new ArrayList<Tree2D>();			
				ArrayList<Tree2D> temp_1 = new ArrayList<Tree2D>();				
				Tree2D realsub=subTree;
				
				while (!next_point.equals(currentChild))
				{   //cherche la distance entre mere_maison et le reste  ne depasse pas bugdet 
					indice_next_chmain=paths[k][points.indexOf(currentChild)];
					k=indice_next_chmain;
					cpt=(int) (next_point.distance(points.get(indice_next_chmain))+cpt) ; 	
					next_point=points.get(indice_next_chmain);		
					System.out.println("je suis la "+next_point.toString());
				    temp_1.add(new Tree2D(next_point, new ArrayList<Tree2D>()));	  /// k1 k2 .....kn 
				}		
				System.out.println("la valeur de count  "+count);

				if(count+cpt >= BUDGET)
				{ 
					System.out.println("Buget atteint ");
					budgetAtteint= true;
					 break; 	 
				}else{
					temp_1.add( new Tree2D(currentChild,new ArrayList<Tree2D>()) );
					Tree2D temp_2;	
					int j = temp_1.size()-1;
		
					 
					 
					//problème icic dans temp_1 
					Tree2D newSub =new Tree2D( temp_1.get(0).getRoot(), temp_1);
					temp.add(subTree);
					temp.add( newSub);			
					newTree= new Tree2D(newchild, temp);
					count =cpt+count;   
					Tree2D newSubTree = algobuget(paths, newTree, points);
					newChildreen.add(newSubTree);
				}	
			}
		}
		budgetAtteint= true;
	}
return new Tree2D(maison_mere, newChildreen);
}
	
			
	
	private Tree2D algo(int[][] paths, Tree2D steinerTree, ArrayList<Point> points) {
		Point racine = steinerTree.getRoot();
		ArrayList<Tree2D> newChildreen = new ArrayList<>();
		for (Tree2D subTree : steinerTree.getSubTrees()) {
			Point currentChild = subTree.getRoot();
			
			//le sucesseur direct de la racine  dans le plus court chemain entre i et j
			int k = paths[points.indexOf(racine)][points.indexOf(currentChild)];

			//Cas1: le sucessseur direct à la racine est le meme que curentchild (aucune modification)  etant donne que le plus cours chemin entre (racine-curentchild) est (racine-curentchild)
			if (points.get(k).equals(currentChild)) {
				Tree2D newSubTree = algo(paths, subTree, points);
				newChildreen.add(newSubTree);
			} else {
				//cas2: existance d'un point k (racine--- k--- ....curentCHILD) qui fait qu'il existe un autre chemin plus court entre racine et curentchild
				//remplacer  :r(acine--- curentchild) par ( racine ----k---curentchild)
				Point newchild = points.get(k);
				ArrayList<Tree2D> temp = new ArrayList<Tree2D>();
				temp.add(subTree);
				Tree2D newTree = new Tree2D(newchild, temp);
				Tree2D newSubTree = algo(paths, newTree, points);
				newChildreen.add(newSubTree);
			}
		}
		 return  new Tree2D(racine, newChildreen);
	
	}
}








