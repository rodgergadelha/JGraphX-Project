import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.mxgraph.analysis.*;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraph.mxICellVisitor;
import com.mxgraph.view.mxGraphView;
import com.mxgraph.model.*;


public class AlgoritmosGrafos extends mxTraversal{
    
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
