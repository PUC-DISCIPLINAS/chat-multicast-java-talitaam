import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    static int port = 6789;
    static MulticastSocket mSocket = null;
    static InetAddress groupIp;
    static boolean ativo = true;

    public static void main(String args[]) {
		Socket s = null;
		Scanner readLine = new Scanner(System.in);
		try {
			s = new Socket("localhost", 7896);

			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			System.out.println("Insira seu nome de usuario: ");
			String nomeUsuario = readLine.nextLine();
            String entrada;
			do {
			    System.out.println("Qual sala deseja ingressar? \nOpções: 1, 2, 3, 4 e 5. \nDigite sair para encerrar o chat.");
			    entrada = readLine.nextLine();
			    if (entrada.equals("sair")) {
			        System.out.println("*** SAINDO... ***");
			        System.exit(200);
			    } else if (!entrada.equals("1") && !entrada.equals("2") && !entrada.equals("3") && !entrada.equals("4") && !entrada.equals("5")) {
			        System.out.println("ERRO: favor inserir um comando valido.");
			    } 
			} while (!entrada.equals("1") && !entrada.equals("2") && !entrada.equals("3") && !entrada.equals("4") && !entrada.equals("5") && !entrada.equals("Sair"));
			String comandoEntrada = "Entrar-" + nomeUsuario + "- Sala-" + entrada;
			out.writeUTF(comandoEntrada);
			
			
			String data = in.readUTF();

			//iniciando grupo
            try {
                groupIp = InetAddress.getByName(data);
                mSocket = new MulticastSocket(port);
                mSocket.joinGroup(groupIp);
                String mensagemEntrada = "*** " + nomeUsuario + " ENTROU NA SALA ***";
                byte[] message = mensagemEntrada.getBytes();
                DatagramPacket messageOut = new DatagramPacket(message, message.length, groupIp, port);
                mSocket.send(messageOut);
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            }

			//exibir mensagens
            Thread thread = new Thread(() -> {
                try {
                    while (ativo) {
                        byte[] buffer = new byte[1000];
                        DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length, groupIp, port);
                        mSocket.receive(messageIn);
                        /*String msg = new String(messageIn.getData()).trim();*/
                        System.out.println( new String(messageIn.getData()).trim());
                    }
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                }
            });
            thread.start();

            String mensagem;
            String mensagemComAutor;
            String comandoSaida = "Sair-" + nomeUsuario + "- da sala -" + entrada;

			while(ativo){
				mensagem= readLine.nextLine();
				if(mensagem.startsWith("sair")){
                    mSocket.leaveGroup(groupIp);
					out.writeUTF(comandoSaida);
					ativo = false;
					System.out.println("**** CHAT FOI ENCERRADO ***");
                    System.exit(200);
				}else{
				    mensagemComAutor = "<"+ nomeUsuario + "> :  " + mensagem;
                    byte[] message = mensagemComAutor.getBytes();
                    DatagramPacket messageOut = new DatagramPacket(message, message.length, groupIp, port);
                    mSocket.send(messageOut);
				}
			}
		} catch (UnknownHostException e) {
			System.out.println("Socket:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (IOException e) {
					System.out.println("close:" + e.getMessage());
				}
		}
	}
}
