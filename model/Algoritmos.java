package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;
import java.util.Arrays;

import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.model.mxCell;


/**
 * Classe com implementações dos algoritmos de busca em Grafos.
 */
public class Algoritmos {

    static int temp; // Variável global usada no dfs

    /**
     * Método que realiza a busca em profundidade em um grafo. Sempre que um vértice é visitado ou decoberto,
     * seu estado (cor) naquele momento é salvo e será usado como se fosse um 'frame' na animação do algoritmo.
     * 
     * @param direcionado
     * @param aGraph
     * @param startVertex
     * @param vertexesStates
     */
    public static void dfs(boolean direcionado, final mxAnalysisGraph aGraph, final Object startVertex,
            final List<Object> vertexesStates) {
        temp = 0;
        dfsRec(direcionado, aGraph, startVertex, null, new HashSet<Object>(), vertexesStates);
    }

    /**
     * Dfs recursivo
     * 
     * @param direcionado
     * @param aGraph
     * @param cell
     * @param edge
     * @param seen
     * @param vertexesStates
     */
    private static void dfsRec(boolean direcionado, final mxAnalysisGraph aGraph, final Object cell, final Object edge,
            final Set<Object> seen, final List<Object> vertexesStates) {
        if (cell != null && !seen.contains(cell)) {
            temp = temp + 1;
            int tempI = temp;
            Object[] curState = { cell, "blue", "I: " + tempI };
            vertexesStates.add(curState);

            seen.add(cell);
            final Object[] edges = aGraph.getEdges(cell, (Object) null, false, true);
            final Object[] opposites = aGraph.getOpposites(edges, cell, !direcionado, true);
            // Visitando descendentes
            for (int i = 0; i < opposites.length; ++i) {
                dfsRec(direcionado, aGraph, opposites[i], edges[i], seen, vertexesStates);
            }

            // Depois de terminar a visita dos descendentes o tempo final é calculado
            temp = temp + 1;
            int tempF = temp;
            Object[] curState2 = { cell, "orange", "I: " + tempI + "\nF: " + tempF };
            vertexesStates.add(curState2);
        }
    }

    /**
     * Método que realiza a busca em largura em um grafo. Sempre que um vértice é visitado ou decoberto,
     * seu estado (cor) naquele momento é salvo e será usado como se fosse um 'frame' na animação do algoritmo.
     * 
     * @param direcionado
     * @param aGraph
     * @param startVertex
     * @param vertexesStates
     * 
     */
    public static void bfs(boolean direcionado, final mxAnalysisGraph aGraph, final Object startVertex,
            final List<Object> vertexesStates) {
        if (aGraph != null && startVertex != null && vertexesStates != null) {

            // Mudando o valor das celulas que sejam vertices para "∞"
            final List<Object> vertexes = Arrays.asList(aGraph.getChildVertices(aGraph.getGraph().getDefaultParent()));
            final int[] distances = new int[vertexes.size()];

            for (int i = 0; i < vertexes.size(); i++) {
                Object v = vertexes.get(i);
                ((mxCell) v).setValue("∞");
                distances[i] = Integer.MAX_VALUE;
            }
            aGraph.getGraph().refresh();

            final Set<Object> queued = new HashSet<Object>();
            final LinkedList<Object[]> queue = new LinkedList<Object[]>();
            final Object[] q = { startVertex, null };
            queue.addLast(q);
            queued.add(startVertex);

            distances[vertexes.indexOf(startVertex)] = 0;
            bfsRec(direcionado, aGraph, queued, queue, vertexesStates, vertexes, distances);
        }
    }

    /**
     * Bfs recursivo.
     * 
     * @param direcionado
     * @param aGraph
     * @param queued
     * @param queue
     * @param vertexesStates
     */
    private static void bfsRec(boolean direcionado, final mxAnalysisGraph aGraph, final Set<Object> queued,
            final LinkedList<Object[]> queue, final List<Object> vertexesStates, final List<Object> vertexes,
            final int[] distances) {
        if (queue.size() > 0) {
            final Object[] q = queue.removeFirst();
            final Object cell = q[0];

            // Mudando a cor do vértice visitado para laranja
            int curDistance = distances[vertexes.indexOf(cell)];
            Object[] curState = { cell, "orange", "" + curDistance };
            vertexesStates.add(curState);

            final Object[] edges = aGraph.getEdges(cell, (Object) null, !direcionado, true, true, false);
            // Procurando vértices filhos e calculando a distância do vértice inicial até eles
            for (int i = 0; i < edges.length; ++i) {
                final Object[] currEdge = { edges[i] };
                
                if (aGraph.getOpposites(currEdge, cell).length == 0)
                    continue;
                final Object opposite = aGraph.getOpposites(currEdge, cell)[0];
                if (!queued.contains(opposite)) {
                    final Object[] current = { opposite, edges[i] };
                    queue.addLast(current);
                    queued.add(opposite);

                    distances[vertexes.indexOf(opposite)] = curDistance + 1;
                    //Vértices descobertos ficam azuis
                    Object[] curState2 = { opposite, "blue", "" + (curDistance + 1) };
                    vertexesStates.add(curState2);
                }
            }
            bfsRec(direcionado, aGraph, queued, queue, vertexesStates, vertexes, distances);
        }
    }


    /**
     * Método que implementa o algoritmo de Dijkstra. Sempre que um vértice ou aresta é percorrida,
     * seu estado (cor, distancia) naquele momento é salvo e será usado como se fosse um 'frame' na animação do algoritmo.
     * 
     * @param direcionado
     * @param aGraph
     * @param startVertex
     * @param vertexesStates
     */
    public static void dijkstra(boolean direcionado, mxAnalysisGraph aGraph, Object startVertex,
            final List<Object> vertexesStates) {

        Object parent = aGraph.getGraph().getDefaultParent();
        Object[] vertexes = aGraph.getChildVertices(parent);
        int vertexCount = vertexes.length;
        double[] distances = new double[vertexCount];
        // parents[][0] é o vértice percorrido
        // parents[][1] é a aresta que sai do vértice percorrido
        Object[][] parents = new Object[vertexCount][2];
        ArrayList<Object> vertexList = new ArrayList<Object>();
        ArrayList<Object> vertexListStatic = new ArrayList<Object>();

        for (int i = 0; i < vertexCount; i++) {
            distances[i] = Integer.MAX_VALUE;
            vertexList.add((Object) vertexes[i]);
            vertexListStatic.add((Object) vertexes[i]);
        }

        // Mudando o valor das celulas que sejam vertices para "∞"
        for (Object v : vertexListStatic) {
            if (((mxCell) v).isVertex())
                ((mxCell) v).setValue("∞");
        }
        aGraph.getGraph().refresh();

        distances[vertexListStatic.indexOf(startVertex)] = 0;

        while (vertexList.size() > 0) {
            // Procurando o vértice com menor distância em verexList
            double minDistance;
            Object currVertex;
            Object closestVertex;
            currVertex = vertexList.get(0);
            int currIndex = vertexListStatic.indexOf(currVertex);
            double currDistance = distances[currIndex];
            minDistance = currDistance;
            closestVertex = currVertex;

            if (vertexList.size() > 1) {
                for (int i = 1; i < vertexList.size(); i++) {
                    currVertex = vertexList.get(i);
                    currIndex = vertexListStatic.indexOf(currVertex);
                    currDistance = distances[currIndex];

                    if (currDistance < minDistance) {
                        minDistance = currDistance;
                        closestVertex = currVertex;
                    }
                }
            }

            // Vértice de menor distância encontrado e removido
            vertexList.remove(closestVertex);

            // Mudando(salvando o novo estado desse vértice para ser pintado na animação) a cor do vértice de menor distância.
            Object[] curState1 = { closestVertex, "orange", minDistance + "" };
            if (minDistance == Integer.MAX_VALUE)
                curState1[2] = "∞";
            vertexesStates.add(curState1);

            Object currEdge = new Object();
            Object[] neighborVertices = aGraph.getOpposites(
                    aGraph.getEdges(closestVertex, null, true, true, true, true), closestVertex,
                    !direcionado, true);

            for (int j = 0; j < neighborVertices.length; j++) {
                Object currNeighbor = neighborVertices[j];

                if (vertexList.contains(currNeighbor)) {
                    // Descobrindo aresta que conecta o vértice de menor distância ao i-ésimo vértice vizinho 
                    Object[] neighborEdges = aGraph.getEdges(currNeighbor, null, true, true, false, true);
                    Object connectingEdge = null;

                    for (int k = 0; k < neighborEdges.length; k++) {
                        currEdge = neighborEdges[k];

                        if (aGraph.getTerminal(currEdge, true).equals(closestVertex)
                                || aGraph.getTerminal(currEdge, false).equals(closestVertex)) {
                            connectingEdge = currEdge;
                        }
                    }

                    // Mudando a cor da aresta vizinha percorrida
                    Object[] curState2 = { connectingEdge, "red" };
                    vertexesStates.add(curState2);

                    // Checando nova distância
                    int neighborIndex = vertexListStatic.indexOf(currNeighbor);
                    double oldDistance = distances[neighborIndex];
                    double currEdgeWeight;

                    // Mudando o peso da aresta vizinha
                    currEdgeWeight = 1.0;
                    String edgeValue = ((mxCell) connectingEdge).getValue().toString();
                    if (!edgeValue.equals(""))
                        currEdgeWeight = Double.parseDouble(edgeValue);

                    double newDistance = minDistance + currEdgeWeight;

                    // parte final - atualizando os vetores distância e pai
                    if (newDistance < oldDistance) {
                        distances[neighborIndex] = newDistance;
                        parents[neighborIndex][0] = closestVertex;
                        parents[neighborIndex][1] = connectingEdge;

                        // Atualizando o valor da label do vértice vizinho
                        Object[] curState3 = { currNeighbor, "white", newDistance + "" };
                        vertexesStates.add(curState3);
                    }
                }
            }

            // Mudando a cor do vértice para verde, indicando que o mesmo está fora de vertexList
            Object[] curState4 = { closestVertex, "#1bd19b", minDistance + "\nFORA" };
            if (minDistance == Integer.MAX_VALUE)
                curState4[2] = "∞\nFORA";
            vertexesStates.add(curState4);

        }

    };

}
