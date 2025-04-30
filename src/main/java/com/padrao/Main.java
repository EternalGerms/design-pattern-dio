package com.padrao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Padrão Singleton - Estoque do Restaurante
        EstoqueRestaurante estoque = EstoqueRestaurante.getInstance();
        estoque.adicionarIngrediente("queijo", 100);
        estoque.adicionarIngrediente("tomate", 50);
        estoque.adicionarIngrediente("carne", 30);
        
        // Padrão Factory - Itens do Menu
        ItemMenu pizza = FabricaItemMenu.criarItemMenu("pizza", "Margherita", 
            12.99, Arrays.asList("queijo", "tomate"));
        ItemMenu hamburguer = FabricaItemMenu.criarItemMenu("hamburguer", "X-Burger", 
            8.99, Arrays.asList("carne", "queijo"));
        
        // Padrão Strategy - Métodos de Pagamento
        EstrategiaPagamento pagamento = new PagamentoCartaoCredito("1234-5678-9012-3456");
        
        // Padrão Observer - Sistema de Notificação da Cozinha
        EquipeCozinha chefe = new EquipeCozinha("Chef João");
        EquipeCozinha assistente = new EquipeCozinha("Assistente Miguel");
        
        // Criar e processar um pedido
        Pedido pedido = new Pedido();
        pedido.adicionarItem(pizza);
        pedido.adicionarItem(hamburguer);
        pedido.adicionarObservador(chefe);
        pedido.adicionarObservador(assistente);
        
        // Verificar ingredientes e fazer pedido
        boolean podePrepararPedido = true;
        for (ItemMenu item : Arrays.asList(pizza, hamburguer)) {
            for (String ingrediente : item.getIngredientes()) {
                if (!estoque.usarIngrediente(ingrediente, 1)) {
                    podePrepararPedido = false;
                    System.out.println("Não é possível preparar o pedido: " + ingrediente + " insuficiente");
                    break;
                }
            }
        }
        
        if (podePrepararPedido) {
            pedido.fazerPedido();
            pagamento.pagar(pedido.getValorTotal());
        }
    }
}

// Padrão Singleton - Estoque do Restaurante
class EstoqueRestaurante {
    private static EstoqueRestaurante instancia;
    private Map<String, Integer> ingredientes;
    
    private EstoqueRestaurante() {
        ingredientes = new HashMap<>();
    }
    
    public static synchronized EstoqueRestaurante getInstance() {
        if (instancia == null) {
            instancia = new EstoqueRestaurante();
        }
        return instancia;
    }
    
    public void adicionarIngrediente(String nome, int quantidade) {
        ingredientes.put(nome, ingredientes.getOrDefault(nome, 0) + quantidade);
    }
    
    public boolean usarIngrediente(String nome, int quantidade) {
        int atual = ingredientes.getOrDefault(nome, 0);
        if (atual >= quantidade) {
            ingredientes.put(nome, atual - quantidade);
            return true;
        }
        return false;
    }
}

// Padrão Factory - Itens do Menu
interface ItemMenu {
    String getNome();
    double getPreco();
    List<String> getIngredientes();
}

class Pizza implements ItemMenu {
    private String nome;
    private double preco;
    private List<String> ingredientes;
    
    public Pizza(String nome, double preco, List<String> ingredientes) {
        this.nome = nome;
        this.preco = preco;
        this.ingredientes = ingredientes;
    }
    
    @Override
    public String getNome() { return nome; }
    
    @Override
    public double getPreco() { return preco; }
    
    @Override
    public List<String> getIngredientes() { return ingredientes; }
}

class Hamburguer implements ItemMenu {
    private String nome;
    private double preco;
    private List<String> ingredientes;
    
    public Hamburguer(String nome, double preco, List<String> ingredientes) {
        this.nome = nome;
        this.preco = preco;
        this.ingredientes = ingredientes;
    }
    
    @Override
    public String getNome() { return nome; }
    
    @Override
    public double getPreco() { return preco; }
    
    @Override
    public List<String> getIngredientes() { return ingredientes; }
}

class FabricaItemMenu {
    public static ItemMenu criarItemMenu(String tipo, String nome, double preco, List<String> ingredientes) {
        switch (tipo.toLowerCase()) {
            case "pizza":
                return new Pizza(nome, preco, ingredientes);
            case "hamburguer":
                return new Hamburguer(nome, preco, ingredientes);
            default:
                throw new IllegalArgumentException("Tipo de item de menu desconhecido");
        }
    }
}

// Padrão Strategy - Métodos de Pagamento
interface EstrategiaPagamento {
    void pagar(double valor);
}

class PagamentoCartaoCredito implements EstrategiaPagamento {
    private String numeroCartao;
    
    public PagamentoCartaoCredito(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }
    
    @Override
    public void pagar(double valor) {
        System.out.println("Pago R$ " + valor + " usando cartão de crédito: " + numeroCartao);
    }
}

class PagamentoDinheiro implements EstrategiaPagamento {
    @Override
    public void pagar(double valor) {
        System.out.println("Pago R$ " + valor + " em dinheiro");
    }
}

// Padrão Observer - Sistema de Notificação da Cozinha
interface ObservadorPedido {
    void atualizar(Pedido pedido);
}

class EquipeCozinha implements ObservadorPedido {
    private String nome;
    
    public EquipeCozinha(String nome) {
        this.nome = nome;
    }
    
    @Override
    public void atualizar(Pedido pedido) {
        System.out.println(nome + " recebeu o pedido: " + pedido.getIdPedido());
    }
}

class Pedido {
    private static int contadorPedidos = 0;
    private int idPedido;
    private List<ItemMenu> itens;
    private List<ObservadorPedido> observadores;
    
    public Pedido() {
        this.idPedido = ++contadorPedidos;
        this.itens = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }
    
    public void adicionarItem(ItemMenu item) {
        itens.add(item);
    }
    
    public void adicionarObservador(ObservadorPedido observador) {
        observadores.add(observador);
    }
    
    public void fazerPedido() {
        for (ObservadorPedido observador : observadores) {
            observador.atualizar(this);
        }
    }
    
    public int getIdPedido() { return idPedido; }
    
    public double getValorTotal() {
        return itens.stream().mapToDouble(ItemMenu::getPreco).sum();
    }
}

// Main Restaurant Management System
class RestaurantManagementSystem {
    public static void main(String[] args) {
        // Initialize inventory
        EstoqueRestaurante estoque = EstoqueRestaurante.getInstance();
        estoque.adicionarIngrediente("queijo", 100);
        estoque.adicionarIngrediente("tomate", 50);
        estoque.adicionarIngrediente("carne", 30);
        
        // Create menu items using factory
        ItemMenu pizza = FabricaItemMenu.criarItemMenu("pizza", "Margherita", 
            12.99, Arrays.asList("queijo", "tomate"));
        ItemMenu hamburguer = FabricaItemMenu.criarItemMenu("hamburguer", "X-Burger", 
            8.99, Arrays.asList("carne", "queijo"));
        
        // Create kitchen staff observers
        EquipeCozinha chefe = new EquipeCozinha("Chef João");
        EquipeCozinha assistente = new EquipeCozinha("Assistente Miguel");
        
        // Create and process an order
        Pedido pedido = new Pedido();
        pedido.adicionarItem(pizza);
        pedido.adicionarItem(hamburguer);
        pedido.adicionarObservador(chefe);
        pedido.adicionarObservador(assistente);
        
        // Check ingredients and place order
        boolean canPrepare = true;
        for (ItemMenu item : Arrays.asList(pizza, hamburguer)) {
            for (String ingrediente : item.getIngredientes()) {
                if (!estoque.usarIngrediente(ingrediente, 1)) {
                    canPrepare = false;
                    System.out.println("Não é possível preparar o pedido: " + ingrediente + " insuficiente");
                    break;
                }
            }
        }
        
        if (canPrepare) {
            pedido.fazerPedido();
            // Process payment using strategy pattern
            EstrategiaPagamento pagamento = new PagamentoCartaoCredito("1234-5678-9012-3456");
            pagamento.pagar(pedido.getValorTotal());
        }
    }
}