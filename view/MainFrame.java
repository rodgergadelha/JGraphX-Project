package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;

import javax.swing.*;

import java.util.Map;
import java.util.List;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxConstants;
import com.mxgraph.model.mxCell;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.util.mxAnimation;

import controller.funcoesBotoes;


public class MainFrame extends JFrame {
	private mxGraph graph;
    private mxGraphComponent graphComponent;
    private mxParallelEdgeLayout layout;
    private JButton botaoDel, botaoDfs, botaoDij, botaoPeso, botaoLimpar, botaoRemoveAll, botaoBfs, botaoGrafoAleat, botaoNormal, botaoInst, botaoSalvar, botaoCarregar;
    private Map<String, Object> style;
    private JPanel grafoContainer, botoesContainer, botoesVelocidade, botoesAlgoritmos, botoesEdicao, botoesArquivo;
    private boolean direcionado;
    static int velocidade = 1000;
    private JLabel tagAlgoritmo, tagEdicao, tagVelocidade, tagArquivo;
    private GridBagConstraints gbc;

    public MainFrame() {
        initGUI();
    }
    
    private void enableButtons(boolean isEnabled) {
        for(Component c : botoesVelocidade.getComponents()) {
            c.setEnabled(isEnabled);
        }
        for(Component c : botoesEdicao.getComponents()) {
            c.setEnabled(isEnabled);
        }
        for(Component c : botoesAlgoritmos.getComponents()) {
            c.setEnabled(isEnabled);
        }
        for(Component c : botoesArquivo.getComponents()) {
            c.setEnabled(isEnabled);
        }



        graphComponent.setConnectable(isEnabled);
        graph.setCellsSelectable(isEnabled);
        graph.setCellsMovable(isEnabled);
    }

    private void showDirecionadoOptions() {
        int option = JOptionPane.showConfirmDialog(null, "Você deseja criar um grafo direcionado?", null, JOptionPane.YES_NO_OPTION);
        direcionado = option == 0;
        style = graph.getStylesheet().getDefaultEdgeStyle();

        if(direcionado) style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        else style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_WIDTH);

        graphComponent.refresh();
    }

    private void initAnimation(List<Object> vertexesStates) {
        enableButtons(false);
        
        mxAnimation animation = new mxAnimation(velocidade) {
            int i = 0;
            public void updateAnimation() {
                if(i == vertexesStates.size()) {
                    botaoLimpar.setEnabled(true);
                    stopAnimation();
                    return;
                }

                Object[] curState = (Object[])(vertexesStates.get(i));
                Object[] cells = {curState[0]};
                String color = (String)curState[1];
                String minDistance = "";
                if(curState.length > 2)  minDistance = (String)curState[2];

                if(((mxCell)cells[0]).isVertex()){
                    graph.setCellStyle(String.format("defaultVertex;fillColor=%s;shape=ellipse", color), cells);
                    ((mxCell)cells[0]).setValue(minDistance);
                    graph.refresh();
                }else {
                    graph.setCellStyle(String.format("strokeColor=%s", color), cells);
                }

                i++;
            }
        };

        animation.startAnimation();
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
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        style.put(mxConstants.STYLE_FONTCOLOR, "black");
        
        grafoContainer = new JPanel();
        grafoContainer.setLayout(new FlowLayout());

        botoesContainer = new JPanel();
        botoesContainer.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        //botoesContainer.setPreferredSize(new Dimension(300, 450));

        tagAlgoritmo = new JLabel("Algoritmos");
        botoesAlgoritmos = new JPanel();
        botoesAlgoritmos.setLayout(new GridLayout(3,2, 10, 10));

        tagEdicao = new JLabel("Edição");
        botoesEdicao = new JPanel();
        botoesEdicao.setLayout(new GridLayout(3,2, 10, 10));

        tagVelocidade = new JLabel("Velocidade");
        botoesVelocidade = new JPanel();
        botoesVelocidade.setLayout(new GridBagLayout());

        tagArquivo = new JLabel("Arquivos");
        botoesArquivo = new JPanel();
        botoesArquivo.setLayout(new GridBagLayout());
        
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setPreferredSize(new Dimension(450, 450));
        grafoContainer.add(graphComponent);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        botoesContainer.add(tagEdicao, gbc);
        botaoDel = new JButton("Deletar");
        botoesEdicao.add(botaoDel);
        botaoDel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	funcoesBotoes.deletarCelula(graph);
            }
        });

        botaoDfs = new JButton("DFS");
        botoesAlgoritmos.add(botaoDfs);
        botaoDfs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Object> vertexesStates = funcoesBotoes.getVisitedVertexesDfs(graph, direcionado);
                if(vertexesStates != null && vertexesStates.size() > 0) initAnimation(vertexesStates);
            }
        });


        botaoBfs = new JButton("BFS");
        botoesAlgoritmos.add(botaoBfs);
        botaoBfs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Object> vertexesStates = funcoesBotoes.getVisitedVertexesBfs(graph, direcionado);
                if(vertexesStates != null && vertexesStates.size() > 0) initAnimation(vertexesStates);
            }
        });



        botaoDij = new JButton("Dijkstra");
        botoesAlgoritmos.add(botaoDij);
        botaoDij.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Object> cellsToPaint = funcoesBotoes.getVisitedVertexesDjikstra(graph, direcionado);
                if(cellsToPaint != null && cellsToPaint.size() > 0) initAnimation(cellsToPaint);
            }
        });


        botaoPeso = new JButton("Mudar peso");
        botoesEdicao.add(botaoPeso);
        botaoPeso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	funcoesBotoes.mudarPeso(graphComponent);
            }
        });


        botaoLimpar = new JButton("Limpar");
        botoesAlgoritmos.add(botaoLimpar);
        botaoLimpar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] edges = graph.getChildEdges(graph.getDefaultParent());
                Object[] vertices = graph.getChildVertices(graph.getDefaultParent());

                graph.setCellStyles("fillColor", "white", vertices);
                graph.setCellStyles("strokeColor", "#6482B9", edges);

                for(Object vertex : vertices) {
                    ((mxCell)vertex).setValue("");
                    graph.refresh();
                }

                enableButtons(true);
            }
        });


        botaoRemoveAll = new JButton("Remover grafo");
        botoesEdicao.add(botaoRemoveAll);
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
            		if(SwingUtilities.isRightMouseButton(e)) funcoesBotoes.log("O número máximo de vértices foi atingido.");
            		return;
            	}
        		
        		if(graph.isCellsSelectable() && SwingUtilities.isRightMouseButton(e)) funcoesBotoes.adicionarVertice(graph, e.getX()-20, e.getY()-20);
        		
        	}
        });


        botaoGrafoAleat = new JButton("Gerar grafo aleatório");
        botoesEdicao.add(botaoGrafoAleat);
        botaoGrafoAleat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	funcoesBotoes.gerarGrafoAleatorio(graphComponent, layout);
            }
        });

        botaoNormal = new JButton("Normal");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 9);
        gbc.ipadx = 100;
        gbc.gridx = 0;
        gbc.gridy = 0;
        botoesVelocidade.add(botaoNormal, gbc);
        botaoNormal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	velocidade = 750;
            }
        });

        botaoInst = new JButton("Instantâneo");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.ipadx = 73;
        gbc.gridx = 1;
        gbc.gridy = 0;
        botoesVelocidade.add(botaoInst, gbc);
        botaoInst.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	velocidade = 0;
            }
        });

        botaoSalvar = new JButton("Salvar Grafo");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 9);
        gbc.ipadx = 68;
        gbc.gridx = 0;
        gbc.gridy = 0;
        botoesArquivo.add(botaoSalvar, gbc);
        botaoSalvar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(null);
                String path = "";
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    path = fileChooser.getSelectedFile().getPath();
                }

                if(!path.equals("")) funcoesBotoes.salvarGrafo(graph, path);
            }
        });
        botaoCarregar = new JButton("Carregar Grafo");
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.ipadx = 52;
        gbc.gridx = 1;
        gbc.gridy = 0;
        botoesArquivo.add(botaoCarregar, gbc);
        botaoCarregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(null);
                String path = "";
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    path = fileChooser.getSelectedFile().getPath();
                }

                if(!path.equals("")) funcoesBotoes.carregarGrafo(graph, path);
            }
        });

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.ipadx = 0;
        gbc.ipady = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        botoesContainer.add(tagEdicao, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        botoesContainer.add(botoesEdicao, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        botoesContainer.add(tagAlgoritmo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        botoesContainer.add(botoesAlgoritmos, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        botoesContainer.add(tagVelocidade, gbc);
        gbc.insets = new Insets(2, 0, 30, 0);
        gbc.gridx = 0;
        gbc.gridy = 5;
        botoesContainer.add(botoesVelocidade, gbc);
        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.gridx = 0;
        gbc.gridy = 6;
        botoesContainer.add(tagArquivo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        botoesContainer.add(botoesArquivo, gbc);

        
        setLayout(new FlowLayout(FlowLayout.CENTER));
        getContentPane().add(grafoContainer);
        getContentPane().add(botoesContainer);

        setTitle("Graph Visualizer");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        showDirecionadoOptions();
        
    }
}
