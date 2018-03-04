package fiap.com.br.hospitalcheckin;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import fiap.com.br.hospitalcheckin.entity.Paciente;

/**
 * Created by caio_ on 03/03/2018.
 */

public class PacienteCheckin extends AsyncTask {

    private Paciente paciente;

    private String position;

    public PacienteCheckin(Paciente paciente){
        this.paciente = paciente;
    }

    public String getPosition() {
        return position;
    }

    public String converteParaJsonCompleto(Paciente paciente) {
        System.out.println(paciente.toString());
        JSONStringer js = new JSONStringer();
        try {
            js.object()
                    .key("id").value(paciente.getId())
                    .key("nome").value(paciente.getNome())
                    .key("sobrenome").value(paciente.getSobrenome())
                    .key("convenio").value(paciente.getConvenio())
                    .key("numCartaoConvenio").value(paciente.getNumCartaoConvenio())
                    .endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return js.toString();
    }

    public String post(String json) {
        try {
            URL url = new URL("http://192.168.0.102:9000/v1/checkin");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoOutput(true);

            PrintStream output = new PrintStream(connection.getOutputStream());
            output.println(json);

            connection.connect();

            Scanner scanner = new Scanner(connection.getInputStream());
            String resposta = scanner.next();
            System.out.println(resposta);
            return resposta;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        this.position = post(converteParaJsonCompleto(paciente));
        return null;
    }
}
