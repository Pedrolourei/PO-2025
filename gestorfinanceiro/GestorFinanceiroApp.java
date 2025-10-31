package gestorfinanceiro;

import g2.gestorfinanceiro.model.conta.*; // Importa todas as contas
import g2.gestorfinanceiro.model.lancamento.*; // Importa lançamentos
import g2.gestorfinanceiro.model.usuario.*; // Importa usuários
import g2.gestorfinanceiro.service.SistemaGestaoFinanceira;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public static void main(String[]args){System.out.println("### INICIANDO SISTEMA DE GESTÃO FINANCEIRA ###\n");

SistemaGestaoFinanceira sistema=new SistemaGestaoFinanceira();

// --- 1. Usuários e Perfis ---
System.out.println("--- 1. Criando Usuários e Grupos ---");Usuario ana=new UsuarioIndividual("u1","Ana");Usuario beto=new UsuarioIndividual("u2","Beto");Usuario carla=new UsuarioIndividual("u3","Carla");Grupo republica=new Grupo("g1","República Java");

sistema.registrarUsuario(ana);sistema.registrarUsuario(beto);sistema.registrarUsuario(carla);sistema.registrarUsuario(republica);

republica.adicionarMembro(ana,Permissao.ADMIN_GRUPO);republica.adicionarMembro(beto,Permissao.MEMBRO_CONTRIBUIDOR);republica.adicionarMembro(carla,Permissao.MEMBRO_CONTRIBUIDOR);

System.out.println("\n--- 2. Criando Contas (Polimorfismo) ---");
// --- 2. Contas e Carteiras (Polimorfismo) ---
ContaFinanceira ccAna=new ContaCorrente("c1","CC Ana (Banco A)",ana,1000.0);ContaFinanceira ccAnaDigital=new ContaDigital("c2","CD Ana (Banco B)",ana,500.0);ContaFinanceira cartaoAna=new CartaoCredito("c3","Cartão Ana (Limite 2000)",ana,2000.0);ContaFinanceira ccBeto=new ContaCorrente("c4","CC Beto (Banco A)",beto,800.0);ContaFinanceira ccCarla=new ContaCorrente("c5","CC Carla (Banco C)",carla,1500.0);ContaFinanceira ccRepublica=new Cofrinho("g1-conta","Cofrinho da República",republica,100.0);

sistema.registrarConta(ccAna);sistema.registrarConta(ccAnaDigital);sistema.registrarConta(cartaoAna);sistema.registrarConta(ccBeto);sistema.registrarConta(ccCarla);sistema.registrarConta(ccRepublica);

sistema.gerarRelatorioConsolidadoUsuario(ana);

// --- 3. Lançamentos Financeiros (Regras Obrigatórias) ---
System.out.println("\n--- 3. Executando Lançamentos ---");

Categoria catSalario=new Categoria("Receita","Salário");Categoria catMoradia=new Categoria("Moradia","Aluguel");Categoria catAlim=new Categoria("Alimentação","Supermercado");Categoria catLazer=new Categoria("Lazer","Streaming");

Lancamento l1=new Lancamento(3500.0,LocalDate.now(),"Salário Mensal",TipoLancamento.RECEITA,catSalario,ccAna);sistema.executarLancamento(l1);

Lancamento l2=new Lancamento(150.0,LocalDate.now(),"Empréstimo",ccAna,ccBeto);sistema.executarLancamento(l2);

System.out.println("\n... Testando Regra: Saldo Insuficiente ...");Lancamento l3_falha=new Lancamento(5000.0,LocalDate.now(),"Tentar comprar PC",TipoLancamento.DESPESA,catLazer,ccAna);sistema.executarLancamento(l3_falha);

System.out.println("\n... Testando Regra: Polimorfismo (Cartão) e Alerta de Limite ...");Lancamento l4_cartao=new Lancamento(1750.0,LocalDate.now(),"Compra Supermercado",TipoLancamento.DESPESA,catAlim,cartaoAna);sistema.executarLancamento(l4_cartao);

System.out.println("\n... Testando Regra: Despesa Compartilhada (Rateio Automático) ...");Lancamento l5_aluguel=new Lancamento(2100.0,LocalDate.now(),"Aluguel República",TipoLancamento.DESPESA,catMoradia,ccCarla);

Map<Usuario,Double>rateioAluguel=new HashMap<>();rateioAluguel.put(ana,700.0);rateioAluguel.put(beto,700.0);rateioAluguel.put(carla,700.0);l5_aluguel.setRateio(rateioAluguel);

sistema.executarLancamento(l5_aluguel);

System.out.println("\n... Testando Regra: Estorno ...");sistema.estornarLancamento(l2.getId());

System.out.println("\n... Testando Regra: Parcelamento ...");sistema.criarDespesaParcelada(180.0,3,LocalDate.now(),"Assinatura Streaming",catLazer,ccAnaDigital);

// --- 4. Relatórios Finais ---
System.out.println("\n\n--- 4. GERANDO RELATÓRIOS FINAIS ---");

sistema.gerarRelatorioConsolidadoUsuario(ana);sistema.gerarRelatorioConsolidadoUsuario(beto);sistema.gerarRelatorioConsolidadoUsuario(carla);sistema.gerarRelatorioConsolidadoUsuario(republica);

sistema.gerarExtratoConta(ccAna);sistema.gerarExtratoConta(ccBeto);sistema.gerarExtratoConta(ccCarla);sistema.gerarExtratoConta(cartaoAna);sistema.gerarExtratoConta(ccAnaDigital);}}