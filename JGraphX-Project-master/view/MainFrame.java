package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.GridLayout;
import java.awt.Component;

import javax.swing.*;

import java.util.Map;
import java.util.List;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxConstants;
import com.mxgraph.model.mxCell;
import com.mxgraph.analysis.StructuralException;
import com.mxgraph.layout.mxParallelEdgeLayout;

import controller.funcoesBotoes;

import java.util.Timer;
import java.util.TimerTask;


public class MainFrame extends JFrame {
	private mxGraph graph;
    private mxGraphComponent graphComponent;
    private mxParallelEdgeLayout layout;
    private JButton botaoDel, botaoDfs, botaoDij, botaoPeso, botaoLimpar, botaoRemoveAll, botaoBfs, botaoGrafoAleat;
    private Map<String, Object> style;
    private JPanel grafoContainer, botoesContainer;
    private boolean direcionado;

    public MainFrame() {
        initGUI();
    }
    

    private void initAnimation(List<Object> cellsToPaint) {
        
        if(cellsToPaint.size() == 0) {
            JOptionPane.showMessageDialog(null, "Não é possível alcançar o vértice alvo.");
            return;
        }
        
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

    
    private void enableButtons(boolean isEnabled) {
        for(Component c : botoesContainer.getComponents()) {
            if(!c.equals(botaoLimpar)) c.setEnabled(isEnabled);
        }

        graph.setCellsSelectable(isEnabled);
    }
    
    
    private void log(String mensagem) {
		 JOptionPane.showMessageDialog(null, mensagem);
	}

    private void showDirecionadoOptions() {
        int option = JOptionPane.showConfirmDialog(null, "Você deseja criar um grafo direcionado?", null, JOptionPane.YES_NO_OPTION);
        direcionado = option == 0;
        style = graph.getStylesheet().getDefaultEdgeStyle();

        if(direcionado) style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        else style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_WIDTH);

        graphComponent.refresh();
    }
    
    

    private void initGUI() {

        graph = new mxGraph();
        graph.setAllowLoops(true);
        graph.setAllowDanglingEdges(false);
        graph.setCellsResizable(false);
        graph.setCellsEditable(false);
        graph.setEdgeLabelsMovable(false);
        graph.setCellsCloneable(false);
        
        layout = new mxParallelEdgeLayout(graph);
        
        style = graph.getStylesheet().getDefaultEdgeStyle();
        style.put(mxConstants.STYLE_FONTSIZE, 20);
        style.put(mxConstants.STYLE_FONTCOLOR, "black");

        style = graph.getStylesheet().getDefaultVertexStyle();
        style.put(mxConstants.STYLE_FILLCOLOR, "white");
        style.put(mxConstants.STYLE_SHAPE, "ellipse");
        
        grafoContainer = new JPanel();
        grafoContainer.setLayout(new FlowLayout());

        botoesContainer = new JPanel();
        botoesContainer.setLayout(new GridLayout(4,2, 10, 10));
        
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setPreferredSize(new Dimension(450, 450));
        grafoContainer.add(graphComponent);
        

        botaoDel = new JButton("delete");
        botoesContainer.add(botaoDel);
        botaoDel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	funcoesBotoes.deletarCelula(graph);
            }
        });

        botaoDfs = new JButton("dfs");
        botoesContainer.add(botaoDfs);
        botaoDfs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
	        	for(Object edge : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
	                if(!((mxCell)edge).getValue().equals("")) {
	                	log("O dfs não funciona em grafos ponderados.");
	                	return;
	                }
	            }
            	
                List<Object> cellsToPaint = funcoesBotoes.getVisitedVertexesDfs(graph, direcionado);
                
                if(cellsToPaint == null) return;
                
                initAnimation(cellsToPaint);
                
            }
        });


        botaoBfs = new JButton("bfs");
        botoesContainer.add(botaoBfs);
        botaoBfs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
	        	for(Object edge : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
	                if(!((mxCell)edge).getValue().equals("")) {
	                	log("O bfs não funciona em grafos ponderados.");
	                	return;
	                }
	            }
            	
                List<Object> cellsToPaint = funcoesBotoes.getVisitedVertexesBfs(graph, direcionado);
                
                if(cellsToPaint == null) return;
                
                initAnimation(cellsToPaint);
                
            }
        });



        botaoDij = new JButton("dijkistra");
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
            	
        		List<Object> cellsToPaint = null;
        		
        		try {
        			cellsToPaint = funcoesBotoes.getVisitedVertexesDjikstra(graph, direcionado);
        		}catch(StructuralException s) {
        			log("O algoritmo de Dijkstra não funciona em grafos desconexos.");
        		}
        		
            	if(cellsToPaint == null) return;
                
            	initAnimation(cellsToPaint);
                
            }
        });


        botaoPeso = new JButton("set weight");
        botoesContainer.add(botaoPeso);
        botaoPeso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Object cell = graph.getSelectionCell();
            	
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
                    else ((mxCell)cell).setValue("");
            	}
            	
            	graphComponent.refresh();
            	
            }
        });


        botaoLimpar = new JButton("clear");
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
                funcoesBotoes.deletarTodasCelulas(graph);
                showDirecionadoOptions();
            }
        });
        
        
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
        	public void mouseReleased(MouseEvent e) {
        		if(graph.getSelectionCell() != null && ((mxCell)(graph.getSelectionCell())).isEdge())
        				layout.execute(graph.getDefaultParent());
        		
        		if(graph.getChildVertices(graph.getDefaultParent()).length == 25) {
            		log("O número máximo de vértices foi atingido.");
            		return;
            	}
        		
        		if(SwingUtilities.isRightMouseButton(e)) funcoesBotoes.adicionarVertice(graph, e.getX()-20, e.getY()-20);
        		
        	}
        });
        
        
        botaoGrafoAleat = new JButton("gerar grafo aleat�rio");
        botoesContainer.add(botaoGrafoAleat);
        botaoGrafoAleat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	funcoesBotoes.gerarGrafoAleatorio(graph);
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

        showDirecionadoOptions();
        
    }
}
