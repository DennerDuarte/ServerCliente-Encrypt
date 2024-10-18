package br.com.fiap;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Scanner;

public class Server {
    Socket socketClient;
    ServerSocket serversocket;

    public boolean connect() {
        try {
            socketClient = serversocket.accept();
            return true;
        } catch (IOException e) {
            System.err.println("Não foi possível estabelecer conexão: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            Server servidor = new Server();
            servidor.runServer();
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Erro durante a execução do servidor: " + e.getMessage());
        }
    }

    public void runServer() throws Exception {
        String textoRecebido = "";
        String textoEnviado = "Olá, Cliente";
        String textoDecifrado;
        String textoCifrado;

        Scanner input = new Scanner(System.in);

        serversocket = new ServerSocket(9600);
        System.out.println("Servidor iniciado!");

        while(true) {
            if (connect()) {
                System.out.println("Gerando chave RSA...");
                KeyPair chaves = Cryptography.generateKey();

                System.out.println("Enviando chave pública...");
                Connection.sendKey(socketClient, chaves.getPublic());
                System.out.println("Chave pública enviada.");

                System.out.println("Recebendo chave pública do cliente...");
                PublicKey chavePublica = Connection.receiveKey(socketClient);
                System.out.println("Chave pública do cliente recebida.");

                textoRecebido = Connection.receive(socketClient);
                System.out.println("Texto recebido do cliente: " + textoRecebido);

                textoDecifrado = Cryptography.decrypt(textoRecebido, chaves.getPrivate());
                System.out.println("Cliente enviou: " + textoDecifrado);

                System.out.print("\nDigite a sua mensagem: ");
                textoEnviado = input.nextLine();

                textoCifrado = Cryptography.encrypt(textoEnviado, chavePublica);
                Connection.send(socketClient, textoCifrado);
                System.out.println("Mensagem cifrada enviada ao cliente.");

                socketClient.close();
            }
        }
    }
}
