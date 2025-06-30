package restcliente;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.awt.GridLayout;


public class ClienteREST {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Menu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            String[] opcoes = {
                "Localização atual",
                "Buscar cidade por nome",
                "Top 5 cidades do Brasil",
                "Cidades por letra",
                "Cidades próximas (coordenadas)",
                "Buscar país por letra ou código",
                "Buscar citação inspiradora"
            };

            JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
            for (int i = 0; i < opcoes.length; i++) {
                final int escolha = i;
                JButton botao = new JButton(opcoes[i]);
                botao.addActionListener(e -> {
                    frame.dispose();
                    switch (escolha) {
                        case 0 -> buscarLocalizacaoAtualEProximidades();
                        case 1 -> buscarCidadePorNome();
                        case 2 -> topCidadesBrasil();
                        case 3 -> cidadesPorLetra();
                        case 4 -> cidadesProximas();
                        case 5 -> buscarPaisPorCodigo();
                        case 6 -> buscarCitacaoInspiradora();
                        default -> JOptionPane.showMessageDialog(null, "Nenhuma opção selecionada.");
                    }
                });
                painel.add(botao);
            }

            frame.getContentPane().add(painel);
            frame.setSize(500, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void buscarLocalizacaoAtualEProximidades() {
        try {
            HttpRequest requisicaoLocal = HttpRequest.newBuilder()
                    .uri(URI.create("https://ipapi.co/json/"))
                    .GET()
                    .build();

            HttpResponse<String> respostaLocal = HttpClient.newHttpClient()
                    .send(requisicaoLocal, HttpResponse.BodyHandlers.ofString());

            JSONObject localizacao = new JSONObject(respostaLocal.body());

            double lat = localizacao.getDouble("latitude");
            double lon = localizacao.getDouble("longitude");
            String cidade = localizacao.getString("city");
            String pais = localizacao.getString("country_name");

            String mensagem = String.format("Localização detectada:\nCidade: %s\nPaís: %s\nLatitude: %.4f\nLongitude: %.4f\n\nBuscando cidades próximas...",
                    cidade, pais, lat, lon);
            JOptionPane.showMessageDialog(null, mensagem, "Localização Atual", JOptionPane.INFORMATION_MESSAGE);

            String coordenadas = lat + "," + lon;
            String url = "cities?location=" + coordenadas + "&radius=100&limit=10";
            fazerRequisicao(url, false);

        } catch (IOException | InterruptedException | JSONException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao obter localização atual:\n" + ex.getMessage());
        }
    }

    private static void buscarCidadePorNome() {
        String nome = JOptionPane.showInputDialog("Digite o nome da cidade:");
        if (nome == null || nome.isEmpty()) return;
        String nomeCodificado = java.net.URLEncoder.encode(nome, java.nio.charset.StandardCharsets.UTF_8);
        fazerRequisicao("cities?namePrefix=" + nomeCodificado + "&limit=1", true);
    }

    private static void topCidadesBrasil() {
        fazerRequisicao("cities?countryIds=BR&limit=5&sort=-population", false);
    }

    private static void cidadesPorLetra() {
        String letra = JOptionPane.showInputDialog("Digite a letra inicial:");
        if (letra == null || letra.isEmpty()) return;
        fazerRequisicao("cities?namePrefix=" + letra + "&limit=10", false);
    }

    private static void cidadesProximas() {
        String lat = JOptionPane.showInputDialog("Latitude:");
        String lon = JOptionPane.showInputDialog("Longitude:");
        if (lat == null || lon == null || lat.isEmpty() || lon.isEmpty()) return;
        String coordenadas = lat + "%2C" + lon;
        String url = "cities?location=" + coordenadas + "&radius=100&limit=10";
        fazerRequisicao(url, false);
    }

    private static void buscarPaisPorCodigo() {
        String letra = JOptionPane.showInputDialog("Digite a letra ou código do país (ex: B, BR):");
        if (letra == null || letra.isEmpty()) return;
        fazerRequisicao("countries?namePrefix=" + letra + "&limit=10", false);
    }

    private static void buscarCitacaoInspiradora() {
        try {
            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create("https://famous-quotes4.p.rapidapi.com/random?category=all&count=1"))
                    .header("X-RapidAPI-Key", "16105cee0amshf3443df0cd724f5p1744ecjsn9a23acbec9a2")
                    .header("X-RapidAPI-Host", "famous-quotes4.p.rapidapi.com")
                    .GET()
                    .build();

            HttpResponse<String> resposta = HttpClient.newHttpClient()
                    .send(requisicao, HttpResponse.BodyHandlers.ofString());

            String respostaTexto = resposta.body();

            if (respostaTexto.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(respostaTexto);
                if (jsonArray.length() > 0) {
                    JSONObject quoteObj = jsonArray.getJSONObject(0);
                    String quote = quoteObj.getString("text");
                    String author = quoteObj.optString("author", "Desconhecido");

                    
                    String quoteTraduzida = traduzirTexto(quote);

                    JOptionPane.showMessageDialog(null, "\"" + quoteTraduzida + "\"\n– " + author,
                            "Citação Inspiradora (Traduzida)", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Nenhuma citação retornada.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Formato inesperado da resposta:\n" + respostaTexto);
            }

        } catch (IOException | InterruptedException | JSONException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar citação:\n" + ex.getMessage());
        }
    }

    private static String traduzirTexto(String textoIngles) {
        try {
            String url = "https://text-translator2.p.rapidapi.com/translate";

            String corpo = "source_language=en&target_language=pt&text=" + 
                    java.net.URLEncoder.encode(textoIngles, java.nio.charset.StandardCharsets.UTF_8);

            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("X-RapidAPI-Key", "16105cee0amshf3443df0cd724f5p1744ecjsn9a23acbec9a2")
                    .header("X-RapidAPI-Host", "text-translator2.p.rapidapi.com")
                    .POST(HttpRequest.BodyPublishers.ofString(corpo))
                    .build();

            HttpResponse<String> resposta = HttpClient.newHttpClient()
                    .send(requisicao, HttpResponse.BodyHandlers.ofString());

            System.out.println("Tradução -> " + resposta.body());

            JSONObject json = new JSONObject(resposta.body());
            JSONObject data = json.getJSONObject("data");
            return data.getString("translatedText");

        } catch (Exception e) {
            e.printStackTrace();
            return textoIngles + " (erro ao traduzir)";
        }
    }
    
    private static void fazerRequisicao(String caminho, boolean mostrarDetalhado) {
        try {
            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create("https://wft-geo-db.p.rapidapi.com/v1/geo/" + caminho))
                    .header("X-RapidAPI-Key", "16105cee0amshf3443df0cd724f5p1744ecjsn9a23acbec9a2")
                    .header("X-RapidAPI-Host", "wft-geo-db.p.rapidapi.com")
                    .GET()
                    .build();

            HttpResponse<String> resposta = HttpClient.newHttpClient()
                    .send(requisicao, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(resposta.body());

            if (!json.has("data")) {
                JOptionPane.showMessageDialog(null, "Nenhum dado encontrado (campo 'data' ausente na resposta).");
                return;
            }

            JSONArray data = json.getJSONArray("data");

            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhum resultado encontrado.");
                return;
            }

            StringBuilder resultado = new StringBuilder();
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);
                if (mostrarDetalhado && obj.has("name") && obj.has("country")) {
                    resultado.append(String.format(
                            "Cidade: %s (%s)\nPopulação: %d\nLat: %.4f | Lon: %.4f\n\n",
                            obj.getString("name"),
                            obj.getString("country"),
                            obj.optInt("population", 0),
                            obj.optDouble("latitude", 0),
                            obj.optDouble("longitude", 0)));
                } else if (obj.has("name") && obj.has("countryCode")) {
                    resultado.append(String.format(
                            "%s (%s)\n", obj.getString("name"), obj.getString("countryCode")));
                } else if (obj.has("name")) {
                    resultado.append(obj.getString("name")).append("\n");
                }
            }

            JTextArea areaTexto = new JTextArea(resultado.toString());
            areaTexto.setEditable(false);
            JScrollPane scroll = new JScrollPane(areaTexto);
            scroll.setPreferredSize(new java.awt.Dimension(500, 300));
            JOptionPane.showMessageDialog(null, scroll, "Resultado", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(null, "Erro:\n" + e.getMessage());
        }
    }
}
