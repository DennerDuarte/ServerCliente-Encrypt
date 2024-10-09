package br.com.fiap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;

public class Connection {

    public static String receive(Socket socket) throws IOException {
        InputStream in = socket.getInputStream();
        byte[] infoBytes = new byte[256];
        int bytesLidos = in.read(infoBytes);

        if (bytesLidos > 0) {
            return Base64.getEncoder().encodeToString(infoBytes);
        } else {
            return "";
        }
    }

    public static PublicKey receiveKey(Socket socket) throws Exception {
        InputStream in = socket.getInputStream();
        byte[] tamanhoBuffer = new byte[4];
        in.read(tamanhoBuffer);
        int tamanho = ((tamanhoBuffer[0] & 0xFF) << 24) | ((tamanhoBuffer[1] & 0xFF) << 16) |
                ((tamanhoBuffer[2] & 0xFF) << 8) | (tamanhoBuffer[3] & 0xFF);

        byte[] chaveBytes = new byte[tamanho];
        in.read(chaveBytes);

        return Cryptography.bytesForKeys(chaveBytes);
    }

    public static void sendKey(Socket socket, PublicKey chave) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] chaveBytes = chave.getEncoded();
        out.write(new byte[] {
                (byte) (chaveBytes.length >> 24),
                (byte) (chaveBytes.length >> 16),
                (byte) (chaveBytes.length >> 8),
                (byte) (chaveBytes.length)
        });
        out.write(chaveBytes);
        out.flush();
    }

    public static void send(Socket socket, String textoRequisicao) throws IOException {
        byte[] bytesRequisicao = Base64.getDecoder().decode(textoRequisicao);
        OutputStream out = socket.getOutputStream();
        out.write(bytesRequisicao);
        out.flush();
    }
}
