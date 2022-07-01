package controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import com.mxgraph.view.mxGraph;
import com.mxgraph.analysis.StructuralException;
import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxGraphStructure;
import com.mxgraph.analysis.mxTraversal;
import com.mxgraph.view.mxGraph.mxICellVisitor;
import com.mxgraph.model.mxCell;

public class funcoesBotoes extends mxTraversal {
	
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
	
	
	
	public static List<Object> getVisitedVertexesDfs(mxGraph graph, boolean direcionado) {
		if(graph.getSelectionCells().length != 1) return null;
		
		for(Object c : graph.getSelectionCells()) {
            if(((mxCell)c).isEdge()) return null;
        }
        
        mxAnalysisGraph aGraph = new mxAnalysisGraph();
        aGraph.setGraph(graph);
        List<Object> cellsToPaint = new ArrayList<>();
        mxCell cell = (mxCell)graph.getSelectionCell();
        
        if(cell != null && cell.isVertex()) {
            dfs(direcionado, aGraph, cell, new mxICellVisitor(){
                public boolean visit(Object vertex, Object edge)
                {
                    cellsToPaint.add(0, vertex);
                    
                    return false;
                }
            });
        }
        
        return cellsToPaint;

	}


	public static List<Object> getVisitedVertexesBfs(mxGraph graph, boolean direcionado) {
		if(graph.getSelectionCells().length != 1) return null;
		
		for(Object c : graph.getSelectionCells()) {
            if(((mxCell)c).isEdge()) return null;
        }
        
        mxAnalysisGraph aGraph = new mxAnalysisGraph();
        aGraph.setGraph(graph);
        List<Object> cellsToPaint = new ArrayList<>();
        mxCell cell = (mxCell)graph.getSelectionCell();
        
        if(cell != null && cell.isVertex()) {
            bfs(direcionado, aGraph, cell, new mxICellVisitor(){
                public boolean visit(Object vertex, Object edge)
                {
                    cellsToPaint.add(vertex);
                    
                    return false;
                }
            });
        }
        
        return cellsToPaint;

	}

	
	
	public static List<Object> getVisitedVertexesDjikstra(mxGraph graph, boolean direcionado) throws StructuralException{
        
		mxAnalysisGraph aGraph = new mxAnalysisGraph();
	    aGraph.setGraph(graph);
        ArrayList<Object> cellsToPaint = new ArrayList<>();
        Object[] cells = graph.getSelectionCells();
        if(cells.length != 2) return null;
        mxCell startVertex = (mxCell)cells[0];
        mxCell endVertex = (mxCell)cells[1];
        
        if(startVertex != null && endVertex != null && startVertex.isVertex() && endVertex.isVertex()) {

            dijkstra(direcionado, aGraph, startVertex, endVertex, new mxICellVisitor(){
                public boolean visit(Object vertex, Object edge)
                {
                    cellsToPaint.add(vertex);
                    return false;
                }
            });
            
        }
        
        return cellsToPaint;
	}


	// Sobrescrita do método dfs
	public static void dfs(boolean direcionado, final mxAnalysisGraph aGraph, final Object startVertex, final mxGraph.mxICellVisitor visitor) {
        dfsRec(direcionado, aGraph, startVertex, null, new HashSet<Object>(), visitor);
    }


	// Sobrescrita do método dfsRec
	private static void dfsRec(boolean direcionado, final mxAnalysisGraph aGraph, final Object cell, final Object edge, final Set<Object> seen, final mxGraph.mxICellVisitor visitor) {
        if (cell != null && !seen.contains(cell)) {
            visitor.visit(cell, edge);
            seen.add(cell);
            final Object[] edges = aGraph.getEdges(cell, (Object)null, false, true);
            final Object[] opposites = aGraph.getOpposites(edges, cell, !direcionado, true);
            for (int i = 0; i < opposites.length; ++i) {
                dfsRec(direcionado, aGraph, opposites[i], edges[i], seen, visitor);
            }
        }
    }


	//Sobrescrita do método bfs
	public static void bfs(boolean direcionado, final mxAnalysisGraph aGraph, final Object startVertex, final mxGraph.mxICellVisitor visitor) {
        if (aGraph != null && startVertex != null && visitor != null) {
            final Set<Object> queued = new HashSet<Object>();
            final LinkedList<Object[]> queue = new LinkedList<Object[]>();
            final Object[] q = { startVertex, null };
            queue.addLast(q);
            queued.add(startVertex);
            bfsRec(direcionado, aGraph, queued, queue, visitor);
        }
    }
    

	// Sobrescrita do método bfsRec
    private static void bfsRec(boolean direcionado, final mxAnalysisGraph aGraph, final Set<Object> queued, final LinkedList<Object[]> queue, final mxGraph.mxICellVisitor visitor) {
        if (queue.size() > 0) {
            final Object[] q = queue.removeFirst();
            final Object cell = q[0];
            final Object incomingEdge = q[1];
            visitor.visit(cell, incomingEdge);
            final Object[] edges = aGraph.getEdges(cell, (Object)null, !direcionado, true, true, false);
            for (int i = 0; i < edges.length; ++i) {
                final Object[] currEdge = { edges[i] };
				if(aGraph.getOpposites(currEdge, cell).length == 0) continue;
                final Object opposite = aGraph.getOpposites(currEdge, cell)[0];
                if (!queued.contains(opposite)) {
                    final Object[] current = { opposite, edges[i] };
                    queue.addLast(current);
                    queued.add(opposite);
                }
            }
            bfsRec(direcionado, aGraph, queued, queue, visitor);
        }
    }
	
	
	// Sobrescrita do método dijkstra
	public static void dijkstra(boolean direcionado, mxAnalysisGraph aGraph, Object startVertex, Object endVertex, mxICellVisitor visitor)
			throws StructuralException
	{
		if (!mxGraphStructure.isConnected(aGraph))
		{
			throw new StructuralException("The current Dijkstra algorithm only works for connected graphs and this graph isn't connected");
		}

		Object parent = aGraph.getGraph().getDefaultParent();
		Object[] vertexes = aGraph.getChildVertices(parent);
		int vertexCount = vertexes.length;
		double[] distances = new double[vertexCount];
		//		parents[][0] is the traveled vertex
		//		parents[][1] is the traveled outgoing edge
		Object[][] parents = new Object[vertexCount][2];
		ArrayList<Object> vertexList = new ArrayList<Object>();
		ArrayList<Object> vertexListStatic = new ArrayList<Object>();

		for (int i = 0; i < vertexCount; i++)
		{
			distances[i] = Integer.MAX_VALUE;
			vertexList.add((Object) vertexes[i]);
			vertexListStatic.add((Object) vertexes[i]);
		}

		distances[vertexListStatic.indexOf(startVertex)] = 0;

		while (vertexList.size() > 0)
		{
			//find closest vertex
			double minDistance;
			Object currVertex;
			Object closestVertex;
			currVertex = vertexList.get(0);
			int currIndex = vertexListStatic.indexOf(currVertex);
			double currDistance = distances[currIndex];
			minDistance = currDistance;
			closestVertex = currVertex;

			if (vertexList.size() > 1)
			{
				for (int i = 1; i < vertexList.size(); i++)
				{
					currVertex = vertexList.get(i);
					currIndex = vertexListStatic.indexOf(currVertex);
					currDistance = distances[currIndex];

					if (currDistance < minDistance)
					{
						minDistance = currDistance;
						closestVertex = currVertex;
					}
				}
			}

			// we found the closest vertex
			vertexList.remove(closestVertex);

			Object currEdge = new Object();
			Object[] neighborVertices = aGraph.getOpposites(aGraph.getEdges(closestVertex, null, true, true, true, true), closestVertex,
					!direcionado, true);

			for (int j = 0; j < neighborVertices.length; j++)
			{
				Object currNeighbor = neighborVertices[j];

				if (vertexList.contains(currNeighbor))
				{
					//find edge that connects to the current vertex
					Object[] neighborEdges = aGraph.getEdges(currNeighbor, null, true, true, false, true);
					Object connectingEdge = null;

					for (int k = 0; k < neighborEdges.length; k++)
					{
						currEdge = neighborEdges[k];

						if (aGraph.getTerminal(currEdge, true).equals(closestVertex)
								|| aGraph.getTerminal(currEdge, false).equals(closestVertex))
						{
							connectingEdge = currEdge;
						}
					}

					// check for new distance
					int neighborIndex = vertexListStatic.indexOf(currNeighbor);
					double oldDistance = distances[neighborIndex];
					double currEdgeWeight;

					// setting value of edge as the weight
					currEdgeWeight = 1.0;
					String edgeValue = ((mxCell)connectingEdge).getValue().toString();
					if(!edgeValue.equals("")) currEdgeWeight = Double.parseDouble(edgeValue);
					
					double newDistance = minDistance + currEdgeWeight;

					//final part - updating the structure
					if (newDistance < oldDistance)
					{
						distances[neighborIndex] = newDistance;
						parents[neighborIndex][0] = closestVertex;
						parents[neighborIndex][1] = connectingEdge;
					}
				}
			}
		}

		ArrayList<Object[]> resultList = new ArrayList<Object[]>();
		Object currVertex = endVertex;

		while (currVertex != startVertex)
		{
			if(currVertex == null) break;

			int currIndex = vertexListStatic.indexOf(currVertex);
			currVertex = parents[currIndex][0];
			resultList.add(0, parents[currIndex]);
		}

		if(resultList.get(0)[0] == null) return;

		resultList.add(resultList.size(), new Object[] { endVertex, null });

		for (int i = 0; i < resultList.size(); i++)
		{
			visitor.visit(resultList.get(i)[0], resultList.get(i)[1]);
		}
	};
	
	
	
}
