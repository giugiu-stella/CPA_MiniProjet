package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class DefaultTeam {

	public final static double BUDGET = 1664;
	static Kruskal k = new Kruskal();
	private static int[][] paths;

	public static int[][] calculShortestPaths(ArrayList<Point> points, int edgeThreshold) {
		System.out.println(" Je récupère la matrice des chemins...");
		int[][] paths=new int[points.size()][points.size()];
		double [][] dist= new double[points.size()][points.size()];
		// initialisation des matrices paths et dist
		for (int i=0;i<paths.length;i++) for (int j=0;j<paths.length;j++) paths[i][j]=-1;
		for (int i=0;i<dist.length;i++) for (int j=0;j<dist.length;j++) dist[i][j]=Double.MAX_VALUE;
		matriceVoisins(dist,paths,points,edgeThreshold);
		for(int k=0;k<paths.length;k++) {
			for(int i=0;i<paths.length;i++) {
				for(int j=0;j<paths.length;j++) {
					//ajoute les chemins indirects dans paths et les distances indirectes dans dist
					// chemin indirect => exemple le point p1 est lié au point p2 et le point p2 est lié au point p3,
					// le chemin p1-p3 est indirect, on passe par les chemins p1-p2 et p2-p3 
					if (dist[i][j]>(dist[i][k]+dist[k][j])) {
						paths[i][j]=paths[i][k];
						dist[i][j]=(dist[i][k]+dist[k][j]);
					}
				}
			}
		}
		    
		return paths;
	}

//ajoute les chemins directs dans paths et les distances directs dans dist
// chemin direct => exemple le point p1 est lié au point p2, le chemin p1-p2 est direct
  	public static void matriceVoisins(double[][] dist,int[][] paths,ArrayList<Point> points,int edgeThreshold){
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
		System.out.println("je suis dans CalculSteiner");
		Kruskal k = new Kruskal();
		// graphe complet qui couvre tous les points de hitpoints
		ArrayList<Edge> steinerEdge = k.kruskal(hitPoints);
		 Tree2D steinerTree=k.edgesToTree(steinerEdge,hitPoints.get(0) );
		//calcul du plus court chemin entre les couples (u,v)
		int[][] paths = calculShortestPaths(points, edgeThreshold);
		//remplace chaque arête (u-v) de poids (p) par d'autres arêtes de poids minimum 
		Tree2D newSteinerTree = algo(paths, steinerTree, points);

		return newSteinerTree;
	}
	
	private Tree2D algo(int[][] paths, Tree2D steinerTree, ArrayList<Point> points) {
		Point racine = steinerTree.getRoot();
		ArrayList<Tree2D> newChildreen = new ArrayList<>();
		for (Tree2D subTree : steinerTree.getSubTrees()) {
			Point currentChild = subTree.getRoot();
			
			//le sucesseur directe de la racine  dans le plus court chemain entre i et j
			int k = paths[points.indexOf(racine)][points.indexOf(currentChild)];

			//Cas1: le sucessseur directe a la racine est le meme que curentchild (aucune modification)  etant donne que le plus cours chemain entre (racine-curentchild) est (racine-curentchild)
			if (points.get(k).equals(currentChild)) {
				Tree2D newSubTree = algo(paths, subTree, points);
				newChildreen.add(newSubTree);
			} else {
				//cas2: existance d'un point k (racine--- k--- ....curentCHILD) qui fait que il existe un autre chemain  entre racine et curentchild plus court 
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
	
	public Tree2D calculSteinerBudget(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		System.out.println("je suis dans calcul Steiner Budget...");
		//recupération de la matrice des chemins 
		paths = calculShortestPaths(points, edgeThreshold);
		//récupération de l'arbre de poids minimum avec les points 
		ArrayList<Edge> edges = arbre_poids_min(points, hitPoints);
		ArrayList<Point> visitedPoints = new ArrayList<>();
		// arbre avec les arêtes qui constituront notre résultat final
		ArrayList<Edge> res = new ArrayList<>(edges);
		// ajoute le maisonMere
		visitedPoints.add(hitPoints.get(0));
		double sommeAretes= 0.0;
		Map<Point, PriorityQueue<Edge>> tasMinEdges = new HashMap<>();
		for (Edge edge: edges) {
			//construction d'un tas minimum avec les arêtes
			if (!tasMinEdges.containsKey(edge.getP())){
				tasMinEdges.put(edge.getP(), new PriorityQueue<>());
			}
			if (!tasMinEdges.containsKey(edge.getQ())){
				tasMinEdges.put(edge.getQ(), new PriorityQueue<>());
			}
			tasMinEdges.get(edge.getP()).add(edge);
			tasMinEdges.get(edge.getQ()).add(edge);
		}
		Edge minEdge;
		System.out.println("budget : "+sommeAretes);
		while (sommeAretes <= BUDGET){
			//récupération de l'arête avec la distance minimale de l'arbre
			minEdge = AreteMin(tasMinEdges, visitedPoints);
			// si l'arête minimale fait dépasser le budget, on ne l'ajoute pas 
			if (sommeAretes+ minEdge.distance() > BUDGET){
				break;
			}
			Point P = minEdge.getP();
			Point Q = minEdge.getQ();
			//on supprime l'arête minimale
			tasMinEdges.get(P).remove(minEdge); 
			tasMinEdges.get(Q).remove(minEdge);
			if (!visitedPoints.contains(P)){
				visitedPoints.add(P);
			}
			if (!visitedPoints.contains(Q)){
				visitedPoints.add(Q);
			}
			sommeAretes += minEdge.distance();
			System.out.println("budget : "+sommeAretes);
			res.add(minEdge);
		}
		res = arbre_poids_min(points, visitedPoints);
		return k.edgesToTree(res , hitPoints.get(0));
	}
	
	//fonction qui cherche l'arête avec la distance minimum de l'arbre
	// On suppose que points forme un arbre
	private static Edge AreteMin(Map<Point, PriorityQueue<Edge>> pointEdgesHeap, ArrayList<Point> points){
		System.out.println(" Je cherche l'arête minimal...");
		PriorityQueue<Edge> candidates = new PriorityQueue<>();
		for (Point point: points) {
			if (!pointEdgesHeap.get(point).isEmpty()){
				candidates.add(pointEdgesHeap.get(point).peek());
			}
		}
		return candidates.peek();
	}
	
	private static ArrayList<Edge> arbre_poids_min(ArrayList<Point> points, ArrayList<Point> hitPoints){
		System.out.println(" Je crée l'arbre de poids minimal...");
        ArrayList<Edge> ListEdges = k.kruskal(hitPoints);
        ArrayList<Point> ListPoints = new ArrayList<>();

        for (Edge e: ListEdges){
            int i = points.indexOf(e.getP());
            int j = points.indexOf(e.getQ());
            ArrayList<Integer> pointsIJ = PointChemin(i, j, paths);
            for (Integer k: pointsIJ){
                ListPoints.add(points.get(k));
            }
        }
        return k.kruskal(ListPoints);
  	}

	//fonction qui créer un chemin entre i et j avec tous les chemins déjà connus
  	public static ArrayList<Integer> PointChemin(int i, int j, int[][] paths){
		System.out.println("Je crée le chemin entre i et j...");
		ArrayList<Integer> chemin = new ArrayList<>();
		chemin.add(i);
		while(paths[i][j] != j){
			chemin.add(paths[i][j]);
			i = paths[i][j];
		}
		chemin.add(j);
		return chemin;
	}

	// calcul Steiner avec du budget version récursive 
	// première version de calcul steiner codée
	// cette version n'est pas correcte, en effet on n'obtient pas le résultat attendu, le graphe passe le test : OK ! mais obtient un buget supérieur à 1664
	public Tree2D calculSteinerBudget_recur(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		Kruskal k = new Kruskal();
		// graphe complet qui couvre tous le points de hitpoints
		ArrayList<Edge> steinerEdge = k.kruskal(hitPoints);
		 Tree2D steinerTree=k.edgesToTree(steinerEdge,hitPoints.get(0) );
		// replace arretes par le chemin le plus court entre deux points
		int[][] paths = calculShortestPaths(points, edgeThreshold);
		
		Tree2D newSteinerTree = algobuget(paths, steinerTree, points);

		return newSteinerTree ;
	}
	
	
	// fonction algobudget utilsée pour calculSteinerBudget_recur
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
				{   algobuget (paths, subTree, points);
					budgetAtteint= true;
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
				ArrayList<Tree2D> temp_final = new ArrayList<Tree2D>();				
				Tree2D realsub=subTree;
				if (temp_1.isEmpty())
				{
					temp_final=temp_1;
					temp_1.add(new Tree2D(newchild, new ArrayList<Tree2D>()));
				}
				while (!next_point.equals(currentChild))
				{   //cherche la distance entre mere_maison et le reste  ne depasse pas bugdet 
					indice_next_chmain=paths[k][points.indexOf(currentChild)];
					k=indice_next_chmain;
					cpt=(int) (next_point.distance(points.get(indice_next_chmain))+cpt) ; 	
					next_point=points.get(indice_next_chmain);		
					System.out.println("je suis la "+next_point.toString());
					temp_1=temp_1.get(0).getSubTrees();
				    temp_1.add(new Tree2D(next_point, new ArrayList<Tree2D>()));	  /// k1 k2 .....kn 
				    
				}
				
				temp_1=temp_1.get(0).getSubTrees();
			    temp_1.add(new Tree2D(currentChild, new ArrayList<Tree2D>()));	 
				System.out.println("la valeur de count  "+count);

				if(budgetAtteint== false && count+cpt >= BUDGET)
				{ 
					Tree2D newSubTree =algobuget (paths, subTree, points);
					System.out.println("Buget atteint ");
					budgetAtteint= true;
					 break; 	 
				}else{
					//problème icic dans temp_1 
					Tree2D newSub =new Tree2D( temp_final.get(0).getRoot(), temp_final.get(0).getSubTrees());
					temp.add(subTree);
				//	temp.add( newSub);			
					newTree= new Tree2D(newchild, temp);
					count =cpt+count; 
					newChildreen.add(newSub);
					Tree2D newSubTree = algobuget(paths, newTree, points);
					newChildreen.add(newSubTree);
				}	
			}
		}
		budgetAtteint= true;
	}
return new Tree2D(maison_mere, newChildreen);
}    
}








