package trabalhoredes_parte2;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import org.json.*;

import java.util.Base64;

public class ServidorArquivos {
    
    private static final int PORTA = 5001;

    
        private static final String DIRETORIO_ARQUIVOS =
        System.getProperty("user.dir") + System.getProperty("file.separator") +
        "src" + System.getProperty("file.separator") +
        "servidor_arquivos";

    public static void main(String[] args) {
        try {
            
            Files.createDirectories(Paths.get(DIRETORIO_ARQUIVOS));

            ServerSocket serverSocket = new ServerSocket();            
            
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(PORTA));
            System.out.println("Servidor iniciado na porta " + PORTA);

	    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!serverSocket.isClosed()) {
                    serverSocket.close();
                    System.out.println("Servidor finalizado.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                }
            }));


            // Loop infinito para aceitar múltiplos clientes
            while (true) {
                Socket cliente = serverSocket.accept(); 
                System.out.println("Cliente conectado: " + cliente.getInetAddress());

                // Cria uma nova thread para lidar com cada cliente de forma independente
                new Thread(() -> {
                    try {
                        
                        BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));                        
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
                        String linha;
                        
                        while ((linha = in.readLine()) != null) {
                            
                            JSONObject req = new JSONObject(linha);
                            String cmd = req.getString("cmd");

                            
                            if (cmd.equals("list_req")) {
                                File pasta = new File(DIRETORIO_ARQUIVOS);
                                String[] arquivos = pasta.list(); // Lista os nomes dos arquivos
                                JSONArray lista = new JSONArray(Arrays.asList(arquivos));

                                
                                JSONObject resp = new JSONObject();
                                resp.put("cmd", "list_resp");
                                resp.put("files", lista);
                                out.write(resp.toString() + "\n");
                                out.flush();

                            } else if (cmd.equals("put_req")) {
                                String nome = req.getString("file");
                                String base64 = req.getString("value");
                                byte[] dados = Base64.getDecoder().decode(base64);

                                Path destino = Paths.get(DIRETORIO_ARQUIVOS, nome);
                                Files.write(destino, dados);

                                JSONObject resp = new JSONObject();
                                resp.put("cmd", "put_resp");
                                resp.put("file", nome);
                                resp.put("status", "ok");
                                out.write(resp.toString() + "\n");
                                out.flush();

                            
                            } else if (cmd.equals("get_req")) {
                                String nome = req.getString("file");
                                Path caminho = Paths.get(DIRETORIO_ARQUIVOS, nome);

                                JSONObject resp = new JSONObject();
                                resp.put("cmd", "get_resp");
                                resp.put("file", nome);

                                if (Files.exists(caminho)) {
                                    byte[] dados = Files.readAllBytes(caminho);
                                    String base64 = Base64.getEncoder().encodeToString(dados); 
                                    String hash = gerarHash(dados); 

                                    resp.put("value", base64);
                                    resp.put("hash", hash);
                                } else {
                                    resp.put("value", "");
                                    resp.put("hash", "");
                                }

                                out.write(resp.toString() + "\n");
                                out.flush();
                            }
                        }

                        cliente.close();
                        System.out.println("Cliente desconectado.");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start(); // Inicia a thread
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String gerarHash(byte[] dados) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(digest.digest(dados));
    }
}