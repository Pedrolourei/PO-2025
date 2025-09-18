package exercicios.exerci2;

public class funcionario {
    private String nome;
    private String matricula;
    private String cargo;
    private double salarioBase;
    private double beneficios;

    public funcionario(String nome, String matricula, String cargo, double salarioBase, double beneficios) {
        this.nome = nome;
        this.matricula = matricula;
        setCargo(cargo);
        setSalarioBase(salarioBase);
        setBeneficios(beneficios);
    }

    public String getNome() {
        return nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo não pode ser vazio.");
        }
        this.cargo = cargo;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        if (salarioBase < 0) {
            throw new IllegalArgumentException("Salário base não pode ser negativo.");
        }
        this.salarioBase = salarioBase;
    }

    public double getBeneficios() {
        return beneficios;
    }

    public void setBeneficios(double beneficios) {
        if (beneficios < 0) {
            throw new IllegalArgumentException("Benefícios não podem ser negativos.");
        }
        this.beneficios = beneficios;
    }

    public void aplicarAjuste(double percentual) {
        double novoSalario = salarioBase + (salarioBase * percentual / 100);
        if (novoSalario < 0) {
            throw new IllegalArgumentException("Ajuste inválido. Salário não pode ser negativo.");
        }
        salarioBase = novoSalario;
    }

    public void promover(String novoCargo, double aumentoPercentual) {
        setCargo(novoCargo);
        aplicarAjuste(aumentoPercentual);
    }

    public double salarioBruto() {
        return salarioBase + beneficios;
    }
}
