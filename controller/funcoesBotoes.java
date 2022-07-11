package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;

import com.mxgraph.analysis.StructuralException;
import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.layout.mxParallelEdgeLayout;

import java.lang.Math;

import model.Algoritmos;

public class funcoesBotoes {

	public static void log(String mensagem) {
		JOptionPane.showMessageDialog(null, mensagem);
   }
	
	public static void adicionarVertice(mxGraph graph, int x, int y) {
        graph.getModel().beginUpdate();
        Object parent = graph.getDefaultParent();
        graph.insertVertex(parent, null, "", x, y, 40, 40);
        graph.getModel().endUpdate();
    }
	    

	public static void deletarCelula(mxGraph graph) {
        graph.getModel().beginUpdate();
        graph.removeCells();
        graph.getModel().endUpdate();
    }

	public static void deletarTodasCelulas(mxGraph graph) {
        graph.getModel().beginUpdate();
        graph.removeCells(graph.getChildCells(graph.getDefaultParent()), true);
        graph.getModel().endUpdate();
    }

	public static void mudarPeso(mxGraphComponent graphComponent) {
		Object cell = graphComponent.getGraph().getSelectionCell();
            	
		if(cell != null && ((mxCell)cell).isEdge()) {
			String pesoStr =  JOptionPane.showInputDialog("Digite o peso dessa aresta:");

			if(pesoStr == null) return;

			boolean valido = false;
			
			try {
				Double.parseDouble(pesoStr);
				valido = true;
			}catch(NumberFormatException n) {
				if(!pesoStr.equals("")) log("Peso inválido.");
			}
			
			
			if(valido) ((mxCell)cell).setValue(pesoStr);
		}
		
		graphComponent.refresh();
	}


	public static void gerarGrafoAleatorio(mxGraphComponent graphComponent, mxParallelEdgeLayout layout) {
	    Random gerador = new Random();
	    int ponderado = gerador.nextInt(2);
	    double q;
	    double p = 0.05;
	    int numeroVertices, pesoAresta;
	    numeroVertices = gerador.nextInt(21) + 5;
	    
	    deletarTodasCelulas(graphComponent.getGraph());
	    List<List<Integer>> listaCoordenadas = new ArrayList<List<Integer>>();
	    // Adicionando vértices com coordenadas aleatórias.
	    for(int i = 0 ; i < numeroVertices ; i++){
			int x, y, xi, yi;
			Object cell;
			List<Integer> coordenadas;
			boolean continuarLoop;
			
			do{
				x = gerador.nextInt(406);
	    		y = gerador.nextInt(406);
				continuarLoop = false;
				for(int j = 0 ; j < listaCoordenadas.size() ; j++){
					xi = listaCoordenadas.get(j).get(0);
					yi = listaCoordenadas.get(j).get(1);
					if ( Math.sqrt(Math.pow(x-xi,2) + Math.pow(y-yi,2)) <= 40){
						continuarLoop = true;
					}
				}
				if (continuarLoop){
					cell = new Object();
					continue;
				}
				cell = graphComponent.getCellAt(x, y);
				coordenadas = new ArrayList<Integer>();
				coordenadas.add(x);
				coordenadas.add(y);
				listaCoordenadas.add(coordenadas);
			}while(cell != null);

	    	adicionarVertice(graphComponent.getGraph(), x, y);
	    }

		Object[] vertexes = graphComponent.getGraph().getChildVertices(graphComponent.getGraph().getDefaultParent());
		pesoAresta = 1;
		for(Object vertex1 : vertexes){
			for(Object vertex2 : vertexes){
				q = Math.random();
				Object parent = graphComponent.getGraph().getDefaultParent();
				if (ponderado == 1){
					pesoAresta = gerador.nextInt(1,101);
					if(q <= p) graphComponent.getGraph().insertEdge(parent, null, "" + pesoAresta, vertex1, vertex2);
					layout.execute(graphComponent.getGraph().getDefaultParent());
				}
				else {
					if(q <= p) graphComponent.getGraph().insertEdge(parent, null, "", vertex1, vertex2);
					layout.execute(graphComponent.getGraph().getDefaultParent());
				}
			}
		}
	    
	    
	}

	
	
	public static List<Object> getVisitedVertexesDfs(mxGraph graph, boolean direcionado) {
		for(Object edge : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
			if(!((mxCell)edge).getValue().equals("")) {
				log("O dfs não funciona em grafos ponderados.");
				return null;
			}
		}

		if(graph.getSelectionCells().length != 1) return null;
		
		for(Object c : graph.getSelectionCells()) {
            if(((mxCell)c).isEdge()) return null;
        }
        
        mxAnalysisGraph aGraph = new mxAnalysisGraph();
        aGraph.setGraph(graph);

        mxCell cell = (mxCell)graph.getSelectionCell();
		List<Object> vertexesStates = new ArrayList<>();

        
        if(cell != null && cell.isVertex()) {
            Algoritmos.dfs(direcionado, aGraph, cell, vertexesStates);
        }

		return vertexesStates;
        
	}


	public static List<Object> getVisitedVertexesBfs(mxGraph graph, boolean direcionado) {
		for(Object edge : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
			if(!((mxCell)edge).getValue().equals("")) {
				log("O bfs não funciona em grafos ponderados.");
				return null;
			}
		}

		if(graph.getSelectionCells().length != 1) return null;
		
		for(Object c : graph.getSelectionCells()) {
            if(((mxCell)c).isEdge()) return null;
        }
        
        mxAnalysisGraph aGraph = new mxAnalysisGraph();
        aGraph.setGraph(graph);

        mxCell cell = (mxCell)graph.getSelectionCell();
		List<Object> vertexesStates = new ArrayList<>();

        if(cell != null && cell.isVertex()) {
            Algoritmos.bfs(direcionado, aGraph, cell, vertexesStates);
        }

		return vertexesStates;

	}

	
	
	public static List<Object> getVisitedVertexesDjikstra(mxGraph graph, boolean direcionado) {
		Object cell = graph.getSelectionCell();
            
		if(cell != null && ((mxCell)cell).isEdge()) return null;
		
		for(Object edge : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
			if(((mxCell)edge).getValue().toString().equals("")) continue;
			
			double edgeValue = Double.parseDouble(((mxCell)edge).getValue().toString());
			
			if(edgeValue < 0) {
				log("O algoritmo de Dijkstra não funciona em grafos com pesos negativos.");
				return null;
			}
		}

		mxAnalysisGraph aGraph = new mxAnalysisGraph();
	    aGraph.setGraph(graph);

		mxCell startVertex = (mxCell)cell;
        List<Object> cellsToPaint = new ArrayList<>();

        if(startVertex != null && startVertex.isVertex()) {
			try {
				Algoritmos.dijkstra(direcionado, aGraph, startVertex, cellsToPaint);
			}catch(StructuralException s) {
				log("O algoritmo de Dijkstra não funciona em grafos desconexos.");
				return null;
			}
            
        }

		return cellsToPaint;

	}
	
	public static void salvarGrafo(mxGraph graph, String path) {
        mxCodec codec = new mxCodec();

        try {
            String xml = mxUtils.getPrettyXml(codec.encode(graph.getModel()));
            mxUtils.writeFile(xml, path);

            log("The Object  was succesfully written to a file");
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	public static void carregarGrafo(mxGraph graph, String path) {
        try {
            Document document = mxXmlUtils.parseXml(mxUtils.readFile(path));
            mxCodec codec = new mxCodec(document);
            codec.decode(document.getDocumentElement(), graph.getModel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        graph.getModel().beginUpdate();
        Object[] cells;
        try
        {
            cells = graph.getChildCells(graph.getDefaultParent(), true, true);

        }
        finally
        {
            graph.getModel().endUpdate();
        }

        graph.addCells(cells);
    }
	
}
