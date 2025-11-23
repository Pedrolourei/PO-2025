package gestorfinanceiro;

import gestorfinanceiro.factory.TipoRelatorio;
import gestorfinanceiro.model.conta.ContaCorrente;
import gestorfinanceiro.model.lancamento.Categoria;
import gestorfinanceiro.model.lancamento.Lancamento;
import gestorfinanceiro.model.lancamento.Orcamento;
import gestorfinanceiro.model.lancamento.TipoLancamento;
import gestorfinanceiro.model.usuario.Usuario;
import gestorfinanceiro.model.usuario.UsuarioIndividual;
import gestorfinanceiro.service.SistemaGestaoFinanceira;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Interação Obrigatória: Menu via Console.
 */
public class GestorFinanceiroApp {

    private final SistemaGestaoFinanceira gestor;
    private final Scanner scanner;

    public GestorFinanceiroApp() {
        // Módulo 7: Tenta carregar o estado salvo
        SistemaGestaoFinanceira.carregarEstado();
        this.gestor = SistemaGestaoFinanceira.getInstance();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        GestorFinanceiroApp app = new GestorFinanceiroApp();
        app.popularDadosIniciaisSeNecessario(); // Popula dados se for a 1ª execução
        app.run();
    }

    /**
     * Popula o sistema com dados de teste se estiver vazio (1ª execução).
     */
    private void popularDadosIniciaisSeNecessario() {
        if (!gestor.getUsuarios().isEmpty()) {
            return; // Já foi populado ou carregado
        }
        System.out.println("Populando dados iniciais (primeira execução)...");
        
        Usuario ana = new UsuarioIndividual("ana", "Ana");
        gestor.registrarUsuario(ana);
        ContaCorrente ccAna = new ContaCorrente("c1", "CC Ana", ana, 2000.0);
        gestor.registrarConta(ccAna);

        Usuario beto = new UsuarioIndividual("beto", "Beto");
        gestor.registrarUsuario(beto);
        ContaCorrente ccBeto = new ContaCorrente("c2", "CC Beto", beto, 1000.0);
        gestor.registrarConta(ccBeto);
        
        // Módulo 4: Orçamento
        Categoria catLazer = new Categoria("Lazer", "Streaming");
        Orcamento orcLazer = new Orcamento("O1", catLazer, 100.0, 
                                           LocalDate.now().withDayOfMonth(1), 
                                           LocalDate.now().withDayOfMonth(30));
        gestor.adicionarOrcamento(orcLazer);
    }


    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- SISTEMA DE GESTÃO FINANCEIRA ---");
            System.out.println("1. Lançamentos");
            System.out.println("2. Orçamentos e Metas");
            System.out.println("3. Relatórios e Análises");
            System.out.println("4. Simulações (Algoritmos)");
            System.out.println("5. Salvar e Sair");
            System.out.print("Escolha uma opção: ");

            int escolha = scanner.nextInt();
            scanner.nextLine(); // Consome o \n

            switch (escolha) {
                case 1: menuLancamentos(); break;
                case 2: menuOrcamentos(); break;
                case 3: menuRelatorios(); break;
                case 4: menuSimulacoes(); break;
                case 5:
                    gestor.salvarEstado(); // Módulo 7
                    running = false;
                    System.out.println("Sistema salvo. Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
        scanner.close();
    }

    private void menuLancamentos() {
        System.out.println("\n--- Menu Lançamentos ---");
        System.out.println("1. Nova Despesa");
        System.out.println("2. Nova Receita");
        // (Outras opções: Transferência, Estorno, etc.)
        System.out.print("Escolha: ");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        if (escolha == 1) {
            try {
                System.out.print("ID do Usuário Pagador (ex: 'ana'): ");
                String userId = scanner.nextLine();
                Usuario u = gestor.getUsuario(userId);
                if (u == null) {
                    System.out.println("Usuário não encontrado.");
                    return;
                }
                // Assume a primeira conta
                ContaCorrente c = (ContaCorrente) u.getContas().get(0); 

                System.out.print("Valor da Despesa: ");
                double valor = scanner.nextDouble();
                scanner.nextLine();
                
                System.out.print("Descrição: ");
                String desc = scanner.nextLine();
                
                System.out.print("Categoria (ex: 'Lazer'): ");
                String catNome = scanner.nextLine();
                System.out.print("Subcategoria (ex: 'Streaming'): ");
                String subCatNome = scanner.nextLine();
                
                Categoria cat = new Categoria(catNome, subCatNome);

                Lancamento l = new Lancamento(valor, LocalDate.now(), desc, 
                                              TipoLancamento.DESPESA, cat, c, null);
                
                gestor.executarLancamento(l); // Executa e verifica alertas de orçamento
                
            } catch (Exception e) {
                System.err.println("Erro ao criar lançamento: " + e.getMessage());
            }
        }
        // Implementar case 2 (Receita)
    }

    private void menuOrcamentos() {
        System.out.println("\n(Módulo 4) Lógica de Orçamentos implementada.");
        System.out.println("Um orçamento de 'Lazer' (R$ 100) foi pré-cadastrado.");
        System.out.println("Tente lançar uma despesa de R$ 110 em Lazer > Streaming para ver o alerta.");
    }
    
    private void menuRelatorios() {
        System.out.println("\n--- Menu Relatórios (Módulo 6) ---");
        // Módulo 6 (Relatórios) e Padrão Factory
        String relatorio = gestor.gerarRelatorio(TipoRelatorio.GASTOS_POR_CATEGORIA);
        System.out.println(relatorio);
    }
    
    private void menuSimulacoes() {
        System.out.println("\n--- Menu Simulações (Módulo 5) ---");
        Usuario u = gestor.getUsuario("ana");
        Categoria cat = new Categoria("Lazer", "Streaming");
        
        // Módulo 5 (Algoritmos) e Padrão Strategy
        gestor.simularCenario(u, -20.0, cat); // Simula "E se eu gastar 20% a menos..."
    }
}