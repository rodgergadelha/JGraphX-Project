package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.layout.mxParallelEdgeLayout;

import java.lang.Math;

import model.Algoritmos;


/**
 * Classe que implementa as funcionalidades dos botões do programa.
 */
public class funcoesBotoes {


	/**
	 * Mostra uma caixa com uma mensagem específica.
	 * 
	 * @param mensagem
	 */
	public static void log(String mensagem) {
		JOptionPane.showMessageDialog(null, mensagem);
	}


	/**
	 * Método para adiconar vértice na interface do usurário dentro do espaço do
	 * container.
	 * 
	 * @param graph
	 * @param x
	 * @param y
	 */
	public static void adicionarVertice(mxGraph graph, int x, int y) {
		graph.getModel().beginUpdate();
		Object parent = graph.getDefaultParent();
		graph.insertVertex(parent, null, "", x, y, 38, 38);
		graph.getModel().endUpdate();
	}


	/**
	 * Deleta vértice(s) e/ou aretas(s) selecionados pelo usuário.
	 * 
	 * @param graph
	 */
	public static void deletarCelula(mxGraph graph) {
		graph.getModel().beginUpdate();
		graph.removeCells();
		graph.getModel().endUpdate();
	}

	
	/**
	 * Deleta todas as arestas e vértices do grafo.
	 * 
	 * @param graph
	 */
	public static void deletarTodasCelulas(mxGraph graph) {
		graph.getModel().beginUpdate();
		graph.removeCells(graph.getChildCells(graph.getDefaultParent()), true);
		graph.getModel().endUpdate();
	}


	/**
	 * Atualiza o peso de uma determinda aresta.
	 * Verifica se o objeto é diferente de null e se é vértice. Nesse método é feito
	 * uma verificação de entrada do usuário, caso o peso inserido seja inválido.
	 * 
	 * @param graphComponent
	 */
	public static void mudarPeso(mxGraphComponent graphComponent) {
		Object cell = graphComponent.getGraph().getSelectionCell();

		if (cell != null && ((mxCell) cell).isEdge()) {
			String pesoStr = JOptionPane.showInputDialog("Digite o peso dessa aresta:");
			if (pesoStr == null)
				return;

			boolean valido = false;

			try {
				double peso = Double.parseDouble(pesoStr);
				if (peso != 1)
					valido = true;
			} catch (NumberFormatException n) {
				if (!pesoStr.equals(""))
					log("Peso inválido.");
			}

			if (valido)
				((mxCell) cell).setValue(pesoStr);
		}

		graphComponent.refresh();
	}



	/**
	 * Método que gera um grafo aleatório. Adiciona os vértices com coordenadas
	 * aleatórias e determina uma probabilidade p de uma aresta ser adicionada entre dois vértices quaisquer do grafo.
	 * 
	 * @param graphComponent
	 * @param layout
	 */
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
		for (int i = 0; i < numeroVertices; i++) {
			int x, y, xi, yi;
			Object cell;
			List<Integer> coordenadas;
			boolean continuarLoop;

			do {
				x = gerador.nextInt(506);
				y = gerador.nextInt(475);
				continuarLoop = false;
				for (int j = 0; j < listaCoordenadas.size(); j++) {
					xi = listaCoordenadas.get(j).get(0);
					yi = listaCoordenadas.get(j).get(1);
					if (Math.sqrt((x - xi)*(x - xi) + (y - yi)*(y - yi)) <= 40) {
						continuarLoop = true;
					}
				}
				if (continuarLoop) {
					cell = new Object();
					continue;
				}
				cell = graphComponent.getCellAt(x, y);
				coordenadas = new ArrayList<Integer>();
				coordenadas.add(x);
				coordenadas.add(y);
				listaCoordenadas.add(coordenadas);
			} while (cell != null);

			adicionarVertice(graphComponent.getGraph(), x, y);
		}

		Object[] vertexes = graphComponent.getGraph().getChildVertices(graphComponent.getGraph().getDefaultParent());
		pesoAresta = 1;
		for (Object vertex1 : vertexes) {
			for (Object vertex2 : vertexes) {
				q = Math.random();
				Object parent = graphComponent.getGraph().getDefaultParent();
				if (ponderado == 1) {
					pesoAresta = gerador.nextInt(100) + 1;
					if (q <= p)
						graphComponent.getGraph().insertEdge(parent, null, "" + pesoAresta, vertex1, vertex2);
					layout.execute(graphComponent.getGraph().getDefaultParent());
				} else {
					if (q <= p)
						graphComponent.getGraph().insertEdge(parent, null, "", vertex1, vertex2);
					layout.execute(graphComponent.getGraph().getDefaultParent());
				}
			}
		}

	}


	/**
	 * Método que retorna uma lista de vértices que será usada posteriormente
	 * na animação no algoritmo de busca em profundidade. Cada posição representa um
	 * momento da animação.
	 * 
	 * @param graph
	 * @param direcionado Indica se é direcionado ou não.
	 * @return List<Object>: Lista de vértices que serão pintados na ordem de execução do algoritmo.
	 */
	public static List<Object> getVisitedVertexesDfs(mxGraph graph, boolean direcionado) {
		if (graph.getSelectionCells().length != 1)
			return null;

		for (Object c : graph.getSelectionCells()) {
			if (((mxCell) c).isEdge())
				return null;
		}

		mxAnalysisGraph aGraph = new mxAnalysisGraph();
		aGraph.setGraph(graph);

		mxCell cell = (mxCell) graph.getSelectionCell();
		List<Object> vertexesStates = new ArrayList<>();

		if (cell != null && cell.isVertex()) {
			Algoritmos.dfs(direcionado, aGraph, cell, vertexesStates);
		}

		return vertexesStates;

	}



	/**
	 * Método que retornar uma lista de vértices que será usada posteriormente
	 * na animação no algoritmo de busca em largura. Cada posição representa um
	 * momento da animação.
	 * 
	 * @param graph
	 * @param direcionado Indica se é direcionado ou não.
	 * @return List<Object>: Lista de vértices que serão pintados na ordem de execução do algoritmo.
	 */
	public static List<Object> getVisitedVertexesBfs(mxGraph graph, boolean direcionado) {
		for (Object edge : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
			if (!((mxCell) edge).getValue().equals("")) {
				log("O bfs não funciona em grafos ponderados.");
				return null;
			}
		}

		if (graph.getSelectionCells().length != 1)
			return null;

		for (Object c : graph.getSelectionCells()) {
			if (((mxCell) c).isEdge())
				return null;
		}

		mxAnalysisGraph aGraph = new mxAnalysisGraph();
		aGraph.setGraph(graph);

		mxCell cell = (mxCell) graph.getSelectionCell();
		List<Object> vertexesStates = new ArrayList<>();

		if (cell != null && cell.isVertex()) {
			Algoritmos.bfs(direcionado, aGraph, cell, vertexesStates);
		}

		return vertexesStates;

	}



	/**
	 * Método que retornar uma lista de vértices e arestas que será usada posteriormente
	 * na animação do algoritmo de Djikstra. Cada posição representa um momento da
	 * animação.
	 * 
	 * @param graph
	 * @param direcionado Indica se é direcionado ou não.
	 * @return List<Object>: Lista de vértices e arestas que serão pintados na ordem de execução do algoritmo.
	 */
	public static List<Object> getVisitedVertexesDjikstra(mxGraph graph, boolean direcionado) {
		Object cell = graph.getSelectionCell();

		if (cell != null && ((mxCell) cell).isEdge())
			return null;

		for (Object edge : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
			if (((mxCell) edge).getValue().toString().equals(""))
				continue;

			double edgeValue = Double.parseDouble(((mxCell) edge).getValue().toString());

			if (edgeValue < 0) {
				log("O algoritmo de Dijkstra não funciona em grafos com pesos negativos.");
				return null;
			}
		}

		mxAnalysisGraph aGraph = new mxAnalysisGraph();
		aGraph.setGraph(graph);

		mxCell startVertex = (mxCell) cell;
		List<Object> cellsToPaint = new ArrayList<>();

		if (startVertex != null && startVertex.isVertex()) {
			Algoritmos.dijkstra(direcionado, aGraph, startVertex, cellsToPaint);
		}

		return cellsToPaint;

	}



	/**
	 * Método que salva o grafo em um arquivo xml.
	 * 
	 * @param graph
	 * @param path
	 */
	public static void salvarGrafo(mxGraph graph, String path) {
		mxCodec codec = new mxCodec();

		try {
			String xml = mxUtils.getPrettyXml(codec.encode(graph.getModel()));
			mxUtils.writeFile(xml, path);

			log("Grafo salvo com sucesso.");

		} catch (Exception ex) {
			log("Ocorreu um erro ao tentar salvar o grafo.");
		}
	}



	/**
	 * Método que faz o carregamento do grafo salvo em um arquivo xml.
	 * 
	 * @param graph
	 * @param path
	 */
	public static void carregarGrafo(mxGraph graph, String path) {
		try {
			Document document = mxXmlUtils.parseXml(mxUtils.readFile(path));
			mxCodec codec = new mxCodec(document);
			codec.decode(document.getDocumentElement(), graph.getModel());
		} catch (Exception e) {
			log("Ocorreu um erro ao tentar carregar o grafo.");
		}

		graph.getModel().beginUpdate();
		Object[] cells;
		try {
			cells = graph.getChildCells(graph.getDefaultParent(), true, true);

		} finally {
			graph.getModel().endUpdate();
		}

		graph.addCells(cells);
	}

}
