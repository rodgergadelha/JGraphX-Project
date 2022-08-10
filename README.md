<h1 align="center">
 <br>JGraphX-Project
</h1>

Aplicação Java que permite ao usuário criar grafos e visualizar os passos dos algoritmos de busca.

# Funcionalidades da aplicação

# Adicionando vértices
O usuário pode criar um vértice clicando com o botão direito do mouse no container da esquerda, pode arrastar um vértice ao segurá-lo com o botão esquerdo do mouse e criar uma nova aresta ao segurar com o botão esquerdo no centro de um vértice até outro vértice

# Deletar
Deleta o vértice escolhido pelo usuário e as arestas que incidem nele ou deleta a aresta escolhida pelo usuário.

# Mudar peso
Permite que o usuário escolha um peso qualquer para a aresta escolhida.

# Remover grafo
Exclui todos os vértices e arestas do grafo.

# Gerar grafo aleatório
Gera um grafo aleatório com 5 <= n <= 25 e uma quantidade aleatória de arestas (Mas o usuário sempre pode modificar o grafo se quiser), o grafo tem um chance de 50% de ser ponderado.

# Algoritmos
A cor branca indica que o vértice não entrou na fila, a cor azul indica que o vértice está na fila e a cor laranja/vermelho indica que o vértice já foi visitado.

# Limpar
É utilizado para limpar as informações que os algoritmos proporcionam para o usuário.

# DFS
Executa o algoritmo de busca em profundidade no vértice escolhido, no final da execução irá mostrar o tempo inicial e final de cada vértice do grafo.

# BFS
Executa o algoritmo de busca em largura no vértice escolhido.

# Dijkstra
Executa o algoritmo de caminhos mínimos Dijkstra no vértice escolhido, no final da execução mostra a distância mínima do vértice escolhido para cada vértice do grafo.

# Velocidade dos algoritmos
Botões que permitem ao usuário escolher entre a velocidade padrão de visualização do algoritmo ou a velocidade instantânea (útil em grafos muito grandes).
Também é possível pausar a animação de um algoritmo, com o botão pausar, ou interromper a animação, com o botão parar.

# Arquivos
Com esse programa também é possível salvar o grafo criado pelo usuário em um arquivo xml e depois carregá-lo novamente para ser utilizado.
