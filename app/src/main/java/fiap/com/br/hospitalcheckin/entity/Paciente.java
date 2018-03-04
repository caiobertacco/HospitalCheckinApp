package fiap.com.br.hospitalcheckin.entity;

/**
 * Created by caio_ on 03/03/2018.
 */

public class Paciente {
    private int id;
    private String nome;
    private String sobrenome;
    private String convenio;
    private String numCartaoConvenio;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }

    public String getNumCartaoConvenio() {
        return numCartaoConvenio;
    }

    public void setNumCartaoConvenio(String numCartaoConvenio) {
        this.numCartaoConvenio = numCartaoConvenio;
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", sobrenome='" + sobrenome + '\'' +
                ", convenio='" + convenio + '\'' +
                ", numCartaoConvenio='" + numCartaoConvenio + '\'' +
                '}';
    }
}
