import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {
	public static void main(String args[]) {

		ServerSocket listenSocket = null;

		try {
			// Porta do servidor
			int serverPort = 7896;
			
			// Fica ouvindo a porta do servidor esperando uma conexao.
			listenSocket = new ServerSocket(serverPort);
			System.out.println("Servidor: ouvindo porta TCP/7896.");

			while (true) {
				Socket clientSocket = listenSocket.accept();
				new Connection(clientSocket);
			}
		} catch (IOException e) {
			System.out.println("Listen socket:" + e.getMessage());
		} finally {
			if (listenSocket != null)
				try {
					listenSocket.close();
					System.out.println("Servidor: liberando porta TCP/7896.");
				} catch (IOException e) {
					/* close falhou */
				}
		}
	}

}

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	static ArrayList <String> participantes = new ArrayList<String>();
	int[] room = {1,2,3,4,5};
	

	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Conexao:" + e.getMessage());
		}
	}

	public void run() {
		Room s1 = new Room();
		s1.roomID = 1;
		Room s2 = new Room();
		s2.roomID = 3;
		Room s3 = new Room();
		s3.roomID = 3;
		Room s4 = new Room();
		s4.roomID = 4;
		Room s5 = new Room();
		s5.roomID = 5;
		
		try {
			while (true){
				String data = in.readUTF();
				System.out.println("Recebido: " + data);
				String comando = data.split("-")[0];
				String nome = data.split("-")[1];
				String sala = data.split("-")[3];
				switch (comando) {
					case "Entrar":
						int numSala = 1;
						try {
				            numSala = Integer.parseInt(sala);
				        } catch (NumberFormatException e) {
				            System.out.println("Número inteiro inválido!");
				        }
						if (numSala==1) {
							s1.members.add(nome);
						}else if (numSala==2) {
							s2.members.add(nome);
						}else if (numSala==3) {
							s3.members.add(nome);
						}else if (numSala==4) {
							s4.members.add(nome);
						}else if (numSala==5) {
							s5.members.add(nome);
						}else {
							break;
						}
						out.writeUTF("228.5.6.7");
						break;
					case "Sair":
						int numSalaSair = 1;
						try {
							numSalaSair = Integer.parseInt(sala);
				        } catch (NumberFormatException e) {
				            System.out.println("Número inteiro inválido!");
				        }
						if (numSalaSair==1) {
							s1.members.remove(nome);
						}else if (numSalaSair==2) {
							s2.members.remove(nome);
						}else if (numSalaSair==3) {
							s3.members.remove(nome);
						}else if (numSalaSair==4) {
							s4.members.remove(nome);
						}else if (numSalaSair==5) {
							s5.members.remove(nome);
						}else {
							break;
						}
						participantes.remove(nome);
						break;
					default:
						System.out.println("ERRO: Nenhum comando valido.");
				}
				System.out.println("*** Participantes ***\n");
				System.out.println("* Sala 1 *\n");
				for (String participante : s1.members)
					System.out.println("<" + participante + ">");
				System.out.println("\n\n* Sala 2 *\n");
				for (String participante : s2.members)
					System.out.println("<" + participante + ">");
				System.out.println("\n\n* Sala 3 *\n");
				for (String participante : s3.members)
					System.out.println("<" + participante + ">");
				System.out.println("\n\n* Sala 4 *\n");
				for (String participante : s4.members)
					System.out.println("<" + participante + ">");
				System.out.println("\n\n* Sala 5 *\n");
				for (String participante : s5.members)
					System.out.println("<" + participante + ">");
			}
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		} finally {
			try {
				clientSocket.close();
				System.out.println("Servidor: fechando conexao com cliente.");
			} catch (IOException e) {
				/* close falhou */
			}
		}
	}
	
	class Room
	{
		public int roomID;
		public ArrayList <String> members = new ArrayList<String>();
	
	}
	
	
}

