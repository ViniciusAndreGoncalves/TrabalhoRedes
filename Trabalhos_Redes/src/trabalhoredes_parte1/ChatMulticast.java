package trabalhoredes_parte1;


import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.swing.JOptionPane;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChatMulticast {

    private static final String MULTICAST_ADDRESS = "230.0.0.0";

    private static final int PORT = 8080;

    public static void main(String[] args) {
                
        try {
            JSONObject template = carregarJson("src/trabalhoredes_parte1/template.json");

            if (template == null) {
                JOptionPane.showMessageDialog(null, "template.json não encontrado ou mal formatado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = template.optString("username", "Anônimo");

            String nomeUsuario = JOptionPane.showInputDialog(null, "Digite seu nome:", username);

            if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nome inválido. Encerrando.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MulticastSocket socket = new MulticastSocket(PORT);

            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);

            socket.joinGroup(group);
            
            String welcomeMessege = "conectou-se ao chat";
            JSONObject jsonWelcome = new JSONObject();
            jsonWelcome.put("username", nomeUsuario);
            jsonWelcome.put("message", welcomeMessege);
            jsonWelcome.put("date", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            jsonWelcome.put("time", new SimpleDateFormat("HH:mm:ss").format(new Date()));
            
            byte[] welcomeBytes = jsonWelcome.toString().getBytes();
            DatagramPacket welcomePacket = new DatagramPacket(welcomeBytes, welcomeBytes.length, group, PORT);
            socket.send(welcomePacket);

            Thread receiver = new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (true) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        socket.receive(packet);

                        String msg = new String(packet.getData(), 0, packet.getLength());

                        JSONObject json = new JSONObject(msg);

                        String date = json.getString("date");
                        String time = json.getString("time");
                        String user = json.getString("username");
                        String message = json.getString("message");

                        System.out.printf("[%s %s] %s: %s%n", date, time, user, message);
                        
                        if (!user.equals(nomeUsuario)) {
                            //JOptionPane.showMessageDialog(null, String.format("[%s %s] %s: %s", date, time, user, message), "Nova Mensagem", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Erro ao receber mensagem: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            receiver.start();

            Scanner scanner = new Scanner(System.in);
            
            System.out.println("    COMANDOS \nsair -> Encerrar programa \nhelp -> Lista comandos \n");

            while (true) {
                               
                System.out.print("Digite sua mensagem: ");
                String message = scanner.nextLine();
                
                if (message.equalsIgnoreCase("sair")) {
                    String exitMessege = "desconectou-se do chat";
                    JSONObject jsonExit = new JSONObject();
                    jsonExit.put("username", nomeUsuario);
                    jsonExit.put("message", exitMessege);
                    jsonExit.put("time", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                    jsonExit.put("date", new SimpleDateFormat("HH:mm:ss").format(new Date()));
            
                    byte[] exitBytes = jsonExit.toString().getBytes();
                    DatagramPacket exitPacket = new DatagramPacket(exitBytes, exitBytes.length, group, PORT);
                    socket.send(exitPacket);
                    System.out.println("Encerrando");
                    socket.leaveGroup(group);
                    socket.close();
                    System.exit(0);
                }
                
                if (message.equalsIgnoreCase("help") || message.equalsIgnoreCase("ajuda")) {
                    System.out.println("    COMANDOS \nsair -> Encerrar programa \nhelp -> Lista comandos \n");
                }

                String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

                JSONObject json = new JSONObject();
                if (!(message.equalsIgnoreCase("help") || message.equalsIgnoreCase("ajuda"))) {
                    json.put("date", date);
                    json.put("time", time);
                    json.put("username", nomeUsuario);
                    json.put("message", message);
                }
                
                byte[] msgBytes = json.toString().getBytes();

                DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, group, PORT);

                socket.send(packet);

                }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro geral: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static JSONObject carregarJson(String caminho) {
        try {
            String conteudo = new String(Files.readAllBytes(Paths.get(caminho)));
            
            return new JSONObject(conteudo);
        } catch (Exception e) {
            System.err.println("Erro ao carregar o JSON: " + e.getMessage());
            return null;
        }
    }
}
