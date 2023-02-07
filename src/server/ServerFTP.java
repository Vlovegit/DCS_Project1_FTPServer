package server;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFTP {

	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;

	public static void main(String[] args)
	{
		// Here we define Server Socket running on port 900
		String currentDir = System.getProperty("user.dir");
		try (ServerSocket serverSocket
			= new ServerSocket(900)) {
			System.out.println(
				"Server is Starting in Port 900");
			// Accept the Client request using accept method
			Socket clientSocket = serverSocket.accept();
			System.out.println("Connected");
			dataInputStream = new DataInputStream(
				clientSocket.getInputStream());
			dataOutputStream = new DataOutputStream(
				clientSocket.getOutputStream());
			// Here we call receiveFile define new for that
			// file
			String command = "";
			while(!(command.equals("quit")))
			{
				command = dataInputStream.readUTF();
				System.out.println(command);
			switch(command)
			{
				case "put" : receiveFile(currentDir+"/src/Server/".concat("NewFile1.docx"));
							 break;
				case "quit" : System.out.println("Client connection closed");
							  dataInputStream.close();
							  dataOutputStream.close();
							  clientSocket.close();
							  break;
				default 	: System.out.println("Valid command not found");
							  break;
			}	
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// receive file function is start here

	private static void receiveFile(String fileName)
		throws Exception
	{
		int bytes = 0;
		FileOutputStream fileOutputStream
			= new FileOutputStream(fileName);

		long size
			= dataInputStream.readLong(); // read file size
		byte[] buffer = new byte[4 * 1024];
		while (size > 0
			&& (bytes = dataInputStream.read(
					buffer, 0,
					(int)Math.min(buffer.length, size)))
					!= -1) {
			// Here we write the file using write method
			fileOutputStream.write(buffer, 0, bytes);
			size -= bytes; // read upto file size
		}
		// Here we received file
		System.out.println("File is Received");
		fileOutputStream.close();
	}
}
