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


/**
 * Classe que inicializa a interface gráfica.
 */
public class MainFrame extends JFrame {
    private mxGraph graph;
    private mxGraphComponent graphComponent;
    private mxParallelEdgeLayout layout;
    private JButton botaoDel, botaoDfs, botaoDij, botaoPeso, botaoRemoveAll, botaoBfs, botaoGrafoAleat,
            botaoNormal, botaoInst, botaoSalvar, botaoCarregar, botoaoParar, botaoPausar;
    private Map<String, Object> style;
    private JPanel botoesContainer, botoesVelocidade, botoesAlgoritmos, botoesEdicao, botoesArquivo;
    private boolean direcionado;
    private static int velocidade = 750;
    private JLabel tagAlgoritmo, tagEdicao, tagVelocidade, tagArquivo;
    private GridBagConstraints gbc;
    private mxAnimation animation;


    public MainFrame() {
        initGUI();
    }

    /**
     * Ativa os ou desativa botões da interface de acordo com o valor do parâmetro isEnabled.
     * Esse método também determina se o grafo pode ser editado ou não.
     * 
     * @param isEnabled
     */
    public void enableButtons(boolean isEnabled) {
        for (Component c : botoesVelocidade.getComponents()) {
            c.setEnabled(isEnabled);
        }
        for (Component c : botoesEdicao.getComponents()) {
            c.setEnabled(isEnabled);
        }
        for (Component c : botoesAlgoritmos.getComponents()) {
            c.setEnabled(isEnabled);
        }
        for (Component c : botoesArquivo.getComponents()) {
            c.setEnabled(isEnabled);
        }

        graphComponent.setConnectable(isEnabled);
        graph.setCellsSelectable(isEnabled);
        graph.setCellsMovable(isEnabled);
    }


    /**
     * Mostra uma caixa de diálogo ao usuário perguntando se o mesmo deseja criar um grafo direcionado.
     * O estilo das arestas muda de acordo com a escolha.
     * 
     */
    public void opcoesDirecionado() {
        Object[] options = {"Sim", "Não"};
        
        int option = JOptionPane.showOptionDialog(null, "Você deseja criar um grafo direcionado?", null,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
        
        direcionado = option == 0;
        style = graph.getStylesheet().getDefaultEdgeStyle();

        if(direcionado) style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        else style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_WIDTH);

        graphComponent.refresh();
    }


    
    /**
     * Implementa, inicia e termina as animações dos algoritmos de busca.
     * 
     * @param vertexesStates
     */
    public void initAnimation(List<Object> vertexesStates) {
        enableButtons(false);
        botaoNormal.setEnabled(true);
        botaoInst.setEnabled(true);
        botaoPausar.setEnabled(true);
        botoaoParar.setEnabled(true);

        animation = new mxAnimation(velocidade) {
            int i = 0;

            public void updateAnimation() {
                if (i == vertexesStates.size()) {
                    stopAnimation();
                    animation = null;
                    return;
                }

                Object[] curState = (Object[]) (vertexesStates.get(i));
                Object[] cells = { curState[0] };
                String color = (String) curState[1];
                String minDistance = "";
                if (curState.length > 2)
                    minDistance = (String) curState[2];

                if (((mxCell) cells[0]).isVertex()) {
                    graph.setCellStyle(String.format("defaultVertex;fillColor=%s;shape=ellipse", color), cells);
                    ((mxCell) cells[0]).setValue(minDistance);
                    graph.refresh();
                } else {
                    graph.setCellStyle(String.format("strokeColor=%s", color), cells);
                }

                i++;
            }
        };

        animation.startAnimation();
    }


    /**
     * Método que inicializa a interface gráfica, como os containers, buttons
     * e layout. Aplica os eventos quando estes são ativados.
     */
    public void initGUI() {

        //Inicializa o grafo
        graph = new mxGraph();
        // Os grafos que serão criados permitem loops.
        graph.setAllowLoops(true);
        // Os grafos que serão criados não permitem arestas sem um vértice em uma de suas extremidades.
        graph.setAllowDanglingEdges(false);
        // Os grafos que serão criados não permitem modificações no tamanho dos vértices.
        graph.setCellsResizable(false);
        // Os grafos que serão criados não permitem que o usuário insira texto nos vértices ou arestas.
        graph.setCellsEditable(false);
        // Os grafos que serão criados não permitem que o usuário mova o texto inserido pelo programa nos vértices ou arestas.
        graph.setEdgeLabelsMovable(false);
        // Os grafos que serão criados não permitem que o usuário copie vértices ou arestas.
        graph.setCellsCloneable(false);

        // Layout das arestas usado para criar espaçamento entre elas caso haja pares de vértices com arestas múltiplas.
        layout = new mxParallelEdgeLayout(graph);

        //Definindo o estilo padrão das arestas.
        style = graph.getStylesheet().getDefaultEdgeStyle();
        style.put(mxConstants.STYLE_FONTSIZE, 15);
        style.put(mxConstants.STYLE_FONTCOLOR, "black");
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);

        // Definindo o estilo padrão dos vértices.
        style = graph.getStylesheet().getDefaultVertexStyle();
        style.put(mxConstants.STYLE_FILLCOLOR, "white");
        style.put(mxConstants.STYLE_SHAPE, "ellipse");
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        style.put(mxConstants.STYLE_FONTCOLOR, "black");

        gbc = new GridBagConstraints();

        /*Container que irá armazenar todos os outros conjuntos de botões*/
        botoesContainer = new JPanel();
        botoesContainer.setLayout(new GridBagLayout());
        
        /*Tag para identifcar no front o grupo de botões relacionados aos algoritmos*/
        tagAlgoritmo = new JLabel("Algoritmos");
        
        /*Panel que armazena os botões de algoritmos*/
        botoesAlgoritmos = new JPanel();
        botoesAlgoritmos.setLayout(new GridLayout(1, 3, 10, 10));
        
        /*Tag de Edição*/
        tagEdicao = new JLabel("Edição");
        
        /*Panel que armazena os botões de edição de grafos*/
        botoesEdicao = new JPanel();
        botoesEdicao.setLayout(new GridLayout(2, 2, 10, 10));
        
        /*Tag de Velocidade*/
        tagVelocidade = new JLabel("Velocidade dos algoritmos");
        
        /*Panel que armazena os botões de alterar a velocidade da animação*/
        botoesVelocidade = new JPanel();
        botoesVelocidade.setLayout(new GridLayout(2, 2, 10, 10));
        
        /*Tag de Arquivos*/
        tagArquivo = new JLabel("Arquivos");
        
        /*Panel que armazena os botões de salvar/carregar grafos*/
        botoesArquivo = new JPanel();
        /*Como as opções de arquivo apresentam apenas dois botões, eles ficavam esticados para caber no GridLayout do container principal. 
        Por isso, usamos GridBagLayout, que é mais maleável que o GridLayout.*/
        botoesArquivo.setLayout(new GridBagLayout());
        
        /*Armazena a parte visual do Grafo */
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setPreferredSize(new Dimension(550, 525));
        
        
        
        //Inclui o botão Deletar no grupo de botões de edição
        botaoDel = new JButton("Deletar");
        botoesEdicao.add(botaoDel);
        //Implementação da funcionalidade do botão
        botaoDel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                funcoesBotoes.deletarCelula(graph);
            }
        });

        
        botaoDfs = new JButton("DFS");
        //Inclui o botão DFS no grupo de botões de algoritmos
        botoesAlgoritmos.add(botaoDfs);
        //Implementação da funcionalidade do botão
        botaoDfs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Object> vertexesStates = funcoesBotoes.getVisitedVertexesDfs(graph, direcionado);
                if (vertexesStates != null && vertexesStates.size() > 0)
                    initAnimation(vertexesStates);
            }
        });


        botaoBfs = new JButton("BFS");
        botoesAlgoritmos.add(botaoBfs);
        //Implementação da funcionalidade do botão
        botaoBfs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Object> vertexesStates = funcoesBotoes.getVisitedVertexesBfs(graph, direcionado);
                if (vertexesStates != null && vertexesStates.size() > 0)
                    initAnimation(vertexesStates);
            }
        });

        botaoDij = new JButton("Dijkstra");
        botoesAlgoritmos.add(botaoDij);
        //Implementação da funcionalidade do botão
        botaoDij.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Object> cellsToPaint = funcoesBotoes.getVisitedVertexesDjikstra(graph, direcionado);
                if (cellsToPaint != null && cellsToPaint.size() > 0)
                    initAnimation(cellsToPaint);
            }
        });

        botaoPeso = new JButton("Mudar peso");
        botoesEdicao.add(botaoPeso);
        //Implementação da funcionalidade do botão
        botaoPeso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                funcoesBotoes.mudarPeso(graphComponent);
            }
        });


        botaoRemoveAll = new JButton("Remover grafo");
        botoesEdicao.add(botaoRemoveAll);
        //Implementação da funcionalidade do botão
        botaoRemoveAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                funcoesBotoes.deletarTodasCelulas(graph);
                opcoesDirecionado();
            }
        });


        //Reconhece as ações do mouse dentro do componente do grafo
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (graph.getSelectionCell() != null && ((mxCell) (graph.getSelectionCell())).isEdge())
                    layout.execute(graph.getDefaultParent());

                if (graph.getChildVertices(graph.getDefaultParent()).length == 25) {
                    if (SwingUtilities.isRightMouseButton(e))
                        funcoesBotoes.log("O número máximo de vértices foi atingido.");
                    return;
                }

                if (graph.isCellsSelectable() && SwingUtilities.isRightMouseButton(e))
                    funcoesBotoes.adicionarVertice(graph, e.getX() - 20, e.getY() - 20);

            }
        });
        

        botaoGrafoAleat = new JButton("Gerar grafo aleatório");
        botoesEdicao.add(botaoGrafoAleat);
        //Implementação da funcionalidade do botão
        botaoGrafoAleat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                funcoesBotoes.gerarGrafoAleatorio(graphComponent, layout);
            }
        });


        //Botão que define a velocidade normal da animação dos algoritmos de busca
        botaoNormal = new JButton("Normal");
        botoesVelocidade.add(botaoNormal);
        //Funcionalidade do botão
        botaoNormal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(animation != null) {
                    animation.stopAnimation();
                    animation.setDelay(velocidade);
                    animation.startAnimation();
                }

            }
        });


        //Botão que aumenta a velocidade da animação dos algoritmos de busca
        botaoInst = new JButton("Instantâneo");
        botoesVelocidade.add(botaoInst);
        //Funcionalidade do botão
        botaoInst.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(animation != null) {
                    animation.stopAnimation();
                    animation.setDelay(0);
                    animation.startAnimation();
                }
            }
        });


        //Botão que pausa a animação de um algoritmo
        botaoPausar = new JButton("Pausar");
        botoesVelocidade.add(botaoPausar);
        //Funcionalidade do botão
        botaoPausar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(animation != null) {
                    if(!animation.isRunning()) animation.startAnimation();
                    else animation.stopAnimation();
                }
            }
        });


        //Botão que interrompe a animação de um algoritmo
        botoaoParar = new JButton("Parar");
        botoesVelocidade.add(botoaoParar);
        //Funcionalidade do botão
        botoaoParar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] edges = graph.getChildEdges(graph.getDefaultParent());
                Object[] vertices = graph.getChildVertices(graph.getDefaultParent());

                graph.setCellStyles("fillColor", "white", vertices);
                graph.setCellStyles("strokeColor", "#6482B9", edges);

                for (Object vertex : vertices) {
                    ((mxCell) vertex).setValue("");
                    graph.refresh();
                }

                enableButtons(true);

                if(animation != null) {
                    animation.stopAnimation();
                    animation = null;
                }
            }
        });


        // Botão que salva o grafo criado em um arquivo xml criado pelo usuário.
        botaoSalvar = new JButton("Salvar Grafo");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        //Distancias laterais entre elementos
        gbc.insets = new Insets(2, 0, 2, 9);
        //Padding
        gbc.ipadx = 68;
        //Coordenadas
        gbc.gridx = 0;
        gbc.gridy = 0;
        botoesArquivo.add(botaoSalvar, gbc);
        //Funcionalidade do botão
        botaoSalvar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(null);
                String path = "";
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    path = fileChooser.getSelectedFile().getPath();
                }

                if (!path.equals(""))
                    funcoesBotoes.salvarGrafo(graph, path);
            }
        });

         // Botão que carrega o grafo salvo em um arquivo xml.
        botaoCarregar = new JButton("Carregar Grafo");
        gbc.anchor = GridBagConstraints.PAGE_START;
        //Distancias laterais entre elementos
        gbc.insets = new Insets(2, 0, 2, 0);
        //Padding
        gbc.ipadx = 52;
        //Coordenadas
        gbc.gridx = 1;
        gbc.gridy = 0;
        botoesArquivo.add(botaoCarregar, gbc);
        //Funcionalidade do botão
        botaoCarregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(null);
                String path = "";
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    path = fileChooser.getSelectedFile().getPath();
                }

                if (!path.equals(""))
                    funcoesBotoes.carregarGrafo(graph, path);
            }
        });


        /*Definição das características do GridBagLayout do container de botões*/
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.ipadx = 0;
        gbc.ipady = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        botoesContainer.add(tagEdicao, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        botoesContainer.add(botoesEdicao, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        botoesContainer.add(tagAlgoritmo, gbc);
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.gridx = 0;
        gbc.gridy = 3;
        botoesContainer.add(botoesAlgoritmos, gbc);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 4;
        botoesContainer.add(tagVelocidade, gbc);
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.gridx = 0;
        gbc.gridy = 5;
        botoesContainer.add(botoesVelocidade, gbc);
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 6;
        botoesContainer.add(tagArquivo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        botoesContainer.add(botoesArquivo, gbc);


        //Define o layout do container geral
        setLayout(new FlowLayout(FlowLayout.CENTER));
        getContentPane().add(graphComponent);
        getContentPane().add(botoesContainer);

        // Definindo alguns atributos das janela.
        setTitle("Visualizador de grafos");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        // Mostra caixa de diálogo.
        opcoesDirecionado();

    }


}
