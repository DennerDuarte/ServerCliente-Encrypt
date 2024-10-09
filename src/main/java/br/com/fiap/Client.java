package br.com.fiap;

import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Scanner;

public class Client {
    Socket socket;

    public void communicateWithServer() throws Exception {
        System.out.println("Tentando conectar ao servidor...");
        socket = new Socket("localhost", 9600);
        System.out.println("Conectado ao servidor!");

        BigInteger p = new BigInteger("61");
        BigInteger q = new BigInteger("53");

        KeyPair chaves = Cryptography.generateKey(p, q);
        Scanner input = new Scanner(System.in);

        System.out.println("Recebendo chave pública do servidor...");
        PublicKey chavePublica = Connection.receiveKey(socket);
        System.out.println("Chave pública recebida do servidor: " + chavePublica);

        System.out.println("Enviando chave pública ao servidor...");
        Connection.sendKey(socket, chaves.getPublic());
        System.out.println("Chave pública enviada ao servidor.");

        while (true) {
            System.out.print("\nDigite a sua mensagem (ou 'sair' para encerrar): ");
            String textoRequisicao = input.nextLine();
            if (textoRequisicao.equalsIgnoreCase("sair")) {
                break;
            }

            String textoCifrado = Cryptography.encrypt(textoRequisicao, chavePublica);
            Connection.send(socket, textoCifrado);
            System.out.println("Mensagem cifrada enviada ao servidor.");

            String textoRecebido = Connection.receive(socket);
            String textoDecifrado = Cryptography.decrypt(textoRecebido, chaves.getPrivate());

            System.out.println("Servidor enviou: " + textoDecifrado);
        }

        socket.close();
        input.close();
    }

    public static void main(String[] args) {
        try {
            Client cliente = new Client();
            cliente.communicateWithServer();
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Erro durante a execução do cliente: " + e.getMessage());
        }
    }
}
