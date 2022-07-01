package controller;

import java.util.ArrayList;
import java.util.List;

import com.mxgraph.view.mxGraph;
import com.mxgraph.analysis.StructuralException;
import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxGraphStructure;
import com.mxgraph.analysis.mxTraversal;
import com.mxgraph.view.mxGraph.mxICellVisitor;
import com.mxgraph.model.mxCell;

public class funcoesBotoes extends mxTraversal {
	
	public static void adicionarVertice(mxGraph graph,int x, int y) {
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
	
	
	
	public static List<Object> getVisitedVertexesDfs(mxGraph graph) {
		if(graph.getSelectionCells().length != 1) return null;
		
		for(Object c : graph.getSelectionCells()) {
            if(((mxCell)c).isEdge()) return null;
        }
        
        mxAnalysisGraph aGraph = new mxAnalysisGraph();
        aGraph.setGraph(graph);
        List<Object> cellsToPaint = new ArrayList<>();
        mxCell cell = (mxCell)graph.getSelectionCell();
        
        if(cell != null && cell.isVertex()) {
            dfs(aGraph, cell, new mxICellVisitor(){
                public boolean visit(Object vertex, Object edge)
                {
                    cellsToPaint.add(0, vertex);
                    
                    return false;
                }
            });
        }
        
        return cellsToPaint;

	}
	
	
	public static List<Object> getVisitedVertexesDjikstra(mxGraph graph) throws StructuralException{
        
		mxAnalysisGraph aGraph = new mxAnalysisGraph();
	    aGraph.setGraph(graph);
        ArrayList<Object> cellsToPaint = new ArrayList<>();
        Object[] cells = graph.getSelectionCells();
        if(cells.length < 2) return null;
        mxCell startVertex = (mxCell)cells[0];
        mxCell endVertex = (mxCell)cells[1];
        
        if(startVertex != null && endVertex != null && startVertex.isVertex() && endVertex.isVertex()) {

            dijkstra(aGraph, startVertex, endVertex, new mxICellVisitor(){
                public boolean visit(Object vertex, Object edge)
                {
                    cellsToPaint.add(vertex);
                    return false;
                }
            });
            
        }
        
        return cellsToPaint;
	}
	
	
	// Sobrescrita do método dijkstra
	public static void dijkstra(mxAnalysisGraph aGraph, Object startVertex, Object endVertex, mxICellVisitor visitor)
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
			Object[] neighborVertices = aGraph.getOpposites(aGraph.getEdges(closestVertex, null, true, true, false, true), closestVertex,
					true, true);

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
			int currIndex = vertexListStatic.indexOf(currVertex);
			currVertex = parents[currIndex][0];
			resultList.add(0, parents[currIndex]);
		}

		resultList.add(resultList.size(), new Object[] { endVertex, null });

		for (int i = 0; i < resultList.size(); i++)
		{
			visitor.visit(resultList.get(i)[0], resultList.get(i)[1]);
		}
	};
	
	
	
}
