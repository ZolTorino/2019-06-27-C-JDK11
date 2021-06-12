package it.polito.tdp.crimes.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;



public class Model {
	
	EventsDao dao;
	private SimpleWeightedGraph<String, DefaultWeightedEdge> grafo;
	private HashSet<String> idSet;
	public Model() {
		dao= new EventsDao();
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idSet=new HashSet<>();
	}
	
	public List<String> categorie(){
		List<String> out= new LinkedList<String>(dao.offences());
		return out;
	}
	public List<LocalDate> date(){
		List<LocalDate> out= new LinkedList<LocalDate>(dao.date());
		return out;
	}
	LinkedList<Arco> archi=new LinkedList<>();
	LinkedList<Arco> speciali=new LinkedList<>();
	public String creaGrafo(String category, LocalDate date) {
		String s="";
		idSet.clear();
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		dao.vertici(category,date, idSet);
		Graphs.addAllVertices(grafo, idSet);
		s+="Vertici: "+grafo.vertexSet().size()+"\n";
		
		archi=new LinkedList<>();
		dao.archi(category, date, archi);
		int min=-1;
		int max=-1;
		for(Arco a:archi)
		{
			if(a.getPeso()<min||a.getPeso()==-1)
				min =a.getPeso();
			if(a.getPeso()>max||a.getPeso()==-1)
				max =a.getPeso();
					
			if(grafo.containsVertex(a.getO1()) && grafo.containsVertex(a.getO2())) {
					Graphs.addEdge(grafo, a.getO1(), a.getO2(), a.getPeso());
				
			}
		}
		s+="Archi: "+grafo.edgeSet().size()+"\n";
		double mediano=(max+min)/2;
		for(Arco a:archi)
		{
		
			if(grafo.containsVertex(a.getO1()) && grafo.containsVertex(a.getO2())) {
				if(a.getPeso()<=mediano)
				{
					s+=a.o1+ " - "+a.o2+ " - "+a.peso+"\n";
					speciali.add(a);
				}
				
			}
		}
		return s;
	}
	public List<Arco> speciali(){
		return speciali;
	}
	int bestpeso=0;
	LinkedList<String> migliore= new LinkedList<>();
	public String percorso(Arco a)
	{
		String oi=a.o1;
		String of=a.o2;
		HashSet<Arco> parziale= new HashSet<>();
		LinkedHashSet<String>result=new LinkedHashSet<>();
		bestpeso=0;
		migliore= new LinkedList<>();
		result.add(a.o1);
		calcola(parziale,result,oi, of);
		String s="";
		for(String m: migliore)
		{
			s+=m+"\n";
		}
		return s+""+migliore.size();
		
	}
	
	public void calcola(HashSet<Arco> parziale,LinkedHashSet<String> result, String ultimo, String of) {
		
		if(ultimo.equals(of))
		{
			//System.out.println(of);
			if(pesotot(parziale)>bestpeso)
			{
				migliore=new LinkedList<>(result);
				bestpeso=pesotot(parziale);
				System.out.println("Best");
				return;
			}
		}
		for(String s: Graphs.neighborListOf(grafo, ultimo))
		{
			if(!result.contains(s))
				{
					Arco a= new Arco(ultimo, s,(int)grafo.getEdgeWeight(grafo.getEdge(ultimo, s)));
					parziale.add(a);
					result.add(s);
					calcola(parziale, result, s, of);
					parziale.remove(a);
					result.remove(s);
				}
		}
		
		
		
	}
	public int pesotot(HashSet<Arco> archi)
	{
		int sum=0;
		for(Arco a: archi)
		{
			sum+=a.peso;
		}
		//System.out.println(sum);
		return sum;
		
	}
}
