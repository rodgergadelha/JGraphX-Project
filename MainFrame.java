import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;

import javax.swing.*;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxConstants;
import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.model.mxCell;
import com.mxgraph.analysis.mxTraversal;
import com.mxgraph.view.mxGraph.mxICellVisitor;
import com.mxgraph.analysis.StructuralException;


import java.util.Timer;
import java.util.TimerTask;


public class MainFrame extends JFrame {
    private mxGraph graph;
    private mxGraphComponent graphComponent;
    private JButton botaoAdd, botaoDel, botaoDfs, botaoDij, botaoPeso, botaoLimpar, botaoRemoveAll;
    private mxAnalysisGraph aGraph;
    private Map<String, Object> style;
    private JPanel grafoContainer, botoesContainer;
    private Object cell;

    public MainFrame() {
        initGUI();
    }

    private void adicionarVertice() {
    	if(graph.getChildVertices(graph.getDefaultParent()).length == 25) {
    		log("O número máximo de vértices foi atingido.");
    		return;
    	}
    	
        graph.getModel().beginUpdate();
        Object parent = this.graph.getDefaultParent();
        graph.insertVertex(parent, null, "", 100, 100, 40, 40);
        graph.getModel().endUpdate();
    }
    
    
    private void adicionarVertice(int x, int y) {
    	if(graph.getChildVertices(graph.getDefaultParent()).length == 25) {
    		log("O número máximo de vértices foi atingido.");
    		return;
    	}
    	
        graph.getModel().beginUpdate();
        Object parent = this.graph.getDefaultParent();
        graph.insertVertex(parent, null, "", x, y, 40, 40);
        graph.getModel().endUpdate();
    }
    

    private void deletarCelula() {
        this.graph.getModel().beginUpdate();
        graph.removeCells();
        this.graph.getModel().endUpdate();
    }

    private void deletarTodasCelulas() {
        graph.getModel().beginUpdate();
        graph.removeCells(graph.getChildCells(graph.getDefaultParent()), true);
        graph.getModel().endUpdate();
    }

    private void initAnimation(List<Object> cellsToPaint) {
        enableButtons(false);
        botaoLimpar.setEnabled(false);

        TimerTask animation_task = new TimerTask() {
            int i = 0;
            int size = cellsToPaint.size();

            public void run() {
                if(i < size) {
                    Object[] cells = {cellsToPaint.get(i)};
                    graph.setCellStyle("defaultVertex;fillColor=orange;shape=ellipse", cells);
                    i++;
                }else {
                    botaoLimpar.setEnabled(true);
                    cancel();
                }

                repaint();
            }
        };
        Timer animation_timer = new Timer();
        animation_timer.schedule(animation_task, 0, 500);

    }
    
 
    private void log(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem);
    }

    private void enableButtons(boolean isEnabled) {
        for(Component c : botoesContainer.getComponents()) {
            if(!c.equals(botaoLimpar)) c.setEnabled(isEnabled);
        }

        graph.setCellsSelectable(isEnabled);
    }

    private void initGUI() {

        grafoContainer = new JPanel();
        grafoContainer.setLayout(new FlowLayout());

        botoesContainer = new JPanel();
        botoesContainer.setLayout(new GridLayout(4,2, 10, 10));
        

        graph = new mxGraph();
        
        style = graph.getStylesheet().getDefaultEdgeStyle();
        style.put(mxConstants.STYLE_ENDARROW, true);
        style.put(mxConstants.STYLE_FONTSIZE, 20);
        style.put(mxConstants.STYLE_FONTCOLOR, "black");

        style = graph.getStylesheet().getDefaultVertexStyle();
        style.put(mxConstants.STYLE_FILLCOLOR, "white");
        style.put(mxConstants.STYLE_SHAPE, "ellipse");

        graph.setAllowLoops(true);
        graph.setAllowDanglingEdges(false);
        graph.setCellsResizable(false);
        graph.setCellsEditable(false);
        graph.setEdgeLabelsMovable(false);
        graph.setMultigraph(true);
        graph.setCellsCloneable(false);

        aGraph = new mxAnalysisGraph();
        aGraph.setGraph(graph);

        graphComponent = new mxGraphComponent(graph);
        graphComponent.setPreferredSize(new Dimension(450, 450));
        grafoContainer.add(graphComponent);


        botaoAdd = new JButton("add");
        botaoAdd.setPreferredSize(new Dimension(100, 50));
        botoesContainer.add(botaoAdd);
        botaoAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adicionarVertice();
            }
        });

        botaoDel = new JButton("delete");
        botaoDel.setPreferredSize(new Dimension(100, 50));
        botoesContainer.add(botaoDel);
        botaoDel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletarCelula();
            }
        });

        botaoDfs = new JButton("dfs");
        botaoDfs.setPreferredSize(new Dimension(100, 50));
        botoesContainer.add(botaoDfs);
        botaoDfs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                if(graph.getSelectionCells().length != 1) return;

                for(Object c : graph.getSelectionCells()) {
                    if(((mxCell)c).isEdge()) return;
                }
            
                
                for(Object edge : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
                    if(!((mxCell)edge).getValue().equals("")) {
                        log("O dfs não funciona em grafos ponderados.");
                        return;
                    }
                }

                List<Object> cellsToPaint = new ArrayList<>();
                mxCell cell = (mxCell)graph.getSelectionCell();
                
                if(cell != null && cell.isVertex()) {
                    mxTraversal.dfs(aGraph, cell, new mxICellVisitor(){
                        public boolean visit(Object vertex, Object edge)
                        {
                            cellsToPaint.add(0, vertex);
                            
                            return false;
                        }
                    });
                }

                initAnimation(cellsToPaint);
                
            }
        });


        botaoDij = new JButton("dijkistra");
        botaoDij.setPreferredSize(new Dimension(100, 50));
        botoesContainer.add(botaoDij);
        botaoDij.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                for(Object c : graph.getSelectionCells()) {
                    if(((mxCell)c).isEdge()) return;
                }
                
                
                for(Object edge : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
                    if(((mxCell)edge).getValue().toString().equals("")) continue;
                    
                    double edgeValue = Double.parseDouble(((mxCell)edge).getValue().toString());
                    
                    if(edgeValue < 0) {
                        log("O algoritmo de Dijkstra não funciona em grafos com pesos negativos.");
                        return;
                    }
                }

                ArrayList<Object> cellsToPaint = new ArrayList<>();
                Object[] cells = graph.getSelectionCells();
                if(cells.length < 2) return;
                mxCell startVertex = (mxCell)cells[0];
                mxCell endVertex = (mxCell)cells[1];
                
                if(startVertex != null && endVertex != null && startVertex.isVertex() && endVertex.isVertex()) {
                    try {
                        AlgoritmosGrafos.dijkstra(aGraph, startVertex, endVertex, new mxICellVisitor(){
                            public boolean visit(Object vertex, Object edge)
                            {
                                cellsToPaint.add(vertex);
                                return false;
                            }
                        });
                    }catch(StructuralException s) {
                        log("O algoritmo de Dijkstra não funciona em grafos desconexos.");
                    }
                    
                }

                initAnimation(cellsToPaint);
                
            }
        });


        botaoPeso = new JButton("set weight");
        botaoAdd.setPreferredSize(new Dimension(100, 50));
        botoesContainer.add(botaoPeso);
        botaoPeso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cell = graph.getSelectionCell();
                
                if(cell != null && ((mxCell)cell).isEdge()) {
                    String pesoStr =  JOptionPane.showInputDialog("Digite o peso dessa aresta:");
                    boolean valido = false;
                    
                    try {
                        Double.parseDouble(pesoStr);
                        valido = true;
                    }catch(NumberFormatException n) {
                        if(!pesoStr.equals("")) log("Peso inválido.");
                    }
                    
                    if(valido) ((mxCell)cell).setValue(pesoStr);
                    else ((mxCell)cell).setValue("");

                    graphComponent.refresh();
                }
            }
        });


        botaoLimpar = new JButton("clear");
        botaoLimpar.setPreferredSize(new Dimension(100, 50));
        botoesContainer.add(botaoLimpar);
        botaoLimpar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.setCellStyles("fillColor", "white", graph.getChildVertices(graph.getDefaultParent()));
                graph.setCellStyles("strokeColor", "#6482B9", graph.getChildEdges(graph.getDefaultParent()));

                enableButtons(true);
            }
        });


        botaoRemoveAll = new JButton("remover grafo");
        botoesContainer.add(botaoRemoveAll);
        botaoRemoveAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletarTodasCelulas();
            }
        });
        
        
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
        	public void mouseReleased(MouseEvent e) {
        		if(SwingUtilities.isRightMouseButton(e)) adicionarVertice(e.getX(), e.getY());
        	}
        });


        
        setLayout(new FlowLayout(FlowLayout.CENTER));
        getContentPane().add(grafoContainer);
        getContentPane().add(botoesContainer);


        setTitle("Graph Visualizer");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
    }
}
